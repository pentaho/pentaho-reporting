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
