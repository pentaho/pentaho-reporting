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
 * Copyright (c) 2001 - 2020 Object Refinery Ltd, Hitachi Vantara and Contributors..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.modules.output.table.html;

import org.pentaho.reporting.engine.classic.core.layout.model.LogicalPageBox;
import org.pentaho.reporting.engine.classic.core.layout.output.ContentProcessingException;
import org.pentaho.reporting.engine.classic.core.layout.output.DisplayAllFlowSelector;
import org.pentaho.reporting.engine.classic.core.layout.output.FlowSelector;
import org.pentaho.reporting.engine.classic.core.layout.output.LogicalPageKey;
import org.pentaho.reporting.engine.classic.core.layout.output.OutputProcessorMetaData;
import org.pentaho.reporting.engine.classic.core.modules.output.table.base.AbstractTableOutputProcessor;
import org.pentaho.reporting.engine.classic.core.modules.output.table.base.TableContentProducer;
import org.pentaho.reporting.engine.classic.core.modules.output.table.html.helper.HtmlOutputProcessorMetaData;
import org.pentaho.reporting.libraries.base.config.Configuration;

/**
 * Creation-Date: 04.05.2007, 16:36:40
 *
 * @author Thomas Morgner
 */
public class StreamHtmlOutputProcessor extends AbstractTableOutputProcessor implements HtmlOutputProcessor {
  private HtmlPrinter printer;
  private OutputProcessorMetaData metaData;
  private FlowSelector flowSelector;

  public StreamHtmlOutputProcessor( final Configuration configuration ) {
    if ( configuration == null ) {
      throw new NullPointerException();
    }

    this.metaData = new HtmlOutputProcessorMetaData( HtmlOutputProcessorMetaData.PAGINATION_NONE );
    this.flowSelector = new DisplayAllFlowSelector();
  }

  protected FlowSelector getFlowSelector() {
    return flowSelector;
  }

  public OutputProcessorMetaData getMetaData() {
    return metaData;
  }

  public HtmlPrinter getPrinter() {
    return printer;
  }

  public void setPrinter( final HtmlPrinter printer ) {
    this.printer = printer;
  }

  protected void processTableContent( final LogicalPageKey logicalPageKey, final LogicalPageBox logicalPage,
      final TableContentProducer contentProducer ) throws ContentProcessingException {
    if ( printer == null ) {
      return;
    }

    printer.print( logicalPageKey, logicalPage, contentProducer, metaData, false );
  }

  protected void updateTableContent( final LogicalPageKey logicalPageKey, final LogicalPageBox logicalPageBox,
      final TableContentProducer tableContentProducer, final boolean performOutput ) throws ContentProcessingException {
    if ( printer == null ) {
      return;
    }

    printer.print( logicalPageKey, logicalPageBox, tableContentProducer, metaData, true, false );
  }

  protected void processingContentFinished() {
    if ( isContentGeneratable() == false ) {
      return;
    }

    this.metaData.commit();
  }
}
