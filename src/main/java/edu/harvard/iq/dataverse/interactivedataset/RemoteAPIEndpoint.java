/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.harvard.iq.dataverse.interactivedataset;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;
import edu.harvard.iq.dataverse.DataverseTheme;
import java.io.Serializable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

/**
 *
 * This is a component of an Interactive Dataset
 * 
 * For the test case, the Interactive Dataset has a single RemoteAPIEndpoint.  
 * It is being split into a separate class--as requirements change, it will
 * likely have its own template, etc.
 * 
 *  
 * 
 * 
 * @author rmp553
 */
@Entity
@Table(indexes = {@Index(columnList="dataset_id")})
public class RemoteAPIEndpoint implements Serializable {
    
    @Expose
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @OneToOne
    @JoinColumn(name="interactivedataset_id")
    //@Expose
    InteractiveDataset interactiveDataset;
    
    @Expose
    @Column(nullable=false)
    private String apiEndpointURL;

    @Expose
    private boolean usePostRequest; // Should the call to the apiEndpointURL use a POST request?
    
    @Expose
    private String apiUsername;
    @Expose
    private String apiEncryptedPassword;
    @Expose
    private String apiParameters;



     /**
     *  Set id
     *  @param id
     */
    public void setId(Long id){
        this.id = id;
    }

    /**
     *  Get for id
     *  @return Long
     */
    public Long getId(){
        return this.id;
    }
    
        
    /**
     *  Set InteractiveDataset
     *  @param interactiveDataset
     */
    public void setInteractiveDataset(InteractiveDataset interactiveDataset){
        this.interactiveDataset = interactiveDataset;
    }

    /**
     *  Get InteractiveDataset
     *  @return InteractiveDataset
     */
    public InteractiveDataset getInteractiveDataset(){
        return this.interactiveDataset;
    }
    
    /**
     *  Set apiEndpointURL
     *  @param apiEndpointURL
     */
    public void setApiEndpointURL(String apiEndpointURL){
        this.apiEndpointURL = apiEndpointURL;
    }

    /**
     *  Get for apiEndpointURL
     *  @return String
     */
    public String getApiEndpointURL(){
        return this.apiEndpointURL;
    }
    

    /**
     *  Set apiUsername
     *  @param apiUsername
     */
    public void setApiUsername(String apiUsername){
        this.apiUsername = apiUsername;
    }

    /**
     *  Get for apiUsername
     *  @return String
     */
    public String getApiUsername(){
        return this.apiUsername;
    }
    

    /**
     *  Set apiEncryptedPassword
     *  @param apiEncryptedPassword
     */
    public void setApiEncryptedPassword(String apiEncryptedPassword){
        this.apiEncryptedPassword = apiEncryptedPassword;
    }

    /**
     *  Get for apiEncryptedPassword
     *  @return String
     */
    public String getApiEncryptedPassword(){
        return this.apiEncryptedPassword;
    }
    

    /**
     *  Set apiParameters
     *  @param apiParameters
     */
    public void setApiParameters(String apiParameters){
        this.apiParameters = apiParameters;
    }

    /**
     *  Get for apiParameters
     *  @return String
     */
    public String getApiParameters(){
        return this.apiParameters;
    }
    
    /**
     *  Set usePostRequest
     *  @param usePostRequest
     */
    public void setUsePostRequest(boolean usePostRequest){
        this.usePostRequest = usePostRequest;
    }
    
    /**
     *  Get for usePostRequest
     *  @return boolean
     */
    public boolean getUsePostRequest(){
        return this.usePostRequest;
    }

    
    
    /**
     * Return as JSON String
     * @return 
     */
    public String asJSON(){
        return this.serializeAsJSON(false);
    }
    
    /**
     * Return as JSON String
     * @return 
     */
    public String asPrettyJSON(){
        return this.serializeAsJSON(true);
    }
        
    /**
     * Return as JSON String
     * 
     *  Example:  
     *   {  "remoteAPIEndpoint" :
     *      {
                "id": 1, 
                "apiUsername": "dv_usr", 
                "apiParameters": null, 
                "apiEncryptedPassword": "secret", 
                "apiEndpointURL": "http://geotweets.cga.harvard.edu/api/dv", 
                "usePostRequest": true 
            }
        }
    
     * @return 
     */
    private String serializeAsJSON(boolean prettyPrint){
        
        String overarchingKey = "remoteAPIEndpoint";
        
        GsonBuilder builder;
        if (prettyPrint){  // Add pretty printing
            builder = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().setPrettyPrinting();
        }else{
            builder = new GsonBuilder().excludeFieldsWithoutExposeAnnotation();                        
        }
        
        builder.serializeNulls();   // correctly capture nulls
        Gson gson = builder.create();

        // serialize this object
        JsonElement jsonObj = gson.toJsonTree(this);

        // Nest the serialized object under::
        // {"removeAPIEndpoint" : { .. other attrs ...}}
        //
        JsonObject interactiveDatasetJSON = new JsonObject();
        interactiveDatasetJSON.add(overarchingKey, jsonObj);
        return interactiveDatasetJSON.toString();        
        
    }
}
