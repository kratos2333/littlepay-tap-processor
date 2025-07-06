package com.littlepay.tapprocessor.models;

import com.littlepay.tapprocessor.utils.LocalDateTimeConverter;
import com.littlepay.tapprocessor.utils.StringTrimConverter;
import com.littlepay.tapprocessor.utils.TapTypeConverter;
import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvCustomBindByName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Tap {
    @CsvBindByName(column="ID")
    private Long id;
    @CsvCustomBindByName(column="DateTimeUTC", converter = LocalDateTimeConverter.class)
    private LocalDateTime dateTimeUtc;
    @CsvCustomBindByName(column="TapType", converter = TapTypeConverter.class)
    private TapType type;
    @CsvCustomBindByName(column="StopId", converter = StringTrimConverter.class)
    private String stopId;
    @CsvCustomBindByName(column="CompanyId", converter = StringTrimConverter.class)
    private String companyId;
    @CsvCustomBindByName(column="BusID", converter = StringTrimConverter.class)
    private String busId;
    @CsvCustomBindByName(column="PAN", converter = StringTrimConverter.class)
    private String pan;
}
