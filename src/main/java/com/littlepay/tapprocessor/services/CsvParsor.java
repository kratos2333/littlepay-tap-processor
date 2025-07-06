package com.littlepay.tapprocessor.services;

import com.littlepay.tapprocessor.models.Tap;
import com.littlepay.tapprocessor.models.Trip;
import com.opencsv.CSVWriter;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.List;

@Service
public class CsvParsor {
    private static final Logger logger = LoggerFactory.getLogger(CsvParsor.class);

    private static final String[] columnMapping = {
            "Started", "Finished", "DurationSecs", "FromStopId", "ToStopId",
            "ChargeAmount", "CompanyId", "BusId", "PAN", "Status"
    };

    /**
     * Use OpenCSV lib to convert csv lines into Tap list
     *
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

    /**
     * Use opencsv to convert trip list into a csv file
     *
     * @param trips generated trip list
     * @param outputFile specified output csv file
     */
    public void writeTripsToCsv(List<Trip> trips, String outputFile) {
        try (Writer writer = new FileWriter(outputFile)) {

            writer.write(String.join(",", columnMapping));
            writer.write("\n");

            ColumnPositionMappingStrategy<Trip> strategy = new ColumnPositionMappingStrategy<>();
            strategy.setType(Trip.class);
            strategy.setColumnMapping(columnMapping);

            StatefulBeanToCsv<Trip> beanToCsv = new StatefulBeanToCsvBuilder<Trip>(writer)
                    .withMappingStrategy(strategy)
                    .withQuotechar(CSVWriter.NO_QUOTE_CHARACTER)
                    .withSeparator(CSVWriter.DEFAULT_SEPARATOR)
                    .build();

            beanToCsv.write(trips);
        } catch (Exception e) {
            logger.error("Error happens when output to CSV: {} ", e.getMessage());
        }
    }
}
