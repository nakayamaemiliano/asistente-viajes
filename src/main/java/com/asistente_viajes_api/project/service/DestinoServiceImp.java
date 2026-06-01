package com.asistente_viajes_api.project.service;

import com.asistente_viajes_api.project.dto.destino.DestinoRequestDTO;
import com.asistente_viajes_api.project.dto.destino.DestinoResponseDTO;
import com.asistente_viajes_api.project.entity.Destino;
import com.asistente_viajes_api.project.exception.DuplicateResourceException;
import com.asistente_viajes_api.project.exception.ResourceNotFoundException;
import com.asistente_viajes_api.project.external.countries.CountryClient;
import com.asistente_viajes_api.project.external.countries.dto.CountryInfoResponseDTO;
import com.asistente_viajes_api.project.external.weather.WeatherClient;
import com.asistente_viajes_api.project.external.weather.dto.OpenMeteoResponseDTO;
import com.asistente_viajes_api.project.external.weather.dto.WeatherInfoResponseDTO;
import com.asistente_viajes_api.project.mapper.DestinoMapper;
import com.asistente_viajes_api.project.repository.IDestinoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DestinoServiceImp implements IDestinoService{

    @Autowired
    private IDestinoRepository destinoRepository;

    @Autowired
    private CountryClient countryClient;

    @Autowired
    private WeatherClient weatherClient;

    @Override
    public DestinoResponseDTO crearDestino(DestinoRequestDTO dto) {
        if(destinoRepository.existsByCiudadIgnoreCaseAndPaisIgnoreCase(dto.ciudad(), dto.pais())){
            throw new DuplicateResourceException(
                    "Ya existe un destino con ciudad: " + dto.ciudad() + " y país: " + dto.pais()
            );
        }
        Destino destino = DestinoMapper.toEntity(dto);
        Destino destinoGuardado = destinoRepository.save(destino);

        return DestinoMapper.toResponseDTO(destinoGuardado);

    }

    @Override
    public List<DestinoResponseDTO> listarDestinos() {
        return destinoRepository.findAll()
                .stream()
                .map(DestinoMapper::toResponseDTO)
                .toList();
    }

    @Override
    public DestinoResponseDTO buscarDestinoPorId(Long id) {
        Destino destino = destinoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Destino no encontrado con id: " + id));
        return DestinoMapper.toResponseDTO(destino);
    }

    @Override
    public DestinoResponseDTO actualizarDestino(Long id, DestinoRequestDTO dto) {
        Destino destino = destinoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Destino no encontrado con id: " + id));

        boolean cambioCiudadOPais =
                !destino.getCiudad().equalsIgnoreCase(dto.ciudad()) ||
                        !destino.getPais().equalsIgnoreCase(dto.pais());

        if (cambioCiudadOPais &&
                destinoRepository.existsByCiudadIgnoreCaseAndPaisIgnoreCase(dto.ciudad(), dto.pais())) {

            throw new DuplicateResourceException(
                    "Ya existe un destino con ciudad: " + dto.ciudad() + " y país: " + dto.pais()
            );
        }

        DestinoMapper.updateEntityFromDTO(dto, destino);

        Destino destinoActualizado = destinoRepository.save(destino);

        return DestinoMapper.toResponseDTO(destinoActualizado);
    }

    @Override
    public void eliminarDestino(Long id) {

        Destino destino = destinoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Destino no encontrado con id: " + id));

        destinoRepository.delete(destino);
    }

    @Override
    public CountryInfoResponseDTO buscarPais(String pais) {
        return countryClient.buscarPais(pais);
    }

    @Override
    public WeatherInfoResponseDTO obtenerClimaPorDestino(Long id) {
        Destino destino = destinoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Destino no encontrado con id: " + id));

        OpenMeteoResponseDTO clima = weatherClient.obtenerClimaActual(
                destino.getLatitud(),
                destino.getLongitud()
        );

        OpenMeteoResponseDTO.Current current = clima.current();

        return new WeatherInfoResponseDTO(
                destino.getDestinoId(),
                destino.getCiudad(),
                destino.getPais(),
                current.temperature(),
                current.apparentTemperature(),
                current.relativeHumidity(),
                current.precipitation(),
                current.windSpeed(),
                current.windDirection(),
                current.weatherCode(),
                traducirCodigoClima(current.weatherCode()),
                current.time()
        );
    }

    private String traducirCodigoClima(Integer codigo) {
        if (codigo == null) {
            return "Sin información";
        }
        return switch (codigo) {
            case 0 -> "Despejado";
            case 1 -> "Principalmente despejado";
            case 2 -> "Parcialmente nublado";
            case 3 -> "Nublado";
            case 45, 48 -> "Niebla";
            case 51, 53, 55 -> "Llovizna";
            case 56, 57 -> "Llovizna helada";
            case 61, 63, 65 -> "Lluvia";
            case 66, 67 -> "Lluvia helada";
            case 71, 73, 75 -> "Nieve";
            case 77 -> "Granos de nieve";
            case 80, 81, 82 -> "Chaparrones";
            case 85, 86 -> "Chaparrones de nieve";
            case 95 -> "Tormenta";
            case 96, 99 -> "Tormenta con granizo";
            default -> "Código de clima desconocido";
        };
    }


}
