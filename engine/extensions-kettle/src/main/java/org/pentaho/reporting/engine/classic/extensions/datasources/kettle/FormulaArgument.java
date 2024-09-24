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
