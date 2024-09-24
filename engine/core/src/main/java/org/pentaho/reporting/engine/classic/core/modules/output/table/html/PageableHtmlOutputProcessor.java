/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2028-08-13
 ******************************************************************************/

package org.pentaho.reporting.engine.classic.core.modules.output.table.html;

import org.pentaho.reporting.engine.classic.core.layout.model.LogicalPageBox;
import org.pentaho.reporting.engine.classic.core.layout.output.ContentProcessingException;
import org.pentaho.reporting.engine.classic.core.layout.output.DisplayAllFlowSelector;
import org.pentaho.reporting.engine.classic.core.layout.output.FlowSelector;
import org.pentaho.reporting.engine.classic.core.layout.output.LogicalPageKey;
import org.pentaho.reporting.engine.classic.core.layout.output.OutputProcessorMetaData;
import org.pentaho.reporting.engine.classic.core.layout.output.PhysicalPageKey;
import org.pentaho.reporting.engine.classic.core.modules.output.pageable.base.PageableOutputProcessor;
import org.pentaho.reporting.engine.classic.core.modules.output.table.base.AbstractTableOutputProcessor;
import org.pentaho.reporting.engine.classic.core.modules.output.table.base.TableContentProducer;
import org.pentaho.reporting.engine.classic.core.modules.output.table.html.helper.HtmlOutputProcessorMetaData;
import org.pentaho.reporting.libraries.base.config.Configuration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Creation-Date: 04.05.2007, 16:36:48
 *
 * @author Thomas Morgner
 */
public class PageableHtmlOutputProcessor extends AbstractTableOutputProcessor implements PageableOutputProcessor,
    HtmlOutputProcessor {
  private List<PhysicalPageKey> physicalPages;
  private HtmlOutputProcessorMetaData metaData;
  private HtmlPrinter printer;
  private FlowSelector flowSelector;

  public PageableHtmlOutputProcessor( final Configuration configuration ) {
    if ( configuration == null ) {
      throw new NullPointerException();
    }

    this.physicalPages = new ArrayList<PhysicalPageKey>();
    this.flowSelector = new DisplayAllFlowSelector();
    this.metaData = new HtmlOutputProcessorMetaData( HtmlOutputProcessorMetaData.PAGINATION_FULL );
  }

  protected void processingPagesFinished() {
    super.processingPagesFinished();
    physicalPages = Collections.unmodifiableList( physicalPages );
  }

  public int getPhysicalPageCount() {
    return physicalPages.size();
  }

  public PhysicalPageKey getPhysicalPage( final int page ) {
    if ( isPaginationFinished() == false ) {
      throw new IllegalStateException();
    }

    return physicalPages.get( page );
  }

  protected LogicalPageKey createLogicalPage( final int width, final int height ) {
    final LogicalPageKey key = super.createLogicalPage( width, height );
    for ( int h = 0; h < key.getHeight(); h++ ) {
      for ( int w = 0; w < key.getWidth(); w++ ) {
        physicalPages.add( key.getPage( w, h ) );
      }
    }
    return key;
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

  public FlowSelector getFlowSelector() {
    return flowSelector;
  }

  public void setFlowSelector( final FlowSelector flowSelector ) {
    this.flowSelector = flowSelector;
  }

  protected void processTableContent( final LogicalPageKey logicalPageKey, final LogicalPageBox logicalPage,
      final TableContentProducer contentProducer ) throws ContentProcessingException {
    if ( printer == null ) {
      return;
    }

    printer.print( logicalPageKey, logicalPage, contentProducer, metaData, false );
  }

  protected void processingContentFinished() {
    if ( isContentGeneratable() == false ) {
      return;
    }

    this.metaData.commit();
  }
}
