package com.sky.config;

import com.sky.properties.AliOssProperties;
import com.sky.utils.AliOssUtil;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 功能简述
 *
 * @author hssy
 * @version 1.0
 */


@Configuration
public class OssConfiguration {

    /**
     * 提供aliyunoss工具类
     *
     * @param aliOssProperties
     * @return
     */
    @Bean
    @ConditionalOnMissingBean
    public AliOssUtil aliOssUtil(AliOssProperties aliOssProperties) {
        return new AliOssUtil(aliOssProperties.getEndpoint(),
            aliOssProperties.getAccessKeyId(),
            aliOssProperties.getAccessKeySecret(),
            aliOssProperties.getBucketName());
    }

}
