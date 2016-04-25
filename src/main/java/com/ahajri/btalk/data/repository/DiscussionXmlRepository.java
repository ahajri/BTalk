package com.ahajri.btalk.data.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.namespace.QName;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.marklogic.client.document.XMLDocumentManager;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.io.JAXBHandle;
import com.marklogic.client.io.SearchHandle;
import com.marklogic.client.query.KeyValueQueryDefinition;
import com.marklogic.client.query.MatchDocumentSummary;
import com.marklogic.client.query.QueryManager;
import com.marklogic.client.query.StructuredQueryBuilder;
import com.marklogic.client.query.StructuredQueryDefinition;

/**
 * Sample implementation of the {@link IRepository}
 * making use of MarkLogic's {@link XMLDocumentManager}.
 *
 * @author Anis HAJRI
 */
@Component("discussionXMLRepository")
public class DiscussionXmlRepository implements IRepository<Map> {

    private static final Logger logger = Logger.getLogger(DiscussionXmlRepository.class);

    public static final String COLLECTION_REF = "/products.xml";
    public static final int PAGE_SIZE = 10;

    @Autowired
    protected QueryManager queryManager;

    @Autowired
    protected XMLDocumentManager xmlDocumentManager;

    @Override
    public void persist(Map Map) {
        // Add this document to a dedicated collection for later retrieval
        DocumentMetadataHandle metadata = new DocumentMetadataHandle();
        metadata.getCollections().add(COLLECTION_REF);
        JAXBHandle contentHandle = getProductHandle();
        contentHandle.set(Map);
        xmlDocumentManager.write("{}", metadata, contentHandle);
    }

    @Override
    public boolean remove(Map d) {
        xmlDocumentManager.delete("");
        return false;
    }

  
    @Override
    public Long count() {
        StructuredQueryBuilder sb = queryManager.newStructuredQueryBuilder();
        StructuredQueryDefinition criteria = sb.collection(COLLECTION_REF);

        SearchHandle resultsHandle = new SearchHandle();
        queryManager.search(criteria, resultsHandle);
        return resultsHandle.getTotalResults();
    }

    @Override
    public List<Map> findAll() {
        StructuredQueryBuilder sb = queryManager.newStructuredQueryBuilder();
        StructuredQueryDefinition criteria = sb.collection(COLLECTION_REF);

        SearchHandle resultsHandle = new SearchHandle();
        queryManager.search(criteria, resultsHandle);
        return toSearchResult(resultsHandle);
    }

    @Override
    public List<Map> findByQuery(String q) {
        KeyValueQueryDefinition query = queryManager.newKeyValueDefinition();
        queryManager.setPageLength(PAGE_SIZE);
        query.put(queryManager.newElementLocator(new QName("name")), q);
        // TODO: How to restrict either to XML or JSON document types?
        SearchHandle resultsHandle = new SearchHandle();
        queryManager.search(query, resultsHandle);
        return toSearchResult(resultsHandle);
    }

   


    private JAXBHandle getProductHandle() {
        try {
            JAXBContext context = JAXBContext.newInstance(Map.class);
            return new JAXBHandle(context);
        } catch (JAXBException e) {
            throw new RuntimeException("Unable to create Map JAXB context", e);
        }
    }

    private String getDocId(Long sku) {
        return String.format("/discuss/%d.xml", sku);
    }

    private List<Map> toSearchResult(SearchHandle resultsHandle) {
        List<Map> products = new ArrayList<>();
        for (MatchDocumentSummary summary : resultsHandle.getMatchResults()) {
            JAXBHandle contentHandle = getProductHandle();
            logger.info("  * found {}"+summary.getUri());
            xmlDocumentManager.read(summary.getUri(), contentHandle);
            products.add((Map) contentHandle.get(Map.class));
        }
        return null;
    }

	

	@Override
	public void update(Map model) {
		// TODO Auto-generated method stub
	}

	@Override
	public void replaceInsert(Map model,String fragment) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<Map> searchByExample(String example) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Map> findById(String id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void persist(Map model, DocumentMetadataHandle metadata) {
		// TODO Auto-generated method stub
	}

	@Override
	public Map findOne(Object... params) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
}
