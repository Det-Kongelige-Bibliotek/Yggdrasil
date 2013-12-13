package dk.kb.yggdrasil.utils;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class TravisUtils {
    
    public static boolean runningOnTravis() {
        final String TRAVIS_ID = "travis";
        InetAddress localhost = null;
        try {
            localhost = InetAddress.getLocalHost();
            String localhostName = localhost.getCanonicalHostName().toLowerCase();
            String user = System.getProperty("user.name");
            boolean onTravis = localhostName.contains(TRAVIS_ID) || TRAVIS_ID.equalsIgnoreCase(user); 
            //System.out.println("user: " + user);
            //System.out.println("Is machine (" + localhostName + "): running on travis" +  onTravis); 
            return onTravis;
        } catch (UnknownHostException e) {
            System.out.println(e);
        }
        return false;
    }

}
