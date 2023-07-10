package com.dong.demo.v1.service.subDemand;

import com.dong.demo.v1.domain.readAuth.ReadAuth;
import com.dong.demo.v1.domain.readAuth.ReadAuthRepository;
import com.dong.demo.v1.domain.subDemand.SubDemand;
import com.dong.demo.v1.domain.subDemand.SubDemandRepository;
import com.dong.demo.v1.exception.NoSuchSubscribeDemandException;
import com.dong.demo.v1.exception.VerifyFailedException;
import com.dong.demo.v1.util.CipherUtil;
import com.dong.demo.v1.util.KeyCompressor;
import com.dong.demo.v1.util.RSAVerifier;
import com.dong.demo.v1.web.dto.SubscribeDemandsAddRequestDto;
import com.dong.demo.v1.web.dto.SubscribeDemandsAllowRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class SubDemandService {

    private final SubDemandRepository subDemandRepository;
    private final ReadAuthRepository readAuthRepository;

    @Transactional(readOnly = true)
    public List<String> getSubscribeDemand(String folderCP) {
        return subDemandRepository.findAccountPublicKeyByFolderCP(folderCP);
    }

    // verify fail test
    // ent, ref integrity constraints test
    // check result rollback is complete!!
    // + test success of transaction
    @Transactional
    public void addSubscribeDemand(SubscribeDemandsAddRequestDto dto) {
        if (!RSAVerifier.verify(dto.getByteSign(), CipherUtil.getPublicKeyFromEncodedKeyString(dto.getAccountPublicKey()))) {
            throw new VerifyFailedException();
        }
        else {
            String accountCP = KeyCompressor.compress(dto.getAccountPublicKey());

            subDemandRepository.save(SubDemand.builder()
                    .accountCP(accountCP)
                    .folderCP(dto.getFolderCP())
                    .accountPublicKey(dto.getAccountPublicKey())
                    .build());
        }
    }

    // check rollback
    // verify fail, ref, ent violation
    @Transactional
    public void allowSubscribe(SubscribeDemandsAllowRequestDto dto) {
        if (!RSAVerifier.verify(
                dto.getByteSign(), CipherUtil.getPublicKeyFromEncodedKeyString(dto.getFolderPublicKey())
        )) {
            throw new VerifyFailedException();
        }
        else {
            String folderCP = KeyCompressor.compress(dto.getFolderPublicKey());

            if (subDemandRepository.exist(folderCP, dto.getAccountCP())) {
                subDemandRepository.delete(folderCP, dto.getAccountCP());
                readAuthRepository.save(ReadAuth.builder()
                        .folderCP(folderCP)
                        .accountCP(dto.getAccountCP())
                        .symmetricKeyEWA(dto.getSymmetricKeyEWA())
                        .build());
            }
            else {
                throw new NoSuchSubscribeDemandException();
            }
        }
    }

}
