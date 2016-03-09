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

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.document.XMLDocumentManager;
import com.marklogic.client.io.JAXBHandle;
import com.marklogic.client.tutorial.topsongs.Util.TutorialProperties;
import com.marklogic.client.tutorial.topsongs.jaxb.TopSong;


// illustrates how to read a POJO from the database
public class POJOReader {
	public static void main(String[] args)
	throws JAXBException, IOException, URISyntaxException {
		run(Util.loadProperties());
	}
	public static void run(TutorialProperties props)
	throws JAXBException, URISyntaxException, IOException {
		System.out.println("tutorial: "+POJOReader.class.getName());

		// create the client
		DatabaseClient dbClient = DatabaseClientFactory.newClient(
				props.host, props.port, props.writerUser, props.writerPassword, props.authType);

		// create the document manager
		XMLDocumentManager docMgr = dbClient.newXMLDocumentManager();

		// initialize JAXB for processing the POJO class
		JAXBContext context = JAXBContext.newInstance(TopSong.class);

		// identify the database document
    	String docId = "/topsongs/Aretha-Franklin+Respect.xml";

		// create a handle for reading from the database
		JAXBHandle docHandle = new JAXBHandle(context);

		// read the POJO from the database
    	docMgr.read(docId, docHandle);

    	// get the POJO from the handle
    	TopSong song = (TopSong) docHandle.get();

		System.out.println("document: "+docId);
		System.out.println("    title     | "+song.getTitle());
		System.out.println("    artist    | "+song.getArtist().getArtistName());
		System.out.print(  "    writers  ");
		for (String writer: song.getWriters().getWriter()) {
			System.out.print(" | "+writer);
		}
		System.out.println();
		System.out.print(  "    producers");
		for (String producer: song.getProducers().getProducer()) {
			System.out.print(" | "+producer);
		}
		System.out.println();
		System.out.print(  "    genres   ");
		for (String genre: song.getGenres().getGenre()) {
			System.out.print(" | "+genre);
		}
		System.out.println();
		System.out.print(  "    weeks    ");
		for (String week: song.getWeeks().getWeek()) {
			System.out.print(" | "+week);
		}
		System.out.println();

		// release the client
		dbClient.release();
	}
}
