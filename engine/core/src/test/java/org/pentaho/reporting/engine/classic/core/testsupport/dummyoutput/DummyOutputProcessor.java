/*!
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
 * Copyright (c) 2002-2017 Hitachi Vantara..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.testsupport.dummyoutput;

import org.pentaho.reporting.engine.classic.core.ReportDefinition;
import org.pentaho.reporting.engine.classic.core.function.ProcessingContext;
import org.pentaho.reporting.engine.classic.core.layout.model.LogicalPageBox;
import org.pentaho.reporting.engine.classic.core.layout.output.ContentProcessingException;
import org.pentaho.reporting.engine.classic.core.layout.output.GenericOutputProcessorMetaData;
import org.pentaho.reporting.engine.classic.core.layout.output.LogicalPageKey;
import org.pentaho.reporting.engine.classic.core.layout.output.OutputProcessor;
import org.pentaho.reporting.engine.classic.core.layout.output.OutputProcessorMetaData;

public class DummyOutputProcessor implements OutputProcessor {
  private OutputProcessorMetaData metaData;
  private boolean finishedPage;

  public DummyOutputProcessor() {
    metaData = new GenericOutputProcessorMetaData();
  }

  public void processingStarted( final ReportDefinition report, final ProcessingContext processingContext ) {
  }

  public OutputProcessorMetaData getMetaData() {
    return metaData;
  }

  public void processContent( final LogicalPageBox pageBox ) throws ContentProcessingException {
  }

  public void processRecomputedContent( final LogicalPageBox pageBox ) throws ContentProcessingException {

  }

  public void processingFinished() {
    finishedPage = true;
  }

  public int getPageCursor() {
    return 0;
  }

  public void setPageCursor( final int pc ) {

  }

  public int getLogicalPageCount() {
    return finishedPage ? 1 : 0;
  }

  public LogicalPageKey getLogicalPage( final int page ) {
    return null;
  }

  public boolean isPaginationFinished() {
    return false;
  }

  public boolean isNeedAlignedPage() {
    return false;
  }

  public int getPhysicalPageCount() {
    return finishedPage ? 1 : 0;
  }
}
