/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2028-08-13
 ******************************************************************************/

package org.pentaho.reporting.designer.layout;

import junit.framework.TestCase;
import org.pentaho.reporting.designer.core.ReportDesignerBoot;
import org.pentaho.reporting.designer.core.auth.GlobalAuthenticationStore;
import org.pentaho.reporting.designer.core.editor.ReportRenderContext;
import org.pentaho.reporting.designer.core.editor.report.layouting.ReportLayouter;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.filter.types.bands.ItemBandType;
import org.pentaho.reporting.engine.classic.core.filter.types.bands.SubReportType;
import org.pentaho.reporting.engine.classic.core.layout.ModelPrinter;
import org.pentaho.reporting.engine.classic.core.layout.model.LogicalPageBox;
import org.pentaho.reporting.engine.classic.core.layout.output.ContentProcessingException;
import org.pentaho.reporting.engine.classic.core.testsupport.selector.MatchFactory;
import org.pentaho.reporting.libraries.resourceloader.Resource;
import org.pentaho.reporting.libraries.resourceloader.ResourceException;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

import java.net.URL;

public class Prd4109Test extends TestCase {
  public Prd4109Test() {
  }

  public Prd4109Test( final String name ) {
    super( name );
  }

  protected void setUp() throws Exception {
    ReportDesignerBoot.getInstance().start();
  }

  public void testItemBandLayout() throws ResourceException, ReportProcessingException, ContentProcessingException {
    final URL url = ReportLayouterTest.class.getResource( "Prd-4109.prpt" );
    final ResourceManager resourceManager = new ResourceManager();
    resourceManager.registerDefaults();
    final Resource resource = resourceManager.createDirectly( url, MasterReport.class );
    final MasterReport report = (MasterReport) resource.getResource();

    final ReportLayouter l = new ReportLayouter
      ( new ReportRenderContext( report, report, null, new GlobalAuthenticationStore() ) );
    final LogicalPageBox layout = l.layout();

    ModelPrinter.INSTANCE.print( layout );
    assertNotNull( MatchFactory.findElementsByElementType( layout, ItemBandType.INSTANCE ) );
    assertNotNull( MatchFactory.findElementsByElementType( layout, SubReportType.INSTANCE ) );

  }
}
