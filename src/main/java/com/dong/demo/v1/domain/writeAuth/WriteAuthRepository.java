package com.dong.demo.v1.domain.writeAuth;

import java.util.List;

public interface WriteAuthRepository {
    public void save(WriteAuth writeAuth); // 중복 예외 rethrow
    public List<WriteAuth> findByAccountCP(String accountCP);
}
