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
