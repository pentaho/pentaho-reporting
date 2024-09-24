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

import org.pentaho.reporting.engine.classic.core.ReportDefinition;
import org.pentaho.reporting.engine.classic.core.function.ProcessingContext;
import org.pentaho.reporting.engine.classic.core.layout.model.LogicalPageBox;

/**
 * The output-processor receives the layouted content and is responsible for translating the received content into the
 * specific output format. The output processor also keeps track of the number of received pages and their physical
 * layout.
 *
 * @author Thomas Morgner
 */
public interface OutputProcessor {
  public void processingStarted( final ReportDefinition report, final ProcessingContext processingContext );

  public OutputProcessorMetaData getMetaData();

  /**
   * A call-back that passes a layouted pagebox to the output processor.
   *
   * @param pageBox
   */
  public void processContent( final LogicalPageBox pageBox ) throws ContentProcessingException;

  public void processRecomputedContent( final LogicalPageBox pageBox ) throws ContentProcessingException;

  /**
   * A call-back to indicate that the processing of the current process-run has been finished.
   */
  public void processingFinished();

  public int getPageCursor();

  public void setPageCursor( int pc );

  public int getLogicalPageCount();

  public LogicalPageKey getLogicalPage( int page );

  /**
   * Checks whether the 'processingFinished' event had been received at least once.
   *
   * @return
   */
  public boolean isPaginationFinished();

  public boolean isNeedAlignedPage();

  public int getPhysicalPageCount();
}
