package com.pranjal.common;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class KeepAliveController {

    private final JdbcTemplate jdbcTemplate;

    public KeepAliveController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @GetMapping("/keepalive")
    public String keepAlive() {
        jdbcTemplate.queryForObject("SELECT 1", Integer.class);
        return "OK";
    }
}