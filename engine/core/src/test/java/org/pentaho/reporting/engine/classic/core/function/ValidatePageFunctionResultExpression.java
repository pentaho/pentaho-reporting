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

package org.pentaho.reporting.engine.classic.core.function;

import org.pentaho.reporting.engine.classic.core.event.PageEventListener;
import org.pentaho.reporting.engine.classic.core.event.ReportEvent;

public class ValidatePageFunctionResultExpression extends AbstractFunction implements PageEventListener {
  private String crosstabFilterGroup;
  private Object tableModelValue;

  public ValidatePageFunctionResultExpression() {
  }

  public ValidatePageFunctionResultExpression( final String name, final String validateCrosstabFilter ) {
    setName( name );
    this.crosstabFilterGroup = validateCrosstabFilter;
  }

  public String getCrosstabFilterGroup() {
    return crosstabFilterGroup;
  }

  public void setCrosstabFilterGroup( final String crosstabFilterGroup ) {
    this.crosstabFilterGroup = crosstabFilterGroup;
  }

  public Object getValue() {
    return tableModelValue;
  }

  public void summaryRowSelection( final ReportEvent event ) {
    if ( event.getState().isPrepareRun() ) {
      return;
    }

    if ( FunctionUtilities.isDefinedGroup( getCrosstabFilterGroup(), event ) ) {
      final String targetName = getName().substring( 1 );
      tableModelValue = getDataRow().get( "validate-" + targetName );
    }
  }

  public void reportStarted( final ReportEvent event ) {
    updateValue( event );
  }

  public void reportInitialized( final ReportEvent event ) {
    updateValue( event );
  }

  public void reportFinished( final ReportEvent event ) {
    updateValue( event );
  }

  public void itemsStarted( final ReportEvent event ) {
    updateValue( event );
  }

  public void itemsFinished( final ReportEvent event ) {
    updateValue( event );
  }

  public void reportDone( final ReportEvent event ) {
    updateValue( event );
  }

  public void groupFinished( final ReportEvent event ) {
    updateValue( event );
  }

  public void groupStarted( final ReportEvent event ) {
    updateValue( event );
  }

  public void itemsAdvanced( final ReportEvent event ) {
    updateValue( event );
  }

  private void updateValue( final ReportEvent event ) {
    if ( event.getState().isPrepareRun() ) {
      return;
    }

    final String targetName = getName().substring( 1 );
    tableModelValue = getDataRow().get( "validate-" + targetName );
  }

  public void pageStarted( final ReportEvent event ) {
    updateValue( event );
  }

  public void pageFinished( final ReportEvent event ) {
    updateValue( event );
  }
}
