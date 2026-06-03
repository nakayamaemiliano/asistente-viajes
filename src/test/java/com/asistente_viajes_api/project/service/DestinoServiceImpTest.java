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
import com.asistente_viajes_api.project.repository.IDestinoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DestinoServiceImpTest {
    @Mock
    private IDestinoRepository destinoRepository;

    @Mock
    private CountryClient countryClient;

    @Mock
    private WeatherClient weatherClient;

    @InjectMocks
    private DestinoServiceImp destinoService;

    @Test
    void crearDestino_deberiaGuardarDestino_cuandoNoExisteCiudadYPais() {
        // Arrange
        DestinoRequestDTO request = new DestinoRequestDTO(
                "Roma",
                "Italia",
                "IT",
                "EUR",
                "Italiano",
                "Europa",
                41.9028,
                12.4964
        );

        Destino destinoGuardado = new Destino();
        destinoGuardado.setDestinoId(1L);
        destinoGuardado.setCiudad("Roma");
        destinoGuardado.setPais("Italia");
        destinoGuardado.setCodigoPais("IT");
        destinoGuardado.setMoneda("EUR");
        destinoGuardado.setIdiomaPrincipal("Italiano");
        destinoGuardado.setRegion("Europa");
        destinoGuardado.setLatitud(41.9028);
        destinoGuardado.setLongitud(12.4964);

        when(destinoRepository.existsByCiudadIgnoreCaseAndPaisIgnoreCase("Roma", "Italia"))
                .thenReturn(false);
        when(destinoRepository.save(any(Destino.class))).thenReturn(destinoGuardado);

        // Act
        DestinoResponseDTO response = destinoService.crearDestino(request);

        // Assert
        assertNotNull(response);
        assertEquals(1L, response.destinoId());
        assertEquals("Roma", response.ciudad());
        assertEquals("Italia", response.pais());
        assertEquals("IT", response.codigoPais());
        assertEquals("EUR", response.moneda());
        assertEquals("Italiano", response.idiomaPrincipal());
        assertEquals("Europa", response.region());
        assertEquals(41.9028, response.latitud());
        assertEquals(12.4964, response.longitud());

        verify(destinoRepository).existsByCiudadIgnoreCaseAndPaisIgnoreCase("Roma", "Italia");
        verify(destinoRepository).save(any(Destino.class));
    }

    @Test
    void crearDestino_deberiaLanzarDuplicateResourceException_cuandoCiudadYPaisYaExisten() {
        // Arrange
        DestinoRequestDTO request = new DestinoRequestDTO(
                "Roma",
                "Italia",
                "IT",
                "EUR",
                "Italiano",
                "Europa",
                41.9028,
                12.4964
        );

        when(destinoRepository.existsByCiudadIgnoreCaseAndPaisIgnoreCase("Roma", "Italia"))
                .thenReturn(true);

        // Act & Assert
        DuplicateResourceException exception = assertThrows(
                DuplicateResourceException.class,
                () -> destinoService.crearDestino(request)
        );

        assertEquals(
                "Ya existe un destino con ciudad: Roma y país: Italia",
                exception.getMessage()
        );

        verify(destinoRepository).existsByCiudadIgnoreCaseAndPaisIgnoreCase("Roma", "Italia");
        verify(destinoRepository, never()).save(any(Destino.class));
    }

    @Test
    void listarDestinos_deberiaRetornarListaDeDestinos() {
        // Arrange
        Destino destino1 = new Destino();
        destino1.setDestinoId(1L);
        destino1.setCiudad("Roma");
        destino1.setPais("Italia");
        destino1.setCodigoPais("IT");
        destino1.setMoneda("EUR");
        destino1.setIdiomaPrincipal("Italiano");
        destino1.setRegion("Europa");
        destino1.setLatitud(41.9028);
        destino1.setLongitud(12.4964);

        Destino destino2 = new Destino();
        destino2.setDestinoId(2L);
        destino2.setCiudad("Buenos Aires");
        destino2.setPais("Argentina");
        destino2.setCodigoPais("AR");
        destino2.setMoneda("ARS");
        destino2.setIdiomaPrincipal("Español");
        destino2.setRegion("America");
        destino2.setLatitud(-34.6037);
        destino2.setLongitud(-58.3816);

        when(destinoRepository.findAll()).thenReturn(List.of(destino1, destino2));

        // Act
        List<DestinoResponseDTO> response = destinoService.listarDestinos();

        // Assert
        assertNotNull(response);
        assertEquals(2, response.size());

        assertEquals(1L, response.get(0).destinoId());
        assertEquals("Roma", response.get(0).ciudad());
        assertEquals("Italia", response.get(0).pais());

        assertEquals(2L, response.get(1).destinoId());
        assertEquals("Buenos Aires", response.get(1).ciudad());
        assertEquals("Argentina", response.get(1).pais());

        verify(destinoRepository).findAll();
    }

    @Test
    void buscarDestinoPorId_deberiaRetornarDestino_cuandoExiste() {
        // Arrange
        Long destinoId = 1L;

        Destino destino = new Destino();
        destino.setDestinoId(destinoId);
        destino.setCiudad("Roma");
        destino.setPais("Italia");
        destino.setCodigoPais("IT");
        destino.setMoneda("EUR");
        destino.setIdiomaPrincipal("Italiano");
        destino.setRegion("Europa");
        destino.setLatitud(41.9028);
        destino.setLongitud(12.4964);

        when(destinoRepository.findById(destinoId)).thenReturn(Optional.of(destino));

        // Act
        DestinoResponseDTO response = destinoService.buscarDestinoPorId(destinoId);

        // Assert
        assertNotNull(response);
        assertEquals(destinoId, response.destinoId());
        assertEquals("Roma", response.ciudad());
        assertEquals("Italia", response.pais());
        assertEquals("IT", response.codigoPais());
        assertEquals("EUR", response.moneda());
        assertEquals("Italiano", response.idiomaPrincipal());
        assertEquals("Europa", response.region());
        assertEquals(41.9028, response.latitud());
        assertEquals(12.4964, response.longitud());

        verify(destinoRepository).findById(destinoId);
    }

    @Test
    void buscarDestinoPorId_deberiaLanzarResourceNotFoundException_cuandoNoExiste() {
        // Arrange
        Long destinoId = 99L;

        when(destinoRepository.findById(destinoId)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> destinoService.buscarDestinoPorId(destinoId)
        );

        assertEquals(
                "Destino no encontrado con id: " + destinoId,
                exception.getMessage()
        );

        verify(destinoRepository).findById(destinoId);
    }


    @Test
    void actualizarDestino_deberiaActualizarDestino_cuandoExisteYNoDuplica() {
        // Arrange
        Long destinoId = 1L;

        DestinoRequestDTO request = new DestinoRequestDTO(
                "Florencia",
                "Italia",
                "IT",
                "EUR",
                "Italiano",
                "Europa",
                43.7696,
                11.2558
        );

        Destino destinoExistente = new Destino();
        destinoExistente.setDestinoId(destinoId);
        destinoExistente.setCiudad("Roma");
        destinoExistente.setPais("Italia");
        destinoExistente.setCodigoPais("IT");
        destinoExistente.setMoneda("EUR");
        destinoExistente.setIdiomaPrincipal("Italiano");
        destinoExistente.setRegion("Europa");
        destinoExistente.setLatitud(41.9028);
        destinoExistente.setLongitud(12.4964);

        Destino destinoActualizado = new Destino();
        destinoActualizado.setDestinoId(destinoId);
        destinoActualizado.setCiudad("Florencia");
        destinoActualizado.setPais("Italia");
        destinoActualizado.setCodigoPais("IT");
        destinoActualizado.setMoneda("EUR");
        destinoActualizado.setIdiomaPrincipal("Italiano");
        destinoActualizado.setRegion("Europa");
        destinoActualizado.setLatitud(43.7696);
        destinoActualizado.setLongitud(11.2558);

        when(destinoRepository.findById(destinoId)).thenReturn(Optional.of(destinoExistente));
        when(destinoRepository.existsByCiudadIgnoreCaseAndPaisIgnoreCase("Florencia", "Italia"))
                .thenReturn(false);
        when(destinoRepository.save(any(Destino.class))).thenReturn(destinoActualizado);

        // Act
        DestinoResponseDTO response = destinoService.actualizarDestino(destinoId, request);

        // Assert
        assertNotNull(response);
        assertEquals(destinoId, response.destinoId());
        assertEquals("Florencia", response.ciudad());
        assertEquals("Italia", response.pais());
        assertEquals("IT", response.codigoPais());
        assertEquals("EUR", response.moneda());
        assertEquals("Italiano", response.idiomaPrincipal());
        assertEquals("Europa", response.region());
        assertEquals(43.7696, response.latitud());
        assertEquals(11.2558, response.longitud());

        verify(destinoRepository).findById(destinoId);
        verify(destinoRepository).existsByCiudadIgnoreCaseAndPaisIgnoreCase("Florencia", "Italia");
        verify(destinoRepository).save(any(Destino.class));
    }

    @Test
    void actualizarDestino_deberiaLanzarResourceNotFoundException_cuandoNoExiste() {
        // Arrange
        Long destinoId = 99L;

        DestinoRequestDTO request = new DestinoRequestDTO(
                "Florencia",
                "Italia",
                "IT",
                "EUR",
                "Italiano",
                "Europa",
                43.7696,
                11.2558
        );

        when(destinoRepository.findById(destinoId)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> destinoService.actualizarDestino(destinoId, request)
        );

        assertEquals(
                "Destino no encontrado con id: " + destinoId,
                exception.getMessage()
        );

        verify(destinoRepository).findById(destinoId);
        verify(destinoRepository, never())
                .existsByCiudadIgnoreCaseAndPaisIgnoreCase(any(), any());
        verify(destinoRepository, never()).save(any(Destino.class));
    }


    @Test
    void actualizarDestino_deberiaLanzarDuplicateResourceException_cuandoCiudadYPaisYaExisten() {
        // Arrange
        Long destinoId = 1L;

        DestinoRequestDTO request = new DestinoRequestDTO(
                "Florencia",
                "Italia",
                "IT",
                "EUR",
                "Italiano",
                "Europa",
                43.7696,
                11.2558
        );

        Destino destinoExistente = new Destino();
        destinoExistente.setDestinoId(destinoId);
        destinoExistente.setCiudad("Roma");
        destinoExistente.setPais("Italia");
        destinoExistente.setCodigoPais("IT");
        destinoExistente.setMoneda("EUR");
        destinoExistente.setIdiomaPrincipal("Italiano");
        destinoExistente.setRegion("Europa");
        destinoExistente.setLatitud(41.9028);
        destinoExistente.setLongitud(12.4964);

        when(destinoRepository.findById(destinoId)).thenReturn(Optional.of(destinoExistente));
        when(destinoRepository.existsByCiudadIgnoreCaseAndPaisIgnoreCase("Florencia", "Italia"))
                .thenReturn(true);

        // Act & Assert
        DuplicateResourceException exception = assertThrows(
                DuplicateResourceException.class,
                () -> destinoService.actualizarDestino(destinoId, request)
        );

        assertEquals(
                "Ya existe un destino con ciudad: Florencia y país: Italia",
                exception.getMessage()
        );

        verify(destinoRepository).findById(destinoId);
        verify(destinoRepository).existsByCiudadIgnoreCaseAndPaisIgnoreCase("Florencia", "Italia");
        verify(destinoRepository, never()).save(any(Destino.class));
    }


    @Test
    void eliminarDestino_deberiaEliminarDestino_cuandoExiste() {
        // Arrange
        Long destinoId = 1L;

        Destino destino = new Destino();
        destino.setDestinoId(destinoId);
        destino.setCiudad("Roma");
        destino.setPais("Italia");
        destino.setCodigoPais("IT");
        destino.setMoneda("EUR");
        destino.setIdiomaPrincipal("Italiano");
        destino.setRegion("Europa");
        destino.setLatitud(41.9028);
        destino.setLongitud(12.4964);

        when(destinoRepository.findById(destinoId)).thenReturn(Optional.of(destino));

        // Act
        destinoService.eliminarDestino(destinoId);

        // Assert
        verify(destinoRepository).findById(destinoId);
        verify(destinoRepository).delete(destino);
    }

    @Test
    void eliminarDestino_deberiaLanzarResourceNotFoundException_cuandoNoExiste() {
        // Arrange
        Long destinoId = 99L;

        when(destinoRepository.findById(destinoId)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> destinoService.eliminarDestino(destinoId)
        );

        assertEquals(
                "Destino no encontrado con id: " + destinoId,
                exception.getMessage()
        );

        verify(destinoRepository).findById(destinoId);
        verify(destinoRepository, never()).delete(any(Destino.class));
    }

    @Test
    void buscarPais_deberiaDelegarEnCountryClient() {
        // Arrange
        String pais = "italy";

        CountryInfoResponseDTO countryInfo = new CountryInfoResponseDTO(
                "Italy",
                "Rome",
                "Europe",
                "IT",
                List.of("Euro"),
                List.of("Italian")
        );

        when(countryClient.buscarPais(pais)).thenReturn(countryInfo);

        // Act
        CountryInfoResponseDTO response = destinoService.buscarPais(pais);

        // Assert
        assertNotNull(response);
        assertEquals("Italy", response.nombre());
        assertEquals("Rome", response.capital());
        assertEquals("Europe", response.region());
        assertEquals("IT", response.codigoPais());
        assertEquals(List.of("Euro"), response.monedas());
        assertEquals(List.of("Italian"), response.idiomas());

        verify(countryClient).buscarPais(pais);
    }

    @Test
    void obtenerClimaPorDestino_deberiaRetornarWeatherInfo_cuandoDestinoExiste() {
        // Arrange
        Long destinoId = 1L;

        Destino destino = new Destino();
        destino.setDestinoId(destinoId);
        destino.setCiudad("Roma");
        destino.setPais("Italia");
        destino.setCodigoPais("IT");
        destino.setMoneda("EUR");
        destino.setIdiomaPrincipal("Italiano");
        destino.setRegion("Europa");
        destino.setLatitud(41.9028);
        destino.setLongitud(12.4964);

        OpenMeteoResponseDTO.Current current = new OpenMeteoResponseDTO.Current(
                "2026-06-03T14:00",
                27.4,
                55,
                29.1,
                0.0,
                1,
                12.5,
                180
        );

        OpenMeteoResponseDTO clima = new OpenMeteoResponseDTO(current);

        when(destinoRepository.findById(destinoId)).thenReturn(Optional.of(destino));
        when(weatherClient.obtenerClimaActual(41.9028, 12.4964)).thenReturn(clima);

        // Act
        WeatherInfoResponseDTO response = destinoService.obtenerClimaPorDestino(destinoId);

        // Assert
        assertNotNull(response);
        assertEquals(destinoId, response.destinoId());
        assertEquals("Roma", response.ciudad());
        assertEquals("Italia", response.pais());
        assertEquals(27.4, response.temperatura());
        assertEquals(29.1, response.sensacionTermica());
        assertEquals(55, response.humedad());
        assertEquals(0.0, response.precipitacion());
        assertEquals(12.5, response.velocidadViento());
        assertEquals(180, response.direccionViento());
        assertEquals(1, response.codigoClima());
        assertEquals("Principalmente despejado", response.descripcion());
        assertEquals("2026-06-03T14:00", response.hora());

        verify(destinoRepository).findById(destinoId);
        verify(weatherClient).obtenerClimaActual(41.9028, 12.4964);
    }

    @Test
    void obtenerClimaPorDestino_deberiaLanzarResourceNotFoundException_cuandoDestinoNoExiste() {
        // Arrange
        Long destinoId = 99L;

        when(destinoRepository.findById(destinoId)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> destinoService.obtenerClimaPorDestino(destinoId)
        );

        assertEquals(
                "Destino no encontrado con id: " + destinoId,
                exception.getMessage()
        );

        verify(destinoRepository).findById(destinoId);
        verify(weatherClient, never()).obtenerClimaActual(any(), any());
    }
}
