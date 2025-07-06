package com.littlepay.tapprocessor.models;

import com.littlepay.tapprocessor.utils.DollarAmountConverter;
import com.littlepay.tapprocessor.utils.LocalDateTimeConverter;
import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvBindByPosition;
import com.opencsv.bean.CsvCustomBindByName;
import com.opencsv.bean.CsvCustomBindByPosition;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Builder
public class Trip {
    @CsvCustomBindByPosition(position = 0, converter = LocalDateTimeConverter.class)
    private LocalDateTime started;

    @CsvCustomBindByPosition(position = 1, converter = LocalDateTimeConverter.class)
    private LocalDateTime finished;

    @CsvBindByPosition(position = 2)
    private Long durationSecs;

    @CsvBindByPosition(position = 3)
    private String fromStopId;

    @CsvBindByPosition(position = 4)
    private String toStopId;

    // Use the custom converter for the currency field
    @CsvCustomBindByPosition(position = 5, converter = DollarAmountConverter.class)
    private BigDecimal chargeAmount;

    @CsvBindByPosition(position = 6)
    private String companyId;

    @CsvBindByPosition(position = 7)
    private String busId;

    @CsvBindByPosition(position = 8)
    private String pan;

    @CsvBindByPosition(position = 9)
    private TripType status;
}
