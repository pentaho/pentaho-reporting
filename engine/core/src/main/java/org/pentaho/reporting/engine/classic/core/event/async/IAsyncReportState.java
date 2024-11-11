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


package org.pentaho.reporting.engine.classic.core.event.async;

import java.io.Serializable;
import java.util.UUID;

public interface IAsyncReportState extends Serializable {

  /**
   * @return Report path to be shown on ui
   */
  String getPath();

  /**
   * @return Identifier of async task
   */
  UUID getUuid();

  /**
   * @return Status of running task
   */
  AsyncExecutionStatus getStatus();

  /**
   * @return Progress from 0 to 100
   */
  int getProgress();

  /**
   * @return Page is currently being processed
   */
  int getPage();

  /**
   * @return Quantity of pages in the report
   */
  int getTotalPages();

  /**
   * @return Quantity of pages that were already generated
   */
  int getGeneratedPage();

  /**
   * @return Row is currently being processed
   */
  int getRow();

  /**
   * @return Quantity of rows in the report
   */
  int getTotalRows();

  /**
   * @return Activity code is currently being processed
   */
  String getActivity();

  /**
   * @return mime type advice of report content that will be generated at the end.
   */
  String getMimeType();

  /**
   * @return error message in case of exception
   */
  String getErrorMessage();

  /**
   * @return flag saying that Query limit is reached
   */
  boolean getIsQueryLimitReached();

}
