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
import javax.xml.namespace.QName;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.admin.QueryOptionsManager;
import com.marklogic.client.admin.config.QueryOptionsBuilder;
import com.marklogic.client.io.QueryOptionsHandle;
import com.marklogic.client.io.SearchHandle;
import com.marklogic.client.query.MatchDocumentSummary;
import com.marklogic.client.query.MatchLocation;
import com.marklogic.client.query.QueryManager;
import com.marklogic.client.query.StructuredQueryBuilder;
import com.marklogic.client.query.StructuredQueryDefinition;
import com.marklogic.client.tutorial.topsongs.Util.TutorialProperties;


// illustrates how to construct search criteria as a structure
public class StructuredSearcher {
	public static void main(String[] args)
	throws JAXBException, IOException, URISyntaxException {
		run(Util.loadProperties());
	}
	public static void run(TutorialProperties props)
	throws JAXBException, URISyntaxException, IOException {
		System.out.println("tutorial: "+StructuredSearcher.class.getName());

		String queryOptionsName = "topsongs";

		// install the query options before the first search
		loadQueryOptions(props, queryOptionsName);

		// search against the query options
		runSearch(props, queryOptionsName);
	}
	public static void loadQueryOptions(TutorialProperties props, String queryOptionsName)
	throws JAXBException, URISyntaxException, IOException {
		// create a client with admin permissions
		DatabaseClient dbClient = DatabaseClientFactory.newClient(
				props.host, props.port, props.adminUser, props.adminPassword, props.authType);

		// create the query options manager
		QueryOptionsManager optMgr = dbClient.newServerConfigManager().newQueryOptionsManager();

		// create the builder for query options
		QueryOptionsBuilder optBldr = new QueryOptionsBuilder();

		// create a handle on the built query options
		QueryOptionsHandle optHandle = new QueryOptionsHandle();

		// support constraints on the artistName and writer element indexes 
		optHandle.withConstraints(
			optBldr.constraint("artist",
				optBldr.elementQuery(new QName("artistName"))
			),
			optBldr.constraint("writer",
				optBldr.elementQuery(new QName("writer"))
			)
		);

		// write the query options to the database
		optMgr.writeOptions(queryOptionsName, optHandle);

		System.out.println("wrote "+queryOptionsName+" query options to the server");

		// release the admin client
		dbClient.release();
	}
	public static void runSearch(TutorialProperties props, String queryOptionsName)
	throws JAXBException, URISyntaxException, IOException {
		// create a client with writer permissions
		DatabaseClient dbClient = DatabaseClientFactory.newClient(
				props.host, props.port, props.writerUser, props.writerPassword, props.authType);

		// create the query manager
		QueryManager queryMgr = dbClient.newQueryManager();

		// build structured criteria for the query options
		StructuredQueryBuilder structureBldr = queryMgr.newStructuredQueryBuilder("topsongs");

		// constrain matches to the artist and writer
		StructuredQueryDefinition structuredQry =
			structureBldr.and(
					structureBldr.elementConstraint("artist",
							structureBldr.term("Aretha Franklin")
					),
					structureBldr.elementConstraint("writer",
							structureBldr.term("Otis Redding")
					)
			);

		// populate the search handle with the results
		SearchHandle searchHandle = queryMgr.search(structuredQry, new SearchHandle());

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

		// release the application client
		dbClient.release();
	}
}
