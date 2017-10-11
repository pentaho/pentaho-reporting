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

package org.pentaho.reporting.engine.classic.extensions.drilldown;

import org.pentaho.reporting.libraries.formula.EvaluationException;
import org.pentaho.reporting.libraries.formula.FormulaContext;

/**
 * Hitachi Vantara dashboards violate the standard tripplet of "solution", "path", "action" and thus we have to map them
 * manually into something sane.
 *
 * @author Thomas Morgner.
 */
public class XActionFormulaLinkCustomizer extends FormulaLinkCustomizer {
  public XActionFormulaLinkCustomizer() {
  }

  public String format( final FormulaContext formulaContext,
                        final String configIndicator,
                        final String reportPath,
                        final ParameterEntry[] entries ) throws EvaluationException {
    final ParameterEntry[] entriesX = entries.clone();
    for ( int i = 0; i < entriesX.length; i++ ) {
      final ParameterEntry parameterEntry = entriesX[ i ];
      if ( "name".equals( parameterEntry.getParameterName() ) ) {
        entriesX[ i ] = new ParameterEntry( "action", parameterEntry.getParameterValue() );
      }
    }
    return super.format( formulaContext, configIndicator, reportPath, entriesX );
  }

}
