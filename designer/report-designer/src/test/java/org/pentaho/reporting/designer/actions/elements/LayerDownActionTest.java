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

package org.pentaho.reporting.designer.actions.elements;

import junit.framework.TestCase;
import org.pentaho.reporting.designer.core.ReportDesignerBoot;
import org.pentaho.reporting.designer.core.actions.elements.LayerDownAction;
import org.pentaho.reporting.engine.classic.core.AbstractReportDefinition;
import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.PageHeader;

import java.util.ArrayList;

public class LayerDownActionTest extends TestCase {
  private class TestLayerDownAction extends LayerDownAction {
    private TestLayerDownAction() {
    }

    @Override
    public boolean collectChange( final Object[] selectedElements,
                                  final AbstractReportDefinition report,
                                  final ArrayList undos ) {
      return super.collectChange( selectedElements, report, undos );
    }
  }

  public LayerDownActionTest() {
  }

  public LayerDownActionTest( final String s ) {
    super( s );
  }

  @Override
  protected void setUp() throws Exception {
    ReportDesignerBoot.getInstance().start();
  }

  public void testLayerDown() {
    final MasterReport report = new MasterReport();
    final PageHeader pageHeader = report.getPageHeader();
    final Element first = new Element();
    final Element second = new Element();
    final Element third = new Element();

    pageHeader.addElement( first );
    pageHeader.addElement( second );
    pageHeader.addElement( third );

    final Element[] selectedElements = new Element[] { second, third };
    final ArrayList list = new ArrayList();
    assertTrue( new TestLayerDownAction().collectChange( selectedElements, report, list ) );
    assertEquals( 2, list.size() );

    assertEquals( pageHeader.getElement( 0 ).getObjectID(), selectedElements[ 0 ].getObjectID() );
    assertEquals( pageHeader.getElement( 1 ).getObjectID(), selectedElements[ 1 ].getObjectID() );
  }
}
