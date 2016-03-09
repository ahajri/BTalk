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

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.document.XMLDocumentManager;
import com.marklogic.client.io.JAXBHandle;
import com.marklogic.client.tutorial.topsongs.Util.TutorialProperties;
import com.marklogic.client.tutorial.topsongs.jaxb.TopSong;

// illustrates how to write POJOs to the database
public class POJOWriter {
	public static void main(String[] args)
	throws JAXBException, IOException, URISyntaxException {
		run(Util.loadProperties());
	}
	public static void run(TutorialProperties props)
	throws JAXBException, URISyntaxException, IOException {
		System.out.println("tutorial: "+POJOWriter.class.getName());

		// locate the directory containing the serialized POJOS
		File inputDir = Util.locateFile("data"+File.separator+"topsongs");
		File[] songfiles = inputDir.listFiles();
		if (songfiles == null || songfiles.length == 0) {
			throw new IOException("could not find files to write");
		}

		// create the client
		DatabaseClient dbClient = DatabaseClientFactory.newClient(
			props.host, props.port, props.writerUser, props.writerPassword,
			props.authType);

		// create the document manager
		XMLDocumentManager docMgr = dbClient.newXMLDocumentManager();

		// initialize JAXB for processing the POJO class
		JAXBContext context = JAXBContext.newInstance(TopSong.class);

		// create a handle on the POJOs for writing to the database
		JAXBHandle writeHandle = new JAXBHandle(context);

		// iterate over the serialized POJOs
		Unmarshaller u = context.createUnmarshaller();
		for (File songfile: songfiles) {
			// an identifier for the POJO in the database
			String docId = "/topsongs/"+songfile.getName();

			System.out.println("deserializing and writing "+docId);

			// deserialize the POJO
			TopSong song = (TopSong) u.unmarshal(songfile);

			// provide a handle for the POJO
			writeHandle.set(song);

			// write the POJO to the database
			docMgr.write(docId, writeHandle);
		}

		System.out.println("wrote "+songfiles.length+" top songs");

		// release the client
		dbClient.release();
	}
}
