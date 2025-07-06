package com.littlepay.tapprocessor.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class PricingServiceTest {

    private PricingService pricingService;

    @BeforeEach
    void setUp() {
        pricingService = new PricingService();
        pricingService.initPriceMap();
    }

    @Test
    void testCalculateIncompleteTrip() {
        BigDecimal result = pricingService.calculateIncompleteTrip("Stop1");
        assertEquals(BigDecimal.valueOf(7.30), result);
    }

    @Test
    void testCalculateCompleteTrip() {
        BigDecimal result = pricingService.calculateCompleteOrCancelledTrip("Stop1", "Stop2");
        assertEquals(BigDecimal.valueOf(3.25), result);
    }

    @Test
    void testCalculateCancelledTrip() {
        BigDecimal result = pricingService.calculateCompleteOrCancelledTrip("Stop1", "Stop1");
        assertEquals(BigDecimal.ZERO, result);
    }

    @Test
    void testCalculateInCompleteTrip_InvalidStopId() {
        BigDecimal result = pricingService.calculateIncompleteTrip("Stop4");
        assertEquals(BigDecimal.ZERO, result);
    }

    @Test
    void testCalculateCompleteTrip_InvalidStopId() {
        BigDecimal result = pricingService.calculateCompleteOrCancelledTrip("Stop4", "Stop5");
        assertEquals(BigDecimal.ZERO, result);
    }

    @Test
    void testCalculateCancelledTrip_InvalidStopId() {
        BigDecimal result = pricingService.calculateCompleteOrCancelledTrip("Stop4", "Stop4");
        assertEquals(BigDecimal.ZERO, result);
    }
}