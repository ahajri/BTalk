package com.ahajri.btalk.data.repository;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.namespace.QName;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ahajri.btalk.data.domain.Discussion;
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
public class DiscussionXmlRepository implements IRepository<Discussion> {

    private static final Logger logger = Logger.getLogger(DiscussionXmlRepository.class);

    public static final String COLLECTION_REF = "/products.xml";
    public static final int PAGE_SIZE = 10;

    @Autowired
    protected QueryManager queryManager;

    @Autowired
    protected XMLDocumentManager xmlDocumentManager;

    @Override
    public void persist(Discussion Discussion) {
        // Add this document to a dedicated collection for later retrieval
        DocumentMetadataHandle metadata = new DocumentMetadataHandle();
        metadata.getCollections().add(COLLECTION_REF);
        JAXBHandle contentHandle = getProductHandle();
        contentHandle.set(Discussion);
        xmlDocumentManager.write("{}", metadata, contentHandle);
    }

    @Override
    public void remove(Discussion d) {
        xmlDocumentManager.delete("");
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
    public List<Discussion> findAll() {
        StructuredQueryBuilder sb = queryManager.newStructuredQueryBuilder();
        StructuredQueryDefinition criteria = sb.collection(COLLECTION_REF);

        SearchHandle resultsHandle = new SearchHandle();
        queryManager.search(criteria, resultsHandle);
        return toSearchResult(resultsHandle);
    }

    @Override
    public List<Discussion> findByQuery(String q) {
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
            JAXBContext context = JAXBContext.newInstance(Discussion.class);
            return new JAXBHandle(context);
        } catch (JAXBException e) {
            throw new RuntimeException("Unable to create Discussion JAXB context", e);
        }
    }

    private String getDocId(Long sku) {
        return String.format("/products/%d.xml", sku);
    }

    private List<Discussion> toSearchResult(SearchHandle resultsHandle) {
        List<Discussion> products = new ArrayList<>();
        for (MatchDocumentSummary summary : resultsHandle.getMatchResults()) {
            JAXBHandle contentHandle = getProductHandle();
            logger.info("  * found {}"+summary.getUri());
            xmlDocumentManager.read(summary.getUri(), contentHandle);
            products.add((Discussion) contentHandle.get(Discussion.class));
        }
        return null;
    }

	@Override
	public Discussion findOne(Object... params) {
		return null;
	}

	@Override
	public void update(Discussion model) {
		// TODO Auto-generated method stub
	}

	@Override
	public void replaceInsert(Discussion model,String fragment) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<Discussion> searchByExample(String example) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Discussion> findById(String id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void persist(Discussion model, DocumentMetadataHandle metadata) {
		// TODO Auto-generated method stub
	}
}
