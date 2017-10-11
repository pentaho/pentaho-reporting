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

package org.pentaho.reporting.engine.classic.core.metadata;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.DataFactory;
import org.pentaho.reporting.engine.classic.core.DataRow;
import org.pentaho.reporting.engine.classic.core.designtime.DataSourcePlugin;
import org.pentaho.reporting.engine.classic.core.metadata.builder.DataFactoryMetaDataBuilder;
import org.pentaho.reporting.libraries.base.config.Configuration;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.libraries.base.util.StringUtils;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

public class DefaultDataFactoryMetaData extends AbstractMetaData implements DataFactoryMetaData {
  private static final Log logger = LogFactory.getLog( DefaultDataFactoryMetaData.class );

  private Class<?> editorClass;
  private boolean editable;
  private boolean freeformQuery;
  private boolean formattingMetadataSource;
  private DataFactoryCore dataFactoryCore;

  @Deprecated
  public DefaultDataFactoryMetaData( final String dataFactory, final String bundleLocation, final String keyPrefix,
      final boolean expert, final boolean preferred, final boolean hidden, final boolean deprecated,
      final boolean editable, final boolean freeformQuery, final boolean formattingMetadataSource,
      final boolean experimental, final DataFactoryCore dataFactoryCore, final int compatibilityLevel ) {
    this( dataFactory, bundleLocation, keyPrefix, expert, preferred, hidden, deprecated, editable, freeformQuery,
        formattingMetadataSource, experimental ? MaturityLevel.Development : MaturityLevel.Production, dataFactoryCore,
        compatibilityLevel );
  }

  public DefaultDataFactoryMetaData( final String dataFactory, final String bundleLocation, final String keyPrefix,
      final boolean expert, final boolean preferred, final boolean hidden, final boolean deprecated,
      final boolean editable, final boolean freeformQuery, final boolean formattingMetadataSource,
      final MaturityLevel maturityLevel, final DataFactoryCore dataFactoryCore, final int compatibilityLevel ) {
    super( dataFactory, bundleLocation, keyPrefix, expert, preferred, hidden, deprecated, maturityLevel,
        compatibilityLevel );
    if ( dataFactoryCore == null ) {
      throw new NullPointerException();
    }

    this.editable = editable;
    this.freeformQuery = freeformQuery;
    this.formattingMetadataSource = formattingMetadataSource;
    this.dataFactoryCore = dataFactoryCore;
  }

  public DefaultDataFactoryMetaData( final DataFactoryMetaDataBuilder builder ) {
    super( builder );
    this.editable = builder.isEditable();
    this.freeformQuery = builder.isFreeformQuery();
    this.formattingMetadataSource = builder.isFormattingMetadataSource();
    this.dataFactoryCore = builder.getDataFactoryCore();
    this.editorClass = builder.getEditorClass();

    if ( dataFactoryCore == null ) {
      throw new NullPointerException();
    }
  }

  protected String computePrefix( final String keyPrefix, final String name ) {
    if ( StringUtils.isEmpty( keyPrefix ) ) {
      return "";
    }
    return super.computePrefix( keyPrefix, name );
  }

  public String[] getReferencedFields( final DataFactory element, final String queryName, final DataRow parameter ) {
    return dataFactoryCore.getReferencedFields( this, element, queryName, parameter );
  }

  public ResourceReference[] getReferencedResources( final DataFactory element, final ResourceManager resourceManager,
      final String queryName, final DataRow parameter ) {
    return dataFactoryCore.getReferencedResources( this, element, resourceManager, queryName, parameter );
  }

  public boolean isEditable() {
    return editable && ensureEditorAvailable();
  }

  public boolean isEditorAvailable() {
    return ensureEditorAvailable();
  }

  public DataSourcePlugin createEditor() {
    if ( ensureEditorAvailable() == false ) {
      return null;
    }

    if ( editorClass == null ) {
      return null;
    }
    try {
      return (DataSourcePlugin) editorClass.newInstance();
    } catch ( Exception e ) {
      return null;
    }
  }

  private boolean ensureEditorAvailable() {
    if ( editorClass == null ) {
      final Configuration configuration = ClassicEngineBoot.getInstance().getGlobalConfig();
      final String className = configuration.getConfigProperty( getEditorConfigurationKey() );
      if ( className != null ) {
        try {
          final ClassLoader loader = ObjectUtilities.getClassLoader( DefaultDataFactoryMetaData.class );
          final Class maybeClass = Class.forName( className, false, loader );
          if ( DataSourcePlugin.class.isAssignableFrom( maybeClass ) ) {
            editorClass = maybeClass;
          }
        } catch ( ClassNotFoundException e ) {
          logger.warn( "Editor class " + className + " cannot be found.", e );
          return false;
        }
      }
    }
    return editorClass != null;
  }

  protected String getEditorConfigurationKey() {
    return "org.pentaho.reporting.engine.classic.metadata.datafactory-editor." + getName();
  }

  public boolean isFreeFormQuery() {
    return freeformQuery;
  }

  public boolean isFormattingMetaDataSource() {
    return formattingMetadataSource;
  }

  public String toString() {
    final StringBuilder sb = new StringBuilder();
    sb.append( "DefaultDataFactoryMetaData" );
    sb.append( "{editorClass=" ).append( editorClass );
    sb.append( ", editable=" ).append( editable );
    sb.append( ", freeformQuery=" ).append( freeformQuery );
    sb.append( ", formattingMetadataSource=" ).append( formattingMetadataSource );
    sb.append( ", super=" ).append( super.toString() );
    sb.append( '}' );
    return sb.toString();
  }

  public String getDisplayConnectionName( final DataFactory dataFactory ) {
    return dataFactoryCore.getDisplayConnectionName( this, dataFactory );
  }

  public Object getQueryHash( final DataFactory dataFactory, final String queryName, final DataRow parameter ) {
    return dataFactoryCore.getQueryHash( this, dataFactory, queryName, parameter );
  }
}
