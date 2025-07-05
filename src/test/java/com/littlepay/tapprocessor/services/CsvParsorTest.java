package com.littlepay.tapprocessor.services;

import com.littlepay.tapprocessor.models.Tap;
import com.littlepay.tapprocessor.models.TapType;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CsvParsorTest {
    private CsvParsor csvParsor = new CsvParsor();

    @Test
    void testReadTapsFromCsvSuccess() {
        List<Tap> taps = csvParsor.readTapsFromCsv("src/test/resources/test_taps.csv");
        assertEquals(6, taps.size());
        Tap tap = taps.get(0);
        assertThat(tap.getId()).isEqualTo(1L);
        assertThat(tap.getDateTimeUtc()).isEqualTo(LocalDateTime.of(2023, 1, 22, 13, 0));
        assertThat(tap.getType()).isEqualTo(TapType.ON);
        assertThat(tap.getStopId()).isEqualTo("Stop1");
    }

    @Test
    void testReadTapsFromCsvEmptyFile() {
        List<Tap> taps = csvParsor.readTapsFromCsv("src/test/resources/test_taps_empty.csv");
        assertEquals(0, taps.size());
    }

    @Test
    void testReadTapsFromCsvEmptyFailure() {
        assertThrows(IllegalArgumentException.class, () -> {
            csvParsor.readTapsFromCsv("src/test/resources/test_taps_invalid.csv");
        });
    }

}