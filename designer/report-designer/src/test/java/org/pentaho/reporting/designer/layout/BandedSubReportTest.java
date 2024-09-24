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

package org.pentaho.reporting.designer.layout;

import junit.framework.TestCase;
import org.pentaho.reporting.designer.core.editor.ReportRenderContext;
import org.pentaho.reporting.designer.core.editor.report.layouting.ReportLayouter;
import org.pentaho.reporting.engine.classic.core.AttributeNames;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.SubReport;
import org.pentaho.reporting.engine.classic.core.layout.ModelPrinter;
import org.pentaho.reporting.engine.classic.core.layout.model.LogicalPageBox;
import org.pentaho.reporting.engine.classic.core.testsupport.selector.MatchFactory;

public class BandedSubReportTest extends TestCase {
  public BandedSubReportTest() {
  }

  public BandedSubReportTest( final String name ) {
    super( name );
  }

  protected void setUp() throws Exception {
    ClassicEngineBoot.getInstance().start();
  }

  public void testRendering() throws Exception {
    MasterReport masterReport = new MasterReport();
    SubReport element = new SubReport();
    masterReport.getReportHeader().addSubReport( element );

    ReportLayouter l = new ReportLayouter( new ReportRenderContext( masterReport ) );
    LogicalPageBox layout = l.layout();
    ModelPrinter.INSTANCE.print( layout );

    MatchFactory.findElementsByAttribute
      ( layout, AttributeNames.Core.NAMESPACE, AttributeNames.Core.ELEMENT_TYPE, element.getElementType() );
  }
}
