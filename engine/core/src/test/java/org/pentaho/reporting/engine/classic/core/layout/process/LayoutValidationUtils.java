/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


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
