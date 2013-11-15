package dk.kb.yggdrasil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.UUID;

/**
 * Wrapper for the HTTP response payload.
 */
public class HttpPayload {

    /** Response content body steam. (Remember to close) */
    public InputStream contentBody;

    /** Response content body content encoding, null if not returned. */
    public String contentEncoding;

    /** Response content type, null if not returned. */
    public String contentType;

    /** Response content length, null if not returned. */
    public Long contentLength;

    /**
     * Construct a payload object with the supplied parameters.
     * @param contentBody <code>InputStream</code> with the content body
     * @param contentEncoding content encoding, null if not returned
     * @param contentType content type, null if not returned
     * @param contentLength content length, null if not returned
     */
    public HttpPayload(InputStream contentBody, String contentEncoding, String contentType, Long contentLength) {
        this.contentBody = contentBody;
        this.contentEncoding = contentEncoding;
        this.contentType = contentType;
        this.contentLength = contentLength;
    }

    /**
     * Close the payload object and release any resources associated with it.
     * @throws IOException an i/o exception if an error occurs while closing stream
     */
    public void close() throws IOException {
        if (contentBody != null) {
            contentBody.close();
            contentBody = null;
        }
    }

    public File writeToFile() throws IOException {
        byte[] tmpBuf = new byte[16384];
        int read;
        File tmpFile = null;
        RandomAccessFile raf;
        UUID uuid = UUID.randomUUID();
        tmpFile = new File(uuid.toString());
        raf = new RandomAccessFile(tmpFile, "rw");
        InputStream in = contentBody;
        while ((read = in.read(tmpBuf)) != -1 ) {
            raf.write(tmpBuf, 0, read);
        }
        raf.close();
        raf = null;
        in.close();
        return tmpFile;
    }

}
