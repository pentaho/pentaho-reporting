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

package org.pentaho.reporting.engine.classic.core.function.formula;

import org.pentaho.reporting.engine.classic.core.function.ReportFormulaContext;
import org.pentaho.reporting.engine.classic.core.modules.output.table.html.HtmlTableModule;
import org.pentaho.reporting.libraries.formula.EvaluationException;
import org.pentaho.reporting.libraries.formula.FormulaContext;
import org.pentaho.reporting.libraries.formula.function.Function;
import org.pentaho.reporting.libraries.formula.function.ParameterCallback;
import org.pentaho.reporting.libraries.formula.lvalues.TypeValuePair;
import org.pentaho.reporting.libraries.formula.typing.coretypes.LogicalType;

public class DashboardModeFunction implements Function {
  public DashboardModeFunction() {
  }

  public String getCanonicalName() {
    return "DASHBOARDMODE";
  }

  public TypeValuePair evaluate( final FormulaContext context, final ParameterCallback parameters )
    throws EvaluationException {
    final ReportFormulaContext rfc = (ReportFormulaContext) context;
    if ( isDashboardMode( rfc ) ) {
      return new TypeValuePair( LogicalType.TYPE, Boolean.TRUE );
    }
    return new TypeValuePair( LogicalType.TYPE, Boolean.FALSE );
  }

  public static boolean isDashboardMode( final ReportFormulaContext rfc ) {
    final boolean value = "true".equals( rfc.getConfiguration().getConfigProperty( HtmlTableModule.BODY_FRAGMENT ) );

    if ( value ) {
      final String exportType = rfc.getExportType();
      if ( exportType.startsWith( "table/html" ) && HtmlTableModule.ZIP_HTML_EXPORT_TYPE.equals( exportType ) == false ) {
        return true;
      }
    }
    return false;
  }
}
