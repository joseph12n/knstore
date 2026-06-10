package com.mycompany.knstore.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * A Direccion.
 */
@Document(collection = "direccion")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Direccion implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    @NotNull
    @Size(max = 100)
    @Field("direccion")
    private String direccion;

    @Size(max = 100)
    @Field("barrio")
    private String barrio;

    @Size(max = 100)
    @Field("localidad")
    private String localidad;

    @NotNull
    @Size(max = 100)
    @Field("departamento")
    private String departamento;

    @NotNull
    @Size(max = 100)
    @Field("municipio")
    private String municipio;

    @NotNull
    @Field("activo")
    private Boolean activo;

    @DBRef
    @Field("cuenta")
    @JsonIgnoreProperties(value = { "user", "tipoDocumento" }, allowSetters = true)
    private Cuenta cuenta;

    @DBRef
    private Pedido pedido;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public String getId() {
        return this.id;
    }

    public Direccion id(String id) {
        this.setId(id);
        return this;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDireccion() {
        return this.direccion;
    }

    public Direccion direccion(String direccion) {
        this.setDireccion(direccion);
        return this;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getBarrio() {
        return this.barrio;
    }

    public Direccion barrio(String barrio) {
        this.setBarrio(barrio);
        return this;
    }

    public void setBarrio(String barrio) {
        this.barrio = barrio;
    }

    public String getLocalidad() {
        return this.localidad;
    }

    public Direccion localidad(String localidad) {
        this.setLocalidad(localidad);
        return this;
    }

    public void setLocalidad(String localidad) {
        this.localidad = localidad;
    }

    public String getDepartamento() {
        return this.departamento;
    }

    public Direccion departamento(String departamento) {
        this.setDepartamento(departamento);
        return this;
    }

    public void setDepartamento(String departamento) {
        this.departamento = departamento;
    }

    public String getMunicipio() {
        return this.municipio;
    }

    public Direccion municipio(String municipio) {
        this.setMunicipio(municipio);
        return this;
    }

    public void setMunicipio(String municipio) {
        this.municipio = municipio;
    }

    public Boolean getActivo() {
        return this.activo;
    }

    public Direccion activo(Boolean activo) {
        this.setActivo(activo);
        return this;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    public Cuenta getCuenta() {
        return this.cuenta;
    }

    public void setCuenta(Cuenta cuenta) {
        this.cuenta = cuenta;
    }

    public Direccion cuenta(Cuenta cuenta) {
        this.setCuenta(cuenta);
        return this;
    }

    public Pedido getPedido() {
        return this.pedido;
    }

    public void setPedido(Pedido pedido) {
        if (this.pedido != null) {
            this.pedido.setDireccion(null);
        }
        if (pedido != null) {
            pedido.setDireccion(this);
        }
        this.pedido = pedido;
    }

    public Direccion pedido(Pedido pedido) {
        this.setPedido(pedido);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Direccion)) {
            return false;
        }
        return getId() != null && getId().equals(((Direccion) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Direccion{" +
            "id=" + getId() +
            ", direccion='" + getDireccion() + "'" +
            ", barrio='" + getBarrio() + "'" +
            ", localidad='" + getLocalidad() + "'" +
            ", departamento='" + getDepartamento() + "'" +
            ", municipio='" + getMunicipio() + "'" +
            ", activo='" + getActivo() + "'" +
            "}";
    }
}
