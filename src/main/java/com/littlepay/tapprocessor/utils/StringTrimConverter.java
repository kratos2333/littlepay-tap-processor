package com.littlepay.tapprocessor.utils;

import com.opencsv.bean.AbstractBeanField;

public class StringTrimConverter extends AbstractBeanField<String, String> {
    @Override
    protected String convert(String value) {
        return value == null ? null : value.trim();
    }
}
