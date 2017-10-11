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

package org.pentaho.reporting.engine.classic.core.layout.process;

import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.layout.model.LogicalPageBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderNode;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderableText;
import org.pentaho.reporting.engine.classic.core.testsupport.DebugReportRunner;
import org.pentaho.reporting.engine.classic.core.testsupport.selector.MatchFactory;
import org.pentaho.reporting.engine.classic.core.util.geom.StrictGeomUtility;
import org.pentaho.reporting.libraries.resourceloader.Resource;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * @author Andrey Khayrutdinov
 */
class LayoutValidationUtils {

  static List<LogicalPageBox> loadPages( String file, int expectedPages ) throws Exception {
    ResourceManager resourceManager = new ResourceManager();
    resourceManager.registerDefaults();
    Resource resource = resourceManager
      .createDirectly( LayoutValidationUtils.class.getResource( "pagination/" + file ), MasterReport.class );
    MasterReport report = (MasterReport) resource.getResource();

    int[] pages = new int[ expectedPages ];
    for ( int i = 0; i < expectedPages; i++ ) {
      pages[ i ] = i;
    }
    return DebugReportRunner.layoutPagesStrict( report, expectedPages, pages );
  }


  static RenderNode findParagraph( LogicalPageBox page, String nodeName, String nodeText ) {
    RenderNode[] nodes = MatchFactory.findElementsByName( page, nodeName );
    assertEquals( "Name lookup returned the paragraph and renderable text", 2, nodes.length );
    assertEquals( nodeText, ( (RenderableText) nodes[ 1 ] ).getRawText() );
    return nodes[ 0 ];
  }

  static void assertPageHeader( LogicalPageBox page, String name, String text, int height ) {
    RenderNode p = findParagraph( page, name, text );
    assertEquals( 0, p.getY() );
    assertEquals( StrictGeomUtility.toInternalValue( height ), p.getHeight() );
  }

  static void assertPageFooter( LogicalPageBox page, String name, String text, int height ) {
    RenderNode p = findParagraph( page, name, text );
    assertEquals( StrictGeomUtility.toInternalValue( height ), p.getHeight() );
    assertEquals( page.getPageEnd(), p.getY2() );
  }
}
