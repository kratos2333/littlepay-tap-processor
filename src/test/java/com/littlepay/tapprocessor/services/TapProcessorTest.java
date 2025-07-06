package com.littlepay.tapprocessor.services;

import com.littlepay.tapprocessor.models.Tap;
import com.littlepay.tapprocessor.models.TapType;
import com.littlepay.tapprocessor.models.Trip;
import com.littlepay.tapprocessor.models.TripType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.shadow.com.univocity.parsers.csv.CsvParser;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TapProcessorTest {
    @Mock
    private PricingService pricingService;

    @Mock
    private CsvParser csvParser;

    @InjectMocks
    private TapProcessor tapProcessor;

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

    @Test
    void testGenerateTrips_MixedTrips(){
        // Cancelled Trip
        Tap tapOn1 = Tap.builder()
                .id(1L)
                .dateTimeUtc(LocalDateTime.of(2025,7,5,20,0,0))
                .type(TapType.ON)
                .stopId("Stop1")
                .companyId("Company1")
                .busId("Bus37")
                .pan("5454545454545454")
                .build();

        Tap tapOff1 = Tap.builder()
                .id(2L)
                .dateTimeUtc(LocalDateTime.of(2025,7,5,20,5,0))
                .type(TapType.OFF)
                .stopId("Stop1")
                .companyId("Company1")
                .busId("Bus37")
                .pan("5454545454545454")
                .build();

        // InComplete Trip
        Tap tapOn2 = Tap.builder()
                .id(3L)
                .dateTimeUtc(LocalDateTime.of(2025,7,5,20,10,0))
                .type(TapType.ON)
                .stopId("Stop1")
                .companyId("Company1")
                .busId("Bus37")
                .pan("5500005555555559")
                .build();

        // Complete trip
        Tap tapOn3 = Tap.builder()
                .id(4L)
                .dateTimeUtc(LocalDateTime.of(2025,7,5,20,15,0))
                .type(TapType.ON)
                .stopId("Stop1")
                .companyId("Company1")
                .busId("Bus37")
                .pan("5555555555554444")
                .build();

        Tap tapOff3 = Tap.builder()
                .id(5L)
                .dateTimeUtc(LocalDateTime.of(2025,7,5,20,20,0))
                .type(TapType.OFF)
                .stopId("Stop2")
                .companyId("Company1")
                .busId("Bus37")
                .pan("5555555555554444")
                .build();

        when(pricingService.calculateCompleteOrCancelledTrip(tapOn1.getStopId(), tapOff1.getStopId()))
                .thenReturn(BigDecimal.valueOf(0));

        when(pricingService.calculateIncompleteTrip(tapOn2.getStopId()))
                .thenReturn(BigDecimal.valueOf(7.30));

        when(pricingService.calculateCompleteOrCancelledTrip(tapOn3.getStopId(), tapOff3.getStopId()))
                .thenReturn(BigDecimal.valueOf(3.25));

        List<Tap> testTaps = new ArrayList<>();
        testTaps.add(tapOn1);
        testTaps.add(tapOff1);
        testTaps.add(tapOn2);
        testTaps.add(tapOn3);
        testTaps.add(tapOff3);
        List<Trip> trips = tapProcessor.generateTrips(testTaps);

        assertEquals(3, trips.size());

        Map<TripType, Trip> tripByType = trips.stream()
                .collect(Collectors.toMap(Trip::getStatus, Function.identity()));

        Trip tripCancelled = tripByType.get(TripType.CANCELLED);
        Trip tripInComplete = tripByType.get(TripType.INCOMPLETE);
        Trip tripCompleted = tripByType.get(TripType.COMPLETED);

        assertEquals(BigDecimal.ZERO, tripCancelled.getChargeAmount());
        assertEquals(BigDecimal.valueOf(7.30), tripInComplete.getChargeAmount());
        assertEquals(BigDecimal.valueOf(3.25), tripCompleted.getChargeAmount());
    }
}