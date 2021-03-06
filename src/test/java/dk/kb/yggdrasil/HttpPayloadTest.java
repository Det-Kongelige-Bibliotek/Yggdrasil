package dk.kb.yggdrasil;

import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.FileUtils;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class HttpPayloadTest {
    protected static File testFileDir = new File("temporarydir");
    @BeforeClass
    public static void beforeClass() throws Exception {
        testFileDir.mkdirs();
    }

    @Test
    public void testWriteTo() throws IOException {
        long contentSize = "helloWorld".length();
        byte[] contentBodyBytes = "helloWorld".getBytes();
        InputStream contentBody = new ByteArrayInputStream(contentBodyBytes);
        HttpPayload hp = new HttpPayload(contentBody, "UTF-8", "text/plain", contentSize, testFileDir);
        File f = hp.writeToFile();
        assertTrue(f.exists() && f.length() == contentSize);
        String readContent = FileUtils.readFileToString(f);
        assertTrue(readContent.equals("helloWorld"));
        f.delete();
        
        hp.close();
        hp.close();
    }
}
