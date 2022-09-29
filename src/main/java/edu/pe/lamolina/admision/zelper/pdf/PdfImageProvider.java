package edu.pe.lamolina.admision.zelper.pdf;

import com.itextpdf.text.BadElementException;
import com.itextpdf.text.Image;
import com.itextpdf.tool.xml.pipeline.html.AbstractImageProvider;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pe.edu.lamolina.model.constantines.GlobalConstantine;

public class PdfImageProvider extends AbstractImageProvider {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public Image retrieve(String src) {
        try {

            return Image.getInstance(this.getClass().getResource(GlobalConstantine.PDF_IMG + src));

        } catch (BadElementException ex) {
            ex.printStackTrace();

        } catch (IOException ex) {
            ex.printStackTrace();

        }
        return null;
    }

    @Override
    public String getImageRootPath() {
        return null;
    }
}
