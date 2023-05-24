package com.dong.demo.v1.exception;

public class ICsViolationCode {
    private static final int REFERENTIAL_MARIADB = 1452;
    private static final int REFERENTIAL_H2DB = 23506;
    // h2 DBMS 에서는 mariaDB 랑 다르게 RICs 에러 코드가 23506 이더라
    // add Write 컨트롤러 테스트에서는 h2 db 환경이라서 1452 대신 H2 가 뜬 듯?
    // 환경 전환할 때 어차피 여기 바꿔야 하니까 캡슐화하길 잘 한 거 같다.
    private static final int ENTITY = 1062;

    // 추후에는 스테이트 패턴도 고려해봐도 될 듯
    public static boolean isReferentialIntegrityViolation(int errorCode) {
        return errorCode == REFERENTIAL_MARIADB || errorCode == REFERENTIAL_H2DB;
    }

    public static boolean isEntityIntegrityViolation(int errorCode) {
        return errorCode == ENTITY;
    }
}
