package com.asistente_viajes_api.project.external.weather.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record OpenMeteoResponseDTO(
        Current current

) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Current(
            String time,

            @JsonProperty("temperature_2m")
            Double temperature,

            @JsonProperty("relative_humidity_2m")
            Integer relativeHumidity,

            @JsonProperty("apparent_temperature")
            Double apparentTemperature,

            Double precipitation,

            @JsonProperty("weather_code")
            Integer weatherCode,

            @JsonProperty("wind_speed_10m")
            Double windSpeed,

            @JsonProperty("wind_direction_10m")
            Integer windDirection
    ){

    }

}
