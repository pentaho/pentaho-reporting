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

package org.pentaho.reporting.engine.classic.core.states;

import org.pentaho.reporting.engine.classic.core.DataRow;
import org.pentaho.reporting.engine.classic.core.ReportDefinition;
import org.pentaho.reporting.engine.classic.core.ResourceBundleFactory;
import org.pentaho.reporting.engine.classic.core.layout.InlineSubreportMarker;
import org.pentaho.reporting.engine.classic.core.states.datarow.DefaultFlowController;
import org.pentaho.reporting.engine.classic.core.states.process.ReportProcessStore;

/**
 * Creation-Date: 03.07.2007, 13:18:11
 *
 * @author Thomas Morgner
 */
public interface ReportState extends Cloneable {
  /**
   * A row number that is 'before' the first row.
   */
  int BEFORE_FIRST_ROW = -1;
  /**
   * A group number that is 'before' the first group.
   */
  int BEFORE_FIRST_GROUP = -1;

  int getNumberOfRows();

  DataRow getDataRow();

  ReportDefinition getReport();

  /**
   * Returns the currently processed row number. This row number contains padded rows and is equivalent to the number of
   * advance() calls made on the master-datarow.
   *
   * @return the current row number.
   */
  int getCurrentRow();

  /**
   * Returns the current data item. The data item is the row number used to access the raw data in the tablemodel. This
   * number is not guaranteed to increase sequentially as sorting may affect the order of rows.
   *
   * @return the current raw access row number.
   */
  int getCurrentDataItem();

  int getCurrentGroupIndex();

  int getPresentationGroupIndex();

  boolean isPrepareRun();

  boolean isFinish();

  int getLevel();

  int getProgressLevel();

  int getProgressLevelCount();

  /**
   * Returns the unique event code for this report state type.
   *
   * @return the event code for this state type.
   */
  int getEventCode();

  Object clone() throws CloneNotSupportedException;

  DefaultFlowController getFlowController();

  boolean isSubReportEvent();

  void setErrorHandler( ReportProcessingErrorHandler errorHandler );

  InlineSubreportMarker getCurrentSubReportMarker();

  ReportProcessingErrorHandler getErrorHandler();

  LayoutProcess getLayoutProcess();

  void firePageFinishedEvent( final boolean noParentPassing );

  void firePageStartedEvent( final int eventCode );

  ReportState getParentState();

  ReportState getParentSubReportState();

  ReportStateKey getProcessKey();

  boolean isInItemGroup();

  boolean isInlineProcess();

  ResourceBundleFactory getResourceBundleFactory();

  GroupingState createGroupingState();

  Integer getPredictedStateCount();

  boolean isStructuralPreprocessingNeeded();

  boolean isCrosstabActive();

  long getGroupSequenceCounter( final int groupIndex );

  long getCrosstabColumnSequenceCounter( final int groupIndex );

  PerformanceMonitorContext getPerformanceMonitorContext();

  ReportProcessStore getProcessStore();
}
