package com.mycompany.knstore.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.mycompany.knstore.domain.Cuenta;
import com.mycompany.knstore.domain.Direccion;
import com.mycompany.knstore.repository.CuentaRepository;
import com.mycompany.knstore.repository.DireccionRepository;
import com.mycompany.knstore.service.dto.DireccionDTO;
import com.mycompany.knstore.service.mapper.DireccionMapper;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class DireccionServiceImplDefaultTest {

    @Mock
    private DireccionRepository direccionRepository;

    @Mock
    private CuentaRepository cuentaRepository;

    @Mock
    private DireccionMapper direccionMapper;

    @InjectMocks
    private DireccionServiceImpl direccionService;

    @Test
    void marcarPredeterminadaActivaObjetivoYDesactivaResto() {
        Cuenta cuenta = new Cuenta();
        cuenta.setId("cuenta-1");

        Direccion objetivo = new Direccion();
        objetivo.setId("dir-1");
        objetivo.setActivo(false);
        objetivo.setCuenta(cuenta);

        Direccion otra = new Direccion();
        otra.setId("dir-2");
        otra.setActivo(true);
        otra.setCuenta(cuenta);

        when(direccionRepository.findById("dir-1")).thenReturn(Optional.of(objetivo));
        when(direccionRepository.findByCuentaId("cuenta-1", Pageable.unpaged())).thenReturn(
            new PageImpl<>(List.of(objetivo, otra), Pageable.unpaged(), 2)
        );
        when(direccionRepository.save(any(Direccion.class))).thenAnswer(invocation -> invocation.getArgument(0));

        DireccionDTO dto = new DireccionDTO();
        dto.setId("dir-1");
        dto.setActivo(true);
        when(direccionMapper.toDto(objetivo)).thenReturn(dto);

        Optional<DireccionDTO> result = direccionService.marcarPredeterminada("dir-1");

        assertThat(result).isPresent();
        assertThat(objetivo.getActivo()).isTrue();
        assertThat(otra.getActivo()).isFalse();
        verify(direccionRepository, times(2)).save(any(Direccion.class));
    }

    @Test
    void marcarPredeterminadaRetornaVacioCuandoNoExiste() {
        when(direccionRepository.findById("no-existe")).thenReturn(Optional.empty());

        Optional<DireccionDTO> result = direccionService.marcarPredeterminada("no-existe");

        assertThat(result).isEmpty();
    }
}
