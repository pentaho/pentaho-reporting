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

package org.pentaho.reporting.engine.classic.core.testsupport;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.layout.model.LogicalPageBox;
import org.pentaho.reporting.engine.classic.core.layout.output.AbstractOutputProcessor;
import org.pentaho.reporting.engine.classic.core.layout.output.LogicalPageKey;
import org.pentaho.reporting.engine.classic.core.layout.output.OutputProcessorMetaData;
import org.pentaho.reporting.engine.classic.core.modules.output.pageable.base.PageableRenderer;
import org.pentaho.reporting.engine.classic.core.modules.output.pageable.graphics.internal.GraphicsOutputProcessorMetaData;

public class DebugRenderer extends PageableRenderer {
  private static final Log logger = LogFactory.getLog( DebugRenderer.class );

  public static class DebugOutputProcessor extends AbstractOutputProcessor {
    private OutputProcessorMetaData metaData;
    private DebugReportValidator validator;

    public DebugOutputProcessor() {
      metaData = new GraphicsOutputProcessorMetaData();
    }

    public DebugReportValidator getValidator() {
      return validator;
    }

    public void setValidator( final DebugReportValidator validator ) {
      this.validator = validator;
    }

    protected DebugOutputProcessor( final OutputProcessorMetaData metaData ) {
      if ( metaData == null ) {
        throw new NullPointerException();
      }
      this.metaData = metaData;
    }

    public OutputProcessorMetaData getMetaData() {
      return metaData;
    }

    protected void processPageContent( final LogicalPageKey logicalPageKey, final LogicalPageBox logicalPage ) {
      if ( validator != null ) {
        validator.processPageContent( logicalPageKey, logicalPage );
      }
      logger.debug( "Page-Offset: " + logicalPage.getPageOffset() );
    }
  }

  private DebugOutputProcessor outputProcessor;

  public DebugRenderer( final DebugOutputProcessor outputProcessor, final DebugReportValidator validator ) {
    super( outputProcessor );
    this.outputProcessor = outputProcessor;
    this.outputProcessor.setValidator( validator );
  }

  public DebugRenderer( final DebugOutputProcessor outputProcessor ) {
    this( outputProcessor, null );
  }

  public DebugRenderer( final OutputProcessorMetaData outputProcessorMetaData ) {
    this( new DebugOutputProcessor( outputProcessorMetaData ) );
  }

  public DebugRenderer() {
    this( new DebugOutputProcessor() );
  }

  public DebugReportValidator getValidator() {
    return outputProcessor.getValidator();
  }

  public void setValidator( final DebugReportValidator validator ) {
    outputProcessor.setValidator( validator );
  }

  protected void debugPrint( final LogicalPageBox pageBox ) {
    // ModelPrinter.print(pageBox);
  }

  public LogicalPageBox getPageBox() {
    return super.getPageBox();
  }

  public void markDirty() {
    super.markDirty();
  }
}
