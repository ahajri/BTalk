/*
 * Copyright 2012 MarkLogic Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.marklogic.client.tutorial.topsongs;

import java.io.IOException;
import java.net.URISyntaxException;

import javax.xml.bind.JAXBException;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.io.SearchHandle;
import com.marklogic.client.query.MatchDocumentSummary;
import com.marklogic.client.query.MatchLocation;
import com.marklogic.client.query.QueryManager;
import com.marklogic.client.query.StringQueryDefinition;
import com.marklogic.client.tutorial.topsongs.Util.TutorialProperties;

// illustrates how to search based on string phrase criteria
public class StringSearcher {
	public static void main(String[] args)
	throws JAXBException, IOException, URISyntaxException {
		run(Util.loadProperties());
	}
	public static void run(TutorialProperties props)
	throws JAXBException, URISyntaxException, IOException {
		System.out.println("tutorial: "+StringSearcher.class.getName());

		// create the client
		DatabaseClient dbClient = DatabaseClientFactory.newClient(
				props.host, props.port, props.writerUser, props.writerPassword, props.authType);

		// create the query manager
		QueryManager queryMgr = dbClient.newQueryManager();

		// search criteria of two phrases
		StringQueryDefinition stringQry = queryMgr.newStringDefinition();
		stringQry.setCriteria("\"Aretha Franklin\" AND \"Otis Redding\"");

		// populate the search handle with the results
		SearchHandle searchHandle = queryMgr.search(stringQry, new SearchHandle());

		// iterate over the matched documents
    	for (MatchDocumentSummary docSum: searchHandle.getMatchResults()) {
    		// iterate over the matched locations in the current document
    		System.out.println("document: "+docSum.getUri());
    		for (MatchLocation docLoc: docSum.getMatchLocations()) {
    			// format the matched location and text
    			System.out.println("    location: "+docLoc.getPath().replaceFirst("^[^)]*\\)", ""));
    			System.out.println("    matched:  "+docLoc.getAllSnippetText().trim());
    		}
		}

		// release the client
		dbClient.release();
	}
}
