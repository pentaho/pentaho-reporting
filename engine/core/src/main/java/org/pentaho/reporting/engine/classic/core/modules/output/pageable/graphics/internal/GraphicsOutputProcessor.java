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

package org.pentaho.reporting.engine.classic.core.modules.output.pageable.graphics.internal;

import org.pentaho.reporting.engine.classic.core.layout.model.LogicalPageBox;
import org.pentaho.reporting.engine.classic.core.layout.model.PageGrid;
import org.pentaho.reporting.engine.classic.core.layout.model.PhysicalPageBox;
import org.pentaho.reporting.engine.classic.core.layout.output.LogicalPageKey;
import org.pentaho.reporting.engine.classic.core.layout.output.OutputProcessorMetaData;
import org.pentaho.reporting.engine.classic.core.layout.output.PhysicalPageKey;
import org.pentaho.reporting.engine.classic.core.modules.output.pageable.base.AbstractPageableOutputProcessor;
import org.pentaho.reporting.engine.classic.core.modules.output.pageable.base.PageFlowSelector;
import org.pentaho.reporting.libraries.base.config.Configuration;
import org.pentaho.reporting.libraries.fonts.awt.AWTFontRegistry;
import org.pentaho.reporting.libraries.fonts.registry.DefaultFontStorage;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

/**
 * Creation-Date: 02.01.2006, 19:55:14
 *
 * @author Thomas Morgner
 */
public class GraphicsOutputProcessor extends AbstractPageableOutputProcessor {
  private OutputProcessorMetaData metaData;
  private GraphicsContentInterceptor interceptor;
  private ResourceManager resourceManager;

  public GraphicsOutputProcessor( final Configuration configuration ) {
    if ( configuration == null ) {
      throw new NullPointerException();
    }
    final DefaultFontStorage fontStorage = new DefaultFontStorage( new AWTFontRegistry() );
    metaData = new GraphicsOutputProcessorMetaData( fontStorage );
    resourceManager = new ResourceManager();
  }

  public GraphicsOutputProcessor( final Configuration configuration, final ResourceManager resourceManager ) {
    if ( configuration == null ) {
      throw new NullPointerException();
    }
    if ( resourceManager == null ) {
      throw new NullPointerException();
    }
    final DefaultFontStorage fontStorage = new DefaultFontStorage( new AWTFontRegistry() );
    metaData = new GraphicsOutputProcessorMetaData( fontStorage );
    this.resourceManager = resourceManager;
  }

  public GraphicsOutputProcessor( final OutputProcessorMetaData metaData, final ResourceManager resourceManager ) {
    if ( resourceManager == null ) {
      throw new NullPointerException();
    }
    this.metaData = metaData;
    this.resourceManager = resourceManager;
  }

  public OutputProcessorMetaData getMetaData() {
    return metaData;
  }

  public GraphicsContentInterceptor getInterceptor() {
    return interceptor;
  }

  public void setInterceptor( final GraphicsContentInterceptor interceptor ) {
    this.interceptor = interceptor;
  }

  protected final PageFlowSelector getFlowSelector() {
    return getInterceptor();
  }

  protected void processPhysicalPage( final PageGrid pageGrid, final LogicalPageBox logicalPage, final int row,
      final int col, final PhysicalPageKey pageKey ) {
    final PhysicalPageBox page = pageGrid.getPage( row, col );
    if ( page != null ) {
      final LogicalPageDrawable drawable = new LogicalPageDrawable();
      drawable.init( logicalPage, metaData, resourceManager );
      final PhysicalPageDrawable pageDrawable = new PhysicalPageDrawable( drawable, page );
      interceptor.processPhysicalPage( pageKey, pageDrawable );
    }
  }

  protected void processLogicalPage( final LogicalPageKey key, final LogicalPageBox logicalPage ) {
    final LogicalPageDrawable drawable = new LogicalPageDrawable();
    drawable.init( logicalPage, metaData, resourceManager );
    interceptor.processLogicalPage( key, drawable );
  }

  protected void processingContentFinished() {
    if ( isContentGeneratable() == false ) {
      return;
    }

    this.metaData.commit();
  }
}
