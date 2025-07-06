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
   
3. **Package and run with custom input/output files:**
   ```bash
   mvn clean package
   java -jar littlepay-tap-processor-0.0.1-SNAPSHOT.jar [input_file_path] [output_file_path]
    ```

4. **Run unit tests:**
    ```bash
    mvn clean test
    ```

## Input/Output Format

### Input CSV Format (taps.csv)
```csv
ID, DateTimeUTC, TapType, StopId, CompanyId, BusID, PAN
1, 22-01-2023 13:00:00, ON, Stop1, Company1, Bus37, 5500005555555559
2, 22-01-2023 13:05:00, OFF, Stop2, Company1, Bus37, 5500005555555559
3, 22-01-2023 09:20:00, ON, Stop3, Company1, Bus36, 4111111111111111
4, 23-01-2023 08:00:00, ON, Stop1, Company1, Bus37, 4111111111111111
5, 23-01-2023 08:02:00, OFF, Stop1, Company1, Bus37, 4111111111111111
6, 24-01-2023 16:30:00, OFF, Stop2, Company1, Bus37, 5500005555555559
```

### Output CSV Format (trips.csv)
```csv
Started,Finished,DurationSecs,FromStopId,ToStopId,ChargeAmount,CompanyId,BusId,PAN,Status
23-01-2023 08:00:00,23-01-2023 08:02:00,120,Stop1,Stop1,$0,Company1,Bus37,4111111111111111,CANCELLED
22-01-2023 13:00:00,22-01-2023 13:05:00,300,Stop1,Stop2,$3.25,Company1,Bus37,5500005555555559,COMPLETED
22-01-2023 09:20:00,,,Stop3,,$7.3,Company1,Bus36,4111111111111111,INCOMPLETE
```

## Assumptions
1. taps.csv is well-formatted and contains valid data (no missing or malformed fields).
2. Tap-off events without a matching prior tap-on will be ignored, and a warning will be logged.
3. Only Stop1, Stop2, and Stop3 are considered valid stopIds. If a trip involves any other stopId, the cost will be treated as zero.