/*
 * This program is free software; you can redistribute it and/or modify it under the
 * terms of the GNU General Public License, version 2 as published by the Free Software
 * Foundation.
 *
 * You should have received a copy of the GNU General Public License along with this
 * program; if not, you can obtain a copy at http://www.gnu.org/licenses/gpl-2.0.html
 * or from the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 *
 * Copyright 2024 Hitachi Vantara.  All rights reserved.
 */

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
