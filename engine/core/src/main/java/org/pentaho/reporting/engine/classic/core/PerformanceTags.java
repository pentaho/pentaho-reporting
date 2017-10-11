/*
 * This program is free software; you can redistribute it and/or modify it under the
 *  terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
 *  Foundation.
 *
 *  You should have received a copy of the GNU Lesser General Public License along with this
 *  program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 *  or from the Free Software Foundation, Inc.,
 *  51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 *  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *  See the GNU Lesser General Public License for more details.
 *
 *  Copyright (c) 2006 - 2017 Hitachi Vantara..  All rights reserved.
 */

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
