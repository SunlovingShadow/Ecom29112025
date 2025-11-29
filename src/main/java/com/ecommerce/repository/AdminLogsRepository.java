package com.ecommerce.repository;

import java.util.List;
import com.ecommerce.model.AdminLog;

public interface AdminLogsRepository {
    Long save(AdminLog log);
    List<AdminLog> findRecent(int limit);
    List<AdminLog> findAll();
}