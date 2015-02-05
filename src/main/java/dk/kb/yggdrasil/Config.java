package dk.kb.yggdrasil;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;

import dk.kb.yggdrasil.exceptions.ArgumentCheck;
import dk.kb.yggdrasil.exceptions.YggdrasilException;
import dk.kb.yggdrasil.utils.YamlTools;

/** The class reading the yggdrasil.yml file. */
public class Config {

    /** The database directory property. */
    private final String DATABASE_DIR_PROPERTY = "database_dir";
    /** The database directory.  (created if it doesn't exist. ) */
    private File databaseDir;
    /** The temporary directory property.  */
    private final String TEMPORARY_DIR_PROPERTY = "temporary_dir";
    /** The prefix for object ids in the management repository (Valhal) **/
    private final String MANAGEMENT_REPO_OBJECT_ID_PREFIX_PROPERTY = "management_repo_object_id_prefix";

    /** The temporary directory. (created if it doesn't exist. )*/
    private File tmpDir;
    /** The configDir where the yggdrasilConfigFile was located. */
    private File configdir;

    /** The monitor endpoint port property. */
    private final String MONITOR_PORT_PROPERTY = "monitor_port";
    /** The monitor port for the RunState service endpoint */
    private int monitorPort;
    
    /**
     * Constructor for class reading the general Yggdrasil config file.
     * @param yggrasilConfigFile the config file.
     * @throws YggdrasilException
     */
    public Config(File yggrasilConfigFile) throws YggdrasilException {
       ArgumentCheck.checkExistsNormalFile(yggrasilConfigFile, "File yggrasilConfigFile");
       configdir = yggrasilConfigFile.getParentFile();
       Map<String, LinkedHashMap> settings = YamlTools.loadYamlSettings(yggrasilConfigFile);
       Map<String, Object> valuesMap = settings.get(RunningMode.getMode().toString());
       String databaseDirAsString = (String) valuesMap.get(DATABASE_DIR_PROPERTY);
       if (databaseDirAsString == null || databaseDirAsString.isEmpty()) {
           throw new YggdrasilException("The property '" + DATABASE_DIR_PROPERTY 
                   + "' is undefined in config file '" 
                   + yggrasilConfigFile.getAbsolutePath() + "'");
       }
       databaseDir = new File(databaseDirAsString);
       if (!databaseDir.exists()) {
           if (!databaseDir.mkdirs()) {
               throw new YggdrasilException("Unable to create necessary database directory '"
                       + databaseDir.getAbsolutePath() + "'");
           }
       }
       
       String temporaryDirAsString = (String) valuesMap.get(TEMPORARY_DIR_PROPERTY);
       if (temporaryDirAsString == null || temporaryDirAsString.isEmpty()) {
           throw new YggdrasilException("The property '" + TEMPORARY_DIR_PROPERTY 
                   + "' is undefined in config file '" 
                   + yggrasilConfigFile.getAbsolutePath() + "'");
       }
       tmpDir = new File(temporaryDirAsString);
       
       if (!tmpDir.exists()) {
           if (!tmpDir.mkdirs()) {
               throw new YggdrasilException("Unable to create necessary tmp directory '"
                       + tmpDir.getAbsolutePath() + "'");
           }
       }

       try {
           monitorPort = (Integer) valuesMap.get(MONITOR_PORT_PROPERTY);
       } catch (Exception e) {
           throw new YggdrasilException("The monitor port wasn't set correctly");
       }
    }
    
    /** 
     * @return the database dir
     */
    public File getDatabaseDir() {
        return databaseDir;
    }
    
    /** 
     * @return the temporary directory
     */
    public File getTemporaryDir() {
        return tmpDir;
    }  
    
    /** 
     * @return the config directory
     */
    public File getConfigDir() {
        return configdir;
    }

    /** 
     * @return the monitor port
     */
    public int getMonitorPort() {
        return monitorPort;
    }

}
