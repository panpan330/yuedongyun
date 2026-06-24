package com.yuedongyun.media.config;

import com.yuedongyun.media.enums.Platform;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "yuedongyun.platform")
public class PlatformProperties {
    private Platform file;
    private Platform media;
}
