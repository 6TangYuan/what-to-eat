package com.recipe.backend.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * AI 服务商配置属性
 * <p>
 * 映射 application.yml 中 ai 前缀下的配置。
 * 支持多服务商，通过 ai.provider 切换。
 */
@Data
@Component
@ConfigurationProperties(prefix = "ai")
public class AiProperties {

    /** 当前使用的 AI 服务商：openai | zhipu */
    private String provider = "openai";

    /** OpenAI 兼容服务商配置 */
    private ProviderConfig openai = new ProviderConfig();

    /** 智谱 AI 配置 */
    private ProviderConfig zhipu = new ProviderConfig();

    /**
     * 获取当前激活的服务商配置
     */
    public ProviderConfig getActiveConfig() {
        return "zhipu".equalsIgnoreCase(provider) ? zhipu : openai;
    }

    @Data
    public static class ProviderConfig {
        /** API 基础地址 */
        private String baseUrl = "https://api.openai.com/v1";
        /** API 密钥 */
        private String apiKey = "";
        /** 模型名称 */
        private String model = "gpt-4o";
        /** 生成温度 (0-2) */
        private double temperature = 0.7;
        /** 请求超时（毫秒） */
        private int timeout = 300000;
    }

}
