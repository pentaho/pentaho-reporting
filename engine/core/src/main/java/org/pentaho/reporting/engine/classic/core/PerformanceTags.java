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


package org.pentaho.reporting.engine.classic.core;

public final class PerformanceTags {
  public static final String SUMMARY_PREFIX = "Summary.";
  public static final String DETAIL_PREFIX = "Detail.";

  public static final String REPORT_PROCESSING = "pentaho.report.processing";
  public static final String REPORT_LAYOUT_GENERATE = "pentaho.report.processing.layout.generate";
  public static final String REPORT_LAYOUT_VALIDATE = "pentaho.report.processing.layout.validate";
  public static final String REPORT_LAYOUT_PROCESS = "pentaho.report.processing.layout.processpage";
  public static final String REPORT_LAYOUT_PROCESS_SUFFIX = "pentaho.report.processing.layout.process.";
  public static final String REPORT_PREPARE = "pentaho.report.processing.prepare";
  public static final String REPORT_PREPARE_CROSSTAB = "pentaho.report.processing.prepare.crosstab";
  public static final String REPORT_PREPARE_DATA = "pentaho.report.processing.prepare.data";
  public static final String REPORT_PAGINATE = "pentaho.report.processing.prepare.paginate";
  public static final String REPORT_GENERATE = "pentaho.report.processing.generate";
  public static final String REPORT_GENERATE_OUTPUT = "pentaho.report.processing.generate.output";

  public static final String REPORT_QUERY_SORT = "pentaho.report.processing.query.sort";
  public static final String REPORT_QUERY = "pentaho.report.processing.query";
  public static final String REPORT_PARAMETER = "pentaho.report.parameter";
  public static final String REPORT_PARAMETER_QUERY = "pentaho.report.parameter.query";

  public static String getSummaryTag( String tag, String suffix ) {
    return SUMMARY_PREFIX + tag + ( ( suffix == null ) ? "" : suffix );
  }

  public static String getDetailTag( String tag, String suffix ) {
    return DETAIL_PREFIX + tag + ( ( suffix == null ) ? "" : suffix );
  }
}
