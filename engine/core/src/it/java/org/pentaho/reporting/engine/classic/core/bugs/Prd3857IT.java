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
import org.junit.Ignore;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.ClassicEngineCoreModule;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.filter.types.bands.ItemBandType;
import org.pentaho.reporting.engine.classic.core.layout.model.LogicalPageBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderNode;
import org.pentaho.reporting.engine.classic.core.testsupport.DebugReportRunner;
import org.pentaho.reporting.engine.classic.core.testsupport.gold.GoldTestBase;
import org.pentaho.reporting.engine.classic.core.testsupport.selector.MatchFactory;
import org.pentaho.reporting.libraries.resourceloader.Resource;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

import java.io.File;

public class Prd3857IT extends TestCase {
  public Prd3857IT() {
  }

  protected void setUp() throws Exception {
    ClassicEngineBoot.getInstance().start();
  }

//  @Ignore
//  public void testGoldRun() throws Exception {
//    final File file = GoldTestBase.locateGoldenSampleReport( "Prd-3239.prpt" );
//    final ResourceManager mgr = new ResourceManager();
//    mgr.registerDefaults();
//    final Resource directly = mgr.createDirectly( file, MasterReport.class );
//    final MasterReport report = (MasterReport) directly.getResource();
//    report.setCompatibilityLevel( ClassicEngineBoot.computeVersionId( 3, 8, 0 ) );
//
//    DebugReportRunner.createXmlFlow( report );
//    DebugReportRunner.showDialog( report );
//  }

  public void testGoldRun3857Visually() throws Exception {
    final File file = GoldTestBase.locateGoldenSampleReport( "Prd-3857-001.prpt" );
    final ResourceManager mgr = new ResourceManager();
    mgr.registerDefaults();
    final Resource directly = mgr.createDirectly( file, MasterReport.class );
    final MasterReport report = (MasterReport) directly.getResource();

    // DebugReportRunner.createXmlFlow(report);
    DebugReportRunner.showDialog( report );
  }

  public void testRowBoxesEstablishOwnBlockContext() throws Exception {
    // this report defines that the group as well as all bands within that group are row-layout.
    // therefore the two itembands end on the same row.

    // The itemband did not define a width, not even a 100% width, and thus ends with a width of auto/zero.
    // therefore the itemband shrinks to the minimal size that still encloses all elements.
    // the elements that have percentage width are resolved against the block context.
    // A band without a width defined (the itemband!), does not establish an own block-context, so it
    // takes the block context of the parent, or as fallback: page.

    final File file = GoldTestBase.locateGoldenSampleReport( "Prd-3479.prpt" );
    final ResourceManager mgr = new ResourceManager();
    mgr.registerDefaults();
    final Resource directly = mgr.createDirectly( file, MasterReport.class );
    final MasterReport report = (MasterReport) directly.getResource();
    report.setCompatibilityLevel( null );
    report.getReportConfiguration().setConfigProperty( ClassicEngineCoreModule.COMPLEX_TEXT_CONFIG_OVERRIDE_KEY,
        "false" );

    final LogicalPageBox logicalPageBox = DebugReportRunner.layoutPage( report, 0 );
    final RenderNode[] itembands = MatchFactory.findElementsByElementType( logicalPageBox, ItemBandType.INSTANCE );

    assertEquals( 2, itembands.length );
    assertEquals( 48208843, itembands[0].getWidth() );
    assertEquals( 48208843, itembands[1].getWidth() );
    assertEquals( 48208843, itembands[1].getX() );
  }
}
