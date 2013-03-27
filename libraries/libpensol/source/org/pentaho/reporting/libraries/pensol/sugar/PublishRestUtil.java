package org.pentaho.reporting.libraries.pensol.sugar;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.ws.rs.core.MediaType;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.WebResource.Builder;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;
import com.sun.jersey.api.json.JSONConfiguration;
import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataMultiPart;
import com.sun.jersey.multipart.impl.MultiPartWriter;

public class PublishRestUtil {

	private static final Log logger = LogFactory.getLog(PublishRestUtil.class);
	
	public static final String REPO_FILES_IMPORT = "api/repo/files/import";
	public static final int HTTP_RESPONSE_OK = 200;

	private String baseUrl;
	private String username;
	private String password;

	private Client client = null;

	public PublishRestUtil(String baseUrl, String username, String password) {
		this.baseUrl = baseUrl;
		this.username = username;
		this.password = password;

		initRestService();
	}
	
	/**
	   * Used for REST Jersey calls
	   */
	  private void initRestService() {
	    ClientConfig clientConfig = new DefaultClientConfig();
	    clientConfig.getClasses().add(MultiPartWriter.class);
	    clientConfig.getFeatures().put(JSONConfiguration.FEATURE_POJO_MAPPING, Boolean.TRUE);
	    client = Client.create(clientConfig);
	    client.addFilter(new HTTPBasicAuthFilter(username, password));
	  }

	/**
	 * Uses /repos/files/import service
	 * 
	 * @param filePath
	 * @param data
	 * @param overwriteIfExists
	 * @return
	 */
	public void publishFile(String filePath, byte[] data, boolean overwriteIfExists) throws IOException {
		
		if(filePath == null || data == null || data.length == 0){
			throw new IOException("missing file path and/or data"); 
		}
		
		String repositoryPath = null;
		String fileName = null;
		
		int fileNameIdx = filePath.lastIndexOf("/");
		if(fileNameIdx >= 0){
			repositoryPath = filePath.substring(0, fileNameIdx);
			fileName = filePath.substring(fileNameIdx + 1);
		}
		
		try{
			publishFile(repositoryPath, fileName, new ByteArrayInputStream(data), true);
		}catch(Exception ex){
			logger.error(ex);
			throw new IOException(ex);
		}
	}

	/**
	 * Uses /repos/files/import service
	 * 
	 * @param repositoryPath
	 * @param fileName
	 * @param fileInputStream
	 * @param overwriteIfExists
	 */
	public void publishFile(String repositoryPath, String fileName, InputStream fileInputStream, boolean overwriteIfExists) throws IOException {

		String url = baseUrl.endsWith("/") ? (baseUrl + REPO_FILES_IMPORT) : (baseUrl + "/" + REPO_FILES_IMPORT);
		
		WebResource resource = client.resource(url);
		try {
			FormDataMultiPart part = new FormDataMultiPart();
			part.field("importDir", repositoryPath, MediaType.MULTIPART_FORM_DATA_TYPE);
			part.field("fileUpload", fileInputStream, MediaType.MULTIPART_FORM_DATA_TYPE);
			part.field("overwriteFile", String.valueOf(overwriteIfExists), MediaType.MULTIPART_FORM_DATA_TYPE);
			part.field("overwriteAclPermissions", "true", MediaType.MULTIPART_FORM_DATA_TYPE);
			part.field("applyAclPermissions", "true", MediaType.MULTIPART_FORM_DATA_TYPE);
			part.field("retainOwnership", "true", MediaType.MULTIPART_FORM_DATA_TYPE);
			part.field("charSet", "UTF-8");

			part.getField("fileUpload").setContentDisposition(FormDataContentDisposition.name("fileUpload").fileName(fileName).build());

			ClientResponse response = resource.type(MediaType.MULTIPART_FORM_DATA).post(ClientResponse.class, part);

			if(response != null){
				String message = response.getEntity(String.class);
				logger.info(message);
				
				if (HTTP_RESPONSE_OK != response.getStatus()) {
					throw new IOException(message);
				}	
			}			
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
			throw new IOException(ex);
		}
	}
}
