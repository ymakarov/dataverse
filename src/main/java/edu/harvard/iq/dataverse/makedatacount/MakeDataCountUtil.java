/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.harvard.iq.dataverse.makedatacount;
import edu.harvard.iq.dataverse.batch.util.LoggingUtil;
/**
 *
 * @author matthew
 */
public class MakeDataCountUtil {
    
    public static final String LOG_HEADER = "#Fields: event_time	client_ip	session_cookie_id	user_cookie_id	user_id	request_url	identifier	filename	size	user-agent	title	publisher	publisher_id	authors	publication_date	version	other_id	target_url	publication_year\n";
    
    public void logEntry(MakeDataCountEntry entry) {
        //MAD: The logDir may need to be configurable?
        //MAD: Also the file name?
        //MAD: I don't like how instanceRoot can be null
        LoggingUtil.saveLogFile(entry.toString(), System.getProperty("com.sun.aas.instanceRoot")+"/logs/", "test-mdc.log", LOG_HEADER);
    }
}
