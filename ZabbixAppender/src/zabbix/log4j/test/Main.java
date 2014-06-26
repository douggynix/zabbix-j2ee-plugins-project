package zabbix.log4j.test;

//import org.apache.log4j.Appender;
//import org.apache.log4j.Layout;
//import org.apache.log4j.Level;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import org.apache.log4j.Logger;


/**
 *
 * @author douggynix
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    private final static Logger log=Logger.getLogger(Main.class);
    public static void main(String[] args) throws Exception {
        // TODO code application logic here
    	//System.out.println("Current Working Directory "+System.getProperty("user.dir"));
    	
        File f=new File("syslog.log");
        if(!f.exists()){
        	f.createNewFile();
        }
    	BufferedReader file=new BufferedReader(new FileReader("syslog.log"));
    	//System.exit(0);
    	String data=null;
    	int i=1;
    	while( (data=file.readLine())!=null ){
    		System.out.println("********** "+"Message #"+i+" **********");
    		log.error(data);
    		System.out.println("********** Pause 1mn.... **************");
    		Thread.sleep(60000);
    	}
    	
        /*log.debug("Debug Message");
        Thread.sleep(12000);
        
        log.warn("Warning Message");
        Thread.sleep(12000);
        log.error("Error messages");
        Thread.sleep(12000);*/
    }

}