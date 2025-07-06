package com.littlepay.tapprocessor.services;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Service
public class PricingService {

    private static final Map<String, BigDecimal> costMap = new HashMap<>();

    @PostConstruct
    public void initPriceMap() {
        costMap.put("Stop1-Stop2", BigDecimal.valueOf(3.25));
        costMap.put("Stop2-Stop1", BigDecimal.valueOf(3.25));

        costMap.put("Stop2-Stop3", BigDecimal.valueOf(5.50));
        costMap.put("Stop3-Stop2", BigDecimal.valueOf(5.50));

        costMap.put("Stop1-Stop3", BigDecimal.valueOf(7.30));
        costMap.put("Stop3-Stop1", BigDecimal.valueOf(7.30));
    }

    /**
     * Calculate the maximum cost for an incomplete trip based on the given stopId
     *
     * @param stopId the given incomplete stopId
     * @return max cost
     */
    public BigDecimal calculateIncompleteTrip(String stopId) {
        BigDecimal maxCost = BigDecimal.ZERO;

        for (Map.Entry<String, BigDecimal> entry : costMap.entrySet()) {
            String route = entry.getKey();
            BigDecimal cost = entry.getValue();

            if (route.startsWith(stopId + "-")) {
                if (cost.compareTo(maxCost) > 0) {
                    maxCost = cost;
                }
            }
        }
        return maxCost;
    }

    /**
     * Calculate cost for a complete trip between two stops or cancelled trip (same stopIds passed in)
     *
     * @param tapOnStopId tap-on stop
     * @param tapOffStopId tap-off stop
     * @return cost amount
     */
    public BigDecimal calculateCompleteOrCancelledTrip(String tapOnStopId, String tapOffStopId) {
        if(tapOnStopId.equals(tapOffStopId)) {
            return BigDecimal.ZERO;
        }
        String key = tapOnStopId + "-" + tapOffStopId;
        return costMap.getOrDefault(key, BigDecimal.ZERO);
    }
}
