package com.littlepay.tapprocessor;

import com.littlepay.tapprocessor.services.TapProcessor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class LittlepayTapProcessorApplication {

    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(LittlepayTapProcessorApplication.class, args);
        TapProcessor tapProcessor = context.getBean(TapProcessor.class);
        tapProcessor.processTaps("src/main/resources/taps.csv", "trips.csv");
    }

}
