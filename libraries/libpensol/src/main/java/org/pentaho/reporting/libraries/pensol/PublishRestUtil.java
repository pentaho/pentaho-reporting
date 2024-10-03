/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


package org.pentaho.reporting.libraries.pensol;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
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
    ClientConfig clientConfig = new ClientConfig();
    clientConfig.register( MultiPartFeature.class );
    HttpAuthenticationFeature feature = HttpAuthenticationFeature.basic( username, password );
    client = ClientBuilder.newClient( clientConfig ).register( feature );
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
    WebTarget target = client.target( url );
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

      target = ( WebTarget ) target.request( MediaType.MULTIPART_FORM_DATA );
      Response response = target.request().post( Entity.entity( fdmp , MediaType.MULTIPART_FORM_DATA ) );

      if ( response != null ) {
        String message = response.readEntity( String.class );
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
