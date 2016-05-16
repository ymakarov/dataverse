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
import edu.harvard.iq.dataverse.Dataset;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;
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
import javax.persistence.Transient;

/**
 *
 * @author raprasad
 * 
 * 
 * 
 */ 
@Entity
@Table(indexes = {@Index(columnList="dataset_id")})
public class InteractiveDataset implements Serializable {
    
    @Transient
    public final static List<String> RequiredFields = Arrays.asList("serviceName", "apiEndpointURL", "contactName", "contactEmail");
    
    @Transient
    public final static List<String> AllFieldNames = Arrays.asList("id","serviceName","serviceDescription","apiEndpointURL","apiUsername","apiEncryptedPassword","apiParameters","visualizationURL","exploreButtonURL","exploreButtonOpensNewWindow","contactName","contactEmail","serviceActive","serviceInactiveMessage","serviceDownMessage","updated","created");
    private static final long serialVersionUID = 1L;
    
    @OneToOne(mappedBy = "interactiveDataset", cascade={ CascadeType.REMOVE, CascadeType.MERGE,CascadeType.PERSIST}, orphanRemoval=true)
   //@Expose
    private RemoteAPIEndpoint remoteAPIEndpoint;
    
    @Expose
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name="dataset_id")
    @Expose
    private Dataset dataset;

    @Expose
    @Column(nullable=false, unique=true)
    private String serviceName;
    @Expose
    private String serviceDescription;


    @Expose
    private String visualizationURL;

    @Expose
    private String exploreButtonURL;
    @Expose
    private boolean exploreButtonOpensNewWindow;

    @Column(nullable=false)
    @Expose
    private String contactName;
    @Column(nullable=false)
    @Expose
    private String contactEmail;

    @Expose
    private boolean serviceActive;
    @Expose
    private String serviceInactiveMessage;
    @Expose
    private String serviceDownMessage;

    @Expose
    private Timestamp updated;
    @Expose
    private Timestamp created;

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
     *  Set dataset
     *  @param dataset
     */
    public void setDataset(Dataset dataset){
        this.dataset = dataset;
    }

    /**
     *  Get dataset
     *  @return Long
     */
    public Dataset getDataset(){
        return this.dataset;
    }
    

    /**
     *  Set serviceName
     *  @param serviceName
     */
    public void setServiceName(String serviceName){
        this.serviceName = serviceName;
    }

    /**
     *  Get for serviceName
     *  @return String
     */
    public String getServiceName(){
        return this.serviceName;
    }
    

    /**
     *  Set serviceDescription
     *  @param serviceDescription
     */
    public void setServiceDescription(String serviceDescription){
        this.serviceDescription = serviceDescription;
    }

    /**
     *  Get for serviceDescription
     *  @return String
     */
    public String getServiceDescription(){
        return this.serviceDescription;
    }
    

    /**
     *  Set visualizationURL
     *  @param visualizationURL
     */
    public void setVisualizationURL(String visualizationURL){
        this.visualizationURL = visualizationURL;
    }

    /**
     *  Get for visualizationURL
     *  @return String
     */
    public String getVisualizationURL(){
        return this.visualizationURL;
    }
    

    /**
     *  Set exploreButtonURL
     *  @param exploreButtonURL
     */
    public void setExploreButtonURL(String exploreButtonURL){
        this.exploreButtonURL = exploreButtonURL;
    }

    /**
     *  Get for exploreButtonURL
     *  @return String
     */
    public String getExploreButtonURL(){
        return this.exploreButtonURL;
    }
    

    /**
     *  Set exploreButtonOpensNewWindow
     *  @param exploreButtonOpensNewWindow
     */
    public void setExploreButtonOpensNewWindow(boolean exploreButtonOpensNewWindow){
        this.exploreButtonOpensNewWindow = exploreButtonOpensNewWindow;
    }

    /**
     *  Get for exploreButtonOpensNewWindow
     *  @return boolean
     */
    public boolean getExploreButtonOpensNewWindow(){
        return this.exploreButtonOpensNewWindow;
    }
    

    /**
     *  Set contactName
     *  @param contactName
     */
    public void setContactName(String contactName){
        this.contactName = contactName;
    }

    /**
     *  Get for contactName
     *  @return String
     */
    public String getContactName(){
        return this.contactName;
    }
    

    /**
     *  Set contactEmail
     *  @param contactEmail
     */
    public void setContactEmail(String contactEmail){
        this.contactEmail = contactEmail;
    }

    /**
     *  Get for contactEmail
     *  @return String
     */
    public String getContactEmail(){
        return this.contactEmail;
    }
    

    /**
     *  Set serviceActive
     *  @param serviceActive
     */
    public void setServiceActive(boolean serviceActive){
        this.serviceActive = serviceActive;
    }

    /**
     *  Get for serviceActive
     *  @return boolean
     */
    public boolean getServiceActive(){
        return this.serviceActive;
    }
    

    /**
     *  Set serviceInactiveMessage
     *  @param serviceInactiveMessage
     */
    public void setServiceInactiveMessage(String serviceInactiveMessage){
        this.serviceInactiveMessage = serviceInactiveMessage;
    }

    /**
     *  Get for serviceInactiveMessage
     *  @return String
     */
    public String getServiceInactiveMessage(){
        return this.serviceInactiveMessage;
    }
    

    /**
     *  Set serviceDownMessage
     *  @param serviceDownMessage
     */
    public void setServiceDownMessage(String serviceDownMessage){
        this.serviceDownMessage = serviceDownMessage;
    }

    /**
     *  Get for serviceDownMessage
     *  @return String
     */
    public String getServiceDownMessage(){
        return this.serviceDownMessage;
    }
    

    /**
     *  Set updated
     *  @param updated
     */
    public void setUpdated(Timestamp updated){
        this.updated = updated;
    }

    /**
     *  Get for updated
     *  @return Timestamp
     */
    public Timestamp getUpdated(){
        return this.updated;
    }
    

    /**
     *  Set created
     *  @param created
     */
    public void setCreated(Timestamp created){
        this.created = created;
    }

    /**
     *  Get for created
     *  @return Timestamp
     */
    public Timestamp getCreated(){
        return this.created;
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
     *  {
            "dsetInfo": {
                "contactEmail": "IQSS@harvard.edu", 
                "updated": "Mar 16, 2016 1:42:32 PM", 
                "serviceInactiveMessage": "scheduled downtime", 
                "created": "Mar 16, 2016 1:42:32 PM", 
                "serviceDownMessage": "Uh oh", 
                "exploreButtonURL": null, 
                "exploreButtonOpensNewWindow": false, 
                "dataset": {
                    "doiSeparator": "/", 
                    "protocol": "doi", 
                    "authority": "10.5072/FK2"
                }, 
                "apiUsername": "dv_usr", 
                "apiParameters": null, 
                "serviceActive": true, 
                "serviceName": "GeoTweet", 
                "contactName": "IQSS SUPPORT", 
                "apiEncryptedPassword": "secret", 
                "apiEndpointURL": "http://geotweets.cga.harvard.edu/api/dv", 
                "visualizationURL": "https://worldmap.harvard.edu/maps/embed/?layer=geonode:net_migration_per_province_2010_dph", 
                "id": 1, 
                "serviceDescription": "GeoTweets"
            }
        }
     * 
     * @return 
     */
    private String serializeAsJSON(boolean prettyPrint){
        
        String overarchingKey = "interactiveDataset";
        
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
        
        // add the API endpoint
        if (this.remoteAPIEndpoint != null){
            JsonElement jsonObjRemoveAPIEndpoint = gson.toJsonTree(this.remoteAPIEndpoint);
           jsonObj.getAsJsonObject().add("remoteAPIEndpoint", jsonObjRemoveAPIEndpoint);
            //jsonObj.getAsJsonObject().addProperty("remoteAPIEndpoint", jsonObjRemoveAPIEndpoint());
            //jsonObj.getAsJsonObject().addProperty("remoteAPIEndpoint", this.remoteAPIEndpoint.asJSON());
            //jsonObj.
            //jsonObj.addProperty("remoteAPIEndpoint", jsonObjRemoveAPIEndpoint);
        }
        //return (gson.toJson(this)); // Return as String w/o nesting attributes

        // Nest the serialized object under::
        // {"interactiveDataset" : { .. other attrs ...}}
        //
        JsonObject interactiveDatasetJSON = new JsonObject();
        interactiveDatasetJSON.add(overarchingKey, jsonObj);
        return interactiveDatasetJSON.toString();

        
        // Add a custom field
        // ------------------------
        //JsonElement jsonObj = gson.toJsonTree(this);
        //jsonObj.getAsJsonObject().addProperty("anotherField", "yes it is");
        //return jsonObj.toString();
        // ------------------------

        
    }
} // end InteractiveDataset