package com.littlepay.tapprocessor.services;

import com.littlepay.tapprocessor.models.Tap;
import com.littlepay.tapprocessor.models.TapType;
import com.littlepay.tapprocessor.models.Trip;
import com.littlepay.tapprocessor.models.TripType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.shadow.com.univocity.parsers.csv.CsvParser;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

class TapProcessorTest {
    @Mock
    private PricingService pricingService;

    @Mock
    private CsvParser csvParser;

    @InjectMocks
    private TapProcessor tapProcessor;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void testGenerateTrips_CompleteTrips(){
        LocalDateTime tapOnTime = LocalDateTime.of(2025,7,5,20,0,0);
        LocalDateTime tapOffTime = LocalDateTime.of(2025,7,5,20,5,0);

        Tap tapOn = Tap.builder()
                .id(1L)
                .dateTimeUtc(tapOnTime)
                .type(TapType.ON)
                .stopId("Stop1")
                .companyId("Company1")
                .busId("Bus37")
                .pan("5500005555555559")
                .build();

        Tap tapOff = Tap.builder()
                .id(2L)
                .dateTimeUtc(tapOffTime)
                .type(TapType.OFF)
                .stopId("Stop2")
                .companyId("Company1")
                .busId("Bus37")
                .pan("5500005555555559")
                .build();

        when(pricingService.calculateCompleteOrCancelledTrip(tapOn.getStopId(), tapOff.getStopId()))
                .thenReturn(BigDecimal.valueOf(3.25));

        List<Tap> testTaps = new ArrayList<>();
        testTaps.add(tapOn);
        testTaps.add(tapOff);
        List<Trip> trips = tapProcessor.generateTrips(testTaps);

        Trip trip = trips.get(0);
        assertEquals(1, trips.size());
        assertEquals(TripType.COMPLETED, trip.getStatus());
        assertEquals(BigDecimal.valueOf(3.25), trip.getChargeAmount());
        assertEquals(300, trip.getDurationSecs());
    }

    @Test
    void testGenerateTrips_IncompleteTrips(){
        LocalDateTime tapOnTime1 = LocalDateTime.of(2025,7,5,20,0,0);
        LocalDateTime tapOnTime2 = LocalDateTime.of(2025,7,5,20,5,0);

        Tap tapOn1 = Tap.builder()
                .id(1L)
                .dateTimeUtc(tapOnTime1)
                .type(TapType.ON)
                .stopId("Stop1")
                .companyId("Company1")
                .busId("Bus37")
                .pan("5500005555555559")
                .build();

        Tap tapOn2 = Tap.builder()
                .id(2L)
                .dateTimeUtc(tapOnTime2)
                .type(TapType.ON)
                .stopId("Stop2")
                .companyId("Company1")
                .busId("Bus37")
                .pan("5500005555555559")
                .build();

        when(pricingService.calculateIncompleteTrip(tapOn1.getStopId()))
                .thenReturn(BigDecimal.valueOf(7.30));

        when(pricingService.calculateIncompleteTrip(tapOn2.getStopId()))
                .thenReturn(BigDecimal.valueOf(5.50));

        List<Tap> testTaps = new ArrayList<>();
        testTaps.add(tapOn1);
        testTaps.add(tapOn2);
        List<Trip> trips = tapProcessor.generateTrips(testTaps);

        Trip trip1 = trips.get(0);
        Trip trip2 = trips.get(1);
        assertEquals(2, trips.size());
        assertEquals(TripType.INCOMPLETE, trip1.getStatus());
        assertEquals(TripType.INCOMPLETE, trip2.getStatus());
        assertEquals(BigDecimal.valueOf(7.30), trip1.getChargeAmount());
        assertEquals(BigDecimal.valueOf(5.50), trip2.getChargeAmount());
    }

    @Test
    void testGenerateTrips_CancelledTrips(){
        LocalDateTime tapOnTime = LocalDateTime.of(2025,7,5,20,0,0);
        LocalDateTime tapOffTime = LocalDateTime.of(2025,7,5,20,5,0);

        Tap tapOn = Tap.builder()
                .id(1L)
                .dateTimeUtc(tapOnTime)
                .type(TapType.ON)
                .stopId("Stop1")
                .companyId("Company1")
                .busId("Bus37")
                .pan("5500005555555559")
                .build();

        Tap tapOff = Tap.builder()
                .id(2L)
                .dateTimeUtc(tapOffTime)
                .type(TapType.OFF)
                .stopId("Stop1")
                .companyId("Company1")
                .busId("Bus37")
                .pan("5500005555555559")
                .build();

        when(pricingService.calculateCompleteOrCancelledTrip(tapOn.getStopId(), tapOff.getStopId()))
                .thenReturn(BigDecimal.valueOf(0));

        List<Tap> testTaps = new ArrayList<>();
        testTaps.add(tapOn);
        testTaps.add(tapOff);
        List<Trip> trips = tapProcessor.generateTrips(testTaps);

        Trip trip = trips.get(0);
        assertEquals(1, trips.size());
        assertEquals(TripType.CANCELLED, trip.getStatus());
        assertEquals(BigDecimal.ZERO, trip.getChargeAmount());
    }
}