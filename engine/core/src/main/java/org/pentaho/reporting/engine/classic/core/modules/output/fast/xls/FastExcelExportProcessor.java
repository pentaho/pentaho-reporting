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

package org.pentaho.reporting.engine.classic.core.modules.output.fast.xls;

import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.function.OutputFunction;
import org.pentaho.reporting.engine.classic.core.layout.model.LogicalPageBox;
import org.pentaho.reporting.engine.classic.core.layout.output.AbstractOutputProcessor;
import org.pentaho.reporting.engine.classic.core.layout.output.AbstractReportProcessor;
import org.pentaho.reporting.engine.classic.core.layout.output.ContentProcessingException;
import org.pentaho.reporting.engine.classic.core.layout.output.LogicalPageKey;
import org.pentaho.reporting.engine.classic.core.layout.output.OutputProcessorFeature;
import org.pentaho.reporting.engine.classic.core.layout.output.OutputProcessorMetaData;
import org.pentaho.reporting.engine.classic.core.modules.output.fast.FastExportOutputFunction;
import org.pentaho.reporting.engine.classic.core.modules.output.table.xls.helper.ExcelOutputProcessorMetaData;
import org.pentaho.reporting.libraries.base.config.Configuration;

import java.io.OutputStream;

public class FastExcelExportProcessor extends AbstractReportProcessor {
  private static class ExcelDataOutputProcessor extends AbstractOutputProcessor {
    private OutputProcessorMetaData metaData;

    private ExcelDataOutputProcessor() {
      metaData = new ExcelOutputProcessorMetaData( ExcelOutputProcessorMetaData.PAGINATION_NONE ) {
        public void initialize( final Configuration configuration ) {
          super.initialize( configuration );
          addFeature( OutputProcessorFeature.FAST_EXPORT );
        }
      };
    }

    protected void processPageContent( final LogicalPageKey logicalPageKey, final LogicalPageBox logicalPage )
      throws ContentProcessingException {
      // not used ..
    }

    public OutputProcessorMetaData getMetaData() {
      return metaData;
    }
  }

  private OutputStream outputStream;
  private boolean useXlsx;

  public FastExcelExportProcessor( final MasterReport report, final OutputStream outputStream, final boolean useXlsx )
    throws ReportProcessingException {
    super( report, new ExcelDataOutputProcessor() );
    this.outputStream = outputStream;
    this.useXlsx = useXlsx;
  }

  protected OutputFunction createLayoutManager() {
    return new FastExportOutputFunction( new FastExcelExportTemplate( outputStream, useXlsx ) );
  }

}
