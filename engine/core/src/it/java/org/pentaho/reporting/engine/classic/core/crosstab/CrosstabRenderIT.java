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

package org.pentaho.reporting.engine.classic.core.crosstab;

import junit.framework.TestCase;
import org.junit.Assert;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.CrosstabElement;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.NoDataBand;
import org.pentaho.reporting.engine.classic.core.SimplePageDefinition;
import org.pentaho.reporting.engine.classic.core.layout.model.LogicalPageBox;
import org.pentaho.reporting.engine.classic.core.testsupport.DebugReportRunner;
import org.pentaho.reporting.engine.classic.core.util.PageSize;
import org.pentaho.reporting.libraries.resourceloader.Resource;
import org.pentaho.reporting.libraries.resourceloader.ResourceException;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

import java.awt.*;
import java.awt.print.PageFormat;
import java.net.URL;

public class CrosstabRenderIT extends TestCase {
  public CrosstabRenderIT() {
  }

  public CrosstabRenderIT( final String name ) {
    super( name );
  }

  protected void setUp() throws Exception {
    ClassicEngineBoot.getInstance().start();
  }

  public void testLegacyRendering() throws Exception {
    final URL url = getClass().getResource( "CrosstabTest.prpt" );
    final ResourceManager manager = new ResourceManager();
    manager.registerDefaults();
    final Resource res = manager.createDirectly( url, MasterReport.class );
    final MasterReport report = (MasterReport) res.getResource();
    report.setCompatibilityLevel( ClassicEngineBoot.computeVersionId( 4, 0, 0 ) );

    final LogicalPageBox box = DebugReportRunner.layoutPage( report, 0 );
    // ModelPrinter.print(box);
  }

  public void testLegacyRenderingLarge() throws Exception {
    final URL url = getClass().getResource( "LargeCrosstabTest.prpt" );
    final ResourceManager manager = new ResourceManager();
    manager.registerDefaults();
    final Resource res = manager.createDirectly( url, MasterReport.class );
    final MasterReport report = (MasterReport) res.getResource();
    report.setCompatibilityLevel( ClassicEngineBoot.computeVersionId( 4, 0, 0 ) );

    report
        .setPageDefinition( new SimplePageDefinition( PageSize.A0, PageFormat.PORTRAIT, new Insets( 10, 10, 10, 10 ) ) );
    final LogicalPageBox box = DebugReportRunner.layoutPage( report, 0 );
    // ModelPrinter.print(box);
  }

  public void testClone() throws ResourceException {
    CrosstabElement element = new CrosstabElement();

    final MasterReport report = new MasterReport();
    report.getReportHeader().addElement( element );

    CrosstabElement ct0 = (CrosstabElement) report.getReportHeader().getElement( 0 );
    NoDataBand noDataBand0 = ct0.getNoDataBand();
    MasterReport derive = (MasterReport) report.derive();
    CrosstabElement ct1 = (CrosstabElement) derive.getReportHeader().getElement( 0 );
    NoDataBand noDataBand1 = ct1.getNoDataBand();
    Assert.assertNotSame( noDataBand0, noDataBand1 );
  }
}
