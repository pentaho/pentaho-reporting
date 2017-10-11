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

package org.pentaho.reporting.engine.classic.extensions.datasources.kettle;

import org.pentaho.reporting.libraries.base.util.ArgumentNullException;
import org.pentaho.reporting.libraries.formula.EvaluationException;
import org.pentaho.reporting.libraries.formula.Formula;
import org.pentaho.reporting.libraries.formula.FormulaContext;
import org.pentaho.reporting.libraries.formula.parser.ParseException;
import org.pentaho.reporting.libraries.formula.util.FormulaUtil;

import java.io.Serializable;
import java.util.ArrayList;

public class FormulaArgument implements Serializable {
  private String formula;

  public FormulaArgument( final String formula ) {
    ArgumentNullException.validate( "formula", formula );

    this.formula = formula;
  }

  public String getFormula() {
    return formula;
  }

  public static FormulaArgument create( final String reportField ) {
    return new FormulaArgument( '=' + FormulaUtil.quoteReference( reportField ) );
  }

  public static FormulaArgument[] convert( final String[] definedArgumentNames ) {
    final FormulaArgument[] args = new FormulaArgument[ definedArgumentNames.length ];
    for ( int i = 0; i < definedArgumentNames.length; i++ ) {
      String definedArgumentName = definedArgumentNames[ i ];
      args[ i ] = new FormulaArgument( '=' + FormulaUtil.quoteReference( definedArgumentName ) );
    }
    return args;
  }

  public static String[] convert( final FormulaArgument[] args ) {
    final ArrayList<String> textList = new ArrayList<String>();
    for ( int i = 0; i < args.length; i++ ) {
      FormulaArgument arg = args[ i ];
      try {
        String[] references = FormulaUtil.getReferences( arg.getFormula() );
        if ( references.length > 0 ) {
          textList.add( references[ 0 ] );
        }
      } catch ( ParseException e ) {
        //
      }
    }
    return textList.toArray( new String[ textList.size() ] );
  }

  public String[] getReferencedFields() throws ParseException {
    return FormulaUtil.getReferences( getFormula() );
  }

  public Object compute( final FormulaContext formulaContext ) throws EvaluationException, ParseException {
    Formula f = new Formula( FormulaUtil.extractFormula( formula ) );
    f.initialize( formulaContext );
    return f.evaluate();
  }

  public boolean equals( final Object o ) {
    if ( this == o ) {
      return true;
    }
    if ( o == null || getClass() != o.getClass() ) {
      return false;
    }

    final FormulaArgument that = (FormulaArgument) o;

    if ( !formula.equals( that.formula ) ) {
      return false;
    }

    return true;
  }

  public int hashCode() {
    return formula.hashCode();
  }
}
