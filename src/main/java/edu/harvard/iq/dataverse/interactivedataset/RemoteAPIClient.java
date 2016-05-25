/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.harvard.iq.dataverse.interactivedataset;


 
/**
 * A simple Java REST GET example using the Apache HTTP library.
 * This executes a call against the Yahoo Weather API service, which is
 * actually an RSS service (<a href="http://developer.yahoo.com/weather/" title="http://developer.yahoo.com/weather/">http://developer.yahoo.com/weather/</a>).
 * 
 * Try this Twitter API URL for another example (it returns JSON results):
 * <a href="http://search.twitter.com/search.json?q=%40apple" title="http://search.twitter.com/search.json?q=%40apple">http://search.twitter.com/search.json?q=%40apple</a>
 * (see this url for more twitter info: <a href="https://dev.twitter.com/docs/using-search" title="https://dev.twitter.com/docs/using-search">https://dev.twitter.com/docs/using-search</a>)
 * 
 * Apache HttpClient: <a href="http://hc.apache.org/httpclient-3.x/" title="http://hc.apache.org/httpclient-3.x/">http://hc.apache.org/httpclient-3.x/</a>
 *
 */
public class RemoteAPIClient {
 
  private final RemoteAPIEndpoint remoteAPIEndpoint;
  private String apiResult;
  private boolean hasError = false;
  private String errorMessage = null;
  
  RemoteAPIClient(RemoteAPIEndpoint remoteAPIEndpoint){
      
      this.remoteAPIEndpoint = remoteAPIEndpoint;
      
  }
    
  public boolean runAPICall(){
      
      
      return false;
  }
  
  
   /**
     *  Set remoteAPIEndpoint
     *  @param remoteAPIEndpoint
     */
    /*public void setRemoteAPIEndpoint(RemoteAPIEndpoint remoteAPIEndpoint){
        this.remoteAPIEndpoint = remoteAPIEndpoint;
    }*/

    /**
     *  Get for remoteAPIEndpoint
     *  @return RemoteAPIEndpoint
     */
    public RemoteAPIEndpoint getRemoteAPIEndpoint(){
        return this.remoteAPIEndpoint;
    }
    

    /**
     *  Set apiResult
     *  @param apiResult
     */
    public void setApiResult(String apiResult){
        this.apiResult = apiResult;
    }

    /**
     *  Get for apiResult
     *  @return String
     */
    public String getApiResult(){
        return this.apiResult;
    }
    

    /**
     *  Set hasError
     *  @param hasError
     */
    public void setHasError(boolean hasError){
        this.hasError = hasError;
    }

    /**
     *  Get for hasError
     *  @return boolean
     */
    public boolean getHasError(){
        return this.hasError;
    }
    

    /**
     *  Set errorMessage
     *  @param errorMessage
     */
    public void setErrorMessage(String errorMessage){
        this.errorMessage = errorMessage;
    }

    /**
     *  Get for errorMessage
     *  @return String
     */
    public String getErrorMessage(){
        return this.errorMessage;
    }

}   // RemoteAPIClient
