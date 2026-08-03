package com.mycompany.knstore.service.invoice;

import com.mycompany.knstore.domain.Factura;

public interface FacturaPdfService {
    byte[] generarPdf(Factura factura);
}
