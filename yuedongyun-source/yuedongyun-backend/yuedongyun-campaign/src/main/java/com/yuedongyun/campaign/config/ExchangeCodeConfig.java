package com.yuedongyun.campaign.config;

import com.yuedongyun.campaign.utils.AESUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ExchangeCodeConfig {
    /**
     * 加密密钥，长度必须是256字节
     */
    @Value("${yuedongyun.campaign.aes.key}")
    private String key;
    /**
     * 初始向量，长度必须是16字节
     */
    @Value("${yuedongyun.campaign.aes.iv}")
    private String iv;

    @Bean
    public AESUtil aesUtil(){
        return new AESUtil(key, iv);
    }
}

