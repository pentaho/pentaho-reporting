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


package org.pentaho.reporting.engine.classic.core.designtime.compat;

import org.pentaho.reporting.engine.classic.core.AbstractReportDefinition;
import org.pentaho.reporting.engine.classic.core.DataFactory;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ReportDefinition;
import org.pentaho.reporting.engine.classic.core.ReportElement;
import org.pentaho.reporting.engine.classic.core.SubReport;
import org.pentaho.reporting.engine.classic.core.function.Expression;
import org.pentaho.reporting.engine.classic.core.metadata.ExpressionMetaData;
import org.pentaho.reporting.engine.classic.core.parameters.ParameterDefinitionEntry;
import org.pentaho.reporting.engine.classic.core.parameters.ReportParameterDefinition;
import org.pentaho.reporting.engine.classic.core.style.StyleKey;
import org.pentaho.reporting.engine.classic.core.util.AbstractStructureVisitor;

public class CompatibilityUpdater extends AbstractStructureVisitor {
  private CompatibilityConverter[] converters;
  private CompatibilityConverter currentConverter;
  private int version;

  public CompatibilityUpdater() {
    converters = CompatibilityConverterRegistry.getInstance().getConverters();
  }

  public void performUpdate( final MasterReport report ) {
    final Integer versionRaw = report.getCompatibilityLevel();
    if ( versionRaw == null ) {
      version = -1;
    } else {
      version = versionRaw.intValue();
    }
    if ( version == -1 ) {
      return;
    }
    performUpdateInternal( report );
    report.setCompatibilityLevel( null );
  }

  public void performUpdate( final SubReport report ) {
    final ReportDefinition reportDefinition = report.getMasterReport();
    if ( reportDefinition == null ) {
      return;
    }

    final MasterReport masterReport = (MasterReport) reportDefinition;
    final Integer versionRaw = masterReport.getCompatibilityLevel();
    if ( versionRaw == null ) {
      version = -1;
    } else {
      version = versionRaw.intValue();
    }
    if ( version == -1 ) {
      return;
    }
    performUpdateInternal( report );
  }

  protected void performUpdateInternal( final AbstractReportDefinition report ) {
    for ( int i = 0; i < converters.length; i++ ) {
      final CompatibilityConverter converter = converters[i];
      if ( converter.getTargetVersion() < version ) {
        // this converter is for an older version
        // the report should already be up to date for this release.
        continue;
      }

      // set the current converter and convert the complete report to the
      // converter's level.
      currentConverter = converter;
      super.inspect( report );
    }
  }

  protected void inspectElement( final ReportElement element ) {
    currentConverter.inspectElement( element );
  }

  protected void inspectAttributeExpression( final ReportElement element, final String attributeNamespace,
      final String attributeName, final Expression expression, final ExpressionMetaData expressionMetaData ) {
    currentConverter.inspectAttributeExpression( element, attributeNamespace, attributeName, expression,
        expressionMetaData );
  }

  protected void inspectStyleExpression( final ReportElement element, final StyleKey styleKey,
      final Expression expression, final ExpressionMetaData expressionMetaData ) {
    currentConverter.inspectStyleExpression( element, styleKey, expression, expressionMetaData );
  }

  protected void inspectExpression( final AbstractReportDefinition report, final Expression expression ) {
    currentConverter.inspectExpression( report, expression );
  }

  protected void inspectParameter( final AbstractReportDefinition report, final ReportParameterDefinition definition,
      final ParameterDefinitionEntry parameter ) {
    currentConverter.inspectParameter( report, definition, parameter );
  }

  protected void inspectDataSource( final AbstractReportDefinition report, final DataFactory dataFactory ) {
    currentConverter.inspectDataSource( report, dataFactory );
  }
}
