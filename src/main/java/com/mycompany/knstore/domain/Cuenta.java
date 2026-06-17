package com.mycompany.knstore.domain;

import com.mycompany.knstore.domain.enumeration.Genero;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * A Cuenta.
 */
@Document(collection = "cuenta")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Cuenta implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    @Size(max = 20)
    @Field("num_documento")
    private String numDocumento;

    @NotNull
    @Size(max = 50)
    @Field("primer_nombre")
    private String primerNombre;

    @Size(max = 50)
    @Field("segundo_nombre")
    private String segundoNombre;

    @NotNull
    @Size(max = 50)
    @Field("primer_apellido")
    private String primerApellido;

    @Size(max = 50)
    @Field("segundo_apellido")
    private String segundoApellido;

    @Field("genero")
    private Genero genero;

    @Field("fecha_nacimiento")
    private LocalDate fechaNacimiento;

    @Size(max = 15)
    @Field("celular")
    private String celular;

    @Size(max = 15)
    @Field("telefono")
    private String telefono;

    @Field("foto_perfil")
    private byte[] fotoPerfil;

    @Field("foto_perfil_content_type")
    private String fotoPerfilContentType;

    @NotNull
    @Field("activo")
    private Boolean activo;

    @DBRef
    @Field("user")
    private User user;

    @DBRef
    @Field("tipoDocumento")
    private TipoDocumento tipoDocumento;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public String getId() {
        return this.id;
    }

    public Cuenta id(String id) {
        this.setId(id);
        return this;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNumDocumento() {
        return this.numDocumento;
    }

    public Cuenta numDocumento(String numDocumento) {
        this.setNumDocumento(numDocumento);
        return this;
    }

    public void setNumDocumento(String numDocumento) {
        this.numDocumento = numDocumento;
    }

    public String getPrimerNombre() {
        return this.primerNombre;
    }

    public Cuenta primerNombre(String primerNombre) {
        this.setPrimerNombre(primerNombre);
        return this;
    }

    public void setPrimerNombre(String primerNombre) {
        this.primerNombre = primerNombre;
    }

    public String getSegundoNombre() {
        return this.segundoNombre;
    }

    public Cuenta segundoNombre(String segundoNombre) {
        this.setSegundoNombre(segundoNombre);
        return this;
    }

    public void setSegundoNombre(String segundoNombre) {
        this.segundoNombre = segundoNombre;
    }

    public String getPrimerApellido() {
        return this.primerApellido;
    }

    public Cuenta primerApellido(String primerApellido) {
        this.setPrimerApellido(primerApellido);
        return this;
    }

    public void setPrimerApellido(String primerApellido) {
        this.primerApellido = primerApellido;
    }

    public String getSegundoApellido() {
        return this.segundoApellido;
    }

    public Cuenta segundoApellido(String segundoApellido) {
        this.setSegundoApellido(segundoApellido);
        return this;
    }

    public void setSegundoApellido(String segundoApellido) {
        this.segundoApellido = segundoApellido;
    }

    public Genero getGenero() {
        return this.genero;
    }

    public Cuenta genero(Genero genero) {
        this.setGenero(genero);
        return this;
    }

    public void setGenero(Genero genero) {
        this.genero = genero;
    }

    public LocalDate getFechaNacimiento() {
        return this.fechaNacimiento;
    }

    public Cuenta fechaNacimiento(LocalDate fechaNacimiento) {
        this.setFechaNacimiento(fechaNacimiento);
        return this;
    }

    public void setFechaNacimiento(LocalDate fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public String getCelular() {
        return this.celular;
    }

    public Cuenta celular(String celular) {
        this.setCelular(celular);
        return this;
    }

    public void setCelular(String celular) {
        this.celular = celular;
    }

    public String getTelefono() {
        return this.telefono;
    }

    public Cuenta telefono(String telefono) {
        this.setTelefono(telefono);
        return this;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public byte[] getFotoPerfil() {
        return this.fotoPerfil;
    }

    public Cuenta fotoPerfil(byte[] fotoPerfil) {
        this.setFotoPerfil(fotoPerfil);
        return this;
    }

    public void setFotoPerfil(byte[] fotoPerfil) {
        this.fotoPerfil = fotoPerfil;
    }

    public String getFotoPerfilContentType() {
        return this.fotoPerfilContentType;
    }

    public Cuenta fotoPerfilContentType(String fotoPerfilContentType) {
        this.fotoPerfilContentType = fotoPerfilContentType;
        return this;
    }

    public void setFotoPerfilContentType(String fotoPerfilContentType) {
        this.fotoPerfilContentType = fotoPerfilContentType;
    }

    public Boolean getActivo() {
        return this.activo;
    }

    public Cuenta activo(Boolean activo) {
        this.setActivo(activo);
        return this;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Cuenta user(User user) {
        this.setUser(user);
        return this;
    }

    public TipoDocumento getTipoDocumento() {
        return this.tipoDocumento;
    }

    public void setTipoDocumento(TipoDocumento tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }

    public Cuenta tipoDocumento(TipoDocumento tipoDocumento) {
        this.setTipoDocumento(tipoDocumento);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Cuenta)) {
            return false;
        }
        return getId() != null && getId().equals(((Cuenta) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Cuenta{" +
            "id=" + getId() +
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
            ", fotoPerfilContentType='" + getFotoPerfilContentType() + "'" +
            ", activo='" + getActivo() + "'" +
            "}";
    }
}
