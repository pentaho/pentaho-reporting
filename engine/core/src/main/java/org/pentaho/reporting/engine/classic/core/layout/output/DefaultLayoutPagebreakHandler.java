/*
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
 * Copyright (c) 2001 - 2013 Object Refinery Ltd, Hitachi Vantara and Contributors..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.layout.output;

import org.pentaho.reporting.engine.classic.core.states.ReportState;

/**
 * Creation-Date: 22.04.2007, 13:47:41
 *
 * @author Thomas Morgner
 */
public class DefaultLayoutPagebreakHandler implements LayoutPagebreakHandler, Cloneable {
  private ReportState reportState;

  public DefaultLayoutPagebreakHandler( final ReportState reportState ) {
    this.reportState = reportState;
  }

  public DefaultLayoutPagebreakHandler() {
  }

  public ReportState getReportState() {
    return reportState;
  }

  public void setReportState( final ReportState reportState ) {
    this.reportState = reportState;
  }

  public void pageFinished() {
    if ( reportState == null ) {
      throw new IllegalStateException(
          "A Report-State must be given. If you dont have a report state, then you're doing something wrong." );
    }
    reportState.firePageFinishedEvent( false );
  }

  public void pageStarted() {
    if ( reportState == null ) {
      throw new IllegalStateException(
          "A Report-State must be given. If you dont have a report state, then you're doing something wrong." );
    }
    reportState.firePageStartedEvent( reportState.getEventCode() );
  }

  public Object clone() throws CloneNotSupportedException {
    return super.clone();
  }
}
