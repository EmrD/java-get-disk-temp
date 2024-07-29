# Disk Temperature Dashboard

![Java Logo](https://www.java.com/images/brand/JavaLogo_300x120.png)

## Overview

The **Disk Temperature Dashboard** is a Java application that provides a visual representation of disk temperatures using speedometer-style gauges. This application reads disk temperature data from a CSV file and displays it using dynamic gauges that update in real-time.

### Features

- **Speedometer-Style Gauges**: Visualize disk temperatures with a speedometer-like design.
- **Real-Time Updates**: Automatically updates the gauges based on new temperature data.
- **Disk ID Labels**: Clearly displays which disk each gauge corresponds to.

## Prerequisites

Before running the application, ensure that you have the following installed:

- [Java Development Kit (JDK) 11 or higher](https://www.oracle.com/java/technologies/javase-jdk11-downloads.html)
- [FlatLaf Look and Feel](https://www.formdev.com/flatlaf/)

## Setup

1. **Navigate to the Project Directory**:

   ```bash
   cd disk-temperature-dashboard
   ```

2. **Compile and Run the Application**:

   Ensure you have all dependencies, then compile and run the application using your IDE or command line.

   ```bash
   javac -cp "path/to/flatlaf.jar" DiskInfoUI.java
   java -cp ".;path/to/flatlaf.jar" DiskInfoUI
   ```

3. **Run the PowerShell Script**:

   - Place your `GetDiskInfo.ps1` script in the `src` directory.
   - Ensure the script outputs the temperature data to `Desktop/output.csv`.

## Usage

1. **Start the Application**:
   
   Launch the application, and it will open a window displaying the disk temperature gauges.

2. **Run the Script**:
   
   Click the "Refresh" button to execute the PowerShell script that gathers disk temperature data.

3. **View the Gauges**:
   
   The gauges will update in real-time to reflect the current temperatures of your disks.

## Code Explanation

- **DiskInfoUI Class**: Contains the main GUI setup and handles the execution of the PowerShell script. It also reads the CSV data and updates the gauges.
- **Speedometer Class**: Custom JPanel that renders a speedometer-style gauge. It displays the temperature values with a non-filled, line-only gauge design.

## Contributing

Contributions are welcome! Please submit issues and pull requests on the GitHub repository.