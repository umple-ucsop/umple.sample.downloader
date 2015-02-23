package cruise.umple.sample.downloader;

import com.google.inject.Inject;
import static org.testng.Assert.*;

import org.apache.commons.io.IOUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Guice;
import org.testng.annotations.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Optional;

/**
 * Test {@link RealDocumentProvider}.
 */
@Guice(modules = DownloaderModule.class)
public class DocumentProviderTest {

    @Inject
    private DocumentFactory docProv;

    private static final String ZOO_URL = "http://www.emn.fr/z-info/atlanmod/index.php/Ecore";
    private static File ZOO_FILE;
    private static String ZOO_FILE_CONTENT;

    @BeforeClass
    private static void beforeClass() {
        try {
            ZOO_FILE = File.createTempFile("AtlanMod-Zoo", ".html");
            ZOO_FILE.deleteOnExit();

            byte[] data = IOUtils.toByteArray(new URL(ZOO_URL));
            ZOO_FILE_CONTENT = Jsoup.connect(ZOO_URL).timeout(30 * 1000).get().toString();
            try (FileOutputStream fos = new FileOutputStream(ZOO_FILE)) {
                IOUtils.write(data, fos);
            }
        } catch (IOException ioe) {
            // fail if we can't create the file
            throw new RuntimeException(ioe);
        }
    }

    @Test
    public void testFromURL() {
        assertFalse(docProv.fromURL("Non-exist").isPresent(), "Loaded proper Document from non-existant URL");

        Optional<Document> odoc = docProv.fromURL(ZOO_URL);
        assertTrue(odoc.isPresent(), "Failed to load proper Document from Zoo repository");
        assertEquals(odoc.get().toString(), ZOO_FILE_CONTENT, "Document content invalid");
    }

    @Test
    public void testFromFile() throws IOException {
        assertFalse(docProv.fromFile("Non-exist").isPresent(), "Loaded proper Document from non-existant file");

        Optional<Document> odoc = docProv.fromFile(ZOO_FILE);
        assertTrue(odoc.isPresent(), "Failed to load proper Document from Zoo repository");
        assertEquals(odoc.get().toString(), ZOO_FILE_CONTENT,
                "Document was not properly loaded.");
    }
}
