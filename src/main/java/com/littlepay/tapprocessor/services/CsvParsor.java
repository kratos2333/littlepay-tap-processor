package com.littlepay.tapprocessor.services;

import com.littlepay.tapprocessor.models.Tap;
import com.littlepay.tapprocessor.models.Trip;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CsvParsor {

    public List<Tap> readTapsFromCsv(String inputFile) {
        return null;
    }

    public void writeTripsToCsv(List<Trip> trips) {

    }
}
