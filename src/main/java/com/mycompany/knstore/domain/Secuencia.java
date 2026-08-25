package com.mycompany.knstore.domain;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * Contador de consecutivos diarios atómicos (RNF-030): un documento por
 * {@code tipo + fecha} que se incrementa con $inc en la coleccion
 * {@code secuencias}, de modo que CHECKOUT, pedidos y facturas compartan una
 * única fuente de verdad sin duplicar colecciones contadoras.
 */
@Document(collection = "secuencias")
public class Secuencia implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    @Field("tipo")
    private String tipo;

    @Field("fecha")
    private LocalDate fecha;

    @Field("seq")
    private Long seq;

    public Secuencia() {}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public Long getSeq() {
        return seq;
    }

    public void setSeq(Long seq) {
        this.seq = seq;
    }
}
