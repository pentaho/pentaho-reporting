/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 - 2026 by Pentaho Canada Inc. : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2030-06-15
 ******************************************************************************/



package org.pentaho.reporting.engine.classic.core.layout.output;

public class ReportProcessorThreadHolder {

  private static final ThreadLocal<AbstractReportProcessor> processorThreadLocal =
    new ThreadLocal<>();

  public static void setProcessor( final AbstractReportProcessor processor ) {
    processorThreadLocal.set( processor );
  }

  public static AbstractReportProcessor getProcessor() {
    return processorThreadLocal.get();
  }

  public static void clear() {
    processorThreadLocal.remove();
  }
}
