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

package org.pentaho.reporting.engine.classic.core.modules.output.pageable.xml;

import org.pentaho.reporting.engine.classic.core.InvalidReportStateException;
import org.pentaho.reporting.engine.classic.core.layout.model.LogicalPageBox;
import org.pentaho.reporting.engine.classic.core.layout.model.PageGrid;
import org.pentaho.reporting.engine.classic.core.layout.output.ContentProcessingException;
import org.pentaho.reporting.engine.classic.core.layout.output.LogicalPageKey;
import org.pentaho.reporting.engine.classic.core.layout.output.OutputProcessorMetaData;
import org.pentaho.reporting.engine.classic.core.layout.output.PhysicalPageKey;
import org.pentaho.reporting.engine.classic.core.modules.output.pageable.base.AbstractPageableOutputProcessor;
import org.pentaho.reporting.engine.classic.core.modules.output.pageable.base.AllPageFlowSelector;
import org.pentaho.reporting.engine.classic.core.modules.output.pageable.base.PageFlowSelector;
import org.pentaho.reporting.engine.classic.core.modules.output.pageable.xml.internal.XmlDocumentWriter;
import org.pentaho.reporting.engine.classic.core.modules.output.pageable.xml.internal.XmlPageOutputProcessorMetaData;
import org.pentaho.reporting.libraries.base.config.Configuration;

import java.io.IOException;
import java.io.OutputStream;

public class XmlPageOutputProcessor extends AbstractPageableOutputProcessor {
  private OutputProcessorMetaData metaData;
  private OutputStream outputStream;
  private PageFlowSelector flowSelector;
  private XmlDocumentWriter writer;

  public XmlPageOutputProcessor( final Configuration configuration, final OutputStream outputStream ) {
    this( outputStream, new XmlPageOutputProcessorMetaData() );
  }

  public XmlPageOutputProcessor( final OutputStream outputStream, final OutputProcessorMetaData metaData ) {
    if ( metaData == null ) {
      throw new NullPointerException( "MetaData must not be null" );
    }
    if ( outputStream == null ) {
      throw new NullPointerException( "OutputStream must not be null" );
    }

    this.outputStream = outputStream;
    this.flowSelector = new AllPageFlowSelector();

    // for the sake of simplicity, we use the AWT font registry for now.
    // This is less accurate than using the iText fonts, but completing
    // the TrueType registry or implementing an iText registry is too expensive
    // for now.
    this.metaData = metaData;
  }

  public void setFlowSelector( final PageFlowSelector flowSelector ) {
    if ( flowSelector == null ) {
      throw new NullPointerException();
    }
    this.flowSelector = flowSelector;
  }

  protected PageFlowSelector getFlowSelector() {
    return flowSelector;
  }

  protected void processingContentFinished() {
    if ( isContentGeneratable() == false ) {
      return;
    }

    if ( writer != null ) {
      try {
        metaData.commit();
        writer.close();
      } catch ( IOException e ) {
        throw new InvalidReportStateException( "Failed to close writer" );
      }
    }
  }

  protected void processPhysicalPage( final PageGrid pageGrid, final LogicalPageBox logicalPage, final int row,
      final int col, final PhysicalPageKey pageKey ) throws ContentProcessingException {
    try {
      if ( writer == null ) {
        writer = new XmlDocumentWriter( outputStream, metaData );
        writer.open();
      }
      writer.processPhysicalPage( pageGrid, logicalPage, row, col );
    } catch ( Exception e ) {
      throw new ContentProcessingException( "Failed to generate Xml document", e );
    }
  }

  protected void processLogicalPage( final LogicalPageKey key, final LogicalPageBox logicalPage )
    throws ContentProcessingException {
    try {
      if ( writer == null ) {
        writer = new XmlDocumentWriter( outputStream, metaData );
        writer.open();
      }
      writer.processLogicalPage( key, logicalPage );
    } catch ( Exception e ) {
      throw new ContentProcessingException( "Failed to generate Xml document", e );
    }
  }

  public OutputProcessorMetaData getMetaData() {
    return metaData;
  }
}
