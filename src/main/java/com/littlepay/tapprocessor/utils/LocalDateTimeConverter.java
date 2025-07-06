package com.littlepay.tapprocessor.utils;

import com.opencsv.bean.AbstractBeanField;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LocalDateTimeConverter extends AbstractBeanField<LocalDateTime, String> {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

    @Override
    protected LocalDateTime convert(String value) {
        return LocalDateTime.parse(value.trim(), FORMATTER);
    }

    @Override
    protected String convertToWrite(Object value) {
        if (value == null) {
            return "";
        }
        LocalDateTime dateTime = (LocalDateTime) value;
        return dateTime.format(FORMATTER);
    }
}
