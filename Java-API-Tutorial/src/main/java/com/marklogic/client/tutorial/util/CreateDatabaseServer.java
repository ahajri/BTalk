package com.marklogic.client.tutorial.util;

import java.io.IOException;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLStreamException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.DatabaseClientFactory.Authentication;
import com.marklogic.client.FailedRequestException;
import com.marklogic.client.admin.ExtensionMetadata;
import com.marklogic.client.admin.MethodType;
import com.marklogic.client.admin.ResourceExtensionsManager;
import com.marklogic.client.admin.ResourceExtensionsManager.MethodParameters;
import com.marklogic.client.io.InputStreamHandle;
import com.marklogic.client.tutorial.topsongs.Util;
import com.marklogic.client.tutorial.topsongs.Util.TutorialProperties;

public class CreateDatabaseServer {

	private static TutorialProperties props;

	private static void invokeBootstrapExtension()
			throws ClientProtocolException, IOException {

		DefaultHttpClient client = new DefaultHttpClient();

		client.getCredentialsProvider().setCredentials(
				new AuthScope(props.host, props.port),
				new UsernamePasswordCredentials(props.bootstrapUser,
						props.bootstrapPassword));

		HttpPost post = new HttpPost("http://" + props.host + ":" + props.port
				+ "/v1/resources/bootstrap");

		post.setEntity(new StringEntity(""));
		HttpResponse response = client.execute(post);
		HttpEntity entity = response.getEntity();
		System.out.println("Invoked bootstrapper");
	}

	private static void installBootstrapExtension() throws IOException {

		ResourceExtensionsManager extensionMgr = DatabaseClientFactory
				.newClient(props.host, props.port, props.bootstrapUser,
						props.bootstrapPassword, Authentication.DIGEST)
				.newServerConfigManager().newResourceExtensionsManager();

		InputStreamHandle handle = new InputStreamHandle();

		ExtensionMetadata metadata = new ExtensionMetadata();
		metadata.setTitle("Tutorial Bootstrapper");
		metadata.setDescription("This library creates users and indices for Java Tutorial");
		metadata.setProvider("MarkLogic");
		metadata.setVersion("0.1");

		MethodParameters[] params = new MethodParameters[1];
		params[0] = new MethodParameters(MethodType.POST);

		handle.set(CreateDatabaseServer.class.getClassLoader()
				.getResourceAsStream("bootstrap.xqy"));

		try {
			extensionMgr.writeServices("bootstrap", handle, metadata, params);
		} catch (FailedRequestException e) {
			e.printStackTrace();
		}

	}

	public static void main(String[] args) throws ClientProtocolException,
			IOException, XMLStreamException, FactoryConfigurationError {

		System.out.println("This utility creates a fresh REST server and sets up users for the MarkLogic POJO Tutorial.");

		
		
		props = Util.loadProperties();
		System.out.println("Bootstrapping REST server with username "
				+ props.bootstrapUser);

		 Bootstrapper.main(new String[] { "-configuser", props.bootstrapUser,
		 "-configpassword", props.bootstrapPassword, "-confighost", props.host,
		 "-restserver", props.serverName, "-restport", "" + props.port,
		 "-restdb", props.databaseName });

		installBootstrapExtension();

		System.out.println("Created bootstrapper module.");
		invokeBootstrapExtension();
		System.out.println("Created users and indices.  Setup Complete.");

	}
}
