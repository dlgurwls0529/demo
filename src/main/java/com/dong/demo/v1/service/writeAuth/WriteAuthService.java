package com.dong.demo.v1.service.writeAuth;

import com.dong.demo.v1.domain.folder.Folder;
import com.dong.demo.v1.domain.folder.FolderRepository;
import com.dong.demo.v1.domain.folder.folderFindBatchBuilder.FolderFindBatchBuilder;
import com.dong.demo.v1.domain.writeAuth.WriteAuth;
import com.dong.demo.v1.domain.writeAuth.WriteAuthRepository;
import com.dong.demo.v1.web.dto.WriteAuthsAddRequestDto;
import com.dong.demo.v1.web.dto.WriteAuthsGetResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface WriteAuthService {
    public void addWriteAuthority(WriteAuthsAddRequestDto writeAuthsAddRequestDto);
    public List<WriteAuthsGetResponseDto> getWriteAuthByAccountCP(String accountCP);
}
