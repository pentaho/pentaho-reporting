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
import org.pentaho.reporting.libraries.repository.ContentItem;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

/**
 * A HTML-Generator that generates one file in a predefined location. Only the first stream is written, all other
 * attempts to generate content will be silently ignored.
 *
 * @author Thomas Morgner
 */
public class SingleItemHtmlPrinter extends HtmlPrinter {
  private boolean printed;

  public SingleItemHtmlPrinter( final ResourceManager resourceManager, final ContentItem documentContentItem ) {
    super( resourceManager );
    if ( documentContentItem == null ) {
      throw new NullPointerException();
    }

    setDocumentContentItem( documentContentItem );
  }

  public void print( final LogicalPageKey logicalPageKey, final LogicalPageBox logicalPage,
      final TableContentProducer contentProducer, final OutputProcessorMetaData metaData, final boolean incremental )
    throws ContentProcessingException {
    if ( printed ) {
      return;
    }

    super.print( logicalPageKey, logicalPage, contentProducer, metaData, incremental );
    if ( incremental == false ) {
      printed = true;
    }
  }
}
