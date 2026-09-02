package com.mycompany.knstore.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.within;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.mycompany.knstore.config.Constants;
import com.mycompany.knstore.domain.Authority;
import com.mycompany.knstore.domain.User;
import com.mycompany.knstore.repository.AuthorityRepository;
import com.mycompany.knstore.repository.UserRepository;
import com.mycompany.knstore.security.AuthoritiesConstants;
import com.mycompany.knstore.service.dto.AdminUserDTO;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthorityRepository authorityRepository;

    private UserService service;

    @BeforeEach
    void setUp() {
        service = new UserService(userRepository, passwordEncoder, authorityRepository);
    }

    @AfterEach
    void limpiarSeguridad() {
        SecurityContextHolder.clearContext();
    }

    private Authority authority(String nombre) {
        Authority authority = new Authority();
        authority.setName(nombre);
        return authority;
    }

    private void autenticarComo(String login) {
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(login, "n/a"));
    }

    private AdminUserDTO dtoRegistro() {
        AdminUserDTO dto = new AdminUserDTO();
        dto.setLogin("Joseph");
        dto.setFirstName("Joseph");
        dto.setLastName("Perez");
        dto.setEmail("Joseph@KnStore.com");
        dto.setLangKey("es");
        return dto;
    }

    @Test
    void registerUserGuardaUsuarioDesactivadoConRolesUserYClienteYPasswordCodificada() {
        when(userRepository.findOneByLogin("joseph")).thenReturn(Optional.empty());
        when(userRepository.findOneByEmailIgnoreCase("Joseph@KnStore.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("Password123!")).thenReturn("password-codificada");
        when(authorityRepository.findById(AuthoritiesConstants.USER)).thenReturn(Optional.of(authority(AuthoritiesConstants.USER)));
        when(authorityRepository.findById(AuthoritiesConstants.CLIENTE)).thenReturn(Optional.of(authority(AuthoritiesConstants.CLIENTE)));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User result = service.registerUser(dtoRegistro(), "Password123!");

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        assertThat(userCaptor.getValue()).isSameAs(result);
        assertThat(result.getLogin()).isEqualTo("joseph");
        assertThat(result.getEmail()).isEqualTo("joseph@knstore.com");
        assertThat(result.getPassword()).isEqualTo("password-codificada");
        assertThat(result.isActivated()).isFalse();
        assertThat(result.getActivationKey()).isNotBlank();
        assertThat(result.getAuthorities())
            .extracting(Authority::getName)
            .containsExactlyInAnyOrder(AuthoritiesConstants.USER, AuthoritiesConstants.CLIENTE);
    }

    @Test
    void registerUserLanzaUsernameAlreadyUsedExceptionSiElLoginYaExisteYEstaActivado() {
        User existente = new User();
        existente.setLogin("joseph");
        existente.setActivated(true);
        when(userRepository.findOneByLogin("joseph")).thenReturn(Optional.of(existente));

        assertThatThrownBy(() -> service.registerUser(dtoRegistro(), "Password123!")).isInstanceOf(UsernameAlreadyUsedException.class);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void registerUserEliminaElUsuarioNoActivadoConMismoLoginYPermiteElRegistro() {
        User noActivado = new User();
        noActivado.setLogin("joseph");
        when(userRepository.findOneByLogin("joseph")).thenReturn(Optional.of(noActivado));
        when(userRepository.findOneByEmailIgnoreCase("Joseph@KnStore.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("Password123!")).thenReturn("password-codificada");
        when(authorityRepository.findById(AuthoritiesConstants.USER)).thenReturn(Optional.of(authority(AuthoritiesConstants.USER)));
        when(authorityRepository.findById(AuthoritiesConstants.CLIENTE)).thenReturn(Optional.of(authority(AuthoritiesConstants.CLIENTE)));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User result = service.registerUser(dtoRegistro(), "Password123!");

        verify(userRepository).delete(noActivado);
        assertThat(result.getLogin()).isEqualTo("joseph");
        assertThat(result.isActivated()).isFalse();
    }

    @Test
    void registerUserLanzaEmailAlreadyUsedExceptionSiElEmailYaExisteYEstaActivado() {
        User existente = new User();
        existente.setLogin("otro");
        existente.setEmail("joseph@knstore.com");
        existente.setActivated(true);
        when(userRepository.findOneByLogin("joseph")).thenReturn(Optional.empty());
        when(userRepository.findOneByEmailIgnoreCase("Joseph@KnStore.com")).thenReturn(Optional.of(existente));

        assertThatThrownBy(() -> service.registerUser(dtoRegistro(), "Password123!")).isInstanceOf(EmailAlreadyUsedException.class);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void registerUserLanzaUsernameAlreadyUsedExceptionSiLaLlaveDuplicadaEsDeLogin() {
        when(userRepository.findOneByLogin("joseph")).thenReturn(Optional.empty());
        when(userRepository.findOneByEmailIgnoreCase("Joseph@KnStore.com")).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenThrow(
            new DuplicateKeyException("E11000 duplicate key collection unique_user_login")
        );

        assertThatThrownBy(() -> service.registerUser(dtoRegistro(), "Password123!")).isInstanceOf(UsernameAlreadyUsedException.class);
    }

    @Test
    void registerUserLanzaEmailAlreadyUsedExceptionSiLaLlaveDuplicadaEsDeOtraRestriccion() {
        when(userRepository.findOneByLogin("joseph")).thenReturn(Optional.empty());
        when(userRepository.findOneByEmailIgnoreCase("Joseph@KnStore.com")).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenThrow(
            new DuplicateKeyException("E11000 duplicate key collection unique_user_email")
        );

        assertThatThrownBy(() -> service.registerUser(dtoRegistro(), "Password123!")).isInstanceOf(EmailAlreadyUsedException.class);
    }

    @Test
    void activateRegistrationActivaElUsuarioYEliminaLaClave() {
        User usuario = new User();
        usuario.setLogin("joseph");
        usuario.setActivationKey("clave-123");
        when(userRepository.findOneByActivationKey("clave-123")).thenReturn(Optional.of(usuario));

        Optional<User> result = service.activateRegistration("clave-123");

        assertThat(result).isPresent();
        assertThat(result.get().isActivated()).isTrue();
        assertThat(result.get().getActivationKey()).isNull();
        verify(userRepository).save(usuario);
    }

    @Test
    void activateRegistrationRetornaVacioSiLaClaveNoExiste() {
        when(userRepository.findOneByActivationKey("clave-invalida")).thenReturn(Optional.empty());

        Optional<User> result = service.activateRegistration("clave-invalida");

        assertThat(result).isEmpty();
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void completePasswordResetCodificaLaNuevaContrasenaYLimpiaLosDatosDeReset() {
        User usuario = new User();
        usuario.setLogin("joseph");
        usuario.setResetKey("reset-123");
        usuario.setResetDate(Instant.now());
        usuario.setPassword("vieja-codificada");
        when(userRepository.findOneByResetKey("reset-123")).thenReturn(Optional.of(usuario));
        when(passwordEncoder.encode("Nueva123!")).thenReturn("nueva-codificada");

        Optional<User> result = service.completePasswordReset("Nueva123!", "reset-123");

        assertThat(result).isPresent();
        assertThat(result.get().getPassword()).isEqualTo("nueva-codificada");
        assertThat(result.get().getResetKey()).isNull();
        assertThat(result.get().getResetDate()).isNull();
        verify(userRepository).save(usuario);
    }

    @Test
    void completePasswordResetRetornaVacioSiLaClaveExpioHaceMasDeUnDia() {
        User usuario = new User();
        usuario.setLogin("joseph");
        usuario.setResetKey("reset-123");
        usuario.setResetDate(Instant.now().minus(2, ChronoUnit.DAYS));
        when(userRepository.findOneByResetKey("reset-123")).thenReturn(Optional.of(usuario));

        Optional<User> result = service.completePasswordReset("Nueva123!", "reset-123");

        assertThat(result).isEmpty();
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void requestPasswordResetGeneraClaveYFechaParaUsuarioActivado() {
        User usuario = new User();
        usuario.setLogin("joseph");
        usuario.setEmail("joseph@knstore.com");
        usuario.setActivated(true);
        when(userRepository.findOneByEmailIgnoreCase("joseph@knstore.com")).thenReturn(Optional.of(usuario));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Optional<User> result = service.requestPasswordReset("joseph@knstore.com");

        assertThat(result).isPresent();
        assertThat(result.get().getResetKey()).isNotBlank();
        assertThat(result.get().getResetDate()).isNotNull();
    }

    @Test
    void requestPasswordResetRetornaVacioSiElUsuarioNoEstaActivado() {
        User usuario = new User();
        usuario.setLogin("joseph");
        usuario.setEmail("joseph@knstore.com");
        when(userRepository.findOneByEmailIgnoreCase("joseph@knstore.com")).thenReturn(Optional.of(usuario));

        Optional<User> result = service.requestPasswordReset("joseph@knstore.com");

        assertThat(result).isEmpty();
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void deleteUserEliminaElUsuarioExistente() {
        User usuario = new User();
        usuario.setLogin("joseph");
        when(userRepository.findOneByLogin("joseph")).thenReturn(Optional.of(usuario));

        service.deleteUser("joseph");

        verify(userRepository).delete(usuario);
    }

    @Test
    void deleteUserNoHaceNadaSiElLoginNoExiste() {
        when(userRepository.findOneByLogin("fantasma")).thenReturn(Optional.empty());

        service.deleteUser("fantasma");

        verify(userRepository, never()).delete(any(User.class));
    }

    @Test
    void createUserAsignaLangKeyPorDefectoGeneraContrasenaYActivaElUsuario() {
        AdminUserDTO dto = new AdminUserDTO();
        dto.setLogin("Manager");
        dto.setEmail("Manager@KnStore.com");
        when(passwordEncoder.encode(anyString())).thenReturn("generada-codificada");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User result = service.createUser(dto, null);

        assertThat(result.getLogin()).isEqualTo("manager");
        assertThat(result.getEmail()).isEqualTo("manager@knstore.com");
        assertThat(result.getPassword()).isEqualTo("generada-codificada");
        assertThat(result.isActivated()).isTrue();
        assertThat(result.getLangKey()).isEqualTo(Constants.DEFAULT_LANGUAGE);
        assertThat(result.getResetKey()).isNotBlank();
        assertThat(result.getResetDate()).isNotNull();
        assertThat(result.getAuthorities()).isEmpty();
    }

    @Test
    void updateUserAdminActualizaDatosYReemplazaAuthorities() {
        User usuario = new User();
        usuario.setId("user-1");
        usuario.setLogin("viejo");
        usuario.setAuthorities(new HashSet<>(Set.of(authority(AuthoritiesConstants.USER))));
        when(userRepository.findById("user-1")).thenReturn(Optional.of(usuario));
        when(authorityRepository.findById(AuthoritiesConstants.ADMIN)).thenReturn(Optional.of(authority(AuthoritiesConstants.ADMIN)));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        AdminUserDTO dto = new AdminUserDTO();
        dto.setId("user-1");
        dto.setLogin("Nuevo.Login");
        dto.setFirstName("Nuevo");
        dto.setLastName("Perez");
        dto.setEmail("Nuevo@KnStore.com");
        dto.setActivated(true);
        dto.setLangKey("es");
        dto.setAuthorities(Set.of(AuthoritiesConstants.ADMIN));

        Optional<AdminUserDTO> result = service.updateUser(dto);

        assertThat(result).isPresent();
        assertThat(result.get().getLogin()).isEqualTo("nuevo.login");
        assertThat(result.get().getEmail()).isEqualTo("nuevo@knstore.com");
        assertThat(result.get().isActivated()).isTrue();
        assertThat(result.get().getAuthorities()).containsExactly(AuthoritiesConstants.ADMIN);
        assertThat(usuario.getAuthorities()).extracting(Authority::getName).containsExactly(AuthoritiesConstants.ADMIN);
        verify(userRepository).save(usuario);
    }

    @Test
    void updateUserAdminRetornaVacioSiElUsuarioNoExiste() {
        AdminUserDTO dto = new AdminUserDTO();
        dto.setId("user-404");
        when(userRepository.findById("user-404")).thenReturn(Optional.empty());

        Optional<AdminUserDTO> result = service.updateUser(dto);

        assertThat(result).isEmpty();
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void updateUserActualizaLosDatosDelUsuarioAutenticado() {
        autenticarComo("joseph");
        User usuario = new User();
        usuario.setLogin("joseph");
        when(userRepository.findOneByLogin("joseph")).thenReturn(Optional.of(usuario));

        service.updateUser("Nuevo", "Perez", "Nuevo@KnStore.com", "es", "https://imagen");

        assertThat(usuario.getFirstName()).isEqualTo("Nuevo");
        assertThat(usuario.getLastName()).isEqualTo("Perez");
        assertThat(usuario.getEmail()).isEqualTo("nuevo@knstore.com");
        assertThat(usuario.getLangKey()).isEqualTo("es");
        assertThat(usuario.getImageUrl()).isEqualTo("https://imagen");
        verify(userRepository).save(usuario);
    }

    @Test
    void changePasswordCodificaLaNuevaContrasena() {
        autenticarComo("joseph");
        User usuario = new User();
        usuario.setLogin("joseph");
        usuario.setPassword("actual-codificada");
        when(userRepository.findOneByLogin("joseph")).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("Actual123!", "actual-codificada")).thenReturn(true);
        when(passwordEncoder.encode("Nueva123!")).thenReturn("nueva-codificada");

        service.changePassword("Actual123!", "Nueva123!");

        assertThat(usuario.getPassword()).isEqualTo("nueva-codificada");
        verify(userRepository).save(usuario);
    }

    @Test
    void changePasswordLanzaInvalidPasswordExceptionConContrasenaActualIncorrecta() {
        autenticarComo("joseph");
        User usuario = new User();
        usuario.setLogin("joseph");
        usuario.setPassword("actual-codificada");
        when(userRepository.findOneByLogin("joseph")).thenReturn(Optional.of(usuario));

        assertThatThrownBy(() -> service.changePassword("Incorrecta123!", "Nueva123!")).isInstanceOf(InvalidPasswordException.class);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void removeNotActivatedUsersEliminaLosUsuariosNoActivadosAntiguos() {
        User viejoUno = new User();
        viejoUno.setLogin("viejo-1");
        User viejoDos = new User();
        viejoDos.setLogin("viejo-2");
        when(userRepository.findAllByActivatedIsFalseAndActivationKeyIsNotNullAndCreatedDateBefore(any(Instant.class))).thenReturn(
            List.of(viejoUno, viejoDos)
        );

        service.removeNotActivatedUsers();

        ArgumentCaptor<Instant> corteCaptor = ArgumentCaptor.forClass(Instant.class);
        verify(userRepository).findAllByActivatedIsFalseAndActivationKeyIsNotNullAndCreatedDateBefore(corteCaptor.capture());
        assertThat(corteCaptor.getValue()).isCloseTo(Instant.now().minus(3, ChronoUnit.DAYS), within(1, ChronoUnit.MINUTES));
        verify(userRepository).delete(viejoUno);
        verify(userRepository).delete(viejoDos);
    }

    @Test
    void getAuthoritiesRetornaLosNombresDeTodasLasAuthorities() {
        when(authorityRepository.findAll()).thenReturn(
            List.of(authority(AuthoritiesConstants.USER), authority(AuthoritiesConstants.ADMIN))
        );

        List<String> result = service.getAuthorities();

        assertThat(result).containsExactlyInAnyOrder(AuthoritiesConstants.USER, AuthoritiesConstants.ADMIN);
    }
}
