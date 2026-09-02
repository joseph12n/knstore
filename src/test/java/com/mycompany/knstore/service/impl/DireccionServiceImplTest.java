package com.mycompany.knstore.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.mycompany.knstore.domain.Cuenta;
import com.mycompany.knstore.domain.Direccion;
import com.mycompany.knstore.repository.CuentaRepository;
import com.mycompany.knstore.repository.DireccionRepository;
import com.mycompany.knstore.security.AuthoritiesConstants;
import com.mycompany.knstore.security.SecurityUtils;
import com.mycompany.knstore.service.dto.CuentaDTO;
import com.mycompany.knstore.service.dto.DireccionDTO;
import com.mycompany.knstore.service.mapper.DireccionMapper;
import com.mycompany.knstore.service.mapper.DireccionMapperImpl;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class DireccionServiceImplTest {

    private final DireccionMapper direccionMapper = new DireccionMapperImpl();

    @Mock
    private DireccionRepository direccionRepository;

    @Mock
    private CuentaRepository cuentaRepository;

    private DireccionServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new DireccionServiceImpl(direccionRepository, cuentaRepository, direccionMapper);
    }

    private DireccionDTO direccionDTODePrueba() {
        DireccionDTO dto = new DireccionDTO();
        dto.setDireccion("Calle 10 #5-25");
        dto.setMunicipio("Bogota");
        dto.setDepartamento("Cundinamarca");
        dto.setActivo(false);
        dto.setTelefonoContacto("3001234567");
        dto.setDestinatario("Joseph Perez");
        dto.setCodigoPostal("110111");
        return dto;
    }

    private Cuenta cuentaConId(String id) {
        Cuenta cuenta = new Cuenta();
        cuenta.setId(id);
        return cuenta;
    }

    private Direccion direccionConCuenta(String id, Cuenta cuenta) {
        Direccion direccion = new Direccion();
        direccion.setId(id);
        direccion.setCuenta(cuenta);
        return direccion;
    }

    private void mockCliente(MockedStatic<SecurityUtils> security) {
        security.when(() -> SecurityUtils.hasCurrentUserThisAuthority(AuthoritiesConstants.CLIENTE)).thenReturn(true);
        security.when(SecurityUtils::getCurrentUserId).thenReturn(Optional.of("user-1"));
        when(cuentaRepository.findOneByUserId("user-1")).thenReturn(Optional.of(cuentaConId("cuenta-1")));
    }

    @Test
    void saveAsignaLaCuentaDelClienteCuandoElDTOVinoSinCuenta() {
        try (MockedStatic<SecurityUtils> security = mockStatic(SecurityUtils.class)) {
            mockCliente(security);
            when(direccionRepository.save(any(Direccion.class))).thenAnswer(invocation -> {
                Direccion direccion = invocation.getArgument(0);
                direccion.setId("dir-1");
                return direccion;
            });

            DireccionDTO resultado = service.save(direccionDTODePrueba());

            ArgumentCaptor<Direccion> captor = ArgumentCaptor.forClass(Direccion.class);
            verify(direccionRepository).save(captor.capture());
            assertThat(captor.getValue().getCuenta().getId()).isEqualTo("cuenta-1");
            assertThat(resultado.getId()).isEqualTo("dir-1");
        }
    }

    @Test
    void saveComoAdminConservaLaCuentaEnviadaEnElDTO() {
        try (MockedStatic<SecurityUtils> security = mockStatic(SecurityUtils.class)) {
            security.when(() -> SecurityUtils.hasCurrentUserThisAuthority(AuthoritiesConstants.CLIENTE)).thenReturn(false);
            DireccionDTO dto = direccionDTODePrueba();
            dto.setCuenta(new CuentaDTO());
            dto.getCuenta().setId("cuenta-99");
            when(direccionRepository.save(any(Direccion.class))).thenAnswer(invocation -> invocation.getArgument(0));

            service.save(dto);

            ArgumentCaptor<Direccion> captor = ArgumentCaptor.forClass(Direccion.class);
            verify(direccionRepository).save(captor.capture());
            assertThat(captor.getValue().getCuenta().getId()).isEqualTo("cuenta-99");
            verify(cuentaRepository, never()).findOneByUserId(anyString());
        }
    }

    @Test
    void updateAsignaLaCuentaDelClienteCuandoElDTOVinoSinCuenta() {
        try (MockedStatic<SecurityUtils> security = mockStatic(SecurityUtils.class)) {
            mockCliente(security);
            DireccionDTO dto = direccionDTODePrueba();
            dto.setId("dir-1");
            when(direccionRepository.save(any(Direccion.class))).thenAnswer(invocation -> invocation.getArgument(0));

            DireccionDTO resultado = service.update(dto);

            ArgumentCaptor<Direccion> captor = ArgumentCaptor.forClass(Direccion.class);
            verify(direccionRepository).save(captor.capture());
            assertThat(captor.getValue().getCuenta().getId()).isEqualTo("cuenta-1");
            assertThat(resultado.getId()).isEqualTo("dir-1");
        }
    }

    @Test
    void partialUpdateAplicaSoloCamposNoNulos() {
        try (MockedStatic<SecurityUtils> security = mockStatic(SecurityUtils.class)) {
            security.when(() -> SecurityUtils.hasCurrentUserThisAuthority(AuthoritiesConstants.CLIENTE)).thenReturn(false);
            Direccion existente = new Direccion();
            existente.setId("dir-1");
            existente.setDireccion("Calle 10 #5-25");
            existente.setMunicipio("Bogota");
            DireccionDTO dto = new DireccionDTO();
            dto.setId("dir-1");
            dto.setMunicipio("Medellin");
            when(direccionRepository.findById("dir-1")).thenReturn(Optional.of(existente));
            when(direccionRepository.save(any(Direccion.class))).thenAnswer(invocation -> invocation.getArgument(0));

            Optional<DireccionDTO> resultado = service.partialUpdate(dto);

            assertThat(resultado).isPresent();
            assertThat(resultado.get().getMunicipio()).isEqualTo("Medellin");
            ArgumentCaptor<Direccion> captor = ArgumentCaptor.forClass(Direccion.class);
            verify(direccionRepository).save(captor.capture());
            assertThat(captor.getValue().getDireccion()).isEqualTo("Calle 10 #5-25");
            assertThat(captor.getValue().getMunicipio()).isEqualTo("Medellin");
        }
    }

    @Test
    void findAllComoClienteRetornaSoloSusDirecciones() {
        try (MockedStatic<SecurityUtils> security = mockStatic(SecurityUtils.class)) {
            mockCliente(security);
            Pageable pageable = PageRequest.of(0, 10);
            when(direccionRepository.findByCuentaId("cuenta-1", pageable)).thenReturn(
                new PageImpl<>(List.of(direccionConCuenta("dir-1", null), direccionConCuenta("dir-2", null)), pageable, 2)
            );

            Page<DireccionDTO> resultado = service.findAll(pageable);

            assertThat(resultado.getTotalElements()).isEqualTo(2);
            assertThat(resultado.getContent()).extracting(DireccionDTO::getId).containsExactly("dir-1", "dir-2");
        }
    }

    @Test
    void findAllComoClienteSinCuentaRetornaPaginaVacia() {
        try (MockedStatic<SecurityUtils> security = mockStatic(SecurityUtils.class)) {
            security.when(() -> SecurityUtils.hasCurrentUserThisAuthority(AuthoritiesConstants.CLIENTE)).thenReturn(true);
            security.when(SecurityUtils::getCurrentUserId).thenReturn(Optional.empty());
            Pageable pageable = PageRequest.of(0, 10);

            Page<DireccionDTO> resultado = service.findAll(pageable);

            assertThat(resultado.getTotalElements()).isZero();
            verify(cuentaRepository, never()).findOneByUserId(anyString());
            verify(direccionRepository, never()).findByCuentaId(anyString(), any(Pageable.class));
        }
    }

    @Test
    void findAllSinRolClienteRetornaTodasLasDirecciones() {
        try (MockedStatic<SecurityUtils> security = mockStatic(SecurityUtils.class)) {
            security.when(() -> SecurityUtils.hasCurrentUserThisAuthority(AuthoritiesConstants.CLIENTE)).thenReturn(false);
            Pageable pageable = PageRequest.of(0, 10);
            when(direccionRepository.findAll(pageable)).thenReturn(
                new PageImpl<>(List.of(direccionConCuenta("dir-1", null), direccionConCuenta("dir-2", null)), pageable, 2)
            );

            Page<DireccionDTO> resultado = service.findAll(pageable);

            assertThat(resultado.getTotalElements()).isEqualTo(2);
            verify(cuentaRepository, never()).findOneByUserId(anyString());
        }
    }

    @Test
    void findAllWherePedidoIsNullComoClienteRetornaSoloDireccionesDisponibles() {
        try (MockedStatic<SecurityUtils> security = mockStatic(SecurityUtils.class)) {
            mockCliente(security);
            when(direccionRepository.findByCuentaIdAndPedidoIsNull("cuenta-1")).thenReturn(List.of(direccionConCuenta("dir-1", null)));

            List<DireccionDTO> resultado = service.findAllWherePedidoIsNull();

            assertThat(resultado).extracting(DireccionDTO::getId).containsExactly("dir-1");
        }
    }

    @Test
    void findOneComoClienteRetornaSusDirecciones() {
        try (MockedStatic<SecurityUtils> security = mockStatic(SecurityUtils.class)) {
            mockCliente(security);
            when(direccionRepository.findByIdAndCuentaId("dir-1", "cuenta-1")).thenReturn(
                Optional.of(direccionConCuenta("dir-1", cuentaConId("cuenta-1")))
            );

            Optional<DireccionDTO> resultado = service.findOne("dir-1");

            assertThat(resultado).isPresent();
            assertThat(resultado.get().getId()).isEqualTo("dir-1");
        }
    }

    @Test
    void findOneComoClienteNoRetornaDireccionesAjenas() {
        try (MockedStatic<SecurityUtils> security = mockStatic(SecurityUtils.class)) {
            mockCliente(security);
            when(direccionRepository.findByIdAndCuentaId("dir-9", "cuenta-1")).thenReturn(Optional.empty());

            Optional<DireccionDTO> resultado = service.findOne("dir-9");

            assertThat(resultado).isEmpty();
            verify(direccionRepository, never()).findById(anyString());
        }
    }

    @Test
    void deleteEliminaLaDireccionPorId() {
        service.delete("dir-1");

        verify(direccionRepository).deleteById("dir-1");
    }

    @Test
    void setPredeterminadaComoClienteDesmarcaLaAnteriorYMarcaLaNueva() {
        try (MockedStatic<SecurityUtils> security = mockStatic(SecurityUtils.class)) {
            mockCliente(security);
            Cuenta cuenta = cuentaConId("cuenta-1");
            Direccion anterior = direccionConCuenta("dir-1", cuenta);
            anterior.setActivo(true);
            Direccion sinEstado = direccionConCuenta("dir-3", cuenta);
            Direccion nueva = direccionConCuenta("dir-2", cuenta);
            nueva.setActivo(false);
            when(direccionRepository.findById("dir-2")).thenReturn(Optional.of(nueva));
            when(direccionRepository.findByCuentaId("cuenta-1")).thenReturn(List.of(anterior, sinEstado, nueva));
            when(direccionRepository.save(any(Direccion.class))).thenAnswer(invocation -> invocation.getArgument(0));

            DireccionDTO resultado = service.setPredeterminada("dir-2");

            assertThat(resultado.getId()).isEqualTo("dir-2");
            assertThat(resultado.getActivo()).isTrue();
            ArgumentCaptor<Direccion> captor = ArgumentCaptor.forClass(Direccion.class);
            verify(direccionRepository, times(2)).save(captor.capture());
            assertThat(captor.getAllValues().get(0).getId()).isEqualTo("dir-1");
            assertThat(captor.getAllValues().get(0).getActivo()).isFalse();
            assertThat(captor.getAllValues().get(1).getId()).isEqualTo("dir-2");
            assertThat(captor.getAllValues().get(1).getActivo()).isTrue();
        }
    }

    @Test
    void setPredeterminadaComoAdminTambienDesmarcaLaAnterior() {
        try (MockedStatic<SecurityUtils> security = mockStatic(SecurityUtils.class)) {
            security.when(() -> SecurityUtils.hasCurrentUserThisAuthority(AuthoritiesConstants.CLIENTE)).thenReturn(false);
            Cuenta cuenta = cuentaConId("cuenta-1");
            Direccion anterior = direccionConCuenta("dir-1", cuenta);
            anterior.setActivo(true);
            Direccion nueva = direccionConCuenta("dir-2", cuenta);
            nueva.setActivo(false);
            when(direccionRepository.findById("dir-2")).thenReturn(Optional.of(nueva));
            when(direccionRepository.findByCuentaId("cuenta-1")).thenReturn(List.of(anterior, nueva));
            when(direccionRepository.save(any(Direccion.class))).thenAnswer(invocation -> invocation.getArgument(0));

            DireccionDTO resultado = service.setPredeterminada("dir-2");

            assertThat(resultado.getActivo()).isTrue();
            ArgumentCaptor<Direccion> captor = ArgumentCaptor.forClass(Direccion.class);
            verify(direccionRepository, times(2)).save(captor.capture());
            assertThat(captor.getAllValues().get(0).getActivo()).isFalse();
            assertThat(captor.getAllValues().get(1).getActivo()).isTrue();
            verify(cuentaRepository, never()).findOneByUserId(anyString());
        }
    }

    @Test
    void setPredeterminadaLanzaExcepcionCuandoLaDireccionNoExiste() {
        when(direccionRepository.findById("dir-x")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.setPredeterminada("dir-x"))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("Direccion not found");
        verify(direccionRepository, never()).save(any());
    }

    @Test
    void setPredeterminadaLanzaExcepcionCuandoLaDireccionEsDeOtraCuenta() {
        try (MockedStatic<SecurityUtils> security = mockStatic(SecurityUtils.class)) {
            mockCliente(security);
            Direccion ajena = direccionConCuenta("dir-2", cuentaConId("cuenta-otra"));
            ajena.setActivo(false);
            when(direccionRepository.findById("dir-2")).thenReturn(Optional.of(ajena));

            assertThatThrownBy(() -> service.setPredeterminada("dir-2"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("does not belong to the current account");
            verify(direccionRepository, never()).save(any());
            verify(direccionRepository, never()).findByCuentaId(anyString());
        }
    }

    @Test
    void setPredeterminadaLanzaExcepcionCuandoElClienteNoTieneCuenta() {
        try (MockedStatic<SecurityUtils> security = mockStatic(SecurityUtils.class)) {
            security.when(() -> SecurityUtils.hasCurrentUserThisAuthority(AuthoritiesConstants.CLIENTE)).thenReturn(true);
            security.when(SecurityUtils::getCurrentUserId).thenReturn(Optional.of("user-1"));
            when(cuentaRepository.findOneByUserId("user-1")).thenReturn(Optional.empty());
            when(direccionRepository.findById("dir-2")).thenReturn(Optional.of(direccionConCuenta("dir-2", cuentaConId("cuenta-1"))));

            assertThatThrownBy(() -> service.setPredeterminada("dir-2"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Current client account not found");
            verify(direccionRepository, never()).save(any());
        }
    }
}
