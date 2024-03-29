package com.dong.demo.v1.service.folder;

import com.dong.demo.v1.domain.folder.Folder;
import com.dong.demo.v1.domain.folder.FolderRepository;
import com.dong.demo.v1.domain.folder.folder_search.FolderSearch;
import com.dong.demo.v1.domain.folder.folder_search.FolderSearchRepository;
import com.dong.demo.v1.exception.DuplicatePrimaryKeyException;
import com.dong.demo.v1.util.LocalDateTime6Digit;
import com.dong.demo.v1.web.dto.FoldersGenerateRequestDto;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.test.context.ActiveProfiles;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

@SpringBootTest
@ActiveProfiles("test-db")
class DistributedFolderServiceTest {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private FolderSearchRepository folderSearchRepository;

    @Autowired
    private FolderRepository folderRepository;

    @Autowired
    @Qualifier("distributed")
    private FolderService folderService;

    @AfterEach
    @BeforeEach
    public void cleanup() {
        folderRepository.deleteAll();
        folderSearchRepository.deleteAll();
    }

    @Test
    public void folderSearch_keyword_equal_test() {
        // given
        String target_keyword = "FCP_3";

        for (int i = 1; i <= 10; i++) {
            Folder folder = Folder.builder()
                    .folderCP("FCP_" + i)
                    .isTitleOpen(true)
                    .title("TITLE_" + i)
                    .symmetricKeyEWF("sym_TEST")
                    .lastChangedDate(LocalDateTime6Digit.now())
                    .build();

            folderRepository.save(folder);
            folderSearchRepository.save(folder.getFolderSearch());
        }

        // when
        List<FolderSearch> folderSearches = folderService.search(target_keyword);

        // then
        Assertions.assertEquals(1, folderSearches.size());
        Assertions.assertEquals(target_keyword, folderSearches.get(0).getFolderCP());
    }

    @Test
    public void folderSearch_keyword_not_equal_test() {
        // given
        String target_keyword = "_3";

        for (int i = 1; i <= 10; i++) {
            Folder folder = Folder.builder()
                    .folderCP("FCP_" + i)
                    .isTitleOpen(true)
                    .title("TITLE_" + i)
                    .symmetricKeyEWF("sym_TEST")
                    .lastChangedDate(LocalDateTime6Digit.now())
                    .build();

            folderRepository.save(folder);
            folderSearchRepository.save(folder.getFolderSearch());
        }

        // when
        List<FolderSearch> folderSearches = folderService.search(target_keyword);

        // then
        Assertions.assertEquals(10, folderSearches.size());
        Assertions.assertEquals("TITLE" + target_keyword, folderSearches.get(0).getTitle());

        for (FolderSearch folderSearch : folderSearches) {
            System.out.print("[folderCP] : ");
            System.out.println(folderSearch.getFolderCP());
            System.out.print("[title] : ");
            System.out.println(folderSearch.getTitle());
            System.out.println();
        }
    }

    @Test
    public void folderSearch_title_duplicate_test() {
        // given
        String target_keyword = "_12";

        for (int i = 1; i <= 8; i++) {
            Folder folder = Folder.builder()
                    .folderCP("FCP_" + i)
                    .isTitleOpen(true)
                    .title("TITLE_" + i)
                    .symmetricKeyEWF("sym_TEST")
                    .lastChangedDate(LocalDateTime6Digit.now())
                    .build();

            folderRepository.save(folder);
            folderSearchRepository.save(folder.getFolderSearch());
        }

        for (int i = 9; i <= 10; i++) {
            Folder folder = Folder.builder()
                    .folderCP("FCP_" + i)
                    .isTitleOpen(true)
                    .title("TITLE" + target_keyword)
                    .symmetricKeyEWF("sym_TEST")
                    .lastChangedDate(LocalDateTime6Digit.now())
                    .build();

            folderRepository.save(folder);
            folderSearchRepository.save(folder.getFolderSearch());
        }

        // when
        List<FolderSearch> folderSearches = folderService.search(target_keyword);

        // then
        Assertions.assertEquals(10, folderSearches.size());
        Assertions.assertEquals("TITLE" + target_keyword, folderSearches.get(0).getTitle());
        Assertions.assertEquals("TITLE" + target_keyword, folderSearches.get(1).getTitle());

        for (FolderSearch folderSearch : folderSearches) {
            System.out.print("[folderCP] : ");
            System.out.println(folderSearch.getFolderCP());
            System.out.print("[title] : ");
            System.out.println(folderSearch.getTitle());
            System.out.println();
        }
    }

    public void concurrent_test_1() throws SQLException {
        Semaphore semaphore1 = new Semaphore(0);
        Semaphore semaphore2 = new Semaphore(0);
        Semaphore semaphore3 = new Semaphore(0);
        CountDownLatch latch = new CountDownLatch(2);
        ExecutorService executorService = Executors.newFixedThreadPool(2);

        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try (Connection connection = DataSourceUtils.getConnection(dataSource)) {
                    connection.setAutoCommit(false);
                    String sql = """
                                INSERT INTO Folder (folderCP, isTitleOpen, title, symmetricKeyEWF, lastChangedDate)
                                VALUES ('folderCP_TEST', true, 'I am TX 1', 'SymmetricKey1', '2023-07-22 12:34:56');
                            """;
                    System.out.println("t1 write start");
                    connection.prepareStatement(sql).execute();
                    System.out.println("t1 write end");

                    semaphore1.release();
                    semaphore2.acquire();

                    sql = "SELECT * FROM Folder";
                    System.out.println("t1 read start");
                    connection.prepareStatement(sql).execute();
                    System.out.println("t1 read end");

                    connection.commit();
                    System.out.println("t1 committed");

                    semaphore3.release();

                } catch (SQLException | InterruptedException e) {
                    latch.countDown();
                } finally {
                    latch.countDown();
                }
            }
        });

        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try (Connection connection = DataSourceUtils.getConnection(dataSource)) {
                    connection.setAutoCommit(false);
                    semaphore1.acquire();
                    String sql = """
                                INSERT INTO Folder (folderCP, isTitleOpen, title, symmetricKeyEWF, lastChangedDate)
                                VALUES ('folderCP_TEST', true, 'I am TX 2', 'SymmetricKey1', '2023-07-22 12:34:56');
                            """;
                    System.out.println("t2 write start");
                    connection.prepareStatement(sql).execute();
                    System.out.println("t2 write end");

                    semaphore2.release();
                    semaphore3.acquire();

                    sql = "SELECT * FROM Folder";
                    System.out.println("t2 read start");
                    connection.prepareStatement(sql).execute();
                    System.out.println("t2 read end");

                    connection.commit();
                    System.out.println("t2 committed");

                } catch (SQLException | InterruptedException e) {
                    latch.countDown();
                } finally {
                    latch.countDown();
                }
            }
        });

        Assertions.assertDoesNotThrow(new Executable() {
            @Override
            public void execute() throws Throwable {
                latch.await();
            }
        });

    }

    // 얘도 충돌
    public void concurrent_test_2() throws SQLException {
        Semaphore semaphore1 = new Semaphore(0);
        Semaphore semaphore2 = new Semaphore(0);
        Semaphore semaphore3 = new Semaphore(0);
        CountDownLatch latch = new CountDownLatch(2);
        ExecutorService executorService = Executors.newFixedThreadPool(2);

        // 'folderCP_TEST', true, 'I am TX 1', 'SymmetricKey1', '2023-07-22 12:34:56'
        folderRepository.save(Folder.builder()
                        .folderCP("folderCP_TEST")
                        .isTitleOpen(true)
                        .title("title_PRIMARY")
                        .symmetricKeyEWF("sym_TEST")
                        .lastChangedDate(LocalDateTime6Digit.now())
                        .build()
        );

        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try (Connection connection = DataSourceUtils.getConnection(dataSource)) {
                    connection.setAutoCommit(false);
                    String sql = """
                                DELETE FROM Folder;
                            """;
                    System.out.println("t1 write start");
                    connection.prepareStatement(sql).execute();
                    System.out.println("t1 write end");

                    semaphore1.release();
                    semaphore2.acquire();

                    sql = "SELECT * FROM Folder";
                    System.out.println("t1 read start");
                    connection.prepareStatement(sql).execute();
                    System.out.println("t1 read end");

                    // connection.commit();
                    connection.rollback();
                    System.out.println("t1 rollback");

                    semaphore3.release();

                } catch (SQLException | InterruptedException e) {
                    latch.countDown();
                } finally {
                    latch.countDown();
                }
            }
        });

        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try (Connection connection = DataSourceUtils.getConnection(dataSource)) {
                    connection.setAutoCommit(false);
                    semaphore1.acquire();
                    String sql = """
                                INSERT INTO Folder (folderCP, isTitleOpen, title, symmetricKeyEWF, lastChangedDate)
                                VALUES ('folderCP_TEST', true, 'title_TX_2', 'sym_TEST', '2023-07-22 12:34:56');
                            """;
                    System.out.println("t2 write start");
                    connection.prepareStatement(sql).execute();
                    System.out.println("t2 write end");

                    semaphore2.release();
                    semaphore3.acquire();

                    sql = "SELECT * FROM Folder";
                    System.out.println("t2 read start");
                    connection.prepareStatement(sql).execute();
                    System.out.println("t2 read end");

                    connection.commit();
                    System.out.println("t2 committed");

                } catch (SQLException | InterruptedException e) {
                    latch.countDown();
                } finally {
                    latch.countDown();
                }
            }
        });

        Assertions.assertDoesNotThrow(new Executable() {
            @Override
            public void execute() throws Throwable {
                latch.await();
            }
        });

        System.out.println("[ RESULT ] : " + folderRepository.find("folderCP_TEST").getTitle());
    }

    // 얘도 데드락, DBMS 락이랑 충돌
    public void concurrent_test_3() throws SQLException {
        Semaphore semaphore1 = new Semaphore(0);
        Semaphore semaphore2 = new Semaphore(0);
        CountDownLatch latch = new CountDownLatch(2);
        ExecutorService executorService = Executors.newFixedThreadPool(2);

        folderRepository.save(Folder.builder()
                .folderCP("folderCP_TEST")
                .isTitleOpen(true)
                .title("title_PRIMARY")
                .symmetricKeyEWF("sym_TEST")
                .lastChangedDate(LocalDateTime6Digit.now())
                .build()
        );

        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try (Connection connection = DataSourceUtils.getConnection(dataSource)) {
                    connection.setAutoCommit(false);
                    String sql = """
                                DELETE FROM Folder WHERE folderCP = 'folderCP_TEST';
                            """;
                    System.out.println("t1 write start");
                    connection.prepareStatement(sql).execute();
                    System.out.println("t1 write end");

                    semaphore1.release();
                    semaphore2.acquire();

                    sql = "SELECT * FROM Folder";
                    System.out.println("t1 read start");
                    connection.prepareStatement(sql).execute();
                    System.out.println("t1 read end");

                    // connection.commit();
                    connection.rollback();
                    System.out.println("t1 rollback");

                } catch (SQLException | InterruptedException e) {
                    latch.countDown();
                } finally {
                    latch.countDown();
                }
            }
        });

        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try (Connection connection = DataSourceUtils.getConnection(dataSource)) {
                    connection.setAutoCommit(false);

                    semaphore1.acquire();
                    String sql = """
                                INSERT INTO Folder (folderCP, isTitleOpen, title, symmetricKeyEWF, lastChangedDate)
                                VALUES ('folderCP_TEST', true, 'title_TX_2', 'sym_TEST', '2023-07-22 12:34:56');
                            """;
                    System.out.println("t2 write start");
                    connection.prepareStatement(sql).execute();
                    System.out.println("t2 write end");

                    sql = "SELECT * FROM Folder";
                    System.out.println("t2 read start");
                    connection.prepareStatement(sql).execute();
                    System.out.println("t2 read end");

                    connection.commit();
                    System.out.println("t2 committed");
                    semaphore2.release();

                } catch (SQLException | InterruptedException e) {
                    latch.countDown();
                } finally {
                    latch.countDown();
                }
            }
        });

        Assertions.assertDoesNotThrow(new Executable() {
            @Override
            public void execute() throws Throwable {
                latch.await();
            }
        });

        System.out.println("[ RESULT ] : " + folderRepository.find("folderCP_TEST").getTitle());
    }

    // 데드락 발생, DBMS lock conflicts Semaphore
    public void concurrent_test_4() throws SQLException {
        Semaphore semaphore1 = new Semaphore(0);
        Semaphore semaphore2 = new Semaphore(0);
        CountDownLatch latch = new CountDownLatch(2);
        ExecutorService executorService = Executors.newFixedThreadPool(2);

        folderRepository.save(Folder.builder()
                .folderCP("folderCP_TEST")
                .isTitleOpen(true)
                .title("title_PRIMARY")
                .symmetricKeyEWF("sym_TEST")
                .lastChangedDate(LocalDateTime6Digit.now())
                .build()
        );

        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try (Connection connection = DataSourceUtils.getConnection(dataSource)) {
                    connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
                    connection.setAutoCommit(false);

                    String sql = """
                                UPDATE Folder SET title = 'updated_title_tx1_1' WHERE folderCP = 'folderCP_TEST';
                            """;
                    System.out.println("t1 update 1 start");
                    connection.prepareStatement(sql).execute();
                    System.out.println("t1 update 1 end");

                    semaphore1.release();
                    semaphore2.acquire();

                    sql = """
                                UPDATE Folder SET title = 'updated_title_tx1_2' WHERE folderCP = 'folderCP_TEST';
                            """;

                    System.out.println("t1 update 2 start");
                    connection.prepareStatement(sql).execute();
                    System.out.println("t1 update 2 end");

                    // connection.commit();
                    connection.commit();
                    System.out.println("t1 commit");

                } catch (SQLException | InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    latch.countDown();
                }
            }
        });

        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try (Connection connection = DataSourceUtils.getConnection(dataSource)) {
                    connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
                    connection.setAutoCommit(false);

                    semaphore1.acquire();
                    String sql = """
                                UPDATE Folder SET title = 'updated_title_tx2' WHERE folderCP = 'folderCP_TEST';
                            """;
                    System.out.println("t2 write start");
                    connection.prepareStatement(sql).execute();
                    System.out.println("t2 write end");

                    sql = "SELECT * FROM Folder";
                    System.out.println("t2 read start");
                    connection.prepareStatement(sql).execute();
                    System.out.println("t2 read end");

                    connection.commit();
                    System.out.println("t2 committed");
                    semaphore2.release();

                } catch (SQLException | InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    latch.countDown();
                }
            }
        });

        Assertions.assertDoesNotThrow(new Executable() {
            @Override
            public void execute() throws Throwable {
                latch.await();
            }
        });

        System.out.println("[ RESULT ] : " + folderRepository.find("folderCP_TEST").getTitle());
    }

    public void rollbackTest() {
        // given
        String folderCP = "folderCP_TEST";

        FoldersGenerateRequestDto dto = FoldersGenerateRequestDto.builder()
                                    .title("title_TEST")
                                    .isTitleOpen(true)
                                    .symmetricKeyEWF("sym_TEST")
                                    .build();

        // when
        Assertions.assertThrows(DuplicatePrimaryKeyException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                folderService.generateFolder(folderCP, dto);
            }
        });

        // then
        Assertions.assertEquals(0, folderRepository.findAllFolderCPAndTitle().size());
    }

    @Test
    public void generateFolder_atomicity_test_1() {
        // given
        String folderCP = "folderCP_TEST";

        FoldersGenerateRequestDto dto = FoldersGenerateRequestDto.builder()
                .title("title_TEST")
                .isTitleOpen(true)
                .symmetricKeyEWF("sym_TEST")
                .build();

        folderSearchRepository.save(FolderSearch.builder()
                .folderCP(folderCP)
                .title(dto.getTitle())
                .build()
        );

        // when
        Assertions.assertThrows(DuplicatePrimaryKeyException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                folderService.generateFolder(folderCP, dto);
            }
        });

        // then
        Assertions.assertEquals(0, folderRepository.findAllFolderCPAndTitle().size());
        Assertions.assertEquals(1, folderSearchRepository.findAll().size());
    }

    @Test
    public void generateFolder_atomicity_test_2() {
        // given
        String folderCP = "folderCP_TEST";

        FoldersGenerateRequestDto dto = FoldersGenerateRequestDto.builder()
                .title("title_TEST")
                .isTitleOpen(true)
                .symmetricKeyEWF("sym_TEST")
                .build();

        // 얘처럼 서비스 테스트는 비 트랜잭션인? 레포지토리 메소드로 환경 세팅하는게 좋다.
        folderRepository.save(Folder.builder()
                .folderCP(folderCP)
                .title("title_TEST")
                .isTitleOpen(true)
                .symmetricKeyEWF("sym_TEST")
                .lastChangedDate(LocalDateTime6Digit.now())
                .build());

        // when
        Assertions.assertThrows(DuplicatePrimaryKeyException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                folderService.generateFolder(folderCP, dto);
            }
        });

        // then
        Assertions.assertEquals(1, folderRepository.findAllFolderCPAndTitle().size());
        Assertions.assertEquals(0, folderSearchRepository.findAll().size());
    }
}
