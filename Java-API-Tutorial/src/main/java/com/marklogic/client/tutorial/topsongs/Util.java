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
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.Properties;

import com.marklogic.client.DatabaseClientFactory.Authentication;

public class Util {
	static public class TutorialProperties {
		public String         host;
		public int            port = -1;
		public String         adminUser;
		public String         adminPassword;
		public String         writerUser;
		public String         writerPassword;
		public Authentication authType;
		public String         serverName;
		public String         databaseName;
		public String         bootstrapUser;
		public String         bootstrapPassword;
		
		public TutorialProperties(Properties props) {
			super();
			host           = props.getProperty("tutorial.host");
			port           = Integer.parseInt(props.getProperty("tutorial.port"));
			adminUser      = props.getProperty("tutorial.admin_user");
			adminPassword  = props.getProperty("tutorial.admin_password");
			writerUser     = props.getProperty("tutorial.writer_user");
			writerPassword = props.getProperty("tutorial.writer_password");
			authType       = Authentication.valueOf(
					props.getProperty("tutorial.authentication_type").toUpperCase()
					);
			serverName = props.getProperty("tutorial.server_name");
			databaseName = props.getProperty("tutorial.database_name");
			bootstrapUser = props.getProperty("tutorial.bootstrap_user");
			bootstrapPassword = props.getProperty("tutorial.bootstrap_password");
		}
	}
	public static TutorialProperties loadProperties() throws IOException {
		String propsName = "Tutorial.properties";

		InputStream propsStream = openStream(propsName);
		if (propsStream == null)
			throw new IOException("Could not read properties "+propsName);

		Properties props = new Properties();
		props.load(propsStream);

		return new TutorialProperties(props);
	}

	public static InputStream openStream(String fileName) throws IOException {
		return Util.class.getClassLoader().getResourceAsStream(fileName);
	}
	public static File locateFile(String fileName) throws URISyntaxException {
		return new File(Util.class.getClassLoader().getResource(fileName).toURI());
	}
}
