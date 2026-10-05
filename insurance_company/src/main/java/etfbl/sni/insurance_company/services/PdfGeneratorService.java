package etfbl.sni.insurance_company.services;

import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfWriter;
import etfbl.sni.insurance_company.entities.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import etfbl.sni.insurance_company.entities.InsuranceType;
import etfbl.sni.insurance_company.entities.InsurancePolicy;
import com.lowagie.text.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Service
public class PdfGeneratorService {

    @Value("${pdf.path}")
    private String path;

    public String generatePolicyPdf(InsurancePolicy policy) throws IOException {
        String timestamp = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmssSSS")
                .withZone(ZoneId.systemDefault())
                .format(Instant.now());

        String filename = "policy_" + policy.getUser().getId() + "_" + timestamp + ".pdf";
        String filePath = path + File.separator + filename;

        Document document = new Document();
        PdfWriter writer= PdfWriter.getInstance(document, new FileOutputStream(filePath));
        document.open();

        PdfContentByte canvas = writer.getDirectContent();
        BaseFont baseFont = BaseFont.createFont(BaseFont.HELVETICA_BOLD, BaseFont.CP1250, BaseFont.NOT_EMBEDDED);
        canvas.beginText();
        canvas.setFontAndSize(baseFont, 14);
        canvas.setTextMatrix(40, document.getPageSize().getHeight() - 40);
        canvas.showText("AL Osiguranje");
        canvas.endText();

        Font titleFont = new Font(Font.HELVETICA, 20, Font.BOLD);
        Paragraph title = new Paragraph(policy.getType().getName(), titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(30);
        document.add(title);

        Font subsubtitleFont = new Font(Font.HELVETICA, 14, Font.BOLDITALIC);
        Paragraph subsubtitle = new Paragraph("Podaci o osiguraniku:", subsubtitleFont);
        subsubtitle.setAlignment(Element.ALIGN_LEFT);
        document.add(subsubtitle);

        Font normalFont = new Font(Font.HELVETICA, 12, Font.NORMAL);
        Paragraph name = new Paragraph("Ime i prezime: " + policy.getUser().getName() + " " + policy.getUser().getLastname(), normalFont);
        document.add(name);
        Paragraph jmbg = new Paragraph("Matični broj: " + policy.getUser().getNationalIdNumber(), normalFont);
        jmbg.setSpacingAfter(20);
        document.add(jmbg);

        Paragraph duration = new Paragraph("Trajanje osiguranja:", subsubtitleFont);
        duration.setAlignment(Element.ALIGN_LEFT);
        document.add(duration);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        String startDate = policy.getStartDate().format(formatter);
        String endDate = policy.getEndDate().format(formatter);

        Paragraph durationInfo = new Paragraph("Od: " + startDate, normalFont);
        document.add(durationInfo);
        Paragraph durationInfoCont = new Paragraph("Do: " + endDate, normalFont);
        durationInfoCont.setSpacingAfter(20);
        document.add(durationInfoCont);

        Paragraph price = new Paragraph("Cijena: "  +  policy.getPricePaid() + " KM", subsubtitleFont);
        document.add(price);

        document.close();
        return filePath;
    }
}
