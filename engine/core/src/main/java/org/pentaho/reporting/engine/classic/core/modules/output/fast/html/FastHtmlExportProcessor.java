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

package org.pentaho.reporting.engine.classic.core.modules.output.fast.html;

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
import org.pentaho.reporting.engine.classic.core.modules.output.table.html.helper.HtmlOutputProcessorMetaData;
import org.pentaho.reporting.libraries.base.config.Configuration;

public class FastHtmlExportProcessor extends AbstractReportProcessor {
  private static class HtmlDataOutputProcessor extends AbstractOutputProcessor {
    private OutputProcessorMetaData metaData;

    private HtmlDataOutputProcessor() {
      metaData = new HtmlOutputProcessorMetaData( HtmlOutputProcessorMetaData.PAGINATION_NONE ) {
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

  private FastHtmlContentItems contentItems;

  public FastHtmlExportProcessor( final MasterReport report, final FastHtmlContentItems contentItems )
    throws ReportProcessingException {
    super( report, new HtmlDataOutputProcessor() );
    this.contentItems = contentItems;
  }

  protected OutputFunction createLayoutManager() {
    return new FastExportOutputFunction( new FastHtmlExportTemplate( contentItems ) );
  }
}
