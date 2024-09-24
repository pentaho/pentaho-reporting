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
