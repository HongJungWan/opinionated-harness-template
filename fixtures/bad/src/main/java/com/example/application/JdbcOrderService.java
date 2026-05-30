package com.example.application;

// 위반 샘플(룰 B): 응용 서비스가 인프라 구체 타입(JdbcTemplate)을 직접 import.
import org.springframework.jdbc.core.JdbcTemplate;

public class JdbcOrderService {

    private final JdbcTemplate jdbc;

    public JdbcOrderService(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public int countOrders() {
        return jdbc.queryForObject("select count(*) from orders", Integer.class);
    }
}
