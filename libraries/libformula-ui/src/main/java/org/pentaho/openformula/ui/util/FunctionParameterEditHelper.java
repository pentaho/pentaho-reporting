/*
 * This program is free software; you can redistribute it and/or modify it under the
 *  terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
 *  Foundation.
 *
 *  You should have received a copy of the GNU Lesser General Public License along with this
 *  program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 *  or from the Free Software Foundation, Inc.,
 *  51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 *  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *  See the GNU Lesser General Public License for more details.
 *
 *  Copyright (c) 2006 - 2017 Hitachi Vantara..  All rights reserved.
 */

package org.pentaho.openformula.ui.util;

import org.pentaho.openformula.ui.ParameterUpdateEvent;
import org.pentaho.openformula.ui.model2.FormulaSemicolonElement;
import org.pentaho.openformula.ui.model2.FunctionInformation;
import org.pentaho.reporting.libraries.base.util.StringUtils;

public class FunctionParameterEditHelper {
  public static class EditResult {
    public final String text;
    public final int caretPositionAfterEdit;

    private EditResult( final String text, final int caretPositionAfterEdit ) {
      this.text = text;
      this.caretPositionAfterEdit = caretPositionAfterEdit;
    }
  }

  public static EditResult buildFormulaText( final ParameterUpdateEvent event,
                                             final FunctionInformation fn,
                                             final String formula ) {

    // The parameter index corresponds to the individual parameter text-fields
    final int globalParameterIndex = event.getParameter();

    // special case: Replace the complete formula
    if ( globalParameterIndex == -1 ) {
      return performGlobalReplace( fn, formula, event );
    }

    // Special case 2: If the event adds a parameter beyond the defined parameter count, then we insert empty
    // parameter values as needed to fit the gap.
    if ( globalParameterIndex >= fn.getParameterCount() ) {
      return performAppendParameter( fn, formula, event );
    }

    // The text entered in a parameter field
    final String parameterText = event.getText();

    // insert an parameter somewhere in the middle.
    if ( event.isCatchAllParameter() == false ) {
      final int start = fn.getParamStart( globalParameterIndex );
      final int end = fn.getParamEnd( globalParameterIndex );
      final StringBuilder formulaText = new StringBuilder( formula );
      formulaText.delete( start, end );
      formulaText.insert( start, parameterText );
      return new EditResult( formulaText.toString(), start + parameterText.length() );
    }

    final int start = fn.getParamStart( globalParameterIndex );

    boolean canClearEmpty = true;
    int paramIdx = globalParameterIndex + 1;
    while ( paramIdx < fn.getParameterCount() ) {
      final String paramText = fn.getParameterText( paramIdx );
      if ( StringUtils.isEmpty( paramText, true ) == false ) {
        canClearEmpty = false;
      }
      paramIdx += 1;
    }

    final int end;
    if ( canClearEmpty ) {
      end = fn.getParamEnd( paramIdx - 1 );
    } else {
      end = fn.getParamEnd( globalParameterIndex );
    }

    final StringBuilder formulaText = new StringBuilder( formula );
    formulaText.delete( start, end );
    formulaText.insert( start, parameterText );
    return new EditResult( formulaText.toString(), start + parameterText.length() );
  }

  private static EditResult performAppendParameter( final FunctionInformation fn,
                                                    final String formula,
                                                    final ParameterUpdateEvent event ) {
    // The parameter index corresponds to the individual parameter text-fields
    final int globalParameterIndex = event.getParameter();

    // The text entered in a parameter field
    final String parameterText = event.getText();

    // In case the parameter is empty, we do NOT generate dummy values. This produces cleaner
    // formulas with infinite parameters.
    if ( StringUtils.isEmpty( parameterText ) ) {
      return new EditResult( formula, fn.getParamEnd( fn.getParameterCount() - 1 ) );
    }

    final int functionParameterCount = fn.getParameterCount();
    // Build the formula text.  Remove the old text and inject the new text in it's place
    final StringBuilder formulaText = new StringBuilder( formula );
    int start = fn.getParamEnd( functionParameterCount - 1 );
    for ( int i = functionParameterCount; i <= globalParameterIndex; i += 1 ) {
      formulaText.insert( start, FormulaSemicolonElement.ELEMENT );
      start += 1;
    }

    formulaText.insert( start, parameterText );
    return new EditResult( formulaText.toString(), start + parameterText.length() );
  }

  private static EditResult performGlobalReplace( final FunctionInformation fn,
                                                  final String formula,
                                                  final ParameterUpdateEvent event ) {
    final String parameterText = event.getText();
    final int start = fn.getFunctionOffset();
    final int end = fn.getFunctionParameterEnd();

    // Build the formula text.  Remove the old text and inject the new text in it's place
    final StringBuilder formulaText = new StringBuilder( formula );
    formulaText.delete( start, end );
    formulaText.insert( start, parameterText );
    return new EditResult( formulaText.toString(), start + parameterText.length() );
  }
}
