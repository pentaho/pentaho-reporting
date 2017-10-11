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
