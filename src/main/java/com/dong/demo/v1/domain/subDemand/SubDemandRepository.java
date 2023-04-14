package com.dong.demo.v1.domain.subDemand;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;

public interface SubDemandRepository {
    public boolean exist(String folderCP, String accountCP);
    public void save(SubDemand demand); // ref, ent
    public void delete(String folderCP, String accountCP);
    public List<String> findAccountPublicKeyByFolderCP(String folderCP);
    public void deleteAll();
}
