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
* Copyright (c) 2002-2018 Hitachi Vantara..  All rights reserved.
*/

package org.pentaho.reporting.libraries.pensol;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;
import com.sun.jersey.api.json.JSONConfiguration;
import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataBodyPart;
import com.sun.jersey.multipart.FormDataMultiPart;
import com.sun.jersey.multipart.impl.MultiPartWriter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.Properties;

import static javax.ws.rs.core.MediaType.MULTIPART_FORM_DATA_TYPE;
import static javax.ws.rs.core.MediaType.APPLICATION_XML_TYPE;

public class PublishRestUtil {

  private static final Log logger = LogFactory.getLog( PublishRestUtil.class );

  public static final String REPORT_TITLE_KEY = "reportTitle";
  public static final String OVERWRITE_FILE_KEY = "overwriteFile";
  public static final String IMPORT_PATH_KEY = "importPath";

  public static final String REPO_FILES_IMPORT = "api/repo/publish/file";

  private static final String REPO_FILES_IMPORT_WITH_OPTIONS = "api/repo/publish/fileWithOptions";

  private final String baseUrl;
  private final String username;
  private final String password;

  private Client client = null;

  public PublishRestUtil( String baseUrl, String username, String password ) {
    this.baseUrl = baseUrl.endsWith( "/" ) ? baseUrl : baseUrl + '/';
    this.username = username;
    this.password = password;

    initRestService();
  }

  /**
   * Used for REST Jersey calls
   */
  private void initRestService() {
    ClientConfig clientConfig = new DefaultClientConfig();
    clientConfig.getClasses().add( MultiPartWriter.class );
    clientConfig.getFeatures().put( JSONConfiguration.FEATURE_POJO_MAPPING, Boolean.TRUE );
    client = Client.create( clientConfig );
    client.addFilter( new HTTPBasicAuthFilter( username, password ) );
  }

  /**
   * @param filePath will be splitted into path and file name and then pass it to #publishFile(String, String, InputStream, Properties)
   * @see #publishFile(String, String, InputStream, Properties)
   */
  public int publishFile( String filePath, byte[] data, Properties fileProperties ) throws IOException {
    if ( filePath == null || data == null || data.length == 0 ) {
      throw new IOException( "missing file path and/or data" );
    }
    String path = filePath;
    String fileName = null;

    int fileNameIdx = filePath.lastIndexOf( "/" );
    if ( fileNameIdx >= 0 ) {
      fileName = filePath.substring( fileNameIdx + 1 );
      path = filePath.substring( 0, fileNameIdx );
    }
    try {
      return publishFile( path, fileName, new ByteArrayInputStream( data ), fileProperties );
    } catch ( Exception ex ) {
      logger.error( ex );
      throw new IOException( ex );
    }
  }

  /**
   * Uses {@code /repos/publish/fileWithOptions} service. Possible response codes are:
   * <ul>
   *   <li>{@code 200} - upload was performed successfully</li>
   *   <li>{@code 401} - upload failed due to insufficient privileges</li>
   *   <li>{@code 422} - upload failed due to invalid {@code fileName} (most likely it contains prohibited symbols, e.g. '\n')</li>
   *   <li>{@code 500} - upload failed due to some internal server error</li>
   * </ul>
   *
   * @param repositoryPath    repository folder where to upload the file; must be separated with /
   * @param fileName          uploaded file's name; must not contain prohibited symbols
   * @param fileProperties    the properties which should be applied to the file on repository
   * @param fileInputStream   file's data stream
   * @return http response code
   * @see org.pentaho.platform.web.http.api.resources.RepositoryPublishResource
   */
  public int publishFile( String repositoryPath, String fileName, InputStream fileInputStream, Properties fileProperties ) throws IOException {
    String url = baseUrl + REPO_FILES_IMPORT_WITH_OPTIONS;
    WebResource resource = client.resource( url );
    int responseCode = 504;
    try ( ByteArrayOutputStream baos = new ByteArrayOutputStream() ) {
      FormDataMultiPart fdmp = new FormDataMultiPart();

      String pathEncoded = URLEncoder.encode( repositoryPath + "/" + fileName, "UTF-8" );
      String nameEncoded = URLEncoder.encode( fileName, "UTF-8" );

      if ( fileProperties == null ) {
        fileProperties = new Properties();
      }
      fileProperties.storeToXML( baos, "file properties", "UTF-8" );

      fdmp.bodyPart( new FormDataBodyPart( "properties", baos.toString( "UTF-8"  ), APPLICATION_XML_TYPE ) );
      fdmp.field( IMPORT_PATH_KEY, pathEncoded, MULTIPART_FORM_DATA_TYPE );
      fdmp.field( "fileUpload", fileInputStream, MULTIPART_FORM_DATA_TYPE );
      fdmp.getField( "fileUpload" ).setContentDisposition( FormDataContentDisposition.name( "fileUpload" ).fileName( nameEncoded ).build() );

      WebResource.Builder builder = resource.type( MULTIPART_FORM_DATA_TYPE );
      ClientResponse response = builder.post( ClientResponse.class, fdmp );

      if ( response != null ) {
        String message = response.getEntity( String.class );
        logger.info( message );
        responseCode = response.getStatus();
      }
    } catch ( Exception ex ) {
      logger.error( ex.getMessage(), ex );
      //throw new IOException(ex);
    }
    return responseCode;
  }

  /**
   * We need to keep the options of report. please use {@link #publishFile(String, byte[], Properties)}
   * We keep the method for backward compatibility 
   * 
   * @since pentaho 8.1
   */
  @Deprecated
  public int publishFile( String filePath, byte[] data, boolean overwriteIfExists ) throws IOException {
    Properties fileProperties =  new Properties();
    fileProperties.setProperty( OVERWRITE_FILE_KEY, String.valueOf( overwriteIfExists ) );
    return publishFile( filePath, data, fileProperties );
  }

  /**
   * We need to keep the options of report. please use {@link #publishFile(String, String, InputStream, Properties)}
   * We keep the method for backward compatibility 
   * 
   * @since pentaho 8.1
   */
  @Deprecated
  public int publishFile( String repositoryPath, String fileName, InputStream fileInputStream, boolean overwriteIfExists ) throws IOException {
    Properties fileProperties = new Properties();
    fileProperties.setProperty( OVERWRITE_FILE_KEY, String.valueOf( overwriteIfExists ) );
    return publishFile( repositoryPath, fileName, fileInputStream, fileProperties );
  }
}
