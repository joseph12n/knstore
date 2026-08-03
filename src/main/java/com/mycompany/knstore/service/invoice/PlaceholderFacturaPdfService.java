package com.mycompany.knstore.service.invoice;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.mycompany.knstore.domain.Factura;
import com.mycompany.knstore.service.util.MoneyUtils;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.EnumMap;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class PlaceholderFacturaPdfService implements FacturaPdfService {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm").withZone(
        ZoneId.systemDefault()
    );

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private static final DecimalFormat MONEY_FORMAT = new DecimalFormat("#,##0.00", DecimalFormatSymbols.getInstance());

    @Override
    public byte[] generarPdf(Factura factura) {
        if (factura == null) {
            throw new IllegalArgumentException("La factura no puede ser null");
        }

        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4, 36, 36, 36, 36);
            PdfWriter.getInstance(document, out);
            document.open();

            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16);
            Font sectionFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11);
            Font textFont = FontFactory.getFont(FontFactory.HELVETICA, 10);

            document.add(new Paragraph("KN Store - Factura electronica", titleFont));
            document.add(new Paragraph(" "));

            PdfPTable table = new PdfPTable(2);
            table.setWidthPercentage(100f);
            table.setWidths(new float[] { 35f, 65f });

            addRow(table, "ID", factura.getId(), sectionFont, textFont);
            addRow(table, "Prefijo", factura.getPrefijo(), sectionFont, textFont);
            addRow(table, "Numero", factura.getNumeroFactura(), sectionFont, textFont);
            addRow(table, "CUFE", factura.getCufe(), sectionFont, textFont);
            addRow(
                table,
                "Fecha emision",
                factura.getFechaEmision() != null ? DATE_TIME_FORMATTER.format(factura.getFechaEmision()) : "N/A",
                sectionFont,
                textFont
            );
            addRow(
                table,
                "Fecha vencimiento",
                factura.getFechaVencimiento() != null ? DATE_FORMATTER.format(factura.getFechaVencimiento()) : "N/A",
                sectionFont,
                textFont
            );
            addRow(table, "Subtotal", formatMoney(factura.getSubtotal()), sectionFont, textFont);
            addRow(table, "Descuentos", formatMoney(factura.getDescuentos()), sectionFont, textFont);
            addRow(table, "Base IVA", formatMoney(factura.getBaseGravableIva()), sectionFont, textFont);
            addRow(table, "Valor IVA", formatMoney(factura.getValorIva()), sectionFont, textFont);
            addRow(table, "Total", formatMoney(factura.getTotal()), sectionFont, textFont);
            addRow(table, "Email enviada", Boolean.TRUE.equals(factura.getEnviada()) ? "Si" : "No", sectionFont, textFont);
            addRow(table, "Notas", factura.getNotasAdicionales(), sectionFont, textFont);

            document.add(table);
            document.add(new Paragraph(" "));

            String qrPayload = buildQrPayload(factura);
            Image qrImage = buildQrImage(qrPayload);
            qrImage.scaleToFit(140f, 140f);
            document.add(new Paragraph("Codigo QR", sectionFont));
            document.add(qrImage);

            document.add(new Paragraph(" "));
            document.add(new Paragraph("Este documento fue generado automaticamente por KN Store.", textFont));

            document.close();
            return out.toByteArray();
        } catch (DocumentException | IOException | WriterException e) {
            throw new IllegalStateException("No fue posible generar el PDF de la factura", e);
        }
    }

    private void addRow(PdfPTable table, String key, String value, Font keyFont, Font valueFont) {
        PdfPCell keyCell = new PdfPCell(new Phrase(key, keyFont));
        keyCell.setBorder(0);
        keyCell.setPaddingBottom(5f);

        PdfPCell valueCell = new PdfPCell(new Phrase(value != null && !value.isBlank() ? value : "N/A", valueFont));
        valueCell.setBorder(0);
        valueCell.setPaddingBottom(5f);

        table.addCell(keyCell);
        table.addCell(valueCell);
    }

    private String formatMoney(BigDecimal value) {
        return MONEY_FORMAT.format(MoneyUtils.normalizeOrZero(value));
    }

    private String buildQrPayload(Factura factura) {
        if (factura.getCodigoQr() != null && !factura.getCodigoQr().isBlank()) {
            return factura.getCodigoQr();
        }
        return (
            "factura=" +
            safeValue(factura.getNumeroFactura()) +
            "|id=" +
            safeValue(factura.getId()) +
            "|total=" +
            formatMoney(factura.getTotal()) +
            "|cufe=" +
            safeValue(factura.getCufe())
        );
    }

    private String safeValue(String value) {
        return value != null ? value : "";
    }

    private Image buildQrImage(String payload) throws WriterException, IOException, DocumentException {
        QRCodeWriter writer = new QRCodeWriter();
        Map<EncodeHintType, Object> hints = new EnumMap<>(EncodeHintType.class);
        hints.put(EncodeHintType.MARGIN, 1);
        BitMatrix matrix = writer.encode(payload, BarcodeFormat.QR_CODE, 256, 256, hints);

        try (ByteArrayOutputStream pngOut = new ByteArrayOutputStream()) {
            MatrixToImageWriter.writeToStream(matrix, "PNG", pngOut);
            return Image.getInstance(pngOut.toByteArray());
        }
    }
}
