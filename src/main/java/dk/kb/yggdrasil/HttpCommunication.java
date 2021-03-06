package dk.kb.yggdrasil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.kb.yggdrasil.exceptions.ArgumentCheck;

/**
 * A small class to send a HTTP GET or PUT request to a given URL.
 */
public class HttpCommunication {

    /** Logging mechanism. */
    private static Logger logger = LoggerFactory.getLogger(HttpCommunication.class.getName());
    /** The directory, where the file should be placed. */
    private final File tmpDir;

    /** 
     * Constructor.
     * @param tmpDir The temporary directory, where the files will be stored while downloading.
     */
    public HttpCommunication(File tmpDir) {
        this.tmpDir = tmpDir;
    }
    
    /**
     * Send a HTTP GET request and return the result, if any, to the caller.
     * @param url the url to send a GET request to
     * @return HTTP response content body or null
     */
    public HttpPayload get(String url) {
        ArgumentCheck.checkNotNullOrEmpty(url, "'url' is null or empty.");

        HttpPayload httpResponse = null;
        InputStream in = null;
        try {
            /*
             * HTTP request.
             */
            CloseableHttpClient httpClient = HttpClients.createDefault();;
            HttpGet getRequest = new HttpGet(url);
            /*
             * HTTP response.
             */
            HttpResponse response = httpClient.execute(getRequest);
            if (response != null) {
                int responseCode = response.getStatusLine().getStatusCode();
                HttpEntity responseEntity = response.getEntity();
                in = responseEntity.getContent();
                if (responseCode == 200) {
                    Header contentEncodingHeader = responseEntity.getContentEncoding();
                    String contentEncoding = null;
                    if (contentEncodingHeader != null) {
                        contentEncoding = contentEncodingHeader.getValue();
                    }
                    Header contentTypeHeader = responseEntity.getContentType();
                    String contentType = null;
                    if (contentTypeHeader != null) {
                        contentType = contentTypeHeader.getValue();
                    }
                    httpResponse = new HttpPayload(in, contentEncoding,
                            contentType, responseEntity.getContentLength(), tmpDir);
                } else {
                    if (in != null) {
                        in.close();
                        in = null;
                    }
                    logger.error("Http request resulted in status code '"
                            + responseCode + "'. (" + url + ")");
                }
            } else {
                logger.error("Could not connect to '" + url + "'. No response received. ");
            }
        } catch (IOException e) {
            logger.error(e.toString(), e);
        }
        return httpResponse;
    }

    /**
     * Send a HTTP POST request with a given content and return the result
     * of the operation to the caller.
     * @param url the url to send a PUT request to
     * @param content The content of the HTTP POST.
     * @return boolean indicating success or failure
     */
    public boolean post(String url, HttpEntity content) {
        ArgumentCheck.checkNotNullOrEmpty(url, "String url");
        ArgumentCheck.checkNotNull(content, "HttpEntity content");

        boolean bSuccess = false;
        try {
            /*
             * HTTP request.
             */
            CloseableHttpClient httpClient = HttpClients.createDefault();
            HttpPost postRequest = new HttpPost(url);
            postRequest.setEntity(content);
            
            /*
             * HTTP response.
             */
            HttpResponse response = httpClient.execute(postRequest);
            if (response != null) {
                int responseCode = response.getStatusLine().getStatusCode();
                HttpEntity responseEntity = response.getEntity();
                InputStream in = responseEntity.getContent();
                if (responseCode == 200) {
                    bSuccess = true;
                } else {
                    logger.warn("Http post request (" + url + ") resulted in status code '" + responseCode + "', "
                            + "with the following reason: " + response.getStatusLine().getReasonPhrase());
                }
                if (in != null) {
                    in.close();
                    in = null;
                }
            } else {
                logger.warn("Could not connect to '" + url + "'. No response received. ");
            }
        } catch (IOException e) {
            logger.error(e.toString(), e);
        }
        return bSuccess;
    }
    
    /**
     * Send a HTTP PUT request including content body and return the result
     * of the operation to the caller.
     * @param url the url to send a PUT request to
     * @param contentBody content body to send to server
     * @param contentType content type of content body
     * @return boolean indicating success or failure
     */
    public boolean post(String url, byte[] contentBody, String contentType) {
        ArgumentCheck.checkNotNullOrEmpty(url, "String url");
        ArgumentCheck.checkNotNullOrEmpty(contentBody, "byte[] contentBody");
        ArgumentCheck.checkNotNullOrEmpty(contentType, "String contentType");

        boolean bSuccess = false;
        try {
            /*
             * HTTP request.
             */
            CloseableHttpClient httpClient = HttpClients.createDefault();
            HttpPost postRequest = new HttpPost(url);
            StringEntity putEntity = new StringEntity(new String(contentBody, Charset.defaultCharset()));
            putEntity.setContentType(contentType);
            postRequest.setEntity(putEntity);
            /*
             * HTTP response.
             */
            HttpResponse response = httpClient.execute(postRequest);
            if (response != null) {
                int responseCode = response.getStatusLine().getStatusCode();
                HttpEntity responseEntity = response.getEntity();
                InputStream in = responseEntity.getContent();
                if (responseCode == 200) {
                    bSuccess = true;
                } else {
                    logger.warn("Http request resulted in status code '"
                            + responseCode + "'. (" + url + ")");
                }
                if (in != null) {
                    in.close();
                    in = null;
                }
            } else {
                logger.warn("Could not connect to '" + url + "'. No response received. ");
            }
        } catch (IOException e) {
            logger.error(e.toString(), e);
        }
        return bSuccess;
    }

}
