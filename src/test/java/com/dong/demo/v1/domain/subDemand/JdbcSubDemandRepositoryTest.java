package com.dong.demo.v1.domain.subDemand;

import com.dong.demo.v1.domain.folder.FolderRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.*;

class JdbcSubDemandRepositoryTest {

    @Autowired
    private FolderRepository folderRepository;

    @Autowired
    private SubDemandRepository subDemandRepository;

    @BeforeEach
    @AfterEach
    void setUpAndTearDown() {
        subDemandRepository.deleteAll();
        folderRepository.deleteAll();
    }

    @Test
    void exist() {
    }

    @Test
    void save() {
    }

    @Test
    void delete() {
    }

    @Test
    void findAccountPublicKeyByFolderCP() {
    }

    @Test
    void deleteAll() {
    }
}