package com.dreamoval.motech.omi.manager;

import com.dreamoval.motech.core.model.GatewayResponse;
import org.apache.log4j.Logger;
import org.motechproject.ws.LogType;
import com.dreamoval.motech.core.model.MStatus;
import org.motechproject.ws.server.RegistrarService;

/**
 * Provides external access to OMI methods
 *
 * @author Kofi A. Asamoah (yoofi@dremoval.com)
 * Date Created: Sept 30, 2009
 */
public class LogStatusActionImpl implements StatusAction {
   private RegistrarService regWs;
   private static Logger logger = Logger.getLogger(LogStatusActionImpl.class);
   
   public void doAction(GatewayResponse response){       
       LogType logType;
       
       String summary = "Status of message with id "
               + response.getRequestId()
               + " is: " 
               + response.getMessageStatus().toString() 
               + ". Response from gateway was: "
               + response.getResponseText();
       
       
       if(response.getMessageStatus() == MStatus.DELIVERED){
           logType = LogType.SUCCESS;
       }
       else{
           logType = LogType.FAILURE;
       }
       
       try{
            getRegWs().log(logType, summary);
       }
       catch(Exception e){
           logger.error("Error communicating with logging service", e);
       }
   }

    public RegistrarService getRegWs() {
        return regWs;
    }

    public void setRegWs(RegistrarService regWs) {
        this.regWs = regWs;
    }
}
