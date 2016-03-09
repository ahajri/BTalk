package com.ahajri.btalk.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({ MarkLogicConfig.class})
public class AppConfig {

}
