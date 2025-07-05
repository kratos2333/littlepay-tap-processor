package com.littlepay.tapprocessor.services;

import com.littlepay.tapprocessor.models.Tap;
import com.littlepay.tapprocessor.models.Trip;
import com.opencsv.bean.CsvToBeanBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.FileReader;
import java.io.IOException;
import java.util.List;

@Service
public class CsvParsor {
    private static final Logger logger = LoggerFactory.getLogger(CsvParsor.class);

    /**
     * Use OpenCSV lib to convert csv lines into Tap list
     * @param inputFile input tap csv file
     * @return taps
     */
    public List<Tap> readTapsFromCsv(String inputFile) {
        try (FileReader reader = new FileReader(inputFile)) {
            return new CsvToBeanBuilder<Tap>(reader)
                    .withType(Tap.class)
                    .build()
                    .parse();
        } catch (IOException e) {
            logger.error("Error while reading file {}", inputFile, e);
            throw new RuntimeException(e);
        } catch (RuntimeException e) {
            logger.error("CSV parsing error in file {}: {}", inputFile, e.getMessage());
            throw new IllegalArgumentException("CSV file format is invalid: " + inputFile, e);
        }
    }

    public void writeTripsToCsv(List<Trip> trips) {

    }
}
