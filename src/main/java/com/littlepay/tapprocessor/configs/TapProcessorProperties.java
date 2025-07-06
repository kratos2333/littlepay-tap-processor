package com.littlepay.tapprocessor.configs;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Data
@ConfigurationProperties(prefix = "tap-processor")
public class TapProcessorProperties {
    private String inputFile;
    private String outputFile;
}


