# Littlepay Tap Processing System

A Java application that processes tap-on and tap-off events from public transport card readers to generate trip information and calculate fares.

## Overview

This system processes CSV files containing tap events and produces trip summaries with appropriate charges based on the trip types:

- **Complete trips**: Passenger taps on at one stop and taps off at another
- **Incomplete trips**: Passenger taps on at one stop but forgets to tap off (will charge the max possible fares)
- **Cancelled trips**: Passenger taps on and off at the same stop (zero charge)

## Pricing Structure

- Trips between Stop 1 and Stop 2 cost $3.25
- Trips between Stop 2 and Stop 3 cost $5.50
- Trips between Stop 1 and Stop 3 cost $7.30
- Prices apply for travel in either direction

## Key Components

### TripParser
- Read taps from CSV
- Process taps into trips
- Write trips to CSV

### PricingService
- Handles complete, incomplete and cancelled trip pricing
- Maintain the cost matrix as an in-memory Hashmap

### CsvParsor
- Handles CSV read and output a tap list
- Handles trip list and output into a csv file

## Dependencies
- springboot
- lombok
- opencsv

## Usage

### Prerequisites
- Java 17 or later
- Maven 3.6 or later

### Running the application

1. **Compile the project:**
    ```bash
    mvn clean compile
    ```
2. **Run the application:**
    ```bash
    mvn clean spring-boot:run
    ```

3. **Run unit tests:**
    ```bash
    mvn clean test
    ```

## Input/Output Format

### Input CSV Format (taps.csv)
```csv
ID, DateTimeUTC, TapType, StopId, CompanyId, BusID, PAN
1, 22-01-2023 13:00:00, ON, Stop1, Company1, Bus37, 5500005555555559
2, 22-01-2023 13:05:00, OFF, Stop2, Company1, Bus37, 5500005555555559
```

### Output CSV Format (trips.csv)
```csv
Started, Finished, DurationSecs, FromStopId, ToStopId, ChargeAmount, CompanyId, BusID, PAN, Status
22-01-2023 13:00:00, 22-01-2023 13:05:00, 300, Stop1, Stop2, $3.25, Company1, Bus37, 5500005555555559, COMPLETED
```

## Assumptions
1. taps.csv is well-formatted and contains valid data (no missing or malformed fields).
2. The combination of PAN and BusID uniquely identifies a trip.
3. Tap-off events without a matching prior tap-on will be ignored, and a warning will be logged.
4. Only Stop1, Stop2, and Stop3 are considered valid stopIds. If a trip involves any other stopId, the cost will be treated as zero.