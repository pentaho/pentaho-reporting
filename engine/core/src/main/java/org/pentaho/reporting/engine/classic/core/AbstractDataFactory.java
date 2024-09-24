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
 * Copyright (c) 2002-2017 Hitachi Vantara..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core;

import org.pentaho.reporting.engine.classic.core.metadata.DataFactoryMetaData;
import org.pentaho.reporting.engine.classic.core.metadata.DataFactoryRegistry;
import org.pentaho.reporting.libraries.base.config.Configuration;
import org.pentaho.reporting.libraries.resourceloader.ResourceKey;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

import javax.swing.table.TableModel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public abstract class AbstractDataFactory implements DataFactoryDesignTimeSupport, Cloneable, DataFactoryMetaProvider {
  private transient Configuration configuration;
  private transient ResourceManager resourceManager;
  private transient ResourceKey contextKey;
  private transient ResourceBundleFactory resourceBundleFactory;
  private transient DataFactoryContext dataFactoryContext;
  private transient Locale locale;

  public AbstractDataFactory() {
    locale = Locale.getDefault();
  }

  public void cancelRunningQuery() {

  }

  protected int calculateQueryLimit( final DataRow parameters ) {
    final Object queryLimit = parameters.get( DataFactory.QUERY_LIMIT );
    if ( queryLimit instanceof Number ) {
      final Number i = (Number) queryLimit;
      return i.intValue();
    }
    return -1;
  }

  protected int calculateQueryTimeOut( final DataRow parameters ) {
    final Object queryTimeOut = parameters.get( DataFactory.QUERY_TIMEOUT );
    if ( queryTimeOut instanceof Number ) {
      final Number i = (Number) queryTimeOut;
      return i.intValue();
    }
    return -1;
  }

  public void initialize( final DataFactoryContext dataFactoryContext ) throws ReportDataFactoryException {
    if ( dataFactoryContext == null ) {
      throw new NullPointerException();
    }
    this.dataFactoryContext = dataFactoryContext;
    this.configuration = dataFactoryContext.getConfiguration();
    this.resourceBundleFactory = dataFactoryContext.getResourceBundleFactory();
    this.resourceManager = dataFactoryContext.getResourceManager();
    this.contextKey = dataFactoryContext.getContextKey();
    this.locale = resourceBundleFactory.getLocale();
    if ( locale == null ) {
      locale = Locale.getDefault();
    }
  }

  public TableModel queryDesignTimeStructure( final String query, final DataRow parameter )
    throws ReportDataFactoryException {
    return queryData( query, new DataRowWrapper( parameter ) );
  }

  public Locale getLocale() {
    return locale;
  }

  public DataFactoryContext getDataFactoryContext() {
    return dataFactoryContext;
  }

  public Configuration getConfiguration() {
    return configuration;
  }

  public ResourceManager getResourceManager() {
    if ( resourceManager == null ) {
      resourceManager = new ResourceManager();
    }
    return resourceManager;
  }

  public ResourceKey getContextKey() {
    return contextKey;
  }

  public ResourceBundleFactory getResourceBundleFactory() {
    return resourceBundleFactory;
  }

  public DataFactory clone() {
    try {
      return (DataFactory) super.clone();
    } catch ( final CloneNotSupportedException e ) {
      throw new IllegalStateException( e );
    }
  }

  public DataFactory derive() {
    return clone();
  }

  public DataFactoryMetaData getMetaData() {
    return DataFactoryRegistry.getInstance().getMetaData( getClass().getName() );
  }

  public String getDisplayConnectionName() {
    return null;
  }

  public Object getQueryHash( final String query, final DataRow dataRow ) throws ReportDataFactoryException {
    return null;
  }

  public String[] getReferencedFields( final String query, final DataRow dataRow ) throws ReportDataFactoryException {
    return null;
  }

  protected static class DataRowWrapper implements DataRow {
    private DataRow parent;
    private String[] columnNames;

    public DataRowWrapper( final DataRow parent ) {
      this.parent = parent;
      this.columnNames = computeEffectiveColumnNameSet();
    }

    private String[] computeEffectiveColumnNameSet() {
      List<String> c = Arrays.asList( parent.getColumnNames() );
      ArrayList<String> retval = new ArrayList<String>( c );
      if ( !retval.contains( DataFactory.QUERY_LIMIT ) ) {
        retval.add( DataFactory.QUERY_LIMIT );
      }
      retval.add( DataFactoryDesignTimeSupport.DESIGN_TIME );
      return retval.toArray( new String[retval.size()] );
    }

    public Object get( final String name ) {
      if ( DESIGN_TIME.equals( name ) ) {
        return true;
      }
      if ( QUERY_LIMIT.equals( name ) ) {
        return 1;
      }
      return parent.get( name );
    }

    public String[] getColumnNames() {
      return columnNames.clone();
    }

    public boolean isChanged( final String name ) {
      if ( DESIGN_TIME.equals( name ) ) {
        return false;
      }
      if ( QUERY_LIMIT.equals( name ) ) {
        return false;
      }
      return parent.isChanged( name );
    }
  }
}
