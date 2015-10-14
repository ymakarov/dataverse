package edu.harvard.iq.dataverse.search;

import edu.harvard.iq.dataverse.DvObject;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import static javax.ejb.TransactionAttributeType.REQUIRES_NEW;

@Stateless
public class IndexInNewTransactionServiceBean {

    private static final Logger logger = Logger.getLogger(IndexInNewTransactionServiceBean.class.getCanonicalName());

    @EJB
    SolrIndexServiceBean solrIndexService;

    /**
     * A new transaction seems to give a speed boost. For example, 9 seconds for
     * "madagascar" instead of 13 per
     * https://github.com/IQSS/dataverse/issues/2036
     */
    @TransactionAttribute(REQUIRES_NEW)
    public IndexResponse indexPermissionsForOneDvObject(DvObject dvObject) {
        IndexResponse indexResponse = solrIndexService.indexPermissionsForOneDvObject(dvObject);
        logger.fine("response was: " + indexResponse.getMessage());
        return indexResponse;
    }

}
