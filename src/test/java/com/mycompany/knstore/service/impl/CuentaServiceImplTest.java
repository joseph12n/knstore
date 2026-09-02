package com.mycompany.knstore.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.mycompany.knstore.domain.Cuenta;
import com.mycompany.knstore.domain.TipoDocumento;
import com.mycompany.knstore.domain.User;
import com.mycompany.knstore.domain.enumeration.Genero;
import com.mycompany.knstore.repository.CuentaRepository;
import com.mycompany.knstore.security.AuthoritiesConstants;
import com.mycompany.knstore.security.SecurityUtils;
import com.mycompany.knstore.service.dto.CuentaDTO;
import com.mycompany.knstore.service.dto.TipoDocumentoDTO;
import com.mycompany.knstore.service.mapper.CuentaMapper;
import com.mycompany.knstore.service.mapper.CuentaMapperImpl;
import java.time.LocalDate;
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
class CuentaServiceImplTest {

    private final CuentaMapper cuentaMapper = new CuentaMapperImpl();

    @Mock
    private CuentaRepository cuentaRepository;

    private CuentaServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new CuentaServiceImpl(cuentaRepository, cuentaMapper);
    }

    private CuentaDTO cuentaDTOCompleta() {
        CuentaDTO dto = new CuentaDTO();
        dto.setNumDocumento("1023456789");
        dto.setPrimerNombre("Joseph");
        dto.setSegundoNombre("Alejandro");
        dto.setPrimerApellido("Perez");
        dto.setSegundoApellido("Gomez");
        dto.setGenero(Genero.MASCULINO);
        dto.setFechaNacimiento(LocalDate.of(1995, 5, 12));
        dto.setCelular("3001234567");
        dto.setTelefono("6011234567");
        dto.setActivo(true);
        TipoDocumentoDTO tipoDocumento = new TipoDocumentoDTO();
        tipoDocumento.setId("td-1");
        tipoDocumento.setSigla("CC");
        dto.setTipoDocumento(tipoDocumento);
        return dto;
    }

    private Cuenta cuentaConRelaciones() {
        User user = new User();
        user.setId("user-1");
        user.setLogin("joseph");
        TipoDocumento tipoDocumento = new TipoDocumento();
        tipoDocumento.setId("td-1");
        tipoDocumento.setSigla("CC");
        Cuenta cuenta = new Cuenta();
        cuenta.setId("cuenta-1");
        cuenta.setUser(user);
        cuenta.setTipoDocumento(tipoDocumento);
        return cuenta;
    }

    @Test
    void savePersisteLaCuentaYRetornaElDTOMapeado() {
        when(cuentaRepository.save(any(Cuenta.class))).thenAnswer(invocation -> {
            Cuenta cuenta = invocation.getArgument(0);
            cuenta.setId("cuenta-1");
            return cuenta;
        });

        CuentaDTO resultado = service.save(cuentaDTOCompleta());

        assertThat(resultado.getId()).isEqualTo("cuenta-1");
        assertThat(resultado.getNumDocumento()).isEqualTo("1023456789");
        assertThat(resultado.getPrimerNombre()).isEqualTo("Joseph");
        assertThat(resultado.getTipoDocumento().getSigla()).isEqualTo("CC");
    }

    @Test
    void updatePreservaUserYTipoDocumentoCuandoNoLleganEnElDTO() {
        CuentaDTO dto = new CuentaDTO();
        dto.setId("cuenta-1");
        dto.setPrimerNombre("Nuevo");
        Cuenta existente = cuentaConRelaciones();
        when(cuentaRepository.findById("cuenta-1")).thenReturn(Optional.of(existente));
        when(cuentaRepository.save(any(Cuenta.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CuentaDTO resultado = service.update(dto);

        ArgumentCaptor<Cuenta> captor = ArgumentCaptor.forClass(Cuenta.class);
        verify(cuentaRepository).save(captor.capture());
        assertThat(captor.getValue().getUser()).isSameAs(existente.getUser());
        assertThat(captor.getValue().getTipoDocumento()).isSameAs(existente.getTipoDocumento());
        assertThat(captor.getValue().getPrimerNombre()).isEqualTo("Nuevo");
        assertThat(resultado.getUser().getLogin()).isEqualTo("joseph");
        assertThat(resultado.getTipoDocumento().getSigla()).isEqualTo("CC");
    }

    @Test
    void updateSinCuentaPreviaGuardaElDTOSinRelaciones() {
        CuentaDTO dto = new CuentaDTO();
        dto.setId("cuenta-1");
        dto.setPrimerNombre("Temporal");
        when(cuentaRepository.findById("cuenta-1")).thenReturn(Optional.empty());
        when(cuentaRepository.save(any(Cuenta.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CuentaDTO resultado = service.update(dto);

        assertThat(resultado.getId()).isEqualTo("cuenta-1");
        assertThat(resultado.getPrimerNombre()).isEqualTo("Temporal");
        assertThat(resultado.getUser()).isNull();
        assertThat(resultado.getTipoDocumento()).isNull();
    }

    @Test
    void partialUpdateAplicaSoloCamposNoNulos() {
        Cuenta existente = new Cuenta();
        existente.setId("cuenta-1");
        existente.setNumDocumento("123456789");
        existente.setPrimerNombre("Jose");
        CuentaDTO dto = new CuentaDTO();
        dto.setId("cuenta-1");
        dto.setPrimerNombre("Carlos");
        when(cuentaRepository.findById("cuenta-1")).thenReturn(Optional.of(existente));
        when(cuentaRepository.save(any(Cuenta.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Optional<CuentaDTO> resultado = service.partialUpdate(dto);

        assertThat(resultado).isPresent();
        assertThat(resultado.get().getPrimerNombre()).isEqualTo("Carlos");
        ArgumentCaptor<Cuenta> captor = ArgumentCaptor.forClass(Cuenta.class);
        verify(cuentaRepository).save(captor.capture());
        assertThat(captor.getValue().getNumDocumento()).isEqualTo("123456789");
    }

    @Test
    void findAllComoClienteRetornaSoloSuCuentaPaginada() {
        try (MockedStatic<SecurityUtils> security = mockStatic(SecurityUtils.class)) {
            security.when(() -> SecurityUtils.hasCurrentUserThisAuthority(AuthoritiesConstants.CLIENTE)).thenReturn(true);
            security.when(SecurityUtils::getCurrentUserId).thenReturn(Optional.of("user-1"));
            Pageable pageable = PageRequest.of(0, 10);
            Cuenta cuenta = cuentaConRelaciones();
            when(cuentaRepository.findByUserId("user-1", pageable)).thenReturn(new PageImpl<>(List.of(cuenta), pageable, 1));

            Page<CuentaDTO> resultado = service.findAll(pageable);

            assertThat(resultado.getTotalElements()).isEqualTo(1);
            assertThat(resultado.getContent().get(0).getId()).isEqualTo("cuenta-1");
        }
    }

    @Test
    void findAllComoClienteSinUserIdRetornaPaginaVacia() {
        try (MockedStatic<SecurityUtils> security = mockStatic(SecurityUtils.class)) {
            security.when(() -> SecurityUtils.hasCurrentUserThisAuthority(AuthoritiesConstants.CLIENTE)).thenReturn(true);
            security.when(SecurityUtils::getCurrentUserId).thenReturn(Optional.empty());
            Pageable pageable = PageRequest.of(0, 10);

            Page<CuentaDTO> resultado = service.findAll(pageable);

            assertThat(resultado.getTotalElements()).isZero();
            verify(cuentaRepository, never()).findByUserId(anyString(), any(Pageable.class));
        }
    }

    @Test
    void findAllSinRolClienteRetornaTodasLasCuentas() {
        try (MockedStatic<SecurityUtils> security = mockStatic(SecurityUtils.class)) {
            security.when(() -> SecurityUtils.hasCurrentUserThisAuthority(AuthoritiesConstants.CLIENTE)).thenReturn(false);
            Pageable pageable = PageRequest.of(0, 10);
            when(cuentaRepository.findAll(pageable)).thenReturn(new PageImpl<>(List.of(cuentaConRelaciones(), new Cuenta()), pageable, 2));

            Page<CuentaDTO> resultado = service.findAll(pageable);

            assertThat(resultado.getTotalElements()).isEqualTo(2);
            verify(cuentaRepository, never()).findByUserId(anyString(), any(Pageable.class));
        }
    }

    @Test
    void findAllWithEagerRelationshipsSinRolClienteUsaLaConsultaEager() {
        try (MockedStatic<SecurityUtils> security = mockStatic(SecurityUtils.class)) {
            security.when(() -> SecurityUtils.hasCurrentUserThisAuthority(AuthoritiesConstants.CLIENTE)).thenReturn(false);
            Pageable pageable = PageRequest.of(0, 10);
            when(cuentaRepository.findAllWithEagerRelationships(pageable)).thenReturn(
                new PageImpl<>(List.of(cuentaConRelaciones()), pageable, 1)
            );

            Page<CuentaDTO> resultado = service.findAllWithEagerRelationships(pageable);

            assertThat(resultado.getTotalElements()).isEqualTo(1);
            assertThat(resultado.getContent().get(0).getUser().getLogin()).isEqualTo("joseph");
        }
    }

    @Test
    void findOneComoClienteSoloRetornaSuPropiaCuenta() {
        try (MockedStatic<SecurityUtils> security = mockStatic(SecurityUtils.class)) {
            security.when(() -> SecurityUtils.hasCurrentUserThisAuthority(AuthoritiesConstants.CLIENTE)).thenReturn(true);
            security.when(SecurityUtils::getCurrentUserId).thenReturn(Optional.of("user-1"));
            when(cuentaRepository.findByIdAndUserId("cuenta-1", "user-1")).thenReturn(Optional.of(cuentaConRelaciones()));

            Optional<CuentaDTO> resultado = service.findOne("cuenta-1");

            assertThat(resultado).isPresent();
            assertThat(resultado.get().getId()).isEqualTo("cuenta-1");
        }
    }

    @Test
    void findOneComoClienteNoRetornaCuentasAjenas() {
        try (MockedStatic<SecurityUtils> security = mockStatic(SecurityUtils.class)) {
            security.when(() -> SecurityUtils.hasCurrentUserThisAuthority(AuthoritiesConstants.CLIENTE)).thenReturn(true);
            security.when(SecurityUtils::getCurrentUserId).thenReturn(Optional.of("user-1"));
            when(cuentaRepository.findByIdAndUserId("cuenta-9", "user-1")).thenReturn(Optional.empty());

            Optional<CuentaDTO> resultado = service.findOne("cuenta-9");

            assertThat(resultado).isEmpty();
            verify(cuentaRepository, never()).findOneWithEagerRelationships(anyString());
        }
    }

    @Test
    void findOneSinRolClienteUsaLaConsultaEager() {
        try (MockedStatic<SecurityUtils> security = mockStatic(SecurityUtils.class)) {
            security.when(() -> SecurityUtils.hasCurrentUserThisAuthority(AuthoritiesConstants.CLIENTE)).thenReturn(false);
            when(cuentaRepository.findOneWithEagerRelationships("cuenta-1")).thenReturn(Optional.of(cuentaConRelaciones()));

            Optional<CuentaDTO> resultado = service.findOne("cuenta-1");

            assertThat(resultado).isPresent();
            assertThat(resultado.get().getUser().getLogin()).isEqualTo("joseph");
        }
    }

    @Test
    void deleteEliminaLaCuentaPorId() {
        service.delete("cuenta-1");

        verify(cuentaRepository).deleteById("cuenta-1");
    }
}
