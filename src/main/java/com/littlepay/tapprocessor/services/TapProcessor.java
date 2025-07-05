package com.littlepay.tapprocessor.services;

import com.littlepay.tapprocessor.models.Tap;
import com.littlepay.tapprocessor.models.Trip;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TapProcessor {
    private static final Logger logger = LoggerFactory.getLogger(TapProcessor.class);

    private final PricingService pricingService;
    private final CsvParsor csvParsor;

    public TapProcessor(PricingService pricingService, CsvParsor csvParsor) {
        this.pricingService = pricingService;
        this.csvParsor = csvParsor;
    }

    /**
     * Main processing method
     * @param inputFile
     * @param outputFile
     */
    public void processTrips(String inputFile, String outputFile) {
        // 1. Read taps from csv file (csv -> List<Tap>)
        logger.info("Start processing taps from {}...", inputFile);
        List<Tap> taps = csvParsor.readTapsFromCsv(inputFile);

        // 2. process taps (List<Tap> -> List<Trip>)
        logger.info("Now calculating trips based on taps...");
        List<Trip> trips = generateTrips(taps);

        // 3. Write output (List<Trip> -> csv)
        logger.info("Writing output to file {}...", outputFile);
        csvParsor.writeTripsToCsv(trips);
    }

    /**
     * Generate trips based on pass in taps
     * @param taps
     * @return trips
     */
    public List<Trip> generateTrips(List<Tap> taps) {
        List<Trip> trips = new ArrayList<>();
        return trips;
    }


}
