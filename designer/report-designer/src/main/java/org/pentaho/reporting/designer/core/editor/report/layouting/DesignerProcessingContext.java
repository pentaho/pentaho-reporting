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

import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.layout.output.DefaultProcessingContext;
import org.pentaho.reporting.libraries.resourceloader.ResourceKey;

public class DesignerProcessingContext extends DefaultProcessingContext {
  private ResourceKey contentBase;

  public DesignerProcessingContext( final MasterReport masterReport ) throws ReportProcessingException {
    super( masterReport );
    setOutputProcessorMetaData( new DesignerOutputProcessorMetaData() );
  }

  /**
   * This constructor exists for test-case use only. If you use this to process a real report, most of the settings of
   * the report will be ignored and your export will not work as expected.
   */
  public DesignerProcessingContext() {
    setOutputProcessorMetaData( new DesignerOutputProcessorMetaData() );
  }

  public ResourceKey getContentBase() {
    return contentBase;
  }

  public void setContentBase( final ResourceKey contentBase ) {
    this.contentBase = contentBase;
  }
}
