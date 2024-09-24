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

package org.pentaho.reporting.engine.classic.core.bugs;

import junit.framework.TestCase;
import org.junit.Assert;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.PageHeader;
import org.pentaho.reporting.engine.classic.core.filter.types.bands.PageHeaderType;
import org.pentaho.reporting.engine.classic.core.layout.model.LogicalPageBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderNode;
import org.pentaho.reporting.engine.classic.core.testsupport.DebugReportRunner;
import org.pentaho.reporting.engine.classic.core.testsupport.selector.MatchFactory;
import org.pentaho.reporting.libraries.resourceloader.Resource;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

import java.net.URL;
import java.util.List;

public class Prd3154IT extends TestCase {
  public Prd3154IT() {
  }

  public Prd3154IT( final String name ) {
    super( name );
  }

  protected void setUp() throws Exception {
    ClassicEngineBoot.getInstance().start();
  }

  public void testReport() throws Exception {
    final URL url = getClass().getResource( "Prd-3154.prpt" );
    assertNotNull( url );
    final ResourceManager resourceManager = new ResourceManager();
    resourceManager.registerDefaults();
    final Resource directly = resourceManager.createDirectly( url, MasterReport.class );
    final MasterReport report = (MasterReport) directly.getResource();

    // having images in the header does not add any information, but slows down the report processing.
    PageHeader pageHeader = report.getPageHeader();
    pageHeader.removeElement( pageHeader.getElement( 5 ) );
    pageHeader.removeElement( pageHeader.getElement( 0 ) );

    List<LogicalPageBox> logicalPageBoxes = DebugReportRunner.layoutPages( report, 0, 1 );
    for ( LogicalPageBox box : logicalPageBoxes ) {
      RenderNode[] elementsByElementType = MatchFactory.findElementsByElementType( box, PageHeaderType.INSTANCE );
      Assert.assertEquals( 1, elementsByElementType.length );
      Assert.assertTrue( elementsByElementType[0] instanceof RenderBox );
      RenderBox ph = (RenderBox) elementsByElementType[0];
      Assert.assertEquals( 4, ph.getChildCount() );
    }
  }

}
