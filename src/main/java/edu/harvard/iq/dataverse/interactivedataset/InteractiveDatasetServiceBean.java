/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.harvard.iq.dataverse.interactivedataset;

import edu.harvard.iq.dataverse.Dataset;
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
public class InteractiveDatasetServiceBean implements java.io.Serializable{
    
    private static final Logger logger = Logger.getLogger(InteractiveDatasetServiceBean.class.getCanonicalName());
    
    @PersistenceContext(unitName = "VDCNet-ejbPU")
    private EntityManager em;
    
    
    public InteractiveDataset find(Object pk) {
        if (pk==null){
            return null;
        }
        return (InteractiveDataset) em.find(InteractiveDataset.class, pk);
    }
    
    
    /**
     * Delete an instance of an InteractiveDataset
     * 
     * @param interactiveDataset 
     */
    public void deleteInteractiveDataset(InteractiveDataset interactiveDataset){
    
        if ((interactiveDataset==null)||(interactiveDataset.getId() == null)){
            return;
        }
        em.remove(interactiveDataset);
    }
    
    /**
     * Find if a Dataset has an InteractiveDataset 
     * 
     * @param dataset
     * @return 
     */
    public InteractiveDataset findByDataset(Dataset dataset) {
        if (dataset==null){
            return null;
        }
        
        TypedQuery<InteractiveDataset> typedQuery = em.createQuery("select object(o) from InteractiveDataset as o where o.dataset =:dataset", InteractiveDataset.class);
        typedQuery.setParameter("dataset", dataset);

        try{
            return typedQuery.getSingleResult();
        }catch (javax.persistence.NoResultException ex){
            return null;
        }
    }
    
       
}
