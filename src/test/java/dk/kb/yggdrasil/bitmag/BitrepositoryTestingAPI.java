package dk.kb.yggdrasil.bitmag;

import java.io.File;

import org.bitrepository.access.getchecksums.GetChecksumsClient;
import org.bitrepository.access.getfile.GetFileClient;
import org.bitrepository.access.getfileids.GetFileIDsClient;
import org.bitrepository.common.settings.Settings;
import org.bitrepository.modify.putfile.PutFileClient;
import org.bitrepository.protocol.FileExchange;
import org.bitrepository.protocol.messagebus.MessageBus;
import org.mockito.Mockito;

import dk.kb.yggdrasil.exceptions.YggdrasilException;
import dk.kb.yggdrasil.utils.TravisUtils;

/**
 * Testing API for the Bitrepository.
 * Allows overriding of the clients, so we do not have to use an actual Bitrepository for testing.
 */
public class BitrepositoryTestingAPI extends Bitrepository {

    FileExchange fileExchange = null;
    
    public BitrepositoryTestingAPI(File configFile) throws YggdrasilException {
        super(new BitrepositoryConfig(configFile));
    }

    public void setPutFileClient(PutFileClient client) {
        this.bitMagPutClient = client;
    }

    public void setGetFileClient(GetFileClient client) {
        this.bitMagGetClient = client;
    }
    
    public void setGetFileIDsClient(GetFileIDsClient client) {
        this.bitMagGetFileIDsClient = client;
    }
    
    public void setGetChecksumsClient(GetChecksumsClient client) {
        this.bitMagGetChecksumsClient = client;
    }
    
    public void setFileExchange(FileExchange fe) {
        this.fileExchange = fe;
    }
    
    @Override
    protected void initBitmagMessageBus() {
        this.bitMagMessageBus = Mockito.mock(MessageBus.class);
    }
    
    @Override
    protected void initBitMagClients() {
        if (!TravisUtils.runningOnTravis()) {
            super.initBitMagClients();
        }
    }
    
    @Override
    protected FileExchange getFileExchange(Settings bitmagSettings) {
        if(fileExchange == null) {
            return super.getFileExchange(bitmagSettings);
        } else {
            return fileExchange;
        }
    }
}
