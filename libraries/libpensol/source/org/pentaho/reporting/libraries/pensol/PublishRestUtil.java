/*!
* This program is free software; you can redistribute it and/or modify it under the
* terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
* Foundation.
*
* You should have received a copy of the GNU Lesser General Public License along with this
* program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
* or from the Free Software Foundation, Inc.,
* 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
*
* This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
* without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
* See the GNU Lesser General Public License for more details.
*
* Copyright (c) 2002-2013 Pentaho Corporation..  All rights reserved.
*/

package org.pentaho.reporting.libraries.pensol;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;

import javax.ws.rs.core.MediaType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
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
	 * @return http response code
	 */
	public int publishFile(String filePath, byte[] data, boolean overwriteIfExists) throws IOException {
		
		if(filePath == null || data == null || data.length == 0){
			throw new IOException("missing file path and/or data"); 
		}
		String path = filePath;
		String fileName = null;
		
		int fileNameIdx = filePath.lastIndexOf("/");
		if(fileNameIdx >= 0){
			fileName = filePath.substring(fileNameIdx + 1);
      path = filePath.substring(0,fileNameIdx);
		}
		
		try{
			return publishFile(path, fileName, new ByteArrayInputStream(data), true);
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
	 * @return http response code
	 */
	public int publishFile(String repositoryPath, String fileName, InputStream fileInputStream, boolean overwriteIfExists) throws IOException {

		String url = baseUrl.endsWith("/") ? (baseUrl + REPO_FILES_IMPORT) : (baseUrl + "/" + REPO_FILES_IMPORT);
		
		WebResource resource = client.resource(url);
		int responseCode = 504;
		try {
			FormDataMultiPart part = new FormDataMultiPart();
			part.field("importDir", repositoryPath, MediaType.MULTIPART_FORM_DATA_TYPE);
			part.field("fileUpload", fileInputStream, MediaType.MULTIPART_FORM_DATA_TYPE);
			part.field("overwriteFile", String.valueOf(overwriteIfExists), MediaType.MULTIPART_FORM_DATA_TYPE);

			part.getField("fileUpload").setContentDisposition(FormDataContentDisposition.name("fileUpload").fileName(URLEncoder.encode( fileName, "utf-8") ).build());

      WebResource.Builder builder = resource.type(MediaType.MULTIPART_FORM_DATA);
			ClientResponse response =  builder.post(ClientResponse.class, part);

			if(response != null){
				String message = response.getEntity(String.class);
				logger.info(message);
				responseCode = response.getStatus();
			}			
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
			//throw new IOException(ex);
		}
		return responseCode;
	}
}
