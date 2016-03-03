package com.ahajri.btalk.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({ MarkLogicConfigTest.class})
public class AppConfig {

}
