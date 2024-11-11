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
