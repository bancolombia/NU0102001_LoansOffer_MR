package co.com.bancolombia.libreinversion.model.commons;

import co.com.bancolombia.libreinversion.model.document.InfoDocument;
import co.com.bancolombia.logging.technical.LoggerFactory;
import co.com.bancolombia.logging.technical.logger.TechLogger;
import com.itextpdf.kernel.pdf.EncryptionConstants;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfDocumentInfo;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfVersion;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.WriterProperties;
import com.itextpdf.kernel.utils.PdfMerger;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class DocumentUtil {

    private static final TechLogger log = LoggerFactory.getLog(DocumentUtil.class.getName());

    private DocumentUtil() {
        throw new IllegalStateException("Utility class");
    }

    public static InfoDocument addMetaDataFromInfoDocument(InfoDocument info) {
        PdfDocument pdfDocument = null;
        try (
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                ByteArrayInputStream is = new ByteArrayInputStream(info.getByteArray())
        ) {

            PdfReader reader = new PdfReader(is);
            PdfWriter pdfWriter = new PdfWriter(bos,
                    new WriterProperties().addXmpMetadata().setPdfVersion(PdfVersion.PDF_1_6)
            );
            pdfDocument = new PdfDocument(reader, pdfWriter);
            PdfDocumentInfo infoPdf = pdfDocument.getDocumentInfo();
            infoPdf.setTitle(getTitle(info, infoPdf));
            infoPdf.setAuthor(getAuthor(info, infoPdf));
            infoPdf.setSubject(getSubject(info, infoPdf));
            infoPdf.setKeywords(getKeywords(info, infoPdf));
            infoPdf.setCreator(getCreator(info, infoPdf));
            info.setTitle(infoPdf.getTitle());
            info.setAuthor(infoPdf.getAuthor());
            info.setSubject(infoPdf.getSubject());
            info.setKeywords(infoPdf.getKeywords());
            info.setCreator(infoPdf.getCreator());
            pdfDocument.close();
            info.setByteArray(bos.toByteArray());
        } catch (IOException e) {
            log.error(e.getMessage());
        } finally {
            if (pdfDocument != null) {
                pdfDocument.close();
            }
        }
        return info;
    }

    public static InfoDocument mergePdfFiles(List<InfoDocument> docs, String newFileMame) {
        ByteArrayOutputStream bos = null;
        PdfMerger merger = null;
        PdfDocument pdfDocMain = null;
        PdfDocumentInfo infoFirstDoc = null;
        InfoDocument infoDocMerged = null;
        PdfDocument sourcePdf = null;
        try {
            infoDocMerged = new InfoDocument();
            bos = new ByteArrayOutputStream();
            for (InfoDocument pdfDoc : docs) {
                PdfReader reader = new PdfReader(new ByteArrayInputStream(pdfDoc.getByteArray()));
                sourcePdf = new PdfDocument(reader);
                //init main doc PDF
                if (merger == null) {
                    infoDocMerged = new InfoDocument();
                    bos = new ByteArrayOutputStream();
                    pdfDocMain = new PdfDocument(new PdfWriter(bos, new WriterProperties()
                            .addXmpMetadata().setPdfVersion(PdfVersion.PDF_1_6)));
                    merger = new PdfMerger(pdfDocMain);
                }
                // take information first PDF
                if (infoFirstDoc == null) {
                    infoFirstDoc = sourcePdf.getDocumentInfo();
                    PdfDocumentInfo infoDocMain = pdfDocMain.getDocumentInfo();
                    infoDocMain.setTitle(getTitle(infoFirstDoc));
                    infoDocMain.setAuthor(getAuthor(infoFirstDoc));
                    infoDocMain.setSubject(getSubject(infoFirstDoc));
                    infoDocMain.setKeywords(getKeywords(infoFirstDoc));
                    infoDocMain.setCreator(getCreator(infoFirstDoc));
                }
                merger.merge(sourcePdf, 1, sourcePdf.getNumberOfPages());
                sourcePdf.close();
            }
            pdfDocMain.close();
            infoDocMerged.setTitle(getTitle(infoFirstDoc));
            infoDocMerged.setAuthor(getAuthor(infoFirstDoc));
            infoDocMerged.setSubject(getSubject(infoFirstDoc));
            infoDocMerged.setKeywords(getKeywords(infoFirstDoc));
            infoDocMerged.setCreator(getCreator(infoFirstDoc));
            infoDocMerged.setByteArray(bos.toByteArray());
            infoDocMerged.setNameFile(newFileMame);
        } catch (Exception e) {
            log.error(e.getMessage());
            infoDocMerged = null;
        } finally {
            closeOutputStream(bos);
            try {
                if (pdfDocMain != null) {
                    pdfDocMain.close();
                }
                if (sourcePdf != null) {
                    sourcePdf.close();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return infoDocMerged;
    }

    public static byte[] addPasswordPdf(String userPass, String ownerPass, byte[] pdf) {
        ByteArrayOutputStream bos = null;
        InputStream is = null;
        byte[] res = null;
        try {
            bos = new ByteArrayOutputStream();
            is = new ByteArrayInputStream(pdf);
            PdfReader pdfReader = new PdfReader(is);
            WriterProperties writerProperties = new WriterProperties().addXmpMetadata()
                    .setPdfVersion(PdfVersion.PDF_1_6);
            writerProperties.setStandardEncryption(userPass.getBytes(StandardCharsets.UTF_8),
                    ownerPass.getBytes(StandardCharsets.UTF_8),
                    EncryptionConstants.ALLOW_PRINTING, EncryptionConstants.ENCRYPTION_AES_128);
            PdfWriter pdfWriter = new PdfWriter(bos, writerProperties);
            PdfDocument pdfDocument = new PdfDocument(pdfReader, pdfWriter);
            pdfDocument.close();
            res = bos.toByteArray();
        } catch (Exception e) {
            log.error(e.getMessage());
        } finally {
            closeImputStream(is);
            closeOutputStream(bos);
        }
        return res;
    }

    private static String getAuthor(PdfDocumentInfo info) {
        if (info.getAuthor() != null) {
            return info.getAuthor();
        }
        return "";
    }

    private static String getTitle(PdfDocumentInfo info) {
        if (info.getTitle() != null) {
            return info.getTitle();
        }
        return "";
    }

    private static String getSubject(PdfDocumentInfo info) {
        if (info.getSubject() != null) {
            return info.getSubject();
        }
        return "";
    }

    private static String getCreator(PdfDocumentInfo info) {
        if (info.getCreator() != null) {
            return info.getCreator();
        }
        return "";
    }

    private static String getKeywords(PdfDocumentInfo info) {
        if (info.getKeywords() != null) {
            return info.getKeywords();
        }
        return "";
    }

    private static String getAuthor(InfoDocument info, PdfDocumentInfo infoPdf) {
        if (info.getAuthor() != null) {
            return info.getAuthor();
        } else if (infoPdf.getAuthor() != null) {
            return infoPdf.getAuthor();
        }
        return "";
    }

    private static String getTitle(InfoDocument info, PdfDocumentInfo infoPdf) {
        if (info.getTitle() != null) {
            return info.getTitle();
        } else if (infoPdf.getTitle() != null) {
            return infoPdf.getTitle();
        }
        return "";
    }

    private static String getSubject(InfoDocument info, PdfDocumentInfo infoPdf) {
        if (info.getSubject() != null) {
            return info.getSubject();
        } else if (infoPdf.getSubject() != null) {
            return infoPdf.getSubject();
        }
        return "";
    }

    private static String getCreator(InfoDocument info, PdfDocumentInfo infoPdf) {
        if (info.getCreator() != null) {
            return info.getCreator();
        } else if (infoPdf.getCreator() != null) {
            return infoPdf.getCreator();
        }
        return "";
    }

    private static String getKeywords(InfoDocument info, PdfDocumentInfo infoPdf) {
        if (info.getKeywords() != null) {
            return info.getKeywords();
        } else if (infoPdf.getKeywords() != null) {
            return infoPdf.getKeywords();
        }
        return "";
    }

    private static void closeImputStream(InputStream is) {
        try {
            if (is != null) {
                is.close();
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    private static void closeOutputStream(OutputStream out) {
        try {
            if (out != null) {
                out.close();
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }
}
