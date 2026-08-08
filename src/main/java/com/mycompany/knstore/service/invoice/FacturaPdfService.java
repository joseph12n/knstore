package com.mycompany.knstore.service.invoice;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Image;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.mycompany.knstore.domain.Factura;
import com.mycompany.knstore.domain.Pedido;
import com.mycompany.knstore.service.util.MoneyUtils;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.HexFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

/**
 * Servicio de generacion de facturas: consecutivo atomico, PDF descargable
 * con QR de validacion interna y payload con hash SHA-256 (RF-066, RF-067).
 */
@Service
public class FacturaPdfService {

    private static final Logger LOG = LoggerFactory.getLogger(FacturaPdfService.class);

    private static final String FACTURA_SEQUENCE_COLLECTION = "factura_sequence";

    private static final DateTimeFormatter FECHA_FORMATO = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final MongoTemplate mongoTemplate;

    public FacturaPdfService(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    /**
     * Genera el siguiente consecutivo de factura usando una coleccion contadora atomica.
     *
     * @param prefijo prefijo de la factura (ej. "FE").
     * @return consecutivo en formato {@code PREFIJO-000001}.
     */
    public String generarConsecutivo(String prefijo) {
        String sequenceKey = "FAC-" + java.time.LocalDate.now();
        Query query = new Query(Criteria.where("_id").is(sequenceKey));
        Update update = new Update().inc("seq", 1);
        FindAndModifyOptions options = new FindAndModifyOptions().upsert(true).returnNew(true);
        org.bson.Document sequence = mongoTemplate.findAndModify(
            query,
            update,
            options,
            org.bson.Document.class,
            FACTURA_SEQUENCE_COLLECTION
        );
        long seq = sequence != null ? ((Number) sequence.get("seq")).longValue() : 1L;
        String prefijoBase = prefijo == null || prefijo.isBlank() ? "FE" : prefijo;
        return "%s-%06d".formatted(prefijoBase, seq);
    }

    /**
     * Construye el payload de validacion interna del QR: numero|fecha|total|hash SHA-256.
     *
     * @param factura factura a validar.
     * @return payload legible del QR.
     */
    public String generarPayloadValidacion(Factura factura) {
        String base = "%s|%s|%s".formatted(factura.getNumero(), factura.getFechaEmision(), MoneyUtils.normalizar(factura.getTotal()));
        return base + "|" + sha256(base);
    }

    /**
     * Genera el PDF de la factura de forma stateless (no guarda archivos en disco).
     *
     * @param factura factura con consecutivo asignado.
     * @param pedido pedido asociado (para datos del cliente).
     * @return bytes del PDF.
     */
    public byte[] generarPdf(Factura factura, Pedido pedido) {
        LOG.debug("Generando PDF de factura {}", factura.getNumero());
        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            PdfWriter.getInstance(document, out);
            document.open();

            Font titulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
            Font subtitulo = FontFactory.getFont(FontFactory.HELVETICA, 10);
            Font normal = FontFactory.getFont(FontFactory.HELVETICA, 10);

            Paragraph encabezado = new Paragraph("KN-STORE", titulo);
            encabezado.setAlignment(Element.ALIGN_CENTER);
            document.add(encabezado);
            document.add(new Paragraph("Factura de venta", subtitulo));
            document.add(new Paragraph("Numero: " + factura.getNumero(), normal));
            document.add(
                new Paragraph(
                    "Fecha: " +
                        (factura.getFechaEmision() != null
                            ? FECHA_FORMATO.format(factura.getFechaEmision().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime())
                            : "-"),
                    normal
                )
            );

            String cliente = "Cliente";
            String numeroPedido = "-";
            if (pedido != null) {
                numeroPedido = pedido.getNumeroPedido() != null ? pedido.getNumeroPedido() : pedido.getId();
                if (pedido.getCuenta() != null && pedido.getCuenta().getUser() != null) {
                    cliente = pedido.getCuenta().getUser().getLogin();
                }
            }
            document.add(new Paragraph("Cliente: " + cliente, normal));
            document.add(new Paragraph("Pedido: " + numeroPedido, normal));
            document.add(new Paragraph(" ", normal));

            PdfPTable tabla = new PdfPTable(2);
            tabla.setWidthPercentage(100);
            agregarCelda(tabla, "Subtotal", MoneyUtils.normalizar(factura.getSubtotal()));
            agregarCelda(tabla, "IVA", MoneyUtils.normalizar(factura.getValorIva()));
            agregarCelda(tabla, "Descuentos", MoneyUtils.normalizar(factura.getDescuentos()));
            agregarCelda(tabla, "TOTAL", MoneyUtils.normalizar(factura.getTotal()));
            document.add(tabla);
            document.add(new Paragraph(" ", normal));

            byte[] qrBytes = generarQr(generarPayloadValidacion(factura));
            if (qrBytes != null) {
                Image qr = Image.getInstance(qrBytes);
                qr.scaleToFit(100, 100);
                qr.setAlignment(Element.ALIGN_RIGHT);
                document.add(qr);
            }
            document.add(new Paragraph("Escanea el codigo QR para validar la factura", subtitulo));
            document.close();
        } catch (Exception e) {
            throw new IllegalStateException("No se pudo generar el PDF de la factura: " + e.getMessage(), e);
        }
        return out.toByteArray();
    }

    private void agregarCelda(PdfPTable tabla, String etiqueta, BigDecimal valor) {
        Font normal = FontFactory.getFont(FontFactory.HELVETICA, 10);
        tabla.addCell(new PdfPCell(new Phrase(etiqueta, normal)));
        tabla.addCell(new PdfPCell(new Phrase(valor != null ? valor.toPlainString() : "-", normal)));
    }

    private byte[] generarQr(String payload) {
        try {
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(payload, BarcodeFormat.QR_CODE, 200, 200);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", baos);
            return baos.toByteArray();
        } catch (WriterException | java.io.IOException e) {
            LOG.warn("No se pudo generar el QR: {}", e.getMessage());
            return null;
        }
    }

    private String sha256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(digest.digest(input.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 no disponible", e);
        }
    }
}
