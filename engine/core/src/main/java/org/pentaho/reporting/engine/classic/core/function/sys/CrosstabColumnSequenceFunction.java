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

package org.pentaho.reporting.engine.classic.core.function.sys;

import org.pentaho.reporting.engine.classic.core.event.ReportEvent;
import org.pentaho.reporting.engine.classic.core.function.AbstractFunction;
import org.pentaho.reporting.engine.classic.core.function.FunctionUtilities;

public class CrosstabColumnSequenceFunction extends AbstractFunction {
  private String crosstabFilterGroup;
  private int lastGroupSequenceNumber;

  public CrosstabColumnSequenceFunction() {
  }

  public String getCrosstabFilterGroup() {
    return crosstabFilterGroup;
  }

  public void setCrosstabFilterGroup( final String crosstabFilterGroup ) {
    this.crosstabFilterGroup = crosstabFilterGroup;
  }

  public void reportStarted( final ReportEvent event ) {
    lastGroupSequenceNumber = 0;
  }

  public void groupStarted( final ReportEvent event ) {
    if ( FunctionUtilities.isDefinedGroup( getCrosstabFilterGroup(), event ) ) {
      final int groupIndex = event.getState().getCurrentGroupIndex();
      this.lastGroupSequenceNumber = (int) event.getState().getCrosstabColumnSequenceCounter( groupIndex );
    }
  }

  public void summaryRowSelection( final ReportEvent event ) {
    if ( FunctionUtilities.isDefinedGroup( getCrosstabFilterGroup(), event ) ) {
      final int groupIndex = event.getState().getCurrentGroupIndex();
      this.lastGroupSequenceNumber = (int) event.getState().getCrosstabColumnSequenceCounter( groupIndex );
    }
  }

  public Object getValue() {
    return lastGroupSequenceNumber;
  }
}
