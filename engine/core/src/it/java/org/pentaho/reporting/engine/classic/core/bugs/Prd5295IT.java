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
import org.pentaho.reporting.engine.classic.core.GroupHeader;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.SubReport;
import org.pentaho.reporting.engine.classic.core.function.GenericExpressionRuntime;
import org.pentaho.reporting.engine.classic.core.layout.model.LogicalPageBox;
import org.pentaho.reporting.engine.classic.core.layout.output.OutputProcessor;
import org.pentaho.reporting.engine.classic.core.modules.output.fast.html.FastHtmlReportUtil;
import org.pentaho.reporting.engine.classic.core.modules.output.fast.template.FastExportTemplateListener;
import org.pentaho.reporting.engine.classic.core.modules.output.fast.template.FastSheetLayoutProducer;
import org.pentaho.reporting.engine.classic.core.modules.output.fast.template.TemplatingOutputProcessor;
import org.pentaho.reporting.engine.classic.core.modules.output.table.html.HtmlReportUtil;
import org.pentaho.reporting.engine.classic.core.testsupport.DebugJndiContextFactoryBuilder;
import org.pentaho.reporting.engine.classic.core.testsupport.DebugReportRunner;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

import javax.naming.spi.NamingManager;
import java.io.ByteArrayOutputStream;
import java.net.URL;

public class Prd5295IT {
  @Before
  public void setUp() throws Exception {
    ClassicEngineBoot.getInstance().start();
    if ( NamingManager.hasInitialContextFactoryBuilder() == false ) {
      NamingManager.setInitialContextFactoryBuilder( new DebugJndiContextFactoryBuilder() );
    }
  }

  @Test
  public void testHtml() throws Exception {
    URL resource = getClass().getResource( "Prd-5295.prpt" );
    ResourceManager mgr = new ResourceManager();
    MasterReport report = (MasterReport) mgr.createDirectly( resource, MasterReport.class ).getResource();

    ByteArrayOutputStream boutFast = new ByteArrayOutputStream();
    ByteArrayOutputStream boutSlow = new ByteArrayOutputStream();
    FastHtmlReportUtil.processStreamHtml( report, boutFast );
    HtmlReportUtil.createStreamHTML( report, boutSlow );
    String htmlFast = boutFast.toString( "UTF-8" );
    String htmlSlow = boutSlow.toString( "UTF-8" );
    Assert.assertEquals( htmlSlow, htmlFast );
  }

  @Test
  public void testInvalidTemplateOnSubReport() throws Exception {
    URL resource = getClass().getResource( "Prd-5295.prpt" );
    ResourceManager mgr = new ResourceManager();
    MasterReport report = (MasterReport) mgr.createDirectly( resource, MasterReport.class ).getResource();

    ExtractLogicalPageTemplateListener tlp = new ExtractLogicalPageTemplateListener();
    GenericExpressionRuntime runtime = new GenericExpressionRuntime();
    runtime.getProcessingContext().getOutputProcessorMetaData().initialize(
        ClassicEngineBoot.getInstance().getGlobalConfig() );
    final OutputProcessor op =
        new TemplatingOutputProcessor( runtime.getProcessingContext().getOutputProcessorMetaData(), tlp );

    GroupHeader band = report.getRelationalGroup( 0 ).getHeader();
    SubReport sr = band.getSubReport( 0 );

    DebugReportRunner.resolveStyle( sr );
    DebugReportRunner.resolveStyle( sr.getPageHeader() );
    FastSheetLayoutProducer.performLayout( sr.getPageHeader(), runtime, op );

    LogicalPageBox pageBox = tlp.getPageBox();
    Assert.assertEquals( 0, pageBox.getHeight() );
  }

  private class ExtractLogicalPageTemplateListener implements FastExportTemplateListener {
    private LogicalPageBox pageBox;

    public void produceTemplate( final LogicalPageBox pageBox ) {
      this.pageBox = pageBox;
    }

    public LogicalPageBox getPageBox() {
      return pageBox;
    }
  }
}
