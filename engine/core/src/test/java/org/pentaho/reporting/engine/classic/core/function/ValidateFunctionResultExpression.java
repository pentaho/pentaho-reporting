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


package org.pentaho.reporting.engine.classic.core.function;

import junit.framework.Assert;
import org.pentaho.reporting.engine.classic.core.event.ReportEvent;
import org.pentaho.reporting.libraries.base.util.DebugLog;

public class ValidateFunctionResultExpression extends AbstractFunction {
  private boolean failHard;
  private int currentDataItem;
  private String crosstabFilterGroup;

  public ValidateFunctionResultExpression() {
  }

  public ValidateFunctionResultExpression( final String name, final boolean failHard,
      final String validateCrosstabFilter ) {
    setName( name );
    this.crosstabFilterGroup = validateCrosstabFilter;
    this.failHard = failHard;
  }

  public String getCrosstabFilterGroup() {
    return crosstabFilterGroup;
  }

  public void setCrosstabFilterGroup( final String crosstabFilterGroup ) {
    this.crosstabFilterGroup = crosstabFilterGroup;
  }

  public boolean isFailHard() {
    return failHard;
  }

  public void setFailHard( final boolean failHard ) {
    this.failHard = failHard;
  }

  public Object getValue() {
    return null;
  }

  public void summaryRowSelection( final ReportEvent event ) {
    if ( FunctionUtilities.isDefinedGroup( getCrosstabFilterGroup(), event ) ) {
      final String targetName = getName().substring( 1 );
      final Object expressionValue = getDataRow().get( targetName );
      final Object tableModelValue = getDataRow().get( "validate-" + targetName );

      currentDataItem = event.getState().getCurrentDataItem();

      if ( !equalNumeric( expressionValue, tableModelValue ) ) {
        if ( failHard ) {
          DebugLog.log( "*" + currentDataItem + "! " + expressionValue + " - " + tableModelValue );
          Assert.assertEquals( tableModelValue, expressionValue );
        } else {
          DebugLog.log( "*" + currentDataItem + "! " + expressionValue + " - " + tableModelValue );
        }
      }
    }
  }

  public void itemsAdvanced( final ReportEvent event ) {
    final String targetName = getName().substring( 1 );
    final Object expressionValue = getDataRow().get( targetName );
    final Object tableModelValue = getDataRow().get( "validate-" + targetName );

    currentDataItem = event.getState().getCurrentDataItem();
    long sequenceCounter = event.getState().getCrosstabColumnSequenceCounter( 3 );
    if ( !failHard ) {
      DebugLog.log( currentDataItem + ":" + sequenceCounter + "# " + expressionValue + " - " + tableModelValue );
    }

    if ( !equalNumeric( expressionValue, tableModelValue ) ) {
      if ( failHard ) {
        // DebugLog.log(currentDataItem + "! " + expressionValue + " - " + tableModelValue);
        Assert.assertEquals( tableModelValue, expressionValue );
      } else {
        // DebugLog.log(currentDataItem + "! " + expressionValue + " - " + tableModelValue);
      }
    }
  }

  private boolean equalNumeric( final Object o1, final Object o2 ) {
    if ( o1 instanceof Number == false ) {
      return false;
    }
    if ( o2 instanceof Number == false ) {
      return false;
    }
    final Number n1 = (Number) o1;
    final Number n2 = (Number) o2;
    return Math.abs( n1.doubleValue() - n2.doubleValue() ) < 0.0000005;
  }
}
