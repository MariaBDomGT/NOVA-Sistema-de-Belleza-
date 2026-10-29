package com.belleza.util;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class Factura {
    private final int idFactura;
    private final String numeroFactura;
    private final Date fecha;
    private final double total;

    public Factura(int idFactura, String numeroFactura, Date fecha, double total) {
        this.idFactura = idFactura;
        this.numeroFactura = numeroFactura;
        this.fecha = fecha != null ? fecha : new Date();
        this.total = total;
    }

    public boolean exportarPDF(String rutaPdf, String nombreCliente, List<String[]> items) {
        try {
            PdfWriter writer = new PdfWriter(rutaPdf);
            PdfDocument pdf = new PdfDocument(writer);
            
            try (Document document = new Document(pdf)) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                
                document.add(new Paragraph("NOVA - HAIR AND MAKEUP").setBold().setFontSize(22));
                document.add(new Paragraph("Documento Tributario / Factura de Venta").setFontSize(10));
                document.add(new Paragraph("=================================================="));
                
                document.add(new Paragraph("N° Factura: " + (numeroFactura != null ? numeroFactura : "FAC-0000")));
                document.add(new Paragraph("Fecha de Emision: " + sdf.format(fecha)));
                document.add(new Paragraph("Cliente: " + nombreCliente));
                document.add(new Paragraph("\n"));
                
                Table table = new Table(4);
                table.addHeaderCell("Descripcion / Servicio");
                table.addHeaderCell("Cant.");
                table.addHeaderCell("Precio U.");
                table.addHeaderCell("Subtotal");
                
                for (String[] item : items) {
                    table.addCell(item[0]);
                    table.addCell(item[1]);
                    table.addCell("Q" + item[2]);
                    table.addCell("Q" + item[3]);
                }
                document.add(table);
                
                double subtotalBase = total / 1.12;
                double ivaCalculado = total - subtotalBase;
                document.add(new Paragraph("\n"));
                document.add(new Paragraph(String.format("Subtotal (sin IVA): Q%.2f", subtotalBase)));
                document.add(new Paragraph(String.format("IVA Incluido (12%%): Q%.2f", ivaCalculado)));
                document.add(new Paragraph(String.format("TOTAL A PAGAR: Q%.2f", total)).setBold().setFontSize(14));
                document.add(new Paragraph("\n¡Gracias por preferir a NOVA!").setItalic());
            }
            return true;
        } catch (FileNotFoundException e) {
            System.err.println("Error al generar PDF de la factura: " + e.getMessage());
            return false;
        }
    }
}