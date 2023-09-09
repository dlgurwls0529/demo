package com.dong.demo.v1.domain.folder.folderFindBatchBuilder;

import com.dong.demo.v1.domain.folder.Folder;
import com.dong.demo.v1.exception.DataAccessException;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/*
목표 : 격리된 자원, 사용이 끝나면 해제, 확장성

<sol1>
메소드 체이닝에서 해당 객체를 리턴할 수는 없다.
객체에서 다시 버퍼에 접근해야 하는데, 접근을 위해선 상태를 유지해야만 하기 때문이다.
따라서, add 메소드가 스트링 버퍼를 리턴하고, 입력으로 스트링 버퍼를 받도록 한다.
근데 사실 이것도 아래와 동일하게 스트링 버퍼가 메모리에 유지된다.

<sol2>
DI Container 를 사용하지 않는 팩토리 패턴을 사용한다.
모듈 전환이 약간 더 어려워지나, stateless 와 관련한 빈 관리 메커니즘을 신경쓰지 않아도 되며,
트랜잭션 종료 후에 해당 빌더 객체가 파괴된다. 개별 버퍼를 유지하면서도 성능 누수를 발생시키지 않는 균형점.

<sol3>
DI Container 를 사용한다. 이 경우 빈 스코프를 리퀘스트 스코프, 혹은 프록시 스코프로 설정해야 하는데,
생성 시점에 따른 예외가 발생할 수 있기도 하고, 리퀘스트 스코프 빈을 가지는 싱글턴 스코프 빈에서 둘 다 싱글톤으로 작동할 수도 있다.
이에 대한 논의가 필요할 것이다.
리퀘스트 스코프는 제어가 매우 어렵다. 프로토타입 스코프를 쓰자. 이거는 생성 후에 DI Container 로부터
제어가 클라이언트로 넘어가는데, 그후부턴 가비지 컬렉션에 의해 알아서 파괴된다.
싱글톤에 중첩되면 싱글톤 스코프처럼 활용된다. 따라서 생성 시점에 디펜던시 룩업을 쓰면 될 것같다.

장점으로 룩업을 통해서 컴포넌트 제어를 할 수 있다. --> 이게 가장 나은 방법인 듯 하다.

https://stackoverflow.com/questions/8618735/spring-how-can-i-destroy-my-prototype-scoped-beans

Spring 은 코드에 필요한 프로토타입 빈을 생성하지만 이를 파괴하지는 않습니다.
프로토타입 종속성이 범위를 벗어나면 가비지 수집기가 프로토타입 종속성을 '선택'하도록
허용하는 것이 더 효과적인 것으로 간주되므로 이 동작은 의도적으로 설계된 것입니다.
이 방식으로 수행되지 않은 경우 유일한 대안은 Spring 컨테이너가 생성된 프로토타입 빈의
모든 인스턴스에 대한 참조를 유지하는 것입니다. 코드가 호출될 때마다 MyPrototypeBean
빈의 새 인스턴스를 생성한다는 점을 고려하면 이는 잠재적으로 너무 많은 MyPrototypeBean
인스턴스가 생성되어 수집되지만 파괴되지 않을 수 있음을 의미합니다. 파괴는 Spring 컨테이너를 닫을 때만 발생하기 때문입니다.
결과적으로 프로토타입 Bean 은 가비지 수집기에 의해 처리되고 범위를 벗어나거나 파괴 메소드가 수동으로 호출될 때 파괴되도록 설계되었습니다.
문서에 따르면 MyPrototypeBean 이 데이터베이스 연결과 같은 리소스를 보유하고 있으면 해당 리소스가 제대로 해제되지 않을 것이라고 합니다.
따라서 이를 관리하는 것은 코더의 책임이 됩니다.
 */

// folderFindBatchBuilder 테스트 작성. 프로토타입 스코프 생성 및 파괴도 같이 테스트.
// -> 파괴는 JVN 이 알아서 잘 해주더라, 생성은 두 개 만들어서 주소 불일치 여부로 테스트함.

@Repository
@Scope("prototype")
@RequiredArgsConstructor
public class JdbcFolderFindBatchBuilder implements FolderFindBatchBuilder {

    private final DataSource dataSource;
    private final String query_prefix = "SELECT * FROM Folder WHERE folderCP IN (";
    private final String query_suffix = ");";
    private final StringBuilder query_buffer = new StringBuilder(query_prefix);

    /*
        may be ...
        @Autowired
        public Constructor(DataSource dataSource) {
            this.dataSource = dataSource;
            this.query_prefix = "SELECT * FROM Folder WHERE folderCP ... ";
            ...
            this.query_buffer = new StringBuilder(query_prefix);
        }
    */

    public FolderFindBatchBuilder append(String folderCP) {
        query_buffer.append('\'')
                    .append(folderCP)
                    .append('\'')
                    .append(",");

        return this;
    }

    public List<Folder> execute() {
        // 아무것도 추가 안되었으면 그냥 빈 배열 리턴
        if (query_buffer.toString().length() == query_prefix.length()) return new ArrayList<>();

        // 마지막 반점 지우고, 괄호 넣기.
        query_buffer.deleteCharAt(query_buffer.length() - 1);
        query_buffer.append(query_suffix);

        // 여기서부터 실행.
        Connection connection = DataSourceUtils.getConnection(dataSource);
        List<Folder> folderList = new ArrayList<>();

        try (ResultSet resultSet = connection.prepareStatement(query_buffer.toString()).executeQuery();) {
            while (resultSet.next()) {
                Folder folder = Folder.builder()
                        .folderCP(resultSet.getString("folderCP"))
                        .isTitleOpen(resultSet.getBoolean("isTitleOpen"))
                        .title(resultSet.getString("title"))
                        .symmetricKeyEWF(resultSet.getString("symmetricKeyEWF"))
                        .lastChangedDate(resultSet.getTimestamp("lastChangedDate").toLocalDateTime())
                        .build();

                folderList.add(folder);
            }

        } catch (SQLException e) {
            throw new DataAccessException(e);
        } finally {
            DataSourceUtils.releaseConnection(connection, dataSource);
        }

        return folderList;
    }
}
