package com.mycompany.knstore.service.dto;

import com.mycompany.knstore.domain.enumeration.Genero;
import com.mycompany.knstore.domain.enumeration.TipoPersona;
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

    @Size(max = 20)
    private String numDocumento;

    @NotNull
    private TipoPersona tipoPersona;

    @NotNull
    @Size(max = 50)
    private String primerNombre;

    @Size(max = 50)
    private String segundoNombre;

    @NotNull
    @Size(max = 50)
    private String primerApellido;

    @Size(max = 50)
    private String segundoApellido;

    @Size(max = 15)
    private String celular;

    @Size(max = 15)
    private String telefono;

    private LocalDate fechaNacimiento;

    private Genero genero;

    private byte[] fotoPerfil;

    private String fotoPerfilContentType;

    @NotNull
    private Boolean activo;

    @NotNull
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

    public TipoPersona getTipoPersona() {
        return tipoPersona;
    }

    public void setTipoPersona(TipoPersona tipoPersona) {
        this.tipoPersona = tipoPersona;
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

    public LocalDate getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(LocalDate fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public Genero getGenero() {
        return genero;
    }

    public void setGenero(Genero genero) {
        this.genero = genero;
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
            ", tipoPersona='" + getTipoPersona() + "'" +
            ", primerNombre='" + getPrimerNombre() + "'" +
            ", segundoNombre='" + getSegundoNombre() + "'" +
            ", primerApellido='" + getPrimerApellido() + "'" +
            ", segundoApellido='" + getSegundoApellido() + "'" +
            ", celular='" + getCelular() + "'" +
            ", telefono='" + getTelefono() + "'" +
            ", fechaNacimiento='" + getFechaNacimiento() + "'" +
            ", genero='" + getGenero() + "'" +
            ", fotoPerfil='" + getFotoPerfil() + "'" +
            ", activo='" + getActivo() + "'" +
            ", user=" + getUser() +
            ", tipoDocumento=" + getTipoDocumento() +
            "}";
    }
}
