package com.mycompany.knstore.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.mycompany.knstore.domain.Direccion} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class DireccionDTO implements Serializable {

    private String id;

    @NotNull
    @Size(max = 100)
    @Pattern(regexp = ".*[A-Za-zÁÉÍÓÚÜÑáéíóúüñ].*", message = "La dirección debe contener al menos una letra")
    private String direccion;

    @Size(max = 100)
    @Pattern(regexp = "^$|^[A-Za-zÁÉÍÓÚÜÑáéíóúüñ' .-]+$", message = "Solo se permiten letras")
    private String barrio;

    @Size(max = 100)
    @Pattern(regexp = "^$|^[A-Za-zÁÉÍÓÚÜÑáéíóúüñ' .-]+$", message = "Solo se permiten letras")
    private String localidad;

    @NotNull
    @Size(max = 100)
    @Pattern(regexp = "^[A-Za-zÁÉÍÓÚÜÑáéíóúüñ' .-]+$", message = "Solo se permiten letras")
    private String municipio;

    @NotNull
    @Size(max = 100)
    @Pattern(regexp = "^[A-Za-zÁÉÍÓÚÜÑáéíóúüñ' .-]+$", message = "Solo se permiten letras")
    private String departamento;

    @NotNull
    private Boolean activo;

    @NotNull
    @Size(max = 100)
    @Pattern(regexp = "^[0-9]{7,15}$")
    private String telefonoContacto;

    @NotNull
    @Size(max = 100)
    @Pattern(regexp = "^[A-Za-zÁÉÍÓÚÜÑáéíóúüñ' .-]+$", message = "Solo se permiten letras")
    private String destinatario;

    @NotNull
    @Size(max = 20)
    @Pattern(regexp = "^[0-9]{1,20}$")
    private String codigoPostal;

    @NotNull
    private CuentaDTO cuenta;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getBarrio() {
        return barrio;
    }

    public void setBarrio(String barrio) {
        this.barrio = barrio;
    }

    public String getLocalidad() {
        return localidad;
    }

    public void setLocalidad(String localidad) {
        this.localidad = localidad;
    }

    public String getMunicipio() {
        return municipio;
    }

    public void setMunicipio(String municipio) {
        this.municipio = municipio;
    }

    public String getDepartamento() {
        return departamento;
    }

    public void setDepartamento(String departamento) {
        this.departamento = departamento;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    public String getTelefonoContacto() {
        return telefonoContacto;
    }

    public void setTelefonoContacto(String telefonoContacto) {
        this.telefonoContacto = telefonoContacto;
    }

    public String getDestinatario() {
        return destinatario;
    }

    public void setDestinatario(String destinatario) {
        this.destinatario = destinatario;
    }

    public String getCodigoPostal() {
        return codigoPostal;
    }

    public void setCodigoPostal(String codigoPostal) {
        this.codigoPostal = codigoPostal;
    }

    public CuentaDTO getCuenta() {
        return cuenta;
    }

    public void setCuenta(CuentaDTO cuenta) {
        this.cuenta = cuenta;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DireccionDTO)) {
            return false;
        }

        DireccionDTO direccionDTO = (DireccionDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, direccionDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "DireccionDTO{" +
            "id='" + getId() + "'" +
            ", direccion='" + getDireccion() + "'" +
            ", barrio='" + getBarrio() + "'" +
            ", localidad='" + getLocalidad() + "'" +
            ", municipio='" + getMunicipio() + "'" +
            ", departamento='" + getDepartamento() + "'" +
            ", activo='" + getActivo() + "'" +
            ", telefonoContacto='" + getTelefonoContacto() + "'" +
            ", destinatario='" + getDestinatario() + "'" +
            ", codigoPostal='" + getCodigoPostal() + "'" +
            ", cuenta=" + getCuenta() +
            "}";
    }
}
