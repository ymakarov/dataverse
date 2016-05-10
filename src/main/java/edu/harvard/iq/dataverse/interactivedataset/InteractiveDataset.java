/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.harvard.iq.dataverse.interactivedataset;

import edu.harvard.iq.dataverse.Dataset;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.json.Json;
import javax.json.JsonObjectBuilder;
import javax.json.JsonValue;
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
 */ 
@Entity
@Table(indexes = {@Index(columnList="dataset_id")})
public class InteractiveDataset implements Serializable {
    
    @Transient
    public final static List<String> RequiredFields = Arrays.asList("serviceName", "apiEndpointURL", "contactName", "contactEmail");
    
    @Transient
    public final static List<String> AllFieldNames = Arrays.asList("id","serviceName","serviceDescription","apiEndpointURL","apiUsername","apiEncryptedPassword","apiParameters","visualizationURL","exploreButtonURL","exploreButtonOpensNewWindow","contactName","contactEmail","serviceActive","serviceInactiveMessage","serviceDownMessage","updated","created");
    private static final long serialVersionUID = 1L;
    

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name="dataset_id")
    private Dataset dataset;

    @Column(nullable=false, unique=true)
    private String serviceName;
    private String serviceDescription;

    @Column(nullable=false)
    private String apiEndpointURL;

    private String apiUsername;
    private String apiEncryptedPassword;
    private String apiParameters;

    private String visualizationURL;

    private String exploreButtonURL;
    private boolean exploreButtonOpensNewWindow;

    @Column(nullable=false)
    private String contactName;
    @Column(nullable=false)
    private String contactEmail;

    private boolean serviceActive;
    private String serviceInactiveMessage;
    private String serviceDownMessage;

    private Timestamp updated;
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
    
    
    
    public String asJSON(){

        // Initialize JSON response
        JsonObjectBuilder jsonData = Json.createObjectBuilder();

        if (this.id==null){
	   jsonData.add("id", JsonValue.NULL);
	}else{
	   jsonData.add("id", this.id);
	}

	if (this.serviceName==null){
	   jsonData.add("serviceName", JsonValue.NULL);
	}else{
	   jsonData.add("serviceName", this.serviceName);
	}

	if (this.serviceDescription==null){
	   jsonData.add("serviceDescription", JsonValue.NULL);
	}else{
	   jsonData.add("serviceDescription", this.serviceDescription);
	}

	if (this.apiEndpointURL==null){
	   jsonData.add("apiEndpointURL", JsonValue.NULL);
	}else{
	   jsonData.add("apiEndpointURL", this.apiEndpointURL);
	}

	if (this.apiUsername==null){
	   jsonData.add("apiUsername", JsonValue.NULL);
	}else{
	   jsonData.add("apiUsername", this.apiUsername);
	}

	if (this.apiEncryptedPassword==null){
	   jsonData.add("apiEncryptedPassword", JsonValue.NULL);
	}else{
	   jsonData.add("apiEncryptedPassword", this.apiEncryptedPassword);
	}

	if (this.apiParameters==null){
	   jsonData.add("apiParameters", JsonValue.NULL);
	}else{
	   jsonData.add("apiParameters", this.apiParameters);
	}

	if (this.visualizationURL==null){
	   jsonData.add("visualizationURL", JsonValue.NULL);
	}else{
	   jsonData.add("visualizationURL", this.visualizationURL);
	}

	if (this.exploreButtonURL==null){
	   jsonData.add("exploreButtonURL", JsonValue.NULL);
	}else{
	   jsonData.add("exploreButtonURL", this.exploreButtonURL);
	}
	jsonData.add("exploreButtonOpensNewWindow", this.exploreButtonOpensNewWindow);

	if (this.contactName==null){
	   jsonData.add("contactName", JsonValue.NULL);
	}else{
	   jsonData.add("contactName", this.contactName);
	}

	if (this.contactEmail==null){
	   jsonData.add("contactEmail", JsonValue.NULL);
	}else{
	   jsonData.add("contactEmail", this.contactEmail);
	}
	jsonData.add("serviceActive", this.serviceActive);

	if (this.serviceInactiveMessage==null){
	   jsonData.add("serviceInactiveMessage", JsonValue.NULL);
	}else{
	   jsonData.add("serviceInactiveMessage", this.serviceInactiveMessage);
	}

	if (this.serviceDownMessage==null){
	   jsonData.add("serviceDownMessage", JsonValue.NULL);
	}else{
	   jsonData.add("serviceDownMessage", this.serviceDownMessage);
	}

	if (this.updated==null){
	   jsonData.add("updated", JsonValue.NULL);
	}else{
	   jsonData.add("updated", this.updated.toString());
	}

	if (this.created==null){
	   jsonData.add("created", JsonValue.NULL);
	}else{
	   jsonData.add("created", this.created.toString());
	}
             
        return jsonData.build().toString();

    }
} // end InteractiveDataset