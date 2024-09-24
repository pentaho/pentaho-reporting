/*
 * This program is free software; you can redistribute it and/or modify it under the
 *  terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
 *  Foundation.
 *
 *  You should have received a copy of the GNU Lesser General Public License along with this
 *  program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 *  or from the Free Software Foundation, Inc.,
 *  51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 *  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *  See the GNU Lesser General Public License for more details.
 *
 *  Copyright (c) 2006 - 2017 Hitachi Vantara..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.modules.output.fast.template;

import org.pentaho.reporting.engine.classic.core.layout.model.LogicalPageBox;
import org.pentaho.reporting.engine.classic.core.layout.output.AbstractOutputProcessor;
import org.pentaho.reporting.engine.classic.core.layout.output.ContentProcessingException;
import org.pentaho.reporting.engine.classic.core.layout.output.LogicalPageKey;
import org.pentaho.reporting.engine.classic.core.layout.output.OutputProcessorMetaData;
import org.pentaho.reporting.engine.classic.core.modules.output.table.base.SheetLayout;
import org.pentaho.reporting.engine.classic.core.modules.output.table.base.TableContentProducer;
import org.pentaho.reporting.engine.classic.core.modules.output.table.base.TableLayoutProducer;

public class TemplatingOutputProcessor extends AbstractOutputProcessor {
  private OutputProcessorMetaData metaData;
  private FastExportTemplateListener templateListener;

  public TemplatingOutputProcessor( final OutputProcessorMetaData metaData,
      final FastExportTemplateListener templateListener ) {
    if ( metaData == null ) {
      throw new NullPointerException();
    }
    this.templateListener = templateListener;
    this.metaData = metaData;
  }

  public OutputProcessorMetaData getMetaData() {
    return metaData;
  }

  protected void processPageContent( final LogicalPageKey logicalPageKey, final LogicalPageBox logicalPage ) {
    if ( templateListener != null ) {
      templateListener.produceTemplate( logicalPage );
    }
  }

  protected void processPaginationContent( final LogicalPageKey logicalPageKey, final LogicalPageBox logicalPage )
    throws ContentProcessingException {
    if ( templateListener != null ) {
      templateListener.produceTemplate( logicalPage );
    }
  }

  public static TableContentProducer produceTableLayout( final LogicalPageBox pageBox, final SheetLayout layout,
      final OutputProcessorMetaData metaData ) {
    layout.clearVerticalInfo();

    TableLayoutProducer currentLayout = new TableLayoutProducer( metaData, layout );
    currentLayout.update( pageBox.derive( true ), false );
    currentLayout.pageCompleted();

    TableContentProducer contentProducer = new TableContentProducer( layout, metaData );
    contentProducer.compute( pageBox, false );
    return contentProducer;
  }
}
