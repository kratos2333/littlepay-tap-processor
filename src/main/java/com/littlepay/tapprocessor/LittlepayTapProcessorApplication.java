package com.littlepay.tapprocessor;

import com.littlepay.tapprocessor.services.TapProcessor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class LittlepayTapProcessorApplication {

    public static void main(String[] args) {
        String inputFile = args.length > 0 ? args[0] : "src/main/resources/taps.csv";
        String outputFile = args.length > 1 ? args[1] : "trips.csv";

        ApplicationContext context = SpringApplication.run(LittlepayTapProcessorApplication.class, args);
        TapProcessor tapProcessor = context.getBean(TapProcessor.class);
        tapProcessor.processTaps(inputFile, outputFile);
    }

}
