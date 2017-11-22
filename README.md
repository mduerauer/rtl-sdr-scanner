# rtl-sdr-scanner

Markus Dürauer 2017 <markus.duerauer@feuerwehr.gv.at>

## Introduction

This is a Spring Boot Application that parses the output of a RTL-SDR-Scanner and displays alerts when sprectral density
levels exceed a configurable standard deviation value compared to a pre-recorded baseline.

## Installation

### Linux

`./gradlew build`

`./gradlew bootRun <FM Scanner executable>`

Open `http://localhost:9000` in your browser an click on `Connect`