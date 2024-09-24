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
import org.pentaho.reporting.engine.classic.core.function.OutputFunction;
import org.pentaho.reporting.engine.classic.core.layout.output.DefaultOutputFunction;
import org.pentaho.reporting.engine.classic.core.modules.output.table.base.StreamReportProcessor;

public class DesignerReportProcessor extends StreamReportProcessor {
  private DesignerOutputProcessor outputProcessor;
  private DesignerRenderComponentFactory componentFactory;

  public DesignerReportProcessor( final MasterReport report,
                                  final DesignerOutputProcessor outputProcessor,
                                  final DesignerRenderComponentFactory componentFactory )
    throws ReportProcessingException {
    super( report, outputProcessor );
    this.outputProcessor = outputProcessor;
    this.componentFactory = componentFactory;
  }

  protected OutputFunction createLayoutManager() {
    final DefaultOutputFunction outputFunction = new DesignerOutputFunction();
    outputFunction.setRenderer( new DesignerRenderer( outputProcessor, componentFactory ) );
    return outputFunction;
  }
}
