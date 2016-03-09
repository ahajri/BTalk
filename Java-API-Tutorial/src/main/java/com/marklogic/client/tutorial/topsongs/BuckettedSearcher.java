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
import com.marklogic.client.admin.config.QueryOptions.Facets;
import com.marklogic.client.admin.config.QueryOptions.FragmentScope;
import com.marklogic.client.admin.config.QueryOptionsBuilder;
import com.marklogic.client.io.QueryOptionsHandle;
import com.marklogic.client.io.SearchHandle;
import com.marklogic.client.query.FacetResult;
import com.marklogic.client.query.FacetValue;
import com.marklogic.client.query.QueryManager;
import com.marklogic.client.query.StringQueryDefinition;
import com.marklogic.client.tutorial.topsongs.Util.TutorialProperties;


// illustrates how to summarize facets with buckets
public class BuckettedSearcher {
	public static void main(String[] args)
	throws JAXBException, IOException, URISyntaxException {
		run(Util.loadProperties());
	}
	public static void run(TutorialProperties props)
	throws JAXBException, URISyntaxException, IOException {
		System.out.println("tutorial: "+BuckettedSearcher.class.getName());

		String queryOptionsName = "bucketsongs";

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

		// support facet constraints with limits on the genre and buckets on the week
		optHandle.withConstraints(
				optBldr.constraint("genre",
					optBldr.range(
						optBldr.elementRangeIndex(
							new QName("genre"),
								optBldr.stringRangeType(
									"http://marklogic.com/collation/"
							)
						),
						Facets.FACETED,
						FragmentScope.DOCUMENTS,
						null,
						"frequency-order",
						"descending",
						"limit=10"
					)
				),
				optBldr.constraint("week",
					optBldr.range(
						optBldr.elementRangeIndex(
							new QName("week"),
							optBldr.rangeType("xs:date")
						),
						Facets.FACETED,
						FragmentScope.DOCUMENTS,
						optBldr.buckets(
							optBldr.bucket(
								"1940s", "40s", "1940-01-01", "1950-01-01"
							),
							optBldr.bucket(
								"1950s", "50s", "1950-01-01", "1960-01-01"
							),
							optBldr.bucket(
								"1960s", "60s", "1960-01-01", "1970-01-01"
							),
							optBldr.bucket(
								"1970s", "70s", "1970-01-01", "1980-01-01"
							),
							optBldr.bucket(
								"1980s", "80s", "1980-01-01", "1990-01-01"
							),
							optBldr.bucket(
								"1990s", "90s", "1990-01-01", "2000-01-01"
							),
							optBldr.bucket(
								"2000s", "00s", "2000-01-01", "2010-01-01"
							)
						)
					)
				)
			);
		optHandle.setReturnResults(false);

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

		// specify criteria for the query options
		StringQueryDefinition stringQry = queryMgr.newStringDefinition(queryOptionsName);

		// match the subset of documents with the term for facet analysis
		stringQry.setCriteria("Grammy");

		// populate the search handle with the results
		SearchHandle searchHandle = queryMgr.search(stringQry, new SearchHandle());

		// iterate over the facets
    	for (FacetResult facet: searchHandle.getFacetResults()) {
    		System.out.println("facet: "+facet.getName());
    		// iterate over the values of the facet
    		for (FacetValue value: facet.getFacetValues()) {
    			System.out.println("    "+value.getLabel()+" = "+value.getCount());
    		}
		}

		// release the application client
		dbClient.release();
	}
}
