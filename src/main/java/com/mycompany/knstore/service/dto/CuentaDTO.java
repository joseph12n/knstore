package com.mycompany.knstore.service.dto;

import com.mycompany.knstore.domain.enumeration.Genero;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

/**
 * A DTO for the {@link com.mycompany.knstore.domain.Cuenta} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class CuentaDTO implements Serializable {

    private String id;

    @NotNull
    @Size(max = 20)
    @Pattern(regexp = "^[0-9]{1,20}$")
    private String numDocumento;

    @NotNull
    @Size(max = 50)
    @Pattern(regexp = "^[A-Za-zÁÉÍÓÚÜÑáéíóúüñ' .-]+$", message = "Solo se permiten letras")
    private String primerNombre;

    @NotNull
    @Size(max = 50)
    @Pattern(regexp = "^[A-Za-zÁÉÍÓÚÜÑáéíóúüñ' .-]+$", message = "Solo se permiten letras")
    private String segundoNombre;

    @NotNull
    @Size(max = 50)
    @Pattern(regexp = "^[A-Za-zÁÉÍÓÚÜÑáéíóúüñ' .-]+$", message = "Solo se permiten letras")
    private String primerApellido;

    @NotNull
    @Size(max = 50)
    @Pattern(regexp = "^[A-Za-zÁÉÍÓÚÜÑáéíóúüñ' .-]+$", message = "Solo se permiten letras")
    private String segundoApellido;

    @NotNull
    private Genero genero;

    @NotNull
    private LocalDate fechaNacimiento;

    @NotNull
    @Size(max = 15)
    @Pattern(regexp = "^[0-9]{7,15}$")
    private String celular;

    @NotNull
    @Size(max = 15)
    @Pattern(regexp = "^[0-9]{7,15}$")
    private String telefono;

    private byte[] fotoPerfil;

    private String fotoPerfilContentType;

    @NotNull
    private Boolean activo;

    private UserDTO user;

    private TipoDocumentoDTO tipoDocumento;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNumDocumento() {
        return numDocumento;
    }

    public void setNumDocumento(String numDocumento) {
        this.numDocumento = numDocumento;
    }

    public String getPrimerNombre() {
        return primerNombre;
    }

    public void setPrimerNombre(String primerNombre) {
        this.primerNombre = primerNombre;
    }

    public String getSegundoNombre() {
        return segundoNombre;
    }

    public void setSegundoNombre(String segundoNombre) {
        this.segundoNombre = segundoNombre;
    }

    public String getPrimerApellido() {
        return primerApellido;
    }

    public void setPrimerApellido(String primerApellido) {
        this.primerApellido = primerApellido;
    }

    public String getSegundoApellido() {
        return segundoApellido;
    }

    public void setSegundoApellido(String segundoApellido) {
        this.segundoApellido = segundoApellido;
    }

    public Genero getGenero() {
        return genero;
    }

    public void setGenero(Genero genero) {
        this.genero = genero;
    }

    public LocalDate getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(LocalDate fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public String getCelular() {
        return celular;
    }

    public void setCelular(String celular) {
        this.celular = celular;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public byte[] getFotoPerfil() {
        return fotoPerfil;
    }

    public void setFotoPerfil(byte[] fotoPerfil) {
        this.fotoPerfil = fotoPerfil;
    }

    public String getFotoPerfilContentType() {
        return fotoPerfilContentType;
    }

    public void setFotoPerfilContentType(String fotoPerfilContentType) {
        this.fotoPerfilContentType = fotoPerfilContentType;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    public UserDTO getUser() {
        return user;
    }

    public void setUser(UserDTO user) {
        this.user = user;
    }

    public TipoDocumentoDTO getTipoDocumento() {
        return tipoDocumento;
    }

    public void setTipoDocumento(TipoDocumentoDTO tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }

    @AssertTrue(message = "La fecha de nacimiento no puede ser futura ni indicar más de 100 años")
    public boolean isFechaNacimientoValida() {
        return (
            fechaNacimiento == null ||
            (!fechaNacimiento.isAfter(LocalDate.now()) && !fechaNacimiento.isBefore(LocalDate.now().minusYears(100)))
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CuentaDTO)) {
            return false;
        }

        CuentaDTO cuentaDTO = (CuentaDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, cuentaDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CuentaDTO{" +
            "id='" + getId() + "'" +
            ", numDocumento='" + getNumDocumento() + "'" +
            ", primerNombre='" + getPrimerNombre() + "'" +
            ", segundoNombre='" + getSegundoNombre() + "'" +
            ", primerApellido='" + getPrimerApellido() + "'" +
            ", segundoApellido='" + getSegundoApellido() + "'" +
            ", genero='" + getGenero() + "'" +
            ", fechaNacimiento='" + getFechaNacimiento() + "'" +
            ", celular='" + getCelular() + "'" +
            ", telefono='" + getTelefono() + "'" +
            ", fotoPerfil='" + getFotoPerfil() + "'" +
            ", activo='" + getActivo() + "'" +
            ", user=" + getUser() +
            ", tipoDocumento=" + getTipoDocumento() +
            "}";
    }
}
