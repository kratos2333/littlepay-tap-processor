package com.littlepay.tapprocessor.utils;

import com.opencsv.bean.AbstractBeanField;

import java.math.BigDecimal;

public class DollarAmountConverter extends AbstractBeanField<BigDecimal, String> {
    @Override
    protected Object convert(String s) {
        // not used
        return null;
    }

    @Override
    protected String convertToWrite(Object value) {
        if (value == null) {
            return "";
        }
        BigDecimal amount = (BigDecimal) value;
        return "$" + amount.toPlainString();
    }
}