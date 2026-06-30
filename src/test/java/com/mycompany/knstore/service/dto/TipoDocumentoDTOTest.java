package com.mycompany.knstore.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.knstore.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class TipoDocumentoDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(TipoDocumentoDTO.class);
        TipoDocumentoDTO tipoDocumentoDTO1 = new TipoDocumentoDTO();
        tipoDocumentoDTO1.setId("id1");
        TipoDocumentoDTO tipoDocumentoDTO2 = new TipoDocumentoDTO();
        assertThat(tipoDocumentoDTO1).isNotEqualTo(tipoDocumentoDTO2);
        tipoDocumentoDTO2.setId(tipoDocumentoDTO1.getId());
        assertThat(tipoDocumentoDTO1).isEqualTo(tipoDocumentoDTO2);
        tipoDocumentoDTO2.setId("id2");
        assertThat(tipoDocumentoDTO1).isNotEqualTo(tipoDocumentoDTO2);
        tipoDocumentoDTO1.setId(null);
        assertThat(tipoDocumentoDTO1).isNotEqualTo(tipoDocumentoDTO2);
    }
}
