package org.pentaho.reporting.engine.classic.extensions.datasources.kettle;

import org.pentaho.reporting.engine.classic.core.ParameterMapping;
import org.pentaho.reporting.libraries.base.util.ArgumentNullException;
import org.pentaho.reporting.libraries.formula.EvaluationException;
import org.pentaho.reporting.libraries.formula.Formula;
import org.pentaho.reporting.libraries.formula.FormulaContext;
import org.pentaho.reporting.libraries.formula.parser.ParseException;
import org.pentaho.reporting.libraries.formula.util.FormulaUtil;

import java.io.Serializable;
import java.util.ArrayList;

public class FormulaParameter implements Serializable {
  private String name;
  private String formula;

  public FormulaParameter( final String name, final String formula ) {
    ArgumentNullException.validate( "name", name );
    ArgumentNullException.validate( "formula", formula );

    this.name = name;
    this.formula = formula;
  }

  public String getName() {
    return name;
  }

  public String getFormula() {
    return formula;
  }

  /**
   * Maps the legacy parameter mapping into the new formula based system. ParameterMapping#getName returns the report's
   * datarow-column name (source) and ParameterMapping#getAlias returns the target parameter in the transformation.
   *
   * @param definedVariableNames
   * @return
   */
  public static FormulaParameter[] convert( final ParameterMapping[] definedVariableNames ) {
    final FormulaParameter[] args = new FormulaParameter[ definedVariableNames.length ];
    for ( int i = 0; i < definedVariableNames.length; i++ ) {
      ParameterMapping definedArgumentName = definedVariableNames[ i ];
      String formula = '=' + FormulaUtil.quoteReference( definedArgumentName.getName() );
      args[ i ] = new FormulaParameter( definedArgumentName.getAlias(), formula );
    }
    return args;
  }

  public static FormulaParameter create( final String reportFieldName, final String transformationParameterName ) {
    String formula = '=' + FormulaUtil.quoteReference( reportFieldName );
    return new FormulaParameter( transformationParameterName, formula );
  }

  public static ParameterMapping[] convert( final FormulaParameter[] args ) {
    final ArrayList<ParameterMapping> textList = new ArrayList<ParameterMapping>();
    for ( int i = 0; i < args.length; i++ ) {
      FormulaParameter arg = args[ i ];
      try {
        String[] references = FormulaUtil.getReferences( arg.getFormula() );
        // some functions can have no references at all like '=FALSE()'
        // but it still be correct function.
        textList.add( new ParameterMapping( references.length > 0 ? references[ 0 ] : "",
                                            arg.getName() ) );
      } catch ( ParseException e ) {
        //
      }
    }
    return textList.toArray( new ParameterMapping[ textList.size() ] );
  }

  public boolean equals( final Object o ) {
    if ( this == o ) {
      return true;
    }
    if ( o == null || getClass() != o.getClass() ) {
      return false;
    }

    final FormulaParameter that = (FormulaParameter) o;

    if ( !formula.equals( that.formula ) ) {
      return false;
    }
    if ( !name.equals( that.name ) ) {
      return false;
    }

    return true;
  }

  public int hashCode() {
    int result = name.hashCode();
    result = 31 * result + formula.hashCode();
    return result;
  }

  public String[] getReferencedFields() throws ParseException {
    return FormulaUtil.getReferences( getFormula() );
  }

  public Object compute( final FormulaContext formulaContext ) throws EvaluationException, ParseException {
    Formula f = new Formula( FormulaUtil.extractFormula( formula ) );
    f.initialize( formulaContext );
    return f.evaluate();
  }
}
