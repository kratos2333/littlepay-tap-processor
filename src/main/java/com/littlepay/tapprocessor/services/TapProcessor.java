package com.littlepay.tapprocessor.services;

import com.littlepay.tapprocessor.models.Tap;
import com.littlepay.tapprocessor.models.TapType;
import com.littlepay.tapprocessor.models.Trip;
import com.littlepay.tapprocessor.models.TripType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.*;

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
     * Main processing method, read the taps, processes them into trips and write into a csv file
     * @param inputFile input csv file with taps
     * @param outputFile output csv file with trips
     */
    public void processTaps(String inputFile, String outputFile) {
        // 1. Read taps from csv file (csv -> List<Tap>)
        logger.info("Start processing taps from {}...", inputFile);
        List<Tap> taps = csvParsor.readTapsFromCsv(inputFile);
        logger.info("Fetched {} taps from {}", taps.size(), inputFile);

        // 2. process taps (List<Tap> -> List<Trip>)
        logger.info("Now calculating trips based on taps...");
        List<Trip> trips = generateTrips(taps);

        // 3. Write output (List<Trip> -> csv)
        logger.info("Writing output to file {}...", outputFile);
        csvParsor.writeTripsToCsv(trips, outputFile);
    }

    /**
     * Generate trips based on pass in taps
     * @param taps tap records fetched from input csv file
     * @return trips generated trips based on the tap records
     */
    public List<Trip> generateTrips(List<Tap> taps) {
        List<Trip> trips = new ArrayList<>();

        Map<String, Tap> pendingTapOns = new HashMap<>();

        // Sort the taps based on the PAN, then busId, then by the time
        taps.sort(
                Comparator.comparing(Tap::getPan)
                        .thenComparing(Tap::getBusId)
                        .thenComparing(Tap::getDateTimeUtc)
        );

        for(Tap tap : taps) {
            String tapKey = tap.getPan() + "_" + tap.getBusId();
            if(tap.getType().equals(TapType.ON)){
                if(pendingTapOns.containsKey(tapKey)) {
                    // handle incomplete trip
                    Tap previousTap = pendingTapOns.get(tapKey);
                    Trip incompleteTrip = createIncompleteTrip(previousTap);
                    trips.add(incompleteTrip);
                }

                pendingTapOns.put(tapKey, tap);
            } else if(tap.getType().equals(TapType.OFF)) {
                if(pendingTapOns.containsKey(tapKey)){
                    // find the matched prior tap-on
                    Trip completeTrip = createCompleteOrCancelledTrip(pendingTapOns.get(tapKey), tap);
                    pendingTapOns.remove(tapKey);
                    trips.add(completeTrip);
                } else {
                    // This should not happen log a warning here
                    logger.warn("Warning: Tap-off without matching tap-on for: {}", tapKey);
                }
            }
        }

        // Handle any remaining pending tap-ons as incomplete trips (tap-ons without offs)
        for (Tap pendingTapOn : pendingTapOns.values()) {
            Trip incompleteTrip = createIncompleteTrip(pendingTapOn);
            trips.add(incompleteTrip);
        }

        return trips;
    }

    private Trip createCompleteOrCancelledTrip(Tap tapOn, Tap tapOff) {
        return Trip.builder()
                .started(tapOn.getDateTimeUtc())
                .finished(tapOff.getDateTimeUtc())
                .durationSecs(Duration.between(tapOn.getDateTimeUtc(), tapOff.getDateTimeUtc()).getSeconds())
                .fromStopId(tapOn.getStopId())
                .toStopId(tapOff.getStopId())
                .chargeAmount(pricingService.calculateCompleteOrCancelledTrip(tapOn.getStopId(), tapOff.getStopId()))
                .companyId(tapOn.getCompanyId())
                .busId(tapOn.getBusId())
                .pan(tapOn.getPan())
                .status(tapOn.getStopId().equals(tapOff.getStopId())?TripType.CANCELLED:TripType.COMPLETED)
                .build();
    }

    private Trip createIncompleteTrip(Tap previousTap) {
        return Trip.builder()
                .started(previousTap.getDateTimeUtc())
                .finished(null)
                .durationSecs(null)
                .fromStopId(previousTap.getStopId())
                .toStopId(null)
                .chargeAmount(pricingService.calculateIncompleteTrip(previousTap.getStopId()))
                .companyId(previousTap.getCompanyId())
                .busId(previousTap.getBusId())
                .pan(previousTap.getPan())
                .status(TripType.INCOMPLETE)
                .build();
    }

}
