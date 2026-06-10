package com.mycompany.knstore.domain;

import com.mycompany.knstore.domain.enumeration.EstadoTipoDocumento;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * A TipoDocumento.
 */
@Document(collection = "tipo_documento")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TipoDocumento implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    @NotNull
    @Field("estado")
    private EstadoTipoDocumento estado;

    @NotNull
    @Size(max = 10)
    @Field("sigla")
    private String sigla;

    @NotNull
    @Size(max = 60)
    @Field("nombre_tipo")
    private String nombreTipo;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public String getId() {
        return this.id;
    }

    public TipoDocumento id(String id) {
        this.setId(id);
        return this;
    }

    public void setId(String id) {
        this.id = id;
    }

    public EstadoTipoDocumento getEstado() {
        return this.estado;
    }

    public TipoDocumento estado(EstadoTipoDocumento estado) {
        this.setEstado(estado);
        return this;
    }

    public void setEstado(EstadoTipoDocumento estado) {
        this.estado = estado;
    }

    public String getSigla() {
        return this.sigla;
    }

    public TipoDocumento sigla(String sigla) {
        this.setSigla(sigla);
        return this;
    }

    public void setSigla(String sigla) {
        this.sigla = sigla;
    }

    public String getNombreTipo() {
        return this.nombreTipo;
    }

    public TipoDocumento nombreTipo(String nombreTipo) {
        this.setNombreTipo(nombreTipo);
        return this;
    }

    public void setNombreTipo(String nombreTipo) {
        this.nombreTipo = nombreTipo;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TipoDocumento)) {
            return false;
        }
        return getId() != null && getId().equals(((TipoDocumento) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TipoDocumento{" +
            "id=" + getId() +
            ", estado='" + getEstado() + "'" +
            ", sigla='" + getSigla() + "'" +
            ", nombreTipo='" + getNombreTipo() + "'" +
            "}";
    }
}
