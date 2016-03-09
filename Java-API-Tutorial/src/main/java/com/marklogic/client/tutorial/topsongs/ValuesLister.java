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
import com.marklogic.client.io.ValuesHandle;
import com.marklogic.client.query.CountedDistinctValue;
import com.marklogic.client.query.QueryManager;
import com.marklogic.client.query.StringQueryDefinition;
import com.marklogic.client.query.ValuesDefinition;
import com.marklogic.client.tutorial.topsongs.Util.TutorialProperties;


// illustrates how to list values from an index for a set of documents
public class ValuesLister {
	public static void main(String[] args)
	throws JAXBException, IOException, URISyntaxException {
		run(Util.loadProperties());
	}
	public static void run(TutorialProperties props)
	throws JAXBException, URISyntaxException, IOException {
		System.out.println("tutorial: "+ValuesLister.class.getName());

		String queryOptionsName = "valuesongs";

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

		// support a constraint on the producer element indexes 
		optHandle.withConstraints(
				optBldr.constraint("producer",
						optBldr.elementQuery(new QName("producer"))
				)
			);

		// support a values constraint on the genre range index
		optHandle.withValues(
				optBldr.values("genre",
					optBldr.range(
						optBldr.elementRangeIndex(
							new QName("genre"),
							optBldr.stringRangeType(
								"http://marklogic.com/collation/"
							)
						)
					)
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
		String producerName = "Quincy Jones";

		// create a client with writer permissions
		DatabaseClient dbClient = DatabaseClientFactory.newClient(
				props.host, props.port, props.writerUser, props.writerPassword, props.authType);

		// create the query manager
		QueryManager queryMgr = dbClient.newQueryManager();

		// specify the query options and constraint for the values
		ValuesDefinition valdef = queryMgr.newValuesDefinition("genre", queryOptionsName);

		// constrain the values to the subset of documents for the producer
		StringQueryDefinition stringQry = queryMgr.newStringDefinition();
		stringQry.setCriteria("producer:\""+producerName+"\"");
		valdef.setQueryDefinition(stringQry);

		// populate the values handle with the values from the genre index
		ValuesHandle genreHandle = queryMgr.values(valdef, new ValuesHandle());

		// iterate over the genre values
		System.out.println("Hit songs per genre for producer "+producerName+":");
		for (CountedDistinctValue value: genreHandle.getValues()) {
			System.out.println(
				"    "+value.getCount()+" "+
				    value.get("xs:string", String.class)
				);
		}

		// release the application client
		dbClient.release();
	}
}
