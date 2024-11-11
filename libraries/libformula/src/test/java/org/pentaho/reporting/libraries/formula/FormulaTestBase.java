/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


package org.pentaho.reporting.libraries.formula;

import junit.framework.TestCase;
import org.junit.Assert;
import org.pentaho.reporting.libraries.base.util.StringUtils;
import org.pentaho.reporting.libraries.formula.common.TestFormulaContext;
import org.pentaho.reporting.libraries.formula.function.FunctionDescription;
import org.pentaho.reporting.libraries.formula.lvalues.TypeValuePair;

import java.util.Locale;

public abstract class FormulaTestBase extends TestCase {
  private static final String FORMULA_TEXT_PATTERN="%s(%s)";
  private FormulaContext context;

  protected FormulaTestBase() {
  }

  protected FormulaTestBase( final String s ) {
    super( s );
  }

  protected void setUp() throws Exception {
    context = new TestFormulaContext( TestFormulaContext.testCaseDataset );
    LibFormulaBoot.getInstance().start();
  }

  protected abstract Object[][] createDataTest();

  public FormulaContext getContext() {
    return context;
  }

  protected void runDefaultTest() throws Exception {
    final Object[][] dataTest = createDataTest();
    runTest( dataTest );
  }

  protected void runTest( final Object[][] dataTest ) throws Exception {
    for ( int i = 0; i < dataTest.length; i++ ) {
      final Object[] objects = dataTest[ i ];
      performTest( (String) objects[ 0 ], objects[ 1 ] );
    }
  }

  protected void performTest( final String formula, final Object result ) throws Exception {
    performTest( formula, result, getContext() );
  }

  @SuppressWarnings( "unchecked" )
  protected void performTest( final String formulaText,
                              final Object expected,
                              final FormulaContext context ) throws Exception {
    final Object formulaResult = evaluateFormula( formulaText, context ).getValue();
    if ( expected instanceof Comparable && formulaResult instanceof Comparable ) {
      final Comparable<Object> resultComparable = (Comparable<Object>) formulaResult;
      final Comparable<Object> expectedComparable = (Comparable<Object>) expected;
      try {
        int compareResult = resultComparable.compareTo( expectedComparable );
        assertTrue( String.format( "For formula [%s]\n - Expected \"%s\"\n but found \"%s\"",
          formulaText, expected, formulaResult ), compareResult == 0 );
      } catch ( final ClassCastException cce ) {
        Assert.assertEquals( expectedComparable, resultComparable );
      }
    } else if ( expected instanceof Object[] && formulaResult instanceof Object[] ) {
      Object[] expectedArray = (Object[]) expected;
      Object[] resultArray = (Object[]) formulaResult;
      Assert.assertArrayEquals( expectedArray, resultArray );
    } else {
      assertEquals( "Failure on [" + formulaText + ']', expected, formulaResult );
    }
  }

  protected void performTranslationTest( String function ) {
    FunctionDescription functionDesc = context.getFunctionRegistry().getMetaData( function );
    assertFalse( StringUtils.isEmpty( functionDesc.getDisplayName( Locale.ENGLISH ) ) );
    assertFalse( StringUtils.isEmpty( functionDesc.getDescription( Locale.ENGLISH ) ) );
    int count = functionDesc.getParameterCount();
    for ( int x = 0; x < count; x++ ) {
      assertFalse( StringUtils.isEmpty( functionDesc.getParameterDescription( x, Locale.ENGLISH ) ) );
      assertFalse( StringUtils.isEmpty( functionDesc.getParameterDisplayName( x, Locale.ENGLISH ) ) );
    }
  }

  /**
   * <p>Returns a text representation of the invocation to the given function using the given parameters.</p>
   *
   * @param functionName    the name of the formula to be used
   * @param parameterValues the parameters to be used (can be empty)
   * @return text representation of the invocation to the given function using the given parameters.
   */
  protected String getFormulaText( Object functionName, Object[] parameterValues ) {

    return String.format( FORMULA_TEXT_PATTERN, functionName, getParametersAsText( parameterValues, ';' ) );
  }

  /**
   * <p>Returns a text representation of the invocation to the given function using the given parameters.</p>
   *
   * @param parameterValues the parameters to be used (can be empty)
   * @return text representation of the invocation to the given function using the given parameters.
   */
  protected String getParametersAsText( Object[] parameterValues, final char separatorToUse ) {
    StringBuilder sb = new StringBuilder();

    if ( null != parameterValues ) {
      char separator = ' ';
      for ( Object parameterValue : parameterValues ) {
        sb.append( separator );
        separator = separatorToUse;

        if ( parameterValue instanceof Object[] ) {
          sb.append( '{' ).append( getParametersAsText( (Object[]) parameterValue, '|' ) ).append( '}' );
        } else if ( parameterValue instanceof Number ) {
          sb.append( parameterValue );
        } else if ( parameterValue instanceof Boolean ) {
          sb.append( parameterValue.toString().toUpperCase() ).append( "()" );
        } else if ( parameterValue instanceof String ) {
          sb.append( '"' ).append( parameterValue ).append( '"' );
        } else {
          sb.append( parameterValue );
        }
      }
    }

    return sb.toString();
  }

  protected TypeValuePair evaluateFormula( final String formulaText, final FormulaContext context )
    throws Exception {
    final Formula formula = new Formula( formulaText );
    formula.initialize( context );
    return formula.evaluateTyped();
  }
}
