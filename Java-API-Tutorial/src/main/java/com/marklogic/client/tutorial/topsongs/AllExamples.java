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

import com.marklogic.client.tutorial.topsongs.Util.TutorialProperties;

// runs all of the examples in one pass in the recommended sequence
public class AllExamples {
	public static void main(String[] args)
	throws JAXBException, IOException, URISyntaxException {
		TutorialProperties props = Util.loadProperties();

		POJOWriter.run(props);
		POJOReader.run(props);
		KeyValueSearcher.run(props);
		StringSearcher.run(props);
		ConstrainedSearcher.run(props);
		StructuredSearcher.run(props);
		FacettedSearcher.run(props);
		BuckettedSearcher.run(props);
		ValuesLister.run(props);
		TuplesLister.run(props);
	}
}
