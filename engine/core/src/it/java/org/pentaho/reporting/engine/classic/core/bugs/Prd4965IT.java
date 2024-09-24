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

package org.pentaho.reporting.engine.classic.core.bugs;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.layout.model.LogicalPageBox;
import org.pentaho.reporting.engine.classic.core.layout.model.ParagraphRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderNode;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderableText;
import org.pentaho.reporting.engine.classic.core.testsupport.DebugReportRunner;
import org.pentaho.reporting.engine.classic.core.testsupport.selector.MatchFactory;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

import java.net.URL;

public class Prd4965IT {
  @Before
  public void setUp() throws Exception {
    ClassicEngineBoot.getInstance().start();
  }

  @Test
  public void testSampleReport() throws Exception {
    URL res = getClass().getResource( "Prd-4965.prpt" );
    MasterReport report = (MasterReport) new ResourceManager().createDirectly( res, MasterReport.class ).getResource();
    LogicalPageBox logicalPageBox = DebugReportRunner.layoutPage( report, 0 );
    RenderNode[] mrs = MatchFactory.findElementsByName( logicalPageBox, "mr" );
    Assert.assertEquals( 2, mrs.length );
    Assert.assertTrue( mrs[0] instanceof ParagraphRenderBox );
    Assert.assertTrue( mrs[1] instanceof RenderableText );

    RenderNode[] sr0s = MatchFactory.findElementsByName( logicalPageBox, "sr-1" );
    Assert.assertEquals( 2, sr0s.length );
    Assert.assertTrue( sr0s[0] instanceof ParagraphRenderBox );
    Assert.assertTrue( sr0s[1] instanceof RenderableText );

    RenderNode[] sr1s = MatchFactory.findElementsByName( logicalPageBox, "sr-2" );
    Assert.assertEquals( 2, sr1s.length );
    Assert.assertTrue( sr1s[0] instanceof ParagraphRenderBox );
    Assert.assertTrue( sr1s[1] instanceof RenderableText );

  }
}
