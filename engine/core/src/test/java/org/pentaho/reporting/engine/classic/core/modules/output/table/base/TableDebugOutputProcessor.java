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

package org.pentaho.reporting.engine.classic.core.modules.output.table.base;

import junit.framework.AssertionFailedError;

import org.pentaho.reporting.engine.classic.core.layout.ModelPrinter;
import org.pentaho.reporting.engine.classic.core.layout.model.LogicalPageBox;
import org.pentaho.reporting.engine.classic.core.layout.output.ContentProcessingException;
import org.pentaho.reporting.engine.classic.core.layout.output.FlowSelector;
import org.pentaho.reporting.engine.classic.core.layout.output.LogicalPageKey;
import org.pentaho.reporting.engine.classic.core.layout.output.OutputProcessorMetaData;
import org.pentaho.reporting.engine.classic.core.modules.output.pageable.base.AllPageFlowSelector;
import org.pentaho.reporting.engine.classic.core.modules.output.table.base.layout.model.ResultTable;

public class TableDebugOutputProcessor extends AbstractTableOutputProcessor {
  private OutputProcessorMetaData metaData;
  private FlowSelector flowSelector;
  private SheetLayout layout;
  private LogicalPageBox logicalPage;
  private TableContentProducer contentProducer;

  public TableDebugOutputProcessor( final OutputProcessorMetaData metaData ) {
    this.metaData = metaData;
    this.flowSelector = new AllPageFlowSelector( true );
  }

  protected void updateTableContent( final LogicalPageKey logicalPageKey, final LogicalPageBox logicalPageBox,
      final TableContentProducer tableContentProducer, final boolean performOutput ) throws ContentProcessingException {
    this.logicalPage = logicalPageBox;
    this.contentProducer = tableContentProducer;
    this.layout = contentProducer.getSheetLayout();
  }

  protected void processTableContent( final LogicalPageKey logicalPageKey, final LogicalPageBox logicalPage,
      final TableContentProducer contentProducer ) throws ContentProcessingException {
    this.logicalPage = logicalPage;
    this.contentProducer = contentProducer;
    this.layout = contentProducer.getSheetLayout();
  }

  public void validate( final ResultTable resultTable ) {
    try {
      // then add it to the layout-producer ..
      resultTable.validate( logicalPage, layout, contentProducer );
    } catch ( AssertionFailedError afe ) {
      ModelPrinter.INSTANCE.print( logicalPage );
      SheetLayoutPrinter.print( logicalPage, layout, contentProducer );
      throw afe;
    }
  }

  protected FlowSelector getFlowSelector() {
    return flowSelector;
  }

  public OutputProcessorMetaData getMetaData() {
    return metaData;
  }

}
