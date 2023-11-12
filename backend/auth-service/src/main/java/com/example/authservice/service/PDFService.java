package com.example.authservice.service;

import com.itextpdf.text.Document;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.List;

@Service
public class PDFService {

    public ByteArrayInputStream createPDF() throws IOException {
        Document document = new Document(PageSize.A4);
        FileOutputStream output = new FileOutputStream("src/main/resources/report.pdf");
        try {
            PdfWriter.getInstance(document, output);
            document.open();

            Paragraph title = new Paragraph("REPORT");
            document.add(title);

            Paragraph infos = new Paragraph("INFO");
            document.add(infos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            document.close();
            output.close();
        }

        // Read the PDF file into a byte array
        File readFile = new File("src/main/resources/report.pdf");
        FileInputStream fileInputStream = new FileInputStream(readFile);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = fileInputStream.read(buffer)) != -1) {
            byteArrayOutputStream.write(buffer, 0, bytesRead);
        }
        fileInputStream.close();

        // Create a ByteArrayInputStream from the byte array
        byte[] pdfBytes = byteArrayOutputStream.toByteArray();
        return new ByteArrayInputStream(pdfBytes);
    }
}
