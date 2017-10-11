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
