package co.com.bancolombia.libreinversion.request;


import co.com.bancolombia.libreinversion.model.commons.Constant;
import co.com.bancolombia.libreinversion.model.commons.DocumentUtil;
import co.com.bancolombia.libreinversion.model.exception.LibreInversionException;
import co.com.bancolombia.libreinversion.model.request.gateways.HtmlPdfGateways;
import co.com.bancolombia.logging.technical.LoggerFactory;
import co.com.bancolombia.logging.technical.logger.TechLogger;
import com.itextpdf.html2pdf.ConverterProperties;
import com.itextpdf.html2pdf.HtmlConverter;
import com.itextpdf.html2pdf.resolver.font.DefaultFontProvider;
import com.itextpdf.kernel.pdf.EncryptionConstants;
import com.itextpdf.kernel.pdf.PdfAConformanceLevel;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfOutputIntent;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.WriterProperties;
import com.itextpdf.layout.Document;
import com.itextpdf.pdfa.PdfADocument;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.stream.Collectors;

@Component
public class HtmlPdfAdapter implements HtmlPdfGateways {

    private static TechLogger log = LoggerFactory.getLog(DocumentUtil.class.getName());

    @Override
    public Mono<byte[]> htmlToPdfa1b(String html) {

        return Mono.just(html)
                .flatMap(htm -> {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    WriterProperties properties = new WriterProperties();
                    PdfWriter pdfWriter = new PdfWriter(baos, properties);
                    return htmlToPdf(pdfWriter, htm, baos);
                }).flatMap(ba -> Mono.just(ba.toByteArray()));
    }

    @Override
    public Mono<byte[]> htmlToPdfa1b(InputStream htmlInputStream) {

        BufferedReader br = new BufferedReader(new InputStreamReader(htmlInputStream));
        String stringHtml = br.lines().parallel().collect(Collectors.joining("\n"));

        return Mono.just(stringHtml)
                .flatMap(htm -> {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    WriterProperties properties = new WriterProperties();
                    PdfWriter pdfWriter = new PdfWriter(baos, properties);
                    return htmlToPdf(pdfWriter, htm, baos);
                }).flatMap(ba -> Mono.just(ba.toByteArray()));
    }

    @Override
    public Mono<byte[]> htmlToPdfa1bEncrypt(String html, String userPassword, String ownerPassword) {

        return Mono.just(html)
                .flatMap(htm -> {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    WriterProperties properties = new WriterProperties()
                            .setStandardEncryption(userPassword.getBytes(),
                                    ownerPassword.getBytes(),
                                    EncryptionConstants.ALLOW_PRINTING,
                                    EncryptionConstants.ENCRYPTION_AES_128 |
                                            EncryptionConstants.DO_NOT_ENCRYPT_METADATA);
                    PdfWriter pdfWriter = new PdfWriter(baos, properties);
                    return htmlToPdf(pdfWriter, htm, baos);
                }).flatMap(baosRes -> Mono.just(baosRes.toByteArray()));
    }

    private Mono<ByteArrayOutputStream> htmlToPdf(PdfWriter pdfWriter,
                                                  String html,
                                                  ByteArrayOutputStream baos) {
        return Mono.fromCallable(() -> {
            InputStream inputStream = null;
            Document document = null;
            try {
                inputStream = new ClassPathResource("sRGB_CS_profile.icm").getInputStream();
            } catch (FileNotFoundException e) {
            }

            PdfADocument pdfDocument = pdfADocument(inputStream, pdfWriter);

            pdfDocument.setTagged();
            ConverterProperties converterProperties = new ConverterProperties();

            DefaultFontProvider fontProvider = new DefaultFontProvider(
                    false, true, true);
            converterProperties.setFontProvider(fontProvider);
            converterProperties.setBaseUri("");

            document = HtmlConverter.convertToDocument(html, pdfDocument, converterProperties);
            document.close();

            return (baos);
        });
    }

    public byte[] basicHtmlToPdf(String html, String name) {
        byte[] res = null;
        try (
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                PdfWriter pdfWriter = new PdfWriter(baos);
                PdfDocument pdfDocument = new PdfDocument(pdfWriter);
        ) {
            ConverterProperties converterProperties = new ConverterProperties();
            DefaultFontProvider fontProvider = new DefaultFontProvider(false, true, true);
            converterProperties.setFontProvider(fontProvider);
            Document document = HtmlConverter.convertToDocument(html, pdfDocument, converterProperties);
            document.close();
            res = baos.toByteArray();
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return res;
    }

    private PdfADocument pdfADocument(InputStream inputStream, PdfWriter pdfWriter) {

        return new PdfADocument(pdfWriter,
                PdfAConformanceLevel.PDF_A_1B,
                new PdfOutputIntent("Custom",
                        "",
                        Constant.COLOR_ORG_URL,
                        Constant.COLOR_RGB, inputStream));
    }
}
