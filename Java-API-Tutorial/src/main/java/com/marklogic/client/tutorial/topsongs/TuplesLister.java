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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.xml.bind.JAXBException;
import javax.xml.namespace.QName;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.admin.QueryOptionsManager;
import com.marklogic.client.admin.config.QueryOptionsBuilder;
import com.marklogic.client.io.QueryOptionsHandle;
import com.marklogic.client.io.TuplesHandle;
import com.marklogic.client.query.QueryManager;
import com.marklogic.client.query.StringQueryDefinition;
import com.marklogic.client.query.Tuple;
import com.marklogic.client.query.TypedDistinctValue;
import com.marklogic.client.query.ValuesDefinition;
import com.marklogic.client.tutorial.topsongs.Util.TutorialProperties;


// illustrates how to list co-occurrence tuples from indexes for a set of documents
public class TuplesLister {
	public static void main(String[] args)
	throws JAXBException, IOException, URISyntaxException {
		run(Util.loadProperties());
	}
	public static void run(TutorialProperties props)
	throws JAXBException, URISyntaxException, IOException {
		System.out.println("tutorial: "+TuplesLister.class.getName());

		String queryOptionsName = "tuplesongs";

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

		// support a tuples constraint on co-occurrences of the week and genre indexes
		optHandle.withTuples(
				optBldr.tuples("week-genre",
					optBldr.tupleSources(
						optBldr.range(
							optBldr.elementRangeIndex(
								new QName("week"),
								optBldr.rangeType("xs:date")
							)
						),
						optBldr.range(
							optBldr.elementRangeIndex(
								new QName("genre"),
								optBldr.stringRangeType(
									"http://marklogic.com/collation/"
								)
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

		// specify the query options and constraint for the tuples
		ValuesDefinition valdef = queryMgr.newValuesDefinition("week-genre", queryOptionsName);

		// constrain the tuple values to the subset of documents for the producer
		StringQueryDefinition stringQry = queryMgr.newStringDefinition();
		stringQry.setCriteria("producer:\""+producerName+"\"");
		valdef.setQueryDefinition(stringQry);

		// populate the values handle with the values from the genre index
		TuplesHandle tuplesHandle = queryMgr.tuples(valdef, new TuplesHandle());

		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

		// iterate over the tuples
		System.out.println("Hit song genres by week for producer "+producerName+":");
		for (Tuple tuple: tuplesHandle.getTuples()) {
			System.out.print("    "+tuple.getCount());
			// iterate over the values in the tuple
			for (TypedDistinctValue value: tuple.getValues()) {
				System.out.print(" ");
				// format the value based on its type
				String type = value.getType();
				if ("xs:date".equals(type)) {
					System.out.print(
						dateFormat.format(
							value.get(Calendar.class).getTime()
						)
					);
				} else if ("xs:string".equals(type)) {
					System.out.print(value.get(String.class));
				}
			}
			System.out.println();
		}

		// release the application client
		dbClient.release();
	}
}
