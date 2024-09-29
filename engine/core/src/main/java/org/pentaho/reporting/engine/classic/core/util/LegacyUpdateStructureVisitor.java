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


package org.pentaho.reporting.engine.classic.core.util;

import org.pentaho.reporting.engine.classic.core.AbstractReportDefinition;
import org.pentaho.reporting.engine.classic.core.LegacyUpdateHandler;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ReportDefinition;
import org.pentaho.reporting.engine.classic.core.ReportElement;
import org.pentaho.reporting.engine.classic.core.SubReport;
import org.pentaho.reporting.engine.classic.core.function.Expression;
import org.pentaho.reporting.engine.classic.core.metadata.ExpressionMetaData;
import org.pentaho.reporting.engine.classic.core.style.StyleKey;

public class LegacyUpdateStructureVisitor extends AbstractStructureVisitor {
  private int version;

  public LegacyUpdateStructureVisitor() {
  }

  public void performUpdate( final MasterReport report ) {
    version = report.getCompatibilityLevel();
    if ( version == -1 ) {
      return;
    }
    super.inspect( report );
  }

  public void performUpdate( final SubReport report ) {
    final ReportDefinition reportDefinition = report.getMasterReport();
    if ( reportDefinition == null ) {
      return;
    }

    final MasterReport masterReport = (MasterReport) reportDefinition;
    version = masterReport.getCompatibilityLevel();
    if ( version == -1 ) {
      return;
    }
    super.inspect( report );
  }

  protected void inspectAttributeExpression( final ReportElement element, final String attributeNamespace,
      final String attributeName, final Expression expression, final ExpressionMetaData expressionMetaData ) {
    if ( expression instanceof LegacyUpdateHandler ) {
      final LegacyUpdateHandler handler = (LegacyUpdateHandler) expression;
      handler.reconfigureForCompatibility( version );
    }
  }

  protected void inspectStyleExpression( final ReportElement element, final StyleKey styleKey,
      final Expression expression, final ExpressionMetaData expressionMetaData ) {
    if ( expression instanceof LegacyUpdateHandler ) {
      final LegacyUpdateHandler handler = (LegacyUpdateHandler) expression;
      handler.reconfigureForCompatibility( version );
    }
  }

  protected void inspectExpression( final AbstractReportDefinition report, final Expression expression ) {
    if ( expression instanceof LegacyUpdateHandler ) {
      final LegacyUpdateHandler handler = (LegacyUpdateHandler) expression;
      handler.reconfigureForCompatibility( version );
    }
  }
}
