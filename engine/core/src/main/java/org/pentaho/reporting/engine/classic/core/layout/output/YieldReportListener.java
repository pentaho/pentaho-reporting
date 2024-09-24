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

import org.pentaho.reporting.engine.classic.core.event.ReportProgressEvent;
import org.pentaho.reporting.engine.classic.core.event.ReportProgressListener;

/**
 * A report listener that calls Thread.yield() on each generated event. Although this slows down the report processing a
 * bit, this also makes the application a lot more responsive as the report-thread does no longer block the CPU all the
 * time.
 *
 * @author Thomas Morgner
 */
public class YieldReportListener implements ReportProgressListener {
  private int rate;

  private transient int lastCall;

  private transient int lastPage;

  public YieldReportListener() {
    rate = 50;
  }

  public YieldReportListener( final int rate ) {
    this.rate = rate;
  }

  public int getRate() {
    return rate;
  }

  public void setRate( final int rate ) {
    this.rate = rate;
  }

  public void reportProcessingStarted( final ReportProgressEvent event ) {

  }

  public void reportProcessingFinished( final ReportProgressEvent event ) {

  }

  public void reportProcessingUpdate( final ReportProgressEvent event ) {
    final int currentRow = event.getRow();
    final int thisCall = currentRow % rate;
    final int page = event.getPage();

    if ( page != lastPage ) {
      Thread.yield();
    } else if ( thisCall != lastCall ) {
      Thread.yield();
    }
    lastCall = thisCall;
    lastPage = page;
  }
}
