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
import org.pentaho.reporting.engine.classic.core.layout.output.LogicalPageKey;
import org.pentaho.reporting.engine.classic.core.layout.output.OutputProcessorMetaData;
import org.pentaho.reporting.engine.classic.core.modules.output.table.base.TableContentProducer;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

/**
 * Creation-Date: 08.05.2007, 20:04:44
 *
 * @author Thomas Morgner
 */
public class AllItemsHtmlPrinter extends HtmlPrinter {
  public AllItemsHtmlPrinter( final ResourceManager resourceManager ) {
    super( resourceManager );
  }

  public void print( final LogicalPageKey logicalPageKey, final LogicalPageBox logicalPage,
      final TableContentProducer contentProducer, final OutputProcessorMetaData metaData, final boolean incremental )
    throws ContentProcessingException {
    try {
      super.print( logicalPageKey, logicalPage, contentProducer, metaData, incremental );
    } catch ( ContentProcessingException ce ) {
      throw ce;
    } catch ( Exception e ) {
      // ignore .. (for now)
      throw new ContentProcessingException( "Processing content failed", e );
    }
  }
}
