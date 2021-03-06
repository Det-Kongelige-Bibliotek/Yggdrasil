package dk.kb.yggdrasil.db;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import dk.kb.yggdrasil.exceptions.ArgumentCheck;
import dk.kb.yggdrasil.exceptions.YggdrasilException;
import dk.kb.yggdrasil.json.preservation.PreservationRequest;
import dk.kb.yggdrasil.preservation.PreservationState;

@RunWith(JUnit4.class)
public class PreservationRequestStateTest {

    @Test
    public void testConstructor() {
        PreservationRequest pr = new PreservationRequest();
        String uuid = UUID.randomUUID().toString();
        PreservationState preservationState = PreservationState.PRESERVATION_REQUEST_RECEIVED;
        PreservationRequestState prs 
            = new PreservationRequestState(pr, preservationState, uuid);
        assertTrue(pr.equals(prs.getRequest()));
        assertTrue(preservationState.equals(prs.getState()));
        assertTrue(uuid.equals(prs.getUUID()));
    }

    @Test
    public void testSetState() throws YggdrasilException {
        PreservationRequest pr = new PreservationRequest();
        String uuid = UUID.randomUUID().toString();
        PreservationState preservationState = PreservationState.PRESERVATION_REQUEST_RECEIVED;
        PreservationRequestState prs 
            = new PreservationRequestState(pr, preservationState, uuid);
        assertTrue(preservationState.equals(prs.getState()));
        prs.setState(PreservationState.PRESERVATION_METADATA_PACKAGED_SUCCESSFULLY);
        assertTrue(PreservationState.PRESERVATION_METADATA_PACKAGED_SUCCESSFULLY.equals(
                prs.getState()));
    }
    
    @Test
    public void testSetAndGetFileMethods() throws YggdrasilException, IOException {
        PreservationRequest pr = new PreservationRequest();
        String uuid = UUID.randomUUID().toString();
        PreservationState preservationState = PreservationState.PRESERVATION_REQUEST_RECEIVED;
        PreservationRequestState prs 
            = new PreservationRequestState(pr, preservationState, uuid);
        assertTrue(preservationState.equals(prs.getState()));
        assertFalse(prs.getContentPayload()!= null);
        assertFalse(prs.getMetadataPayload()!= null);
        assertFalse(prs.getWarcId() != null);
        
        File nonexistingFile = new File(UUID.randomUUID().toString());
        assertFalse(nonexistingFile.exists());
        
        try {
            prs.setContentPayload(nonexistingFile);
            fail("Should have thrown ArgumentCheck exception on nonexisting file");
        } catch (ArgumentCheck e) {
            // Expected
        }
        
        try {
            prs.setMetadataPayload(nonexistingFile);
            fail("Should have thrown ArgumentCheck exception on nonexisting file");
        } catch (ArgumentCheck e) {
            // Expected
        }
        
        try {
            prs.setMetadataWarcFile(nonexistingFile);
            fail("Should have thrown ArgumentCheck exception on nonexisting file");
        } catch (ArgumentCheck e) {
            // Expected
        }
        File existingFileOne = new File(UUID.randomUUID().toString());
        File existingFileTwo = new File(UUID.randomUUID().toString());
        File existingFileThree = new File(UUID.randomUUID().toString());
        try {
            existingFileOne.createNewFile();
            existingFileTwo.createNewFile();
            existingFileThree.createNewFile();
            assertTrue(existingFileOne.exists());
            assertTrue(existingFileTwo.exists());
            assertTrue(existingFileThree.exists());

            prs.setContentPayload(existingFileOne);
            prs.setMetadataPayload(existingFileTwo);
            prs.setMetadataWarcFile(existingFileThree);
            assertTrue(existingFileOne.equals(prs.getContentPayload()));
            assertTrue(existingFileTwo.equals(prs.getMetadataPayload()));
            assertTrue(existingFileThree.getName().equals(prs.getWarcId()));
        } finally {
            existingFileOne.delete();
            existingFileTwo.delete();
            existingFileThree.delete();
        }
        
    }
    
    @Test
    public void testCleanupWhenNoFiles() throws Exception {
        PreservationRequest pr = new PreservationRequest();
        String uuid = UUID.randomUUID().toString();
        PreservationState preservationState = PreservationState.PRESERVATION_REQUEST_RECEIVED;
        PreservationRequestState prs = new PreservationRequestState(pr, preservationState, uuid);

        prs.cleanup();
    }

    @Test
    public void testCleanupOfBothFiles() throws Exception {
        PreservationRequest pr = new PreservationRequest();
        String uuid = UUID.randomUUID().toString();
        PreservationState preservationState = PreservationState.PRESERVATION_REQUEST_RECEIVED;
        PreservationRequestState prs = new PreservationRequestState(pr, preservationState, uuid);

        File contentFile = File.createTempFile("contentFile", null);
        File metadataFile = File.createTempFile("metadataFile", null);
        
        assertTrue(contentFile.exists());
        assertTrue(metadataFile.exists());
        
        prs.setContentPayload(contentFile);
        prs.setMetadataPayload(metadataFile);
        
        prs.cleanup();
        
        assertFalse(contentFile.exists());
        assertFalse(metadataFile.exists());        
    }

    @Test
    public void testCleanupWhenFilesAreAlreadyRemoved() throws Exception {
        PreservationRequest pr = new PreservationRequest();
        String uuid = UUID.randomUUID().toString();
        PreservationState preservationState = PreservationState.PRESERVATION_REQUEST_RECEIVED;
        PreservationRequestState prs = new PreservationRequestState(pr, preservationState, uuid);

        File contentFile = File.createTempFile("contentFile", null);
        File metadataFile = File.createTempFile("metadataFile", null);
        
        assertTrue(contentFile.exists());
        assertTrue(metadataFile.exists());
        
        prs.setContentPayload(contentFile);
        prs.setMetadataPayload(metadataFile);
        
        assertTrue(contentFile.delete());
        assertTrue(metadataFile.delete());
        
        prs.cleanup();
        prs.cleanup();
        
        assertFalse(contentFile.exists());
        assertFalse(metadataFile.exists());        
    }
    
    @Test
    public void testCleanupOfDirectories() throws Exception {
        PreservationRequest pr = new PreservationRequest();
        String uuid = UUID.randomUUID().toString();
        PreservationState preservationState = PreservationState.PRESERVATION_REQUEST_RECEIVED;
        PreservationRequestState prs = new PreservationRequestState(pr, preservationState, uuid);

        File contentFile = File.createTempFile("contentFile", null);
        File metadataFile = File.createTempFile("metadataFile", null);
        
        assertTrue(contentFile.exists());
        assertTrue(metadataFile.exists());
        
        prs.setContentPayload(contentFile);
        prs.setMetadataPayload(metadataFile);

        assertTrue(contentFile.delete());
        assertTrue(contentFile.mkdir());
        File.createTempFile("OddSubFolderFile", null, contentFile);

        assertTrue(metadataFile.delete());
        assertTrue(metadataFile.mkdir());
        File.createTempFile("OddSubFolderFile", null, metadataFile);

        assertTrue(contentFile.exists());
        assertTrue(metadataFile.exists());

        prs.cleanup();
        
        assertTrue(contentFile.exists());
        assertTrue(metadataFile.exists());
    }
}
