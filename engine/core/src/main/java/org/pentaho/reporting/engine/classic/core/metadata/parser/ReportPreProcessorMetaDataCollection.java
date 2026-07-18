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



package org.pentaho.reporting.engine.classic.core.metadata.parser;

import org.pentaho.reporting.engine.classic.core.metadata.ReportPreProcessorMetaData;

import java.io.Serializable;

public class ReportPreProcessorMetaDataCollection implements Serializable {
  private ReportPreProcessorMetaData[] expressionMetaData;

  public ReportPreProcessorMetaDataCollection( final ReportPreProcessorMetaData[] expressionMetaData ) {
    if ( expressionMetaData == null ) {
      throw new NullPointerException();
    }

    this.expressionMetaData = expressionMetaData.clone();
  }

  public ReportPreProcessorMetaData[] getReportPreProcessorMetaData() {
    return expressionMetaData.clone();
  }
}
