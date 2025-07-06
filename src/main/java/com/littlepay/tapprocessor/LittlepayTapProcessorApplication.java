package com.littlepay.tapprocessor;

import com.littlepay.tapprocessor.configs.TapProcessorProperties;
import com.littlepay.tapprocessor.services.TapProcessor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class LittlepayTapProcessorApplication {

    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(LittlepayTapProcessorApplication.class, args);

        TapProcessorProperties tapProcessorProperties = context.getBean(TapProcessorProperties.class);

        String inputFile = args.length > 0 ? args[0] : tapProcessorProperties.getInputFile();
        String outputFile = args.length > 1 ? args[1] : tapProcessorProperties.getOutputFile();

        TapProcessor tapProcessor = context.getBean(TapProcessor.class);
        tapProcessor.processTaps(inputFile, outputFile);
    }

}
