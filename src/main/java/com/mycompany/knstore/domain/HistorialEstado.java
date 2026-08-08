package com.mycompany.knstore.domain;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * Registro de auditoria de transiciones de estado de las entidades de negocio.
 */
@Document(collection = "historial_estado")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class HistorialEstado implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    @NotNull
    @Size(max = 50)
    @Indexed
    @Field("entidad")
    private String entidad;

    @NotNull
    @Size(max = 100)
    @Indexed
    @Field("id_entidad")
    private String idEntidad;

    @NotNull
    @Size(max = 100)
    @Field("campo")
    private String campo;

    @Size(max = 255)
    @Field("valor_anterior")
    private String valorAnterior;

    @Size(max = 255)
    @Field("valor_nuevo")
    private String valorNuevo;

    @NotNull
    @Field("fecha")
    private Instant fecha;

    @Size(max = 100)
    @Field("actor")
    private String actor;

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public HistorialEstado id(String id) {
        this.setId(id);
        return this;
    }

    public String getEntidad() {
        return this.entidad;
    }

    public void setEntidad(String entidad) {
        this.entidad = entidad;
    }

    public HistorialEstado entidad(String entidad) {
        this.setEntidad(entidad);
        return this;
    }

    public String getIdEntidad() {
        return this.idEntidad;
    }

    public void setIdEntidad(String idEntidad) {
        this.idEntidad = idEntidad;
    }

    public HistorialEstado idEntidad(String idEntidad) {
        this.setIdEntidad(idEntidad);
        return this;
    }

    public String getCampo() {
        return this.campo;
    }

    public void setCampo(String campo) {
        this.campo = campo;
    }

    public HistorialEstado campo(String campo) {
        this.setCampo(campo);
        return this;
    }

    public String getValorAnterior() {
        return this.valorAnterior;
    }

    public void setValorAnterior(String valorAnterior) {
        this.valorAnterior = valorAnterior;
    }

    public HistorialEstado valorAnterior(String valorAnterior) {
        this.setValorAnterior(valorAnterior);
        return this;
    }

    public String getValorNuevo() {
        return this.valorNuevo;
    }

    public void setValorNuevo(String valorNuevo) {
        this.valorNuevo = valorNuevo;
    }

    public HistorialEstado valorNuevo(String valorNuevo) {
        this.setValorNuevo(valorNuevo);
        return this;
    }

    public Instant getFecha() {
        return this.fecha;
    }

    public void setFecha(Instant fecha) {
        this.fecha = fecha;
    }

    public HistorialEstado fecha(Instant fecha) {
        this.setFecha(fecha);
        return this;
    }

    public String getActor() {
        return this.actor;
    }

    public void setActor(String actor) {
        this.actor = actor;
    }

    public HistorialEstado actor(String actor) {
        this.setActor(actor);
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof HistorialEstado)) {
            return false;
        }
        return getId() != null && getId().equals(((HistorialEstado) o).getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "HistorialEstado{" +
            "id='" + getId() + "'" +
            ", entidad='" + getEntidad() + "'" +
            ", idEntidad='" + getIdEntidad() + "'" +
            ", campo='" + getCampo() + "'" +
            ", valorAnterior='" + getValorAnterior() + "'" +
            ", valorNuevo='" + getValorNuevo() + "'" +
            ", fecha='" + getFecha() + "'" +
            ", actor='" + getActor() + "'" +
            "}";
    }
}
