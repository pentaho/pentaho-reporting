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

package org.pentaho.reporting.designer.extensions.pentaho.drilldown;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.vfs2.FileContent;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystem;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.VFS;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.pentaho.reporting.designer.core.ReportDesignerContext;
import org.pentaho.reporting.designer.core.auth.AuthenticationData;
import org.pentaho.reporting.designer.core.editor.ReportDocumentContext;
import org.pentaho.reporting.designer.core.editor.drilldown.DrillDownParameterRefreshEvent;
import org.pentaho.reporting.designer.core.editor.drilldown.DrillDownParameterRefreshListener;
import org.pentaho.reporting.designer.core.editor.drilldown.DrillDownParameterTable;
import org.pentaho.reporting.designer.core.editor.drilldown.model.DrillDownParameter;
import org.pentaho.reporting.designer.core.editor.drilldown.model.Parameter;
import org.pentaho.reporting.designer.core.editor.drilldown.model.ParameterDocument;
import org.pentaho.reporting.designer.core.settings.WorkspaceSettings;
import org.pentaho.reporting.designer.core.util.exceptions.UncaughtExceptionsModel;
import org.pentaho.reporting.designer.extensions.pentaho.repository.actions.AuthenticatedServerTask;
import org.pentaho.reporting.designer.extensions.pentaho.repository.actions.LoginTask;
import org.pentaho.reporting.designer.extensions.pentaho.repository.util.PublishException;
import org.pentaho.reporting.designer.extensions.pentaho.repository.util.PublishUtil;
import org.pentaho.reporting.engine.classic.core.parameters.ParameterAttributeNames;
import org.pentaho.reporting.engine.classic.core.util.HttpClientManager;
import org.pentaho.reporting.engine.classic.core.util.HttpClientUtil;
import org.pentaho.reporting.libraries.base.util.IOUtils;
import org.pentaho.reporting.libraries.base.util.StringUtils;
import org.pentaho.reporting.libraries.designtime.swing.background.BackgroundCancellableProcessHelper;
import org.pentaho.reporting.libraries.designtime.swing.background.GenericCancelHandler;
import org.pentaho.reporting.libraries.formatting.FastMessageFormat;
import org.pentaho.reporting.libraries.pensol.vfs.WebSolutionFileSystem;
import org.pentaho.reporting.libraries.resourceloader.Resource;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

import javax.swing.SwingUtilities;
import java.awt.Component;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class PentahoParameterRefreshHandler implements DrillDownParameterRefreshListener {
  private class UpdateRequestParamsTask implements AuthenticatedServerTask {
    private RequestParamsFromServerTask requestParamsFromServerTask;
    private Component uiContext;
    private AuthenticationData loginData;
    private DrillDownParameter[] existingParameters;

    private UpdateRequestParamsTask( final RequestParamsFromServerTask requestParamsFromServerTask,
                                     final Component uiContext, final DrillDownParameterRefreshEvent event ) {
      this.requestParamsFromServerTask = requestParamsFromServerTask;
      this.uiContext = uiContext;
      this.existingParameters = event.getParameter();
    }

    public void setLoginData( final AuthenticationData loginData, final boolean storeUpdate ) {
      this.loginData = loginData;
      requestParamsFromServerTask.setLoginData( loginData, storeUpdate );
    }

    /**
     * When an object implementing interface <code>Runnable</code> is used to create a thread, starting the thread
     * causes the object's <code>run</code> method to be called in that separately executing thread.
     * <p/>
     * The general contract of the method <code>run</code> is that it may take any action whatsoever.
     *
     * @see Thread#run()
     */
    public void run() {
      if ( loginData == null ) {
        return;
      }

      final ReportDocumentContext reportRenderContext = reportDesignerContext.getActiveContext();
      final Object o = reportRenderContext.getProperties().get( "pentaho-login-url" );
      if ( o == null ) {
        reportRenderContext.getProperties().put( "pentaho-login-url", loginData.getUrl() );
      }

      final Thread loginThread = new Thread( requestParamsFromServerTask );
      loginThread.setName( "Request-parameter-from-server" );
      loginThread.setDaemon( true );
      loginThread.setPriority( Thread.MIN_PRIORITY );

      final GenericCancelHandler cancelHandler = new GenericCancelHandler( loginThread );
      BackgroundCancellableProcessHelper.executeProcessWithCancelDialog( loginThread, cancelHandler, uiContext,
        "Requesting Parameter Information .." );
      if ( cancelHandler.isCancelled() ) {
        return;
      }

      final Exception error = requestParamsFromServerTask.getError();
      if ( error != null ) {
        UncaughtExceptionsModel.getInstance().addException( error );
        logger.warn( "Failed to read parameter from server", error );
        return;
      }

      if ( requestParamsFromServerTask.getParameters() == null ) {
        logger.warn( "Failed to read parameter from server: File does not exist." );
        return;
      }

      final Parameter[] parameters = requestParamsFromServerTask.getParameters();

      final HashMap<String, DrillDownParameter> parameterFromServer = new HashMap<String, DrillDownParameter>();
      final ArrayList<DrillDownParameter> drillDownParameters = new ArrayList<DrillDownParameter>();
      for ( int i = 0; i < parameters.length; i++ ) {
        final Parameter parameter = parameters[ i ];
        final DrillDownParameter drillDownParameter = new DrillDownParameter( parameter.getName() );
        if ( "system".equals( parameter.getAttribute( ParameterAttributeNames.Core.PARAMETER_GROUP ) )
          || "subscription".equals( parameter.getAttribute( ParameterAttributeNames.Core.PARAMETER_GROUP ) )
          || "output-target".equals( parameter.getName() ) ) {
          drillDownParameter.setType( DrillDownParameter.Type.SYSTEM );
        } else {
          drillDownParameter.setType( DrillDownParameter.Type.PREDEFINED );
        }

        if ( "false".equals( parameter.getAttribute( ParameterAttributeNames.Core.NAMESPACE,
          ParameterAttributeNames.Core.PREFERRED ) ) ) {
          drillDownParameter.setPreferred( false );
        } else {
          drillDownParameter.setPreferred( true );
        }

        drillDownParameters.add( drillDownParameter );
        parameterFromServer.put( parameter.getName(), drillDownParameter );
      }

      for ( int i = 0; i < existingParameters.length; i++ ) {
        final DrillDownParameter parameter = existingParameters[ i ];
        if ( parameterFromServer.containsKey( parameter.getName() ) ) {
          // this parameter also exists in the listing from the server, so rescue its contents
          final DrillDownParameter existingOne = parameterFromServer.get( parameter.getName() );
          existingOne.setFormulaFragment( parameter.getFormulaFragment() );
          existingParameters[ i ] = null;
        }
      }

      // finally add all left-over parameters as manual parameters.
      for ( int i = 0; i < existingParameters.length; i++ ) {
        final DrillDownParameter parameter = existingParameters[ i ];
        if ( parameter == null ) {
          continue;
        }

        parameter.setType( DrillDownParameter.Type.MANUAL );
        parameter.setPosition( drillDownParameters.size() );
        drillDownParameters.add( parameter );
      }

      final DrillDownParameter[] computedSet =
        drillDownParameters.toArray( new DrillDownParameter[ drillDownParameters.size() ] );
      if ( parameterTable != null ) {
        parameterTable.setDrillDownParameter( computedSet );
      }
    }
  }

  public static HttpClient createHttpClient( final AuthenticationData loginData ) {
    HttpClientManager.HttpClientBuilderFacade clientBuilder = HttpClientManager.getInstance().createBuilder();

    HttpClient client = clientBuilder.setSocketTimeout( WorkspaceSettings.getInstance().getConnectionTimeout() * 1000 )
      .setCredentials( loginData.getUsername(), loginData.getPassword() ).setCookieSpec( CookieSpecs.DEFAULT ).build();

    return client;
  }

  private static class RequestParamsFromServerTask implements AuthenticatedServerTask {
    private AuthenticationData loginData;
    private Exception error;
    private Parameter[] parameters;
    private PentahoPathModel pathModel;

    private RequestParamsFromServerTask( final PentahoPathModel pathModel ) {
      this.pathModel = pathModel;
    }

    public void setLoginData( final AuthenticationData loginData, final boolean storeUpdates ) {
      this.loginData = loginData;
    }

    public Exception getError() {
      return error;
    }

    public Parameter[] getParameters() {
      return parameters;
    }

    /**
     * When an object implementing interface <code>Runnable</code> is used to create a thread, starting the thread
     * causes the object's <code>run</code> method to be called in that separately executing thread.
     * <p/>
     * The general contract of the method <code>run</code> is that it may take any action whatsoever.
     *
     * @see Thread#run()
     */
    public void run() {
      pathModel.setLoginData( loginData );

      final String paramServiceText = getParameterServicePath( loginData, pathModel );
      if ( paramServiceText == null ) {
        return;
      }

      try {
        final HttpClient httpClient = createHttpClient( loginData );

        final HttpGet method = new HttpGet( paramServiceText );

        /*
         * With the work done for BISERVER-13648, the server no longer return an auth challenge to the issued request;
         * ( when this used to be the case, the HttpClient lib would handle such a 401 response OOTB by issuing the credentials
         * should any have been set in the HTTPContext ).
         *
         * Now ( i.e. post BISERVER-13648 ), the server returns a 302 REDIRECT to the server's /Login page.
         * We can circumvent by defining an  HttpClientContext with preemptive authentication set.
         */
        HttpClientContext httpCtx = buildPreemptiveAuthRequestContext( new URI( paramServiceText ), loginData );
        HttpResponse httpResponse = httpCtx != null ? httpClient.execute( method, httpCtx ) : httpClient.execute( method );
        final int result = httpResponse.getStatusLine().getStatusCode();
        if ( result != HttpStatus.SC_OK ) {
          if ( result == HttpStatus.SC_MOVED_TEMPORARILY || result == HttpStatus.SC_FORBIDDEN
            || result == HttpStatus.SC_UNAUTHORIZED ) {
            // notify the world that the login data is no longer valid
            throw new PublishException( PublishException.ERROR_INVALID_USERNAME_OR_PASSWORD );
          } else {
            throw new PublishException( PublishException.ERROR_FAILED, result );
          }
        }
        final byte[] responseBody = HttpClientUtil.responseToByteArray( httpResponse );
        final ResourceManager manager = new ResourceManager();
        final Resource resource = manager.createDirectly( responseBody, ParameterDocument.class );
        final ParameterDocument o = (ParameterDocument) resource.getResource();
        parameters = o.getParameter();
      } catch ( Exception e ) {
        error = e;
      }
    }

    /**
     * HttpClient does not support preemptive authentication out of the box, because if misused or used incorrectly the
     * preemptive authentication can lead to significant security issues, such as sending user credentials in clear text
     * to an unauthorized third party. Therefore, users are expected to evaluate potential benefits of preemptive
     * authentication versus security risks in the context of their specific application environment.
     *
     * Nonetheless one can configure HttpClient to authenticate preemptively by prepopulating the authentication data cache.
     *
     * @see https://hc.apache.org/httpcomponents-client-ga/tutorial/html/authentication.html
     *
     * @param target target URI
     * @param auth login data
     * @return
     */
    private HttpClientContext buildPreemptiveAuthRequestContext( final URI target, final AuthenticationData auth ) {

      if ( target == null || auth == null || StringUtils.isEmpty( auth.getUsername() ) ) {
        return null; // nothing to do here; if no credentials were passed, there's no need to create a preemptive auth Context
      }

      HttpHost targetHost = URIUtils.extractHost( target );

      CredentialsProvider credsProvider = new BasicCredentialsProvider();
      credsProvider.setCredentials( new AuthScope( targetHost.getHostName(), targetHost.getPort() ),
          new UsernamePasswordCredentials( auth.getUsername(), auth.getPassword() ) );

      // Create AuthCache instance
      AuthCache authCache = new BasicAuthCache();

      // Generate BASIC scheme object and add it to the local auth cache
      BasicScheme basicAuth = new BasicScheme();
      authCache.put( targetHost, basicAuth );

      HttpClientContext context = HttpClientContext.create();
      context.setCredentialsProvider( credsProvider );
      context.setAuthCache( authCache );

      return context;
    }
  }

  private static final Log logger = LogFactory.getLog( PentahoParameterRefreshHandler.class );

  private PentahoPathModel pathModel;
  private DrillDownParameterTable parameterTable;
  private ReportDesignerContext reportDesignerContext;
  private Component component;

  public PentahoParameterRefreshHandler( final PentahoPathModel pentahoPathWrapper,
                                         final ReportDesignerContext reportDesignerContext,
                                         final Component component ) {
    if ( pentahoPathWrapper == null ) {
      throw new NullPointerException();
    }
    if ( reportDesignerContext == null ) {
      throw new NullPointerException();
    }
    if ( component == null ) {
      throw new NullPointerException();
    }
    this.reportDesignerContext = reportDesignerContext;
    this.component = component;
    this.pathModel = pentahoPathWrapper;
  }

  public PentahoPathModel getPathModel() {
    return pathModel;
  }

  public DrillDownParameterTable getParameterTable() {
    return parameterTable;
  }

  public void setParameterTable( final DrillDownParameterTable parameterTable ) {
    this.parameterTable = parameterTable;
  }

  private static String getParameterServicePath( final AuthenticationData loginData,
                                                 final PentahoPathModel pathModel ) {
    try {
      final FileObject fileSystemRoot = PublishUtil.createVFSConnection( VFS.getManager(), loginData );
      final FileSystem fileSystem = fileSystemRoot.getFileSystem();

      // as of version 3.7 we do not need to check anything other than that the version information is there
      // later we may have to add additional checks in here to filter out known broken versions.

      final String localPath = pathModel.getLocalPath();
      final FileObject object = fileSystemRoot.resolveFile( localPath );
      final FileContent content = object.getContent();
      final String majorVersionText = (String) fileSystem.getAttribute( WebSolutionFileSystem.MAJOR_VERSION );

      if ( StringUtils.isEmpty( majorVersionText ) == false ) {
        final String paramService = (String) content.getAttribute( "param-service-url" );
        if ( StringUtils.isEmpty( paramService ) ) {
          return null;
        }
        if ( paramService.startsWith( "http://" ) || paramService.startsWith( "https://" ) ) {
          return paramService;
        }

        try {
          // Encode the URL (must use URI as URL encoding doesn't work on spaces correctly)
          final URL target = new URL( loginData.getUrl() );
          final String host;
          if ( target.getPort() != -1 ) {
            host = target.getHost() + ":" + target.getPort();
          } else {
            host = target.getHost();
          }

          return target.getProtocol() + "://" + host + paramService;
        } catch ( MalformedURLException e ) {
          UncaughtExceptionsModel.getInstance().addException( e );
          return null;
        }
      }

      final String extension = IOUtils.getInstance().getFileExtension( localPath );
      if ( ".prpt".equals( extension ) ) {
        logger.debug( "Ancient pentaho system detected: parameter service does not deliver valid parameter values" );

        final String name = pathModel.getName();
        final String path = pathModel.getPath();
        final String solution = pathModel.getSolution();

        final FastMessageFormat messageFormat =
          new FastMessageFormat( "/content/reporting/?renderMode=XML&amp;solution={0}&amp;path={1}&amp;name={2}" );
        messageFormat.setNullString( "" );
        return loginData.getUrl() + messageFormat.format( new Object[] { solution, path, name } );
      }

      logger.debug( "Ancient pentaho system detected: We will not have access to a working parameter service" );
      return null;
    } catch ( FileSystemException e ) {
      UncaughtExceptionsModel.getInstance().addException( e );
      return null;
    }
  }

  public void requestParameterRefresh( final DrillDownParameterRefreshEvent event ) {
    final String localPath = pathModel.getLocalPath();
    if ( StringUtils.isEmpty( localPath ) ) {
      return;
    }

    try {
      final RequestParamsFromServerTask requestParamsFromServerTask = new RequestParamsFromServerTask( pathModel );
      final LoginTask loginTask =
        new LoginTask( reportDesignerContext, component, new UpdateRequestParamsTask( requestParamsFromServerTask,
          component, event ), pathModel.getLoginData() );
      SwingUtilities.invokeLater( loginTask );

    } catch ( Exception e ) {
      UncaughtExceptionsModel.getInstance().addException( e );
      logger.warn( "Failed to access parameter system", e );
    }
  }
}
