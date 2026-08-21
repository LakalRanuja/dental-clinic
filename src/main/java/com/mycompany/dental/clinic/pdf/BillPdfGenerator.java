package com.mycompany.dental.clinic.pdf;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;

/** Draws a centered, receipt-style payment bill PDF for an appointment. */
public class BillPdfGenerator {

    private static final PDType1Font FONT_BOLD = new PDType1Font(Standard14Fonts.FontName.COURIER_BOLD);
    private static final PDType1Font FONT_REGULAR = new PDType1Font(Standard14Fonts.FontName.COURIER);

    private static final float BOX_WIDTH = 340f;
    private static final float BOX_HEIGHT = 460f;
    private static final float BOX_TOP_MARGIN = 80f;
    private static final float INNER_PADDING = 22f;

    private static final Color BACKGROUND = new Color(15, 23, 42);
    private static final Color ACCENT = new Color(45, 212, 191);
    private static final Color TEXT = Color.WHITE;

    public static void generate(File file, String appointmentNumber, String patientName, String contactNumber,
            String dentistName, String treatmentType, BigDecimal consultationFee, BigDecimal treatmentCost,
            BigDecimal totalAmount, String paymentMethod, String billDate) throws IOException {

        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);

            float pageWidth = page.getMediaBox().getWidth();
            float pageHeight = page.getMediaBox().getHeight();
            float boxX = (pageWidth - BOX_WIDTH) / 2f;
            float boxTop = pageHeight - BOX_TOP_MARGIN;
            float boxBottom = boxTop - BOX_HEIGHT;
            float contentLeft = boxX + INNER_PADDING;
            float contentWidth = BOX_WIDTH - INNER_PADDING * 2;

            try (PDPageContentStream content = new PDPageContentStream(document, page)) {
                content.setNonStrokingColor(BACKGROUND);
                content.addRect(boxX, boxBottom, BOX_WIDTH, BOX_HEIGHT);
                content.fill();

                content.setStrokingColor(ACCENT);
                content.setLineWidth(1.2f);
                content.addRect(boxX, boxBottom, BOX_WIDTH, BOX_HEIGHT);
                content.stroke();
                content.addRect(boxX + 4, boxBottom + 4, BOX_WIDTH - 8, BOX_HEIGHT - 8);
                content.stroke();

                float y = boxTop - INNER_PADDING - 8;

                content.setNonStrokingColor(TEXT);
                y = centeredLine(content, FONT_BOLD, 15, "DENTAL CLINIC", boxX, BOX_WIDTH, y, 20);
                y = centeredLine(content, FONT_BOLD, 12, "PAYMENT BILL", boxX, BOX_WIDTH, y, 20);
                y = divider(content, contentLeft, contentWidth, y);

                content.setNonStrokingColor(ACCENT);
                y = leftLine(content, FONT_REGULAR, 10.5f, "Appointment No: " + appointmentNumber, contentLeft, y);
                y = leftLine(content, FONT_REGULAR, 10.5f, "Date: " + billDate, contentLeft, y);
                y = divider(content, contentLeft, contentWidth, y);

                y = leftLine(content, FONT_REGULAR, 10.5f, "Patient: " + patientName, contentLeft, y);
                y = leftLine(content, FONT_REGULAR, 10.5f, "Contact: " + contactNumber, contentLeft, y);
                y = divider(content, contentLeft, contentWidth, y);

                y = leftLine(content, FONT_REGULAR, 10.5f, "Treatment: " + treatmentType, contentLeft, y);
                y = leftLine(content, FONT_REGULAR, 10.5f, "Dentist: " + dentistName, contentLeft, y);
                y = divider(content, contentLeft, contentWidth, y);

                y = leftLine(content, FONT_REGULAR, 10.5f, moneyRow("Consultation Fee:", consultationFee), contentLeft, y);
                y = leftLine(content, FONT_REGULAR, 10.5f, moneyRow("Treatment Cost:", treatmentCost), contentLeft, y);
                y -= 4;
                y = thinRule(content, contentLeft, contentWidth, y);
                y -= 12;

                content.setNonStrokingColor(TEXT);
                y = leftLine(content, FONT_BOLD, 11.5f, moneyRow("TOTAL:", totalAmount), contentLeft, y);

                content.setNonStrokingColor(ACCENT);
                y = divider(content, contentLeft, contentWidth, y);

                y = leftLine(content, FONT_REGULAR, 10.5f, "Payment Method: " + paymentMethod, contentLeft, y);
                y = divider(content, contentLeft, contentWidth, y);

                content.setNonStrokingColor(TEXT);
                centeredLine(content, FONT_BOLD, 11, "Thank you for visiting us!", boxX, BOX_WIDTH, y, 18);
            }

            document.save(file);
        }
    }

    private static String moneyRow(String label, BigDecimal amount) {
        String value = "Rs. " + (amount == null ? "0.00" : amount.setScale(2, RoundingMode.HALF_UP).toPlainString());
        return String.format("%-19s%s", label, value);
    }

    private static float centeredLine(PDPageContentStream content, PDType1Font font, float fontSize, String text,
            float boxX, float boxWidth, float y, float lineHeight) throws IOException {
        float textWidth = font.getStringWidth(text) / 1000 * fontSize;
        float x = boxX + (boxWidth - textWidth) / 2f;
        content.setFont(font, fontSize);
        content.beginText();
        content.newLineAtOffset(x, y);
        content.showText(text);
        content.endText();
        return y - lineHeight;
    }

    private static float leftLine(PDPageContentStream content, PDType1Font font, float fontSize, String text,
            float x, float y) throws IOException {
        content.setFont(font, fontSize);
        content.beginText();
        content.newLineAtOffset(x, y);
        content.showText(text);
        content.endText();
        return y - 16;
    }

    /** A cyan double rule marking the boundary between two sections, plus the spacing around it. */
    private static float divider(PDPageContentStream content, float x, float width, float y) throws IOException {
        y -= 4;
        y = thinRule(content, x, width, y);
        y -= 3;
        y = thinRule(content, x, width, y);
        return y - 14;
    }

    private static float thinRule(PDPageContentStream content, float x, float width, float y) throws IOException {
        content.setStrokingColor(ACCENT);
        content.setLineWidth(0.8f);
        content.moveTo(x, y);
        content.lineTo(x + width, y);
        content.stroke();
        return y;
    }
}
