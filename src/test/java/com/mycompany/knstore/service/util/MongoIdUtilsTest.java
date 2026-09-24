package com.mycompany.knstore.service.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;

class MongoIdUtilsTest {

    private static final String ID_VALIDO = "507f1f77bcf86cd799439011";

    @Test
    void toObjectIdConvierteUnHexadecimalValidoDe24Caracteres() {
        ObjectId resultado = MongoIdUtils.toObjectId(ID_VALIDO);

        assertThat(resultado).isNotNull();
        assertThat(resultado.toHexString()).isEqualTo(ID_VALIDO);
    }

    @Test
    void toObjectIdGeneraUnObjectIdIgualAlOriginal() {
        ObjectId original = new ObjectId();

        ObjectId resultado = MongoIdUtils.toObjectId(original.toHexString());

        assertThat(resultado).isEqualTo(original);
    }

    @Test
    void toObjectIdRetornaNullParaUnStringInvalido() {
        assertThat(MongoIdUtils.toObjectId("no-es-un-objectid")).isNull();
    }

    @Test
    void toObjectIdRetornaNullParaUnStringVacio() {
        assertThat(MongoIdUtils.toObjectId("")).isNull();
    }

    @Test
    void toObjectIdRetornaNullCuandoElIdEsNull() {
        assertThat(MongoIdUtils.toObjectId(null)).isNull();
    }

    @Test
    void toObjectIdsConvierteSoloLosIdsValidosManteniendoElOrden() {
        String otroValido = new ObjectId().toHexString();

        Collection<ObjectId> resultado = MongoIdUtils.toObjectIds(Arrays.asList(ID_VALIDO, "invalido", otroValido));

        assertThat(resultado).hasSize(2);
        assertThat(resultado).containsExactly(new ObjectId(ID_VALIDO), new ObjectId(otroValido));
    }

    @Test
    void toObjectIdsIgnoraLosElementosNulosDeLaColeccion() {
        Collection<ObjectId> resultado = MongoIdUtils.toObjectIds(Arrays.asList(ID_VALIDO, null, "x"));

        assertThat(resultado).containsExactly(new ObjectId(ID_VALIDO));
    }

    @Test
    void toObjectIdsRetornaColeccionVaciaSiNingunIdEsValido() {
        Collection<ObjectId> resultado = MongoIdUtils.toObjectIds(List.of("a", "b", "c"));

        assertThat(resultado).isEmpty();
    }
}
