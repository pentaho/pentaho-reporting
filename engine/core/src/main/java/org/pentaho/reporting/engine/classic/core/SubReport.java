/*
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
 * Copyright (c) 2001 - 2013 Object Refinery Ltd, Hitachi Vantara and Contributors..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core;

import org.pentaho.reporting.engine.classic.core.designtime.DesignTimeUtil;
import org.pentaho.reporting.engine.classic.core.designtime.SubReportParameterChange;
import org.pentaho.reporting.engine.classic.core.filter.types.bands.SubReportType;
import org.pentaho.reporting.engine.classic.core.function.Expression;
import org.pentaho.reporting.engine.classic.core.util.InstanceID;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

import java.awt.print.PageFormat;
import java.util.LinkedHashMap;

/**
 * A subreport element. A subreport can be attached to a root-level band and will be printed afterwards. Subreports have
 * their own tablemodel (queried with the sub-reports's defined query and the master report's data-factory).
 * <p/>
 * A sub-report that has been added to a root-level band will always be printed below the root-level band.
 * <p/>
 * Sub-reports can have import and export parameters. The parameter mapping can be defined freely, so a subreport is not
 * required to use the same column names as the parent report.
 * <p/>
 * If a global import or export is defined (by adding the parameter mapping "*" => "*") the other defined parameter
 * mappings will be ignored.
 *
 * @author Thomas Morgner
 */
public class SubReport extends AbstractReportDefinition {
  /**
   * A mapping of export parameters.
   */
  private LinkedHashMap<String, String> exportParameters;
  /**
   * A mapping of import parameters.
   */
  private LinkedHashMap<String, String> inputParameters;

  private DataFactory dataFactory;

  /**
   * Creates a new subreport instance.
   */
  public SubReport() {
    setElementType( new SubReportType() );

    exportParameters = new LinkedHashMap<String, String>();
    inputParameters = new LinkedHashMap<String, String>();
  }

  public SubReport( final InstanceID id ) {
    super( id );
    setElementType( new SubReportType() );

    exportParameters = new LinkedHashMap<String, String>();
    inputParameters = new LinkedHashMap<String, String>();

  }

  /**
   * Returns the page definition assigned to the report definition. The page definition defines the report area and how
   * the report is subdivided by the child pages.
   *
   * @return null, as subreports have no page-definition at all.
   */
  public PageDefinition getPageDefinition() {
    ReportElement parent = getParentSection();
    while ( parent != null ) {
      if ( parent instanceof MasterReport ) {
        final MasterReport masterReport = (MasterReport) parent;
        return masterReport.getPageDefinition();
      }
      parent = parent.getParentSection();
    }
    return new SimplePageDefinition( new PageFormat() );
  }

  /**
   * Clones the report.
   *
   * @return the clone.
   */
  public SubReport derive( final boolean preserveElementInstanceIds ) {
    final SubReport o = (SubReport) super.derive( preserveElementInstanceIds );
    o.exportParameters = (LinkedHashMap<String, String>) exportParameters.clone();
    o.inputParameters = (LinkedHashMap<String, String>) inputParameters.clone();
    if ( dataFactory != null ) {
      o.dataFactory = dataFactory.derive();
    }
    return o;
  }

  public void reconnectParent( final Section parentSection ) {
    parentSection.registerAsChild( this );
  }

  /**
   * Clones the report.
   *
   * @return the clone.
   */
  public SubReport clone() {
    final SubReport o = (SubReport) super.clone();
    o.exportParameters = (LinkedHashMap<String, String>) exportParameters.clone();
    o.inputParameters = (LinkedHashMap<String, String>) inputParameters.clone();
    if ( dataFactory != null ) {
      o.dataFactory = dataFactory.derive();
    }
    return o;
  }

  /**
   * Adds an export-parameter mapping to the subreport. The parameter specified by 'sourceColumn' will be made available
   * with the name 'outerName' in the parent report.
   *
   * @param outerName
   *          the name the parameter will get in the master report.
   * @param sourceColumn
   *          the source-column in the sub-report.
   */
  public void addExportParameter( final String outerName, final String sourceColumn ) {
    if ( outerName == null ) {
      throw new NullPointerException();
    }
    if ( sourceColumn == null ) {
      throw new NullPointerException();
    }

    final ParameterMapping[] oldMappings = getExportMappings();
    exportParameters.put( outerName, sourceColumn );
    notifyNodePropertiesChanged( new SubReportParameterChange( SubReportParameterChange.Type.EXPORT, oldMappings,
        getExportMappings() ) );
  }

  /**
   * Removes the export parameter from the mapping.
   *
   * @param outerName
   *          the name of the parameter as it is known in the master report.
   */
  public void removeExportParameter( final String outerName ) {
    if ( outerName == null ) {
      throw new NullPointerException();
    }
    final ParameterMapping[] oldMappings = getExportMappings();
    exportParameters.remove( outerName );
    notifyNodePropertiesChanged( new SubReportParameterChange( SubReportParameterChange.Type.EXPORT, oldMappings,
        getExportMappings() ) );
  }

  /**
   * Returns the parameter mappings for the subreport. The parameter mappings define how columns of the sub-report get
   * mapped into the master report.
   *
   * @return the parameter mappings array.
   */
  public ParameterMapping[] getExportMappings() {
    final int length = exportParameters.size();
    final String[] keys = exportParameters.keySet().toArray( new String[length] );
    final ParameterMapping[] mapping = new ParameterMapping[length];

    for ( int i = 0; i < length; i++ ) {
      final String name = keys[i];
      final String alias = exportParameters.get( name );
      mapping[i] = new ParameterMapping( name, alias );
    }
    return mapping;
  }

  public void setExportMappings( final ParameterMapping[] mappings ) {
    if ( mappings == null ) {
      throw new NullPointerException();
    }

    final ParameterMapping[] oldMappings = getExportMappings();

    exportParameters.clear();
    for ( int i = 0; i < mappings.length; i++ ) {
      final ParameterMapping mapping = mappings[i];
      exportParameters.put( mapping.getName(), mapping.getAlias() );
    }

    notifyNodePropertiesChanged( new SubReportParameterChange( SubReportParameterChange.Type.EXPORT, oldMappings,
        getExportMappings() ) );
  }

  /**
   * Adds an input-parameter mapping to the subreport. Input parameters define how columns that exist in the parent
   * report get mapped into the subreport.
   * <p/>
   * Input parameter mapping happens only once, so after the report has been started, changes to the parameters will not
   * pass through to the subreport.
   *
   * @param outerName
   *          the name of the parent report's column that provides the data.
   * @param sourceColumn
   *          the name under which the parameter will be available in the subreport.
   */
  public void addInputParameter( final String outerName, final String sourceColumn ) {
    if ( outerName == null ) {
      throw new NullPointerException();
    }
    if ( sourceColumn == null ) {
      throw new NullPointerException();
    }
    final ParameterMapping[] oldMappings = getInputMappings();

    inputParameters.put( sourceColumn, outerName );
    notifyNodePropertiesChanged( new SubReportParameterChange( SubReportParameterChange.Type.INPUT, oldMappings,
        getInputMappings() ) );
  }

  /**
   * Removes the input parameter from the parameter mapping.
   *
   * @param sourceColumn
   *          the name of the column of the subreport report that acts as source for the input parameter.
   */
  public void removeInputParameter( final String sourceColumn ) {
    if ( sourceColumn == null ) {
      throw new NullPointerException();
    }

    final ParameterMapping[] oldMappings = getInputMappings();

    inputParameters.remove( sourceColumn );
    notifyNodePropertiesChanged( new SubReportParameterChange( SubReportParameterChange.Type.INPUT, oldMappings,
        getInputMappings() ) );
  }

  public void clearInputParameters() {
    final ParameterMapping[] oldMappings = getInputMappings();

    inputParameters.clear();
    notifyNodePropertiesChanged( new SubReportParameterChange( SubReportParameterChange.Type.INPUT, oldMappings,
        getInputMappings() ) );
  }

  public void clearExportParameters() {
    final ParameterMapping[] oldMappings = getExportMappings();

    exportParameters.clear();
    notifyNodePropertiesChanged();
    notifyNodePropertiesChanged( new SubReportParameterChange( SubReportParameterChange.Type.EXPORT, oldMappings,
        getExportMappings() ) );
  }

  /**
   * Returns the input mappings defined for this subreport.
   *
   * @return the input mappings, never null.
   */
  public ParameterMapping[] getInputMappings() {
    final int length = inputParameters.size();
    final String[] keys = inputParameters.keySet().toArray( new String[length] );
    final ParameterMapping[] mapping = new ParameterMapping[length];

    for ( int i = 0; i < length; i++ ) {
      final String alias = keys[i];
      final String name = inputParameters.get( alias );
      mapping[i] = new ParameterMapping( name, alias );
    }
    return mapping;
  }

  public void setInputMappings( final ParameterMapping[] mappings ) {
    if ( mappings == null ) {
      throw new NullPointerException();
    }

    final ParameterMapping[] oldMappings = getInputMappings();

    inputParameters.clear();
    for ( int i = 0; i < mappings.length; i++ ) {
      final ParameterMapping mapping = mappings[i];
      inputParameters.put( mapping.getAlias(), mapping.getName() );
    }

    notifyNodePropertiesChanged( new SubReportParameterChange( SubReportParameterChange.Type.INPUT, oldMappings,
        getInputMappings() ) );
  }

  /**
   * Checks whether a global import is defined. A global import effectly overrides all other imports.
   *
   * @return true, if there is a global import defined, false otherwise.
   */
  public boolean isGlobalImport() {
    return "*".equals( inputParameters.get( "*" ) );
  }

  /**
   * Checks whether a global export is defined. A global export effectly overrides all other export mappings.
   *
   * @return true, if there is a global export defined, false otherwise.
   */
  public boolean isGlobalExport() {
    return "*".equals( exportParameters.get( "*" ) );
  }

  public DataFactory getDataFactory() {
    return dataFactory;
  }

  /**
   * The (optional) data-factory for the subreport. If no datafactory is defined here, the subreport will use the master
   * report's data-factory.
   *
   * @param dataFactory
   */
  public void setDataFactory( final DataFactory dataFactory ) {
    final DataFactory old = this.dataFactory;
    this.dataFactory = dataFactory;
    if ( old != null ) {
      notifyNodeChildRemoved( old );
    }
    if ( dataFactory != null ) {
      notifyNodeChildAdded( dataFactory );
    }
  }

  public Expression getActivationExpression() {
    return getAttributeExpression( AttributeNames.Core.NAMESPACE, AttributeNames.Core.SUBREPORT_ACTIVE );
  }

  public void setActivationExpression( final Expression activationExpression ) {
    setAttributeExpression( AttributeNames.Core.NAMESPACE, AttributeNames.Core.SUBREPORT_ACTIVE, activationExpression );
  }

  protected void updateChangedFlagInternal( final ReportElement element, final int type, final Object parameter ) {
    // also notify all local listeners on all changes.
    super.fireModelLayoutChanged( element, type, parameter );
    super.updateChangedFlagInternal( element, type, parameter );
  }

  @Deprecated
  public ResourceManager getResourceManager() {
    return DesignTimeUtil.getResourceManager( this );
  }

  @Deprecated
  public ResourceBundleFactory getResourceBundleFactory() {
    return super.getResourceBundleFactory();
  }
}
