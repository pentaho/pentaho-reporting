/*!
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
* Copyright (c) 2002-2017 Hitachi Vantara..  All rights reserved.
*/

package org.pentaho.reporting.designer.core.editor.report.layouting;

import org.pentaho.reporting.engine.classic.core.layout.model.LogicalPageBox;
import org.pentaho.reporting.engine.classic.core.layout.output.AbstractOutputProcessor;
import org.pentaho.reporting.engine.classic.core.layout.output.ContentProcessingException;
import org.pentaho.reporting.engine.classic.core.layout.output.LogicalPageKey;
import org.pentaho.reporting.engine.classic.core.layout.output.OutputProcessorMetaData;

public class DesignerOutputProcessor extends AbstractOutputProcessor {
  private OutputProcessorMetaData metadata;
  private LogicalPageBox logicalPage;

  public DesignerOutputProcessor() {
    this( new DesignerOutputProcessorMetaData() );
  }

  public DesignerOutputProcessor( final DesignerOutputProcessorMetaData metaData ) {
    if ( metaData == null ) {
      throw new NullPointerException();
    }
    this.metadata = metaData;
  }

  public boolean isNeedAlignedPage() {
    // this guarantees that we get a copy of the logical page. 
    return true;
  }

  protected void processPageContent( final LogicalPageKey logicalPageKey,
                                     final LogicalPageBox logicalPage ) throws ContentProcessingException {
    this.logicalPage = logicalPage;
  }

  public OutputProcessorMetaData getMetaData() {
    return metadata;
  }

  public LogicalPageBox getLogicalPage() {
    return logicalPage;
  }
}
