/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2028-08-13
 ******************************************************************************/


package org.pentaho.reporting.engine.classic.core.designtime;

import org.pentaho.reporting.engine.classic.core.AbstractReportDefinition;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ParameterDataRow;
import org.pentaho.reporting.engine.classic.core.ParameterMapping;
import org.pentaho.reporting.engine.classic.core.SubReport;
import org.pentaho.reporting.engine.classic.core.parameters.ParameterDefinitionEntry;
import org.pentaho.reporting.engine.classic.core.util.ReportParameterValues;
import org.pentaho.reporting.engine.classic.core.wizard.ContextAwareDataSchemaModel;
import org.pentaho.reporting.engine.classic.core.wizard.DataAttributeContext;
import org.pentaho.reporting.engine.classic.core.wizard.DataSchemaDefinition;
import org.pentaho.reporting.engine.classic.core.wizard.DataSchemaUtility;
import org.pentaho.reporting.engine.classic.core.wizard.DefaultDataAttributeContext;
import org.pentaho.reporting.libraries.base.util.ArgumentNullException;
import org.pentaho.reporting.libraries.base.util.LinkedMap;

import java.util.Date;

public abstract class AbstractDesignTimeDataSchemaModel implements ContextAwareDataSchemaModel {
  private final AbstractReportDefinition parent;
  private final DataSchemaDefinition dataSchemaDefinition;
  private final DataAttributeContext dataAttributeContext;
  private final MasterReport masterReportElement;

  protected AbstractDesignTimeDataSchemaModel( final MasterReport masterReportElement,
      final AbstractReportDefinition report ) {
    ArgumentNullException.validate( "masterReportElement", masterReportElement );
    ArgumentNullException.validate( "report", report );

    this.masterReportElement = masterReportElement;
    this.parent = report;
    this.dataSchemaDefinition = createDataSchemaDefinition( masterReportElement );
    this.dataAttributeContext = new DefaultDataAttributeContext();
  }

  public DataSchemaDefinition getDataSchemaDefinition() {
    return dataSchemaDefinition;
  }

  protected DataSchemaDefinition createDataSchemaDefinition( final MasterReport masterReportElement ) {
    DataSchemaDefinition dataSchemaDefinition = masterReportElement.getDataSchemaDefinition();
    if ( dataSchemaDefinition == null ) {
      return DataSchemaUtility.parseDefaults( masterReportElement.getResourceManager() );
    }
    return dataSchemaDefinition;
  }

  protected ParameterDataRow computeParameterData() {
    AbstractReportDefinition parent = getReport();
    final ParameterDataRow parameterRow;
    if ( parent instanceof MasterReport ) {
      final MasterReport mr = (MasterReport) parent;
      final LinkedMap values = computeParameterValueMap( mr );
      parameterRow = new ParameterDataRow( (String[]) values.keys( new String[values.size()] ), values.values() );
    } else if ( parent instanceof SubReport ) {
      final SubReport sr = (SubReport) parent;
      final ParameterMapping[] inputMappings = sr.getInputMappings();
      final Object[] values = new Object[inputMappings.length];
      final String[] names = new String[inputMappings.length];
      for ( int i = 0; i < inputMappings.length; i++ ) {
        final ParameterMapping inputMapping = inputMappings[i];
        names[i] = inputMapping.getAlias();
      }
      parameterRow = new ParameterDataRow( names, values );
    } else {
      parameterRow = new ParameterDataRow();
    }
    return parameterRow;
  }

  protected ParameterDefinitionEntry[] computeParameterDefinitionEntries() {
    AbstractReportDefinition parent = getReport();
    if ( parent instanceof MasterReport ) {
      final MasterReport mr = (MasterReport) parent;
      return mr.getParameterDefinition().getParameterDefinitions();
    }
    return null;
  }

  protected LinkedMap computeParameterValueMap( final MasterReport report ) {
    final LinkedMap retval = new LinkedMap();
    retval.put( MasterReport.REPORT_DATE_PROPERTY, new Date() );

    final ReportParameterValues reportParameterValues = report.getParameterValues();
    final ParameterDefinitionEntry[] columnNames = report.getParameterDefinition().getParameterDefinitions();
    for ( int i = 0; i < columnNames.length; i++ ) {
      final ParameterDefinitionEntry parameter = columnNames[i];
      final String columnName = parameter.getName();
      if ( columnName == null ) {
        continue;
      }
      retval.put( columnName, reportParameterValues.get( columnName ) );
    }
    return retval;
  }

  public String[] getColumnNames() {
    return getDataSchema().getNames();
  }

  public DataAttributeContext getDataAttributeContext() {
    return dataAttributeContext;
  }

  protected AbstractReportDefinition getReport() {
    return parent;
  }

  protected MasterReport getMasterReportElement() {
    return masterReportElement;
  }
}
