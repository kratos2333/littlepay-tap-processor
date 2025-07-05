package com.littlepay.tapprocessor.utils;

import com.littlepay.tapprocessor.models.TapType;
import com.opencsv.bean.AbstractBeanField;

public class TapTypeConverter extends AbstractBeanField<TapType, String> {

    @Override
    protected TapType convert(String value) {
        if (value == null) return null;
        return TapType.valueOf(value.trim().toUpperCase());
    }
}
