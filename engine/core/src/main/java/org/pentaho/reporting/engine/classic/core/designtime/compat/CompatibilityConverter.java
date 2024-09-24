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

package org.pentaho.reporting.engine.classic.core.designtime.compat;

import org.pentaho.reporting.engine.classic.core.AbstractReportDefinition;
import org.pentaho.reporting.engine.classic.core.DataFactory;
import org.pentaho.reporting.engine.classic.core.ReportElement;
import org.pentaho.reporting.engine.classic.core.function.Expression;
import org.pentaho.reporting.engine.classic.core.metadata.ExpressionMetaData;
import org.pentaho.reporting.engine.classic.core.parameters.ParameterDefinitionEntry;
import org.pentaho.reporting.engine.classic.core.parameters.ReportParameterDefinition;
import org.pentaho.reporting.engine.classic.core.style.StyleKey;

import java.util.Locale;

public interface CompatibilityConverter {
  int getTargetVersion();

  String getUpgradeDescription( Locale locale );

  void inspectElement( final ReportElement element );

  void inspectAttributeExpression( final ReportElement element, final String attributeNamespace,
      final String attributeName, final Expression expression, final ExpressionMetaData expressionMetaData );

  void inspectStyleExpression( final ReportElement element, final StyleKey styleKey, final Expression expression,
      final ExpressionMetaData expressionMetaData );

  void inspectExpression( final AbstractReportDefinition report, final Expression expression );

  void inspectParameter( final AbstractReportDefinition report, final ReportParameterDefinition definition,
      final ParameterDefinitionEntry parameter );

  void inspectDataSource( final AbstractReportDefinition report, final DataFactory dataFactory );
}
