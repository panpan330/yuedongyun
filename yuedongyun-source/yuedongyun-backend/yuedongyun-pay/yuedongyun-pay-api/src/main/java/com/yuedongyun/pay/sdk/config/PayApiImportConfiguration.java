package com.yuedongyun.pay.sdk.config;


import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableFeignClients(basePackages = "com.yuedongyun.pay.sdk.client")
public class PayApiImportConfiguration {

}