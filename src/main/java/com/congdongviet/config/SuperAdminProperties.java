package com.congdongviet.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "app.super-admin")
public class SuperAdminProperties {

    private String username;

    private String email;

    /** Raw password from ENV — used once at bootstrap, never logged or stored in plain text. */
    private String password;

    private String phone;
}
