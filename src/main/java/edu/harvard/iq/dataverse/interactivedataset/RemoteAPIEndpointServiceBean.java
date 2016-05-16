/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.harvard.iq.dataverse.interactivedataset;

import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

/**
 *
 * @author raprasad
 */
@Stateless
@Named
public class RemoteAPIEndpointServiceBean implements java.io.Serializable{
    
    private static final Logger logger = Logger.getLogger(RemoteAPIEndpointServiceBean.class.getCanonicalName());
    
    @PersistenceContext(unitName = "VDCNet-ejbPU")
    private EntityManager em;
    
    
    public RemoteAPIEndpoint find(Object pk) {
        if (pk==null){
            return null;
        }
        return (RemoteAPIEndpoint) em.find(RemoteAPIEndpoint.class, pk);
    }
    
    
    public RemoteAPIEndpoint save(RemoteAPIEndpoint remoteAPIEndpoint) {
        return em.merge(remoteAPIEndpoint);
    }

    
    /**
     * Delete an instance of an InteractiveDataset
     * 
     * @param remoteAPIEndpoint 
     */
    public void deleteRemoteAPIEndpoint(RemoteAPIEndpoint remoteAPIEndpoint){
    
        if ((remoteAPIEndpoint==null)||(remoteAPIEndpoint.getId() == null)){
            return;
        }
        em.remove(remoteAPIEndpoint);
    }
    
    /**
     * Find if a Dataset has an InteractiveDataset 
     * 
     * @param interactiveDataset
     * @return 
     */
    public RemoteAPIEndpoint findByInteractiveDataset(InteractiveDataset interactiveDataset) {
        if (interactiveDataset==null){
            return null;
        }
        
        TypedQuery<RemoteAPIEndpoint> typedQuery = em.createQuery("select object(o) from RemoteAPIEndpoint as o where o.interactiveDataset =:interactiveDataset", RemoteAPIEndpoint.class);
        typedQuery.setParameter("interactiveDataset", interactiveDataset);

        try{
            return typedQuery.getSingleResult();
        }catch (javax.persistence.NoResultException ex){
            return null;
        }
    }
    
       
}
