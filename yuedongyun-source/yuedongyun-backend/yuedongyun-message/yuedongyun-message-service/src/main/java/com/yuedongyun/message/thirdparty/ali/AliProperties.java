package com.yuedongyun.message.thirdparty.ali;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "yuedongyun.sms.ali")
public class AliProperties {
    private String accessId;
    private String accessSecret;
}
