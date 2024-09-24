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
 * Copyright (c) 2002-2023 Hitachi Vantara..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.modules.misc.datafactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.DataFactory;
import org.pentaho.reporting.engine.classic.core.DataFactoryContext;
import org.pentaho.reporting.engine.classic.core.DataRow;
import org.pentaho.reporting.engine.classic.core.ReportDataFactoryException;
import org.pentaho.reporting.engine.classic.core.ResourceBundleFactory;
import org.pentaho.reporting.libraries.base.config.Configuration;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.libraries.base.util.StringUtils;
import org.pentaho.reporting.libraries.fonts.encoding.EncodingRegistry;
import org.pentaho.reporting.libraries.resourceloader.ResourceData;
import org.pentaho.reporting.libraries.resourceloader.ResourceException;
import org.pentaho.reporting.libraries.resourceloader.ResourceKey;
import org.pentaho.reporting.libraries.resourceloader.ResourceLoadingException;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

import javax.script.Bindings;
import javax.script.Invocable;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.script.SimpleScriptContext;
import javax.swing.table.TableModel;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public final class DataFactoryScriptingSupport implements Cloneable, Serializable {
  private static class QueryCarrier implements Serializable {
    private String query;
    private String scriptingLanguage;
    private String script;

    private QueryCarrier( final String query, final String scriptingLanguage, final String script ) {
      this.query = query;
      this.scriptingLanguage = scriptingLanguage;
      this.script = script;
    }

    public String getQuery() {
      return query;
    }

    public String getScriptingLanguage() {
      return scriptingLanguage;
    }

    public String getScript() {
      return script;
    }
  }

  public static class ScriptHelper {
    private ScriptContext context;
    private String defaultScriptLanguage;
    private ResourceManager resourceManager;
    private ResourceKey contextKey;

    public ScriptHelper( final ScriptContext context, final String defaultScriptLanguage,
        final ResourceManager resourceManager, final ResourceKey contextKey ) {
      this.context = context;
      this.defaultScriptLanguage = defaultScriptLanguage;
      this.resourceManager = resourceManager;
      this.contextKey = contextKey;
    }

    public Object eval( final String script ) throws ScriptException {
      return eval( script, defaultScriptLanguage );
    }

    public Object eval( final String script, final String scriptLanguage ) throws ScriptException {
      final ScriptEngine scriptEngine = new ScriptEngineManager().getEngineByName( scriptLanguage );
      if ( scriptEngine == null ) {
        throw new ScriptException( String.format(
            "DataFactoryScriptingSupport: Failed to locate scripting engine for language '%s'.", scriptLanguage ) );
      }

      scriptEngine.setContext( context );
      return scriptEngine.eval( script );
    }

    public Object evalFile( final String file ) throws ScriptException {
      return evalFile( file, defaultScriptLanguage );
    }

    public Object evalFile( final String file, final String language ) throws ScriptException {
      return evalFile( file, language, EncodingRegistry.getPlatformDefaultEncoding() );
    }

    public Object evalFile( final String file, final String defaultScriptLanguage, final String encoding )
      throws ScriptException {
      final ResourceKey resourceKey = createKeyFromString( resourceManager, contextKey, file );
      if ( resourceKey == null ) {
        throw new ScriptException( "Unable to load script" );
      }
      try {
        final ResourceData loadedResource = resourceManager.load( resourceKey );
        final byte[] resource = loadedResource.getResource( resourceManager );
        return eval( new String( resource, encoding ), defaultScriptLanguage );
      } catch ( ResourceLoadingException e ) {
        throw new ScriptException( e );
      } catch ( UnsupportedEncodingException e ) {
        throw new ScriptException( e );
      }
    }

    private ResourceKey createKeyFromString( final ResourceManager resourceManager, final ResourceKey contextKey,
        final String file ) {

      try {
        if ( contextKey != null ) {
          return resourceManager.deriveKey( contextKey, file );
        }
      } catch ( ResourceException re ) {
        // failed to load from context
        logger.debug( "Failed to load datasource as derived path: ", re );
      }

      try {
        return resourceManager.createKey( new URL( file ) );
      } catch ( ResourceException re ) {
        logger.debug( "Failed to load datasource as URL: ", re );
      } catch ( MalformedURLException e ) {
        //
      }

      try {
        return resourceManager.createKey( new File( file ) );
      } catch ( ResourceException re ) {
        // failed to load from context
        logger.debug( "Failed to load datasource as file: ", re );
      }

      return null;
    }
  }

  private static class QueryScriptContext {
    private static final String NASHORN_GLOBAL = "nashorn.global";
    private Invocable invocableEngine;
    private ScriptEngine scriptEngine;
    private ScriptContext context;

    private QueryScriptContext() {
    }

    public void init( final String queryName, final String scriptLanguage, final String script,
        final ScriptContext globalContext, final ResourceManager resourceManager, final ResourceKey contextKey,
        final DataFactory dataFactory, final Configuration configuration,
        final ResourceBundleFactory resourceBundleFactory ) throws ReportDataFactoryException {

      this.context = new SimpleScriptContext();

      if ( globalContext != null ) {
        final Bindings bindings = globalContext.getBindings( ScriptContext.ENGINE_SCOPE );

        if ( bindings.containsKey( NASHORN_GLOBAL ) ) {
          Bindings nashornGlobal = (Bindings) bindings.get( NASHORN_GLOBAL );
          this.context.getBindings( ScriptContext.ENGINE_SCOPE ).putAll( nashornGlobal );
        }

        this.context.getBindings( ScriptContext.ENGINE_SCOPE ).putAll( bindings );
      } else {
        context.setAttribute( "dataFactory", dataFactory, ScriptContext.ENGINE_SCOPE );
        context.setAttribute( "configuration", configuration, ScriptContext.ENGINE_SCOPE );
        context.setAttribute( "resourceManager", resourceManager, ScriptContext.ENGINE_SCOPE );
        context.setAttribute( "contextKey", contextKey, ScriptContext.ENGINE_SCOPE );
        context.setAttribute( "resourceBundleFactory", resourceBundleFactory, ScriptContext.ENGINE_SCOPE );
      }

      this.context.setAttribute( "scriptHelper", new ScriptHelper( this.context, scriptLanguage, resourceManager,
          contextKey ), ScriptContext.ENGINE_SCOPE );

      this.scriptEngine = new ScriptEngineManager().getEngineByName( scriptLanguage );
      if ( scriptEngine instanceof Invocable == false ) {
        throw new ReportDataFactoryException( String.format( "Query script language '%s' is not usable.",
            scriptLanguage ) );
      }
      this.invocableEngine = (Invocable) scriptEngine;
      this.scriptEngine.setContext( this.context );
      try {
        this.scriptEngine.eval( script );
      } catch ( ScriptException e ) {
        throw new ReportDataFactoryException( "DataFactoryScriptingSupport: Failed to initialize local query script: "
            + queryName, e );
      }

      try {
        this.invocableEngine.invokeFunction( "initQuery" );
      } catch ( ScriptException e ) {
        throw new ReportDataFactoryException( "DataFactoryScriptingSupport: Failed to invoke local init method: "
            + queryName, e );
      } catch ( NoSuchMethodException e ) {
        // ignored ..
        logger.debug( "Global script does not contain an 'init' function" );
      }
    }

    public String computeQuery( final String query, final String queryName, final DataRow parameter )
      throws ReportDataFactoryException {
      if ( invocableEngine == null ) {
        return query;
      }

      try {
        final Object computeQuery = this.invocableEngine.invokeFunction( "computeQuery", query, queryName, parameter );
        final Object translated = convert( computeQuery );
        if ( translated == null ) {
          throw new ReportDataFactoryException(
              "DataFactoryScriptingSupport: computeQuery method did not return a valid query." );
        }
        return String.valueOf( translated );
      } catch ( ScriptException e ) {
        throw new ReportDataFactoryException( "DataFactoryScriptingSupport: Failed to invoke computeQuery method.", e );
      } catch ( NoSuchMethodException e ) {
        // ignored ..
        logger.debug( "Query script does not contain an 'computeQuery' function" );
        return query;
      }
    }

    public TableModel postProcessResult( final String query, final String queryName, final DataRow parameter,
        final TableModel dataSet ) throws ReportDataFactoryException {
      if ( invocableEngine == null ) {
        return dataSet;
      }

      try {
        final Object computeQuery =
            this.invocableEngine.invokeFunction( "postProcessResult", query, queryName, parameter, dataSet );
        final Object translated = convert( computeQuery );
        if ( translated instanceof TableModel == false ) {
          throw new ReportDataFactoryException(
              "DataFactoryScriptingSupport: postProcessResult method did not return a valid query." );
        }
        return (TableModel) translated;
      } catch ( ScriptException e ) {
        throw new ReportDataFactoryException(
            "DataFactoryScriptingSupport: Failed to invoke postProcessResult method.", e );
      } catch ( NoSuchMethodException e ) {
        // ignored ..
        logger.debug( "Query script does not contain an 'postProcessResult' function" );
        return dataSet;
      }
    }

    public void shutdown() throws ReportDataFactoryException {
      if ( invocableEngine == null ) {
        return;
      }

      try {
        invocableEngine.invokeFunction( "shutdownQuery" );
      } catch ( ScriptException e ) {
        throw new ReportDataFactoryException( "DataFactoryScriptingSupport: Failed to invoke query shutdown method.", e );
      } catch ( NoSuchMethodException e ) {
        // ignored ..
        logger.debug( "Global script does not contain an 'shutdownQuery' function" );
      }
    }

    public String[] computeAdditionalQueryFields( final String query, final String queryName )
      throws ReportDataFactoryException {
      if ( invocableEngine == null ) {
        return new String[0];
      }

      try {
        final Object computeQuery = this.invocableEngine.invokeFunction( "computeQueryFields", query, queryName );
        final Object translated = convert( computeQuery );
        if ( translated == null ) {
          return null;
        }

        final Object[] rawArray;
        if ( translated instanceof Object[] ) {
          rawArray = (Object[]) translated;
        } else if ( translated instanceof Collection ) {
          final Collection c = (Collection) translated;
          rawArray = c.toArray();
        } else {
          rawArray = new Object[] { translated };
        }

        final ArrayList<String> retval = new ArrayList<String>();
        for ( int i = 0; i < rawArray.length; i++ ) {
          final Object o = rawArray[i];
          if ( o != null ) {
            retval.add( String.valueOf( o ) );
          }
        }
        return retval.toArray( new String[retval.size()] );
      } catch ( ScriptException e ) {
        throw new ReportDataFactoryException(
            "DataFactoryScriptingSupport: Failed to invoke computeQueryFields method.", e );
      } catch ( NoSuchMethodException e ) {
        // ignored ..
        logger.debug( "Query script does not contain an 'computeQueryFields' function" );
        return null;
      }
    }
  }

  private static ArrayList<ScriptValueConverter> converters;
  private static final Log logger = LogFactory.getLog( DataFactoryScriptingSupport.class );

  private String globalScriptLanguage;
  private String globalScript;
  private HashMap<String, QueryCarrier> queryMappings;
  private transient HashMap<String, QueryScriptContext> contextsByQuery;
  private transient ScriptContext globalScriptContext;
  private transient Invocable globalScriptEngine;
  private transient ResourceManager resourceManager;
  private transient ResourceKey contextKey;
  private transient DataFactory dataFactory;
  private transient Configuration configuration;
  private transient ResourceBundleFactory resourceBundleFactory;
  private transient boolean initialized;
  private transient DataFactoryContext dataFactoryContext;

  public DataFactoryScriptingSupport() {
    queryMappings = new HashMap<String, QueryCarrier>();
    contextsByQuery = new HashMap<String, QueryScriptContext>();
  }

  public Object clone() {
    try {
      final DataFactoryScriptingSupport clone = (DataFactoryScriptingSupport) super.clone();
      clone.queryMappings = (HashMap<String, QueryCarrier>) queryMappings.clone();
      clone.globalScriptContext = null;
      clone.contextsByQuery = (HashMap<String, QueryScriptContext>) contextsByQuery.clone();
      clone.contextsByQuery.clear();
      clone.dataFactory = null;
      clone.resourceBundleFactory = null;
      clone.resourceManager = null;
      clone.configuration = null;
      clone.contextKey = null;
      clone.initialized = false;
      return clone;
    } catch ( CloneNotSupportedException e ) {
      throw new IllegalStateException();
    }
  }

  public void setQuery( final String name, final String query, final String scriptLanguage, final String script ) {
    this.queryMappings.put( name, new QueryCarrier( query, scriptLanguage, script ) );
  }

  public String getScriptingLanguage( final String name ) {
    final QueryCarrier queryCarrier = queryMappings.get( name );
    if ( queryCarrier == null ) {
      return null;
    }
    return queryCarrier.getScriptingLanguage();
  }

  protected String computeScriptingLanguage( final String name ) {
    final QueryCarrier queryCarrier = queryMappings.get( name );
    if ( queryCarrier == null ) {
      return null;
    }
    final String scriptingLanguage = queryCarrier.getScriptingLanguage();
    if ( scriptingLanguage == null ) {
      return this.globalScriptLanguage;
    }
    return scriptingLanguage;
  }

  public String getScript( final String name ) {
    final QueryCarrier queryCarrier = queryMappings.get( name );
    if ( queryCarrier == null ) {
      return null;
    }
    return queryCarrier.getScript();
  }

  public String getQuery( final String name ) {
    final QueryCarrier queryCarrier = queryMappings.get( name );
    if ( queryCarrier == null ) {
      return null;
    }
    return queryCarrier.getQuery();
  }

  public String[] getQueryNames() {
    return queryMappings.keySet().toArray( new String[queryMappings.size()] );
  }

  public String getGlobalScript() {
    return globalScript;
  }

  public void setGlobalScript( final String globalScript ) {
    this.globalScript = globalScript;
  }

  public String getGlobalScriptLanguage() {
    return globalScriptLanguage;
  }

  public void setGlobalScriptLanguage( final String globalScriptLanguage ) {
    this.globalScriptLanguage = globalScriptLanguage;
  }

  public void initialize( final DataFactory dataFactory, final DataFactoryContext dataFactoryContext )
    throws ReportDataFactoryException {
    if ( globalScriptContext != null ) {
      return;
    }
    if ( StringUtils.isEmpty( globalScriptLanguage ) ) {
      return;
    }
    boolean allowScriptEval = ClassicEngineBoot.getInstance().getGlobalConfig().getConfigProperty(
                    "org.pentaho.reporting.engine.classic.core.allowScriptEvaluation", "false" )
            .equalsIgnoreCase( "true" );

    if ( !allowScriptEval ) {
      DataFactoryScriptingSupport.logger.error( "Scripts are prevented from running by default in order to avoid"
              + " potential remote code execution.  The system administrator must enable this capability by changing"
              + " the value of org.pentaho.reporting.engine.classic.core.allowScriptEvaluation to true." );
      return;
    }

    this.dataFactory = dataFactory;
    this.resourceManager = dataFactoryContext.getResourceManager();
    this.contextKey = dataFactoryContext.getContextKey();
    this.configuration = dataFactoryContext.getConfiguration();
    this.resourceBundleFactory = dataFactoryContext.getResourceBundleFactory();
    this.dataFactoryContext = dataFactoryContext;

    globalScriptContext = new SimpleScriptContext();
    globalScriptContext.setAttribute( "dataFactory", dataFactory, ScriptContext.ENGINE_SCOPE );
    globalScriptContext.setAttribute( "configuration", configuration, ScriptContext.ENGINE_SCOPE );
    globalScriptContext.setAttribute( "resourceManager", resourceManager, ScriptContext.ENGINE_SCOPE );
    globalScriptContext.setAttribute( "contextKey", contextKey, ScriptContext.ENGINE_SCOPE );
    globalScriptContext.setAttribute( "resourceBundleFactory", resourceBundleFactory, ScriptContext.ENGINE_SCOPE );

    globalScriptContext.setAttribute( "scriptHelper", new ScriptHelper( globalScriptContext, globalScriptLanguage,
        resourceManager, contextKey ), ScriptContext.ENGINE_SCOPE );

    final ScriptEngine maybeInvocableEngine = new ScriptEngineManager().getEngineByName( globalScriptLanguage );
    if ( maybeInvocableEngine == null ) {
      throw new ReportDataFactoryException( String.format(
          "DataFactoryScriptingSupport: Failed to locate scripting engine for language '%s'.", globalScriptLanguage ) );
    }
    if ( maybeInvocableEngine instanceof Invocable == false ) {
      return;
    }
    this.globalScriptEngine = (Invocable) maybeInvocableEngine;

    maybeInvocableEngine.setContext( globalScriptContext );
    try {
      maybeInvocableEngine.eval( globalScript );
    } catch ( ScriptException e ) {
      throw new ReportDataFactoryException( "DataFactoryScriptingSupport: Failed to execute datafactory init script.",
          e );
    }
  }

  protected void callGlobalInitialize( final DataRow parameter ) throws ReportDataFactoryException {
    if ( initialized ) {
      return;
    }

    try {
      initialized = true;
      if ( globalScriptEngine != null ) {
        this.globalScriptEngine.invokeFunction( "init", parameter );
      }
    } catch ( ScriptException e ) {
      throw new ReportDataFactoryException( "DataFactoryScriptingSupport: Failed to invoke global init method.", e );
    } catch ( NoSuchMethodException e ) {
      // ignored ..
      logger.debug( "Global script does not contain an 'init' function" );
    }
  }

  public String computeQuery( final String queryName, final DataRow parameter ) throws ReportDataFactoryException {
    callGlobalInitialize( parameter );

    final String queryScriptLanguage = computeScriptingLanguage( queryName );
    final String queryScript = getScript( queryName );
    if ( StringUtils.isEmpty( queryScriptLanguage ) || StringUtils.isEmpty( queryScript ) ) {
      return getQuery( queryName );
    }

    QueryScriptContext queryScriptContext = contextsByQuery.get( queryName );
    if ( queryScriptContext == null ) {
      queryScriptContext = new QueryScriptContext();
      queryScriptContext.init( queryName, queryScriptLanguage, queryScript, globalScriptContext, resourceManager,
          contextKey, dataFactory, configuration, resourceBundleFactory );
      contextsByQuery.put( queryName, queryScriptContext );
    }

    return queryScriptContext.computeQuery( getQuery( queryName ), queryName, parameter );
  }

  public TableModel postProcessResult( final String queryName, final DataRow parameter, final TableModel result )
    throws ReportDataFactoryException {
    callGlobalInitialize( parameter );

    final String queryScriptLanguage = computeScriptingLanguage( queryName );
    final String queryScript = getScript( queryName );
    if ( StringUtils.isEmpty( queryScriptLanguage ) || StringUtils.isEmpty( queryScript ) ) {
      return result;
    }

    QueryScriptContext queryScriptContext = contextsByQuery.get( queryName );
    if ( queryScriptContext == null ) {
      queryScriptContext = new QueryScriptContext();
      queryScriptContext.init( queryName, queryScriptLanguage, queryScript, globalScriptContext, resourceManager,
          contextKey, dataFactory, configuration, resourceBundleFactory );
      contextsByQuery.put( queryName, queryScriptContext );
    }

    return queryScriptContext.postProcessResult( getQuery( queryName ), queryName, parameter, result );
  }

  public String[] computeAdditionalQueryFields( final String queryName, final DataRow parameter )
    throws ReportDataFactoryException {
    callGlobalInitialize( parameter );

    final String queryScriptLanguage = computeScriptingLanguage( queryName );
    final String queryScript = getScript( queryName );
    if ( StringUtils.isEmpty( queryScriptLanguage ) || StringUtils.isEmpty( queryScript ) ) {
      return new String[0];
    }

    QueryScriptContext queryScriptContext = contextsByQuery.get( queryName );
    if ( queryScriptContext == null ) {
      queryScriptContext = new QueryScriptContext();
      queryScriptContext.init( queryName, queryScriptLanguage, queryScript, globalScriptContext, resourceManager,
          contextKey, dataFactory, configuration, resourceBundleFactory );
      contextsByQuery.put( queryName, queryScriptContext );
    }

    return queryScriptContext.computeAdditionalQueryFields( getQuery( queryName ), queryName );
  }

  public void shutdown() {
    for ( final Map.Entry<String, QueryScriptContext> entry : contextsByQuery.entrySet() ) {
      try {
        final QueryScriptContext context = entry.getValue();
        context.shutdown();
      } catch ( ReportDataFactoryException se ) {
        logger.warn( "Failed to shut down query script context: " + entry.getKey() );
      }
    }

    if ( globalScriptEngine != null ) {
      try {
        globalScriptEngine.invokeFunction( "shutdown" );
      } catch ( ScriptException e ) {
        logger.warn( "DataFactoryScriptingSupport: Failed to invoke global shutdown method.", e );
      } catch ( NoSuchMethodException e ) {
        // ignored ..
        logger.debug( "Global script does not contain an 'shutdown' function" );
      }
    }

    globalScriptContext = null;
    resourceManager = null;
    contextKey = null;
    dataFactory = null;
    resourceBundleFactory = null;
    configuration = null;
    initialized = false;
  }

  public static Object convert( final Object object ) {
    if ( object == null ) {
      return null;
    }
    synchronized ( DataFactoryScriptingSupport.class ) {
      if ( converters == null ) {
        converters = new ArrayList<ScriptValueConverter>();
        final Configuration globalConfig = ClassicEngineBoot.getInstance().getGlobalConfig();
        final Iterator propertyKeys =
            globalConfig
                .findPropertyKeys( "org.pentaho.reporting.engine.classic.core.modules.misc.datafactory.script-value-converters." );
        while ( propertyKeys.hasNext() ) {
          final String key = (String) propertyKeys.next();
          final String impl = globalConfig.getConfigProperty( key );
          final ScriptValueConverter converter =
              (ScriptValueConverter) ObjectUtilities.loadAndInstantiate( impl, ScriptValueConverter.class,
                  ScriptValueConverter.class );
          if ( converter != null ) {
            converters.add( converter );
          }
        }
      }
    }

    if ( converters.isEmpty() ) {
      return object;
    }

    for ( final ScriptValueConverter converter : converters ) {
      final Object convert = converter.convert( object );
      if ( convert != null ) {
        return convert;
      }
    }
    return object;
  }

  public boolean containsQuery( final String query ) {
    return queryMappings.containsKey( query );
  }

  public void remove( final String name ) {
    queryMappings.remove( name );
  }

  private void readObject( final ObjectInputStream stream ) throws IOException, ClassNotFoundException {
    stream.defaultReadObject();
    contextsByQuery = new HashMap<String, QueryScriptContext>();
  }
}

