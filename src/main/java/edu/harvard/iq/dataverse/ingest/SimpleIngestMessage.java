/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.harvard.iq.dataverse.ingest;

/**
 *
 * @author rmp553
 */
public class SimpleIngestMessage{
        
        public boolean success = true;
        public String errorMessage = null;
        
        public SimpleIngestMessage(boolean success, String errorMessage){
            this.success = success;
            if (this.success == false){
                if (errorMessage == null){
                    throw new NullPointerException("errorMessage cannot be null.  If this was unsuccessfuly, you must include an errorMessage.");
                }
                this.errorMessage = errorMessage;
            }
        }

       public boolean wasErrorFound(){
           return this.success;
       }
       
       public String getErrorMessage(){
           return this.errorMessage;
       }
        
       public static SimpleIngestMessage getInfoSuccess(){
            return new SimpleIngestMessage(true, null);
       }

       public static SimpleIngestMessage getInfoFail(String errorMsg){
            return new SimpleIngestMessage(false, errorMsg);
       }

} // end SimpleIngestMessage
    
