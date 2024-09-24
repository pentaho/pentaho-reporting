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
 * Copyright (c) 2002-2021 Hitachi Vantara..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.extensions.datasources.scriptable;

import org.apache.bsf.BSFException;
import org.apache.bsf.BSFManager;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.AbstractDataFactory;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.DataFactory;
import org.pentaho.reporting.engine.classic.core.DataRow;
import org.pentaho.reporting.engine.classic.core.ReportDataFactoryException;
import org.pentaho.reporting.engine.classic.core.ResourceBundleFactory;
import org.pentaho.reporting.engine.classic.core.states.LegacyDataRowWrapper;
import org.pentaho.reporting.libraries.base.config.Configuration;
import org.pentaho.reporting.libraries.resourceloader.ResourceKey;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

import javax.swing.table.TableModel;
import java.util.LinkedHashMap;

/**
 * A datafactory that uses a bean-scripting framework script to produce a tablemodel.
 *
 * @author Thomas Morgner
 */
public class ScriptableDataFactory extends AbstractDataFactory {
  private static final Log logger = LogFactory.getLog( ScriptableDataFactory.class );
  private LinkedHashMap<String, String> queries;
  private String language;
  private transient BSFManager interpreter;
  private transient LegacyDataRowWrapper dataRowWrapper;
  private String script;
  private String shutdownScript;

  public ScriptableDataFactory() {
    queries = new LinkedHashMap<String, String>();
  }

  public String getLanguage() {
    return language;
  }

  public void setLanguage( final String language ) {
    this.language = language;
  }

  public void setQuery( final String name, final String value ) {
    if ( value == null ) {
      queries.remove( name );
    } else {
      queries.put( name, value );
    }
  }

  public String getScript() {
    return script;
  }

  public void setScript( final String script ) {
    this.script = script;
  }

  public String getShutdownScript() {
    return shutdownScript;
  }

  public void setShutdownScript( final String shutdownScript ) {
    this.shutdownScript = shutdownScript;
  }

  public String getQuery( final String name ) {
    return queries.get( name );
  }

  public String[] getQueryNames() {
    return queries.keySet().toArray( new String[queries.size()] );
  }

  /**
   * Creates a new interpreter instance.
   *
   * @return the interpreter or null, if there was an error.
   */
  protected BSFManager createInterpreter() throws BSFException {
    final BSFManager interpreter = new BSFManager();
    initializeInterpreter( interpreter );
    return interpreter;
  }

  /**
   * Initializes the Bean-Scripting Framework manager.
   *
   * @param interpreter
   *          the BSF-Manager that should be initialized.
   * @throws BSFException
   *           if an error occurred.
   */
  protected void initializeInterpreter( final BSFManager interpreter ) throws BSFException {
    dataRowWrapper = new LegacyDataRowWrapper();
    interpreter.declareBean( "dataRow", dataRowWrapper, DataRow.class ); //$NON-NLS-1$
    interpreter.declareBean( "configuration", getConfiguration(), Configuration.class ); //$NON-NLS-1$
    interpreter.declareBean( "contextKey", getContextKey(), ResourceKey.class ); //$NON-NLS-1$
    interpreter.declareBean( "resourceManager", getResourceManager(), ResourceManager.class ); //$NON-NLS-1$
    interpreter.declareBean( "resourceBundleFactory", getResourceBundleFactory(), ResourceBundleFactory.class ); //$NON-NLS-1$
    interpreter.declareBean( "dataFactoryContext", getDataFactoryContext(), ResourceBundleFactory.class ); //$NON-NLS-1$
    if ( script != null ) {
      interpreter.exec( getLanguage(), "startup-script", 1, 1, getScript() ); //$NON-NLS-1$
    }
  }

  /**
   * Queries a datasource. The string 'query' defines the name of the query. The Parameterset given here may contain
   * more data than actually needed for the query.
   * <p/>
   * The parameter-dataset may change between two calls, do not assume anything, and do not hold references to the
   * parameter-dataset or the position of the columns in the dataset.
   *
   * @param query
   *          the query string
   * @param parameters
   *          the parameters for the query
   * @return the result of the query as table model.
   * @throws ReportDataFactoryException
   *           if an error occurred while performing the query.
   */
  public TableModel queryData( final String query, final DataRow parameters ) throws ReportDataFactoryException {
    boolean allowScriptEval = ClassicEngineBoot.getInstance().getGlobalConfig().getConfigProperty(
      "org.pentaho.reporting.engine.classic.core.allowScriptEvaluation", "false" )
      .equalsIgnoreCase( "true" );

    if ( !allowScriptEval ) {
      throw new ReportDataFactoryException( "Scripts are prevented from running by default in order to avoid"
        + " potential remote code execution.  The system administrator must enable this capability." );
    }

    final String queryScript = queries.get( query );
    if ( queryScript == null ) {
      throw new ReportDataFactoryException( "No such query" );
    }

    if ( interpreter == null ) {
      try {
        this.interpreter = createInterpreter();
      } catch ( BSFException e ) {
        throw new ReportDataFactoryException( "Failed to initialize the BSF-Framework", e );
      }
    }

    try {
      dataRowWrapper.setParent( parameters );
      final Object o = interpreter.eval( getLanguage(), "expression", 1, 1, queryScript );
      if ( o instanceof TableModel == false ) {
        throw new ReportDataFactoryException( "Resulting value is not a tablemodel" );
      }
      return (TableModel) o; //$NON-NLS-1$
    } catch ( ReportDataFactoryException rde ) {
      throw rde;
    } catch ( Exception e ) {
      throw new ReportDataFactoryException( "Evaluation error", e );
    }
  }

  public ScriptableDataFactory clone() {
    final ScriptableDataFactory dataFactory = (ScriptableDataFactory) super.clone();
    dataFactory.queries = (LinkedHashMap<String, String>) queries.clone();
    dataFactory.interpreter = null;
    dataFactory.dataRowWrapper = null;
    return dataFactory;
  }

  /**
   * Returns a copy of the data factory that is not affected by its ancestor and holds no connection to the ancestor
   * anymore. A data-factory will be derived at the beginning of the report processing.
   *
   * @return a copy of the data factory.
   */
  public DataFactory derive() {
    return clone();
  }

  /**
   * Closes the data factory and frees all resources held by this instance.
   */
  public void close() {
    if ( this.interpreter != null && this.shutdownScript != null ) {
      try {
        this.interpreter.eval( getLanguage(), "shutdown-script", 1, 1, getShutdownScript() );
      } catch ( BSFException e ) {
        logger.warn( "Failed to evaluate shutdown-script", e );
      }
    }
    this.dataRowWrapper = null;
    this.interpreter = null;
  }

  /**
   * Checks whether the query would be executable by this datafactory. This performs a rough check, not a full query.
   *
   * @param query
   * @param parameters
   * @return
   */
  public boolean isQueryExecutable( final String query, final DataRow parameters ) {
    return queries.containsKey( query );
  }

  public void cancelRunningQuery() {
    // not all scripting engines actually support that.
    if ( interpreter != null ) {
      interpreter.terminate();
    }
  }
}
