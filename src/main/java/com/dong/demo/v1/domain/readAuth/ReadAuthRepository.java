package com.dong.demo.v1.domain.readAuth;

import java.util.List;

public interface ReadAuthRepository {
    public void save(ReadAuth readAuth); // 역시 key 중복 예외 rethrow
    public List<ReadAuth> findByAccountCP(String accountCP);
}
