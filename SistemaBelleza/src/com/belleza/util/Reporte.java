package com.belleza.util;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.FileOutputStream;
import java.util.Date;
import java.util.List;

public class Reporte {
    private final int idReporte;
    private final String tipo;
    private final Date fechaInicio;
    private final Date fechaFin;
    private final String formato;

    public Reporte(int idReporte, String tipo, Date fechaInicio, Date fechaFin, String formato) {
        this.idReporte = idReporte;
        this.tipo = tipo;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.formato = formato;
    }

    public boolean exportarExcelNativo(String rutaDestino, String[] encabezados, List<Object[]> filasDatos) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Reporte - " + tipo);
            CellStyle headerStyle = workbook.createCellStyle();
            Font font = workbook.createFont();
            font.setBold(true);
            headerStyle.setFont(font);

            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < encabezados.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(encabezados[i]);
                cell.setCellStyle(headerStyle);
            }

            int rowNum = 1;
            for (Object[] filaData : filasDatos) {
                Row row = sheet.createRow(rowNum++);
                for (int colNum = 0; colNum < filaData.length; colNum++) {
                    Cell cell = row.createCell(colNum);
                    Object val = filaData[colNum];
                    if (val instanceof Number number) cell.setCellValue(number.doubleValue());
                    else if (val instanceof Boolean aBoolean) cell.setCellValue(aBoolean);
                    else cell.setCellValue(val != null ? val.toString() : "");
                }
            }

            for (int i = 0; i < encabezados.length; i++) sheet.autoSizeColumn(i);

            try (FileOutputStream fileOut = new FileOutputStream(rutaDestino)) {
                workbook.write(fileOut);
            }
            return true;
        } catch (Exception e) {
            System.err.println("Error al generar Excel: " + e.getMessage());
            return false;
        }
    }
}