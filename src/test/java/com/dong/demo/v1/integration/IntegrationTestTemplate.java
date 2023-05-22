package com.dong.demo.v1.integration;

import com.dong.demo.v1.domain.file.FileRepository;
import com.dong.demo.v1.domain.folder.FolderRepository;
import com.dong.demo.v1.domain.readAuth.ReadAuthRepository;
import com.dong.demo.v1.domain.subDemand.SubDemandRepository;
import com.dong.demo.v1.domain.writeAuth.WriteAuthRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.After;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ActiveProfiles("real-db")
@Disabled  // 이거는 템플릿이니까.
@AutoConfigureMockMvc // SpringBootTest 할 때, WebMvcTest 에 있는 빈 띄워주는 듯?
public class IntegrationTestTemplate {

    @Autowired
    protected MockMvc mvc;
    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected FolderRepository folderRepository;
    @Autowired
    protected FileRepository fileRepository;
    @Autowired
    protected ReadAuthRepository readAuthRepository;
    @Autowired
    protected WriteAuthRepository writeAuthRepository;
    @Autowired
    protected SubDemandRepository subDemandRepository;

    @AfterEach
    @BeforeEach
    public void cleanUp() {
        subDemandRepository.deleteAll();
        readAuthRepository.deleteAll();
        writeAuthRepository.deleteAll();
        fileRepository.deleteAll();
        folderRepository.deleteAll();
    }
}
