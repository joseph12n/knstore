package com.mycompany.knstore.service.dto;

import com.mycompany.knstore.domain.enumeration.EstadoTipoDocumento;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.mycompany.knstore.domain.TipoDocumento} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TipoDocumentoDTO implements Serializable {

    private String id;

    @NotNull
    @Size(max = 10)
    private String sigla;

    @NotNull
    @Size(max = 60)
    private String nombreTipo;

    @NotNull
    private EstadoTipoDocumento estado;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSigla() {
        return sigla;
    }

    public void setSigla(String sigla) {
        this.sigla = sigla;
    }

    public String getNombreTipo() {
        return nombreTipo;
    }

    public void setNombreTipo(String nombreTipo) {
        this.nombreTipo = nombreTipo;
    }

    public EstadoTipoDocumento getEstado() {
        return estado;
    }

    public void setEstado(EstadoTipoDocumento estado) {
        this.estado = estado;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TipoDocumentoDTO)) {
            return false;
        }

        TipoDocumentoDTO tipoDocumentoDTO = (TipoDocumentoDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, tipoDocumentoDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TipoDocumentoDTO{" +
            "id='" + getId() + "'" +
            ", sigla='" + getSigla() + "'" +
            ", nombreTipo='" + getNombreTipo() + "'" +
            ", estado='" + getEstado() + "'" +
            "}";
    }
}
