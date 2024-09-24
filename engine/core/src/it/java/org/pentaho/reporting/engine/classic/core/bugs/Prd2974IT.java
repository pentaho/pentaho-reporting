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
import org.pentaho.reporting.engine.classic.core.AttributeNames;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.filter.types.LabelType;
import org.pentaho.reporting.engine.classic.core.layout.model.CanvasRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.LogicalPageBox;
import org.pentaho.reporting.engine.classic.core.layout.model.ParagraphRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderNode;
import org.pentaho.reporting.engine.classic.core.layout.model.SectionRenderBox;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleKeys;
import org.pentaho.reporting.engine.classic.core.testsupport.DebugReportRunner;
import org.pentaho.reporting.engine.classic.core.testsupport.selector.MatchFactory;
import org.pentaho.reporting.engine.classic.core.testsupport.selector.NodeMatcher;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.libraries.resourceloader.Resource;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

import java.net.URL;

public class Prd2974IT extends TestCase {
  private static class FooterTextMatcher implements NodeMatcher {
    private String text;

    private FooterTextMatcher( final String text ) {
      this.text = text;
    }

    public boolean matches( final RenderNode node ) {
      if ( !( node instanceof ParagraphRenderBox ) ) {
        return false;
      }
      if ( !( node.getParent() instanceof CanvasRenderBox ) ) {
        return false;
      }
      RenderBox parent = node.getParent().getParent();
      if ( parent instanceof SectionRenderBox == false ) {
        return false;
      }
      ParagraphRenderBox para = (ParagraphRenderBox) node;
      Object attribute = para.getAttributes().getAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.VALUE );
      if ( ObjectUtilities.equal( text, attribute ) ) {
        return true;
      }
      return false;
    }
  }

  public Prd2974IT() {
  }

  public Prd2974IT( final String name ) {
    super( name );
  }

  protected void setUp() throws Exception {
    ClassicEngineBoot.getInstance().start();
  }

  public void testRunStickySub() throws Exception {
    final URL url = getClass().getResource( "Prd-2974-2.prpt" );
    assertNotNull( url );
    final ResourceManager resourceManager = new ResourceManager();
    resourceManager.registerDefaults();
    final Resource directly = resourceManager.createDirectly( url, MasterReport.class );
    final MasterReport report = (MasterReport) directly.getResource();
    report.getPageFooter().setSticky( false );
    report.getPageFooter().addElement( createLabel() );
    report.getPageFooter().setName( "Master-Footer" );
    report.getReportHeader().getSubReport( 0 ).getPageFooter().setName( "Subreport-Footer" );
    report.getReportHeader().getSubReport( 0 ).getPageFooter().setSticky( true );
    report.getReportHeader().getSubReport( 0 ).getReportHeader().addElement( createLabel( "ReportHeader-label" ) );

    report.getReportConfiguration().setConfigProperty(
        "org.pentaho.reporting.engine.classic.core.modules.output.pageable.xml.Encoding", "UTF-8" );

    LogicalPageBox logicalPageBox = DebugReportRunner.layoutPage( report, 0 );
    assertNotNull( MatchFactory.match( logicalPageBox.getFooterArea(), new FooterTextMatcher( "Label" ) ) );
    assertNotNull( MatchFactory.match( logicalPageBox.getFooterArea(), new FooterTextMatcher( "XASDAS" ) ) );
  }

  public void testRunStickyEverything() throws Exception {
    final URL url = getClass().getResource( "Prd-2974-2.prpt" );
    assertNotNull( url );
    final ResourceManager resourceManager = new ResourceManager();
    resourceManager.registerDefaults();
    final Resource directly = resourceManager.createDirectly( url, MasterReport.class );
    final MasterReport report = (MasterReport) directly.getResource();
    report.getPageFooter().setSticky( true );
    report.getPageFooter().addElement( createLabel() );
    report.getPageFooter().setName( "Master-Footer" );
    report.getReportHeader().getSubReport( 0 ).getPageFooter().setName( "Subreport-Footer" );
    report.getReportHeader().getSubReport( 0 ).getPageFooter().setSticky( true );
    report.getReportHeader().getSubReport( 0 ).getReportHeader().addElement( createLabel( "ReportHeader-label" ) );

    report.getReportConfiguration().setConfigProperty(
        "org.pentaho.reporting.engine.classic.core.modules.output.pageable.xml.Encoding", "UTF-8" );

    LogicalPageBox logicalPageBox = DebugReportRunner.layoutPage( report, 0 );
    assertNotNull( MatchFactory.match( logicalPageBox.getFooterArea(), new FooterTextMatcher( "Label" ) ) );
    assertNotNull( MatchFactory.match( logicalPageBox.getFooterArea(), new FooterTextMatcher( "XASDAS" ) ) );
  }

  public void testRunNonStickyEverything() throws Exception {
    final URL url = getClass().getResource( "Prd-2974-2.prpt" );
    assertNotNull( url );
    final ResourceManager resourceManager = new ResourceManager();
    resourceManager.registerDefaults();
    final Resource directly = resourceManager.createDirectly( url, MasterReport.class );
    final MasterReport report = (MasterReport) directly.getResource();
    report.getPageFooter().setSticky( false );
    report.getPageFooter().addElement( createLabel() );
    report.getPageFooter().setName( "Master-Footer" );
    report.getReportHeader().getSubReport( 0 ).getPageFooter().setName( "Subreport-Footer" );
    report.getReportHeader().getSubReport( 0 ).getPageFooter().setSticky( false );
    report.getReportHeader().getSubReport( 0 ).getReportHeader().addElement( createLabel( "ReportHeader-label" ) );

    report.getReportConfiguration().setConfigProperty(
        "org.pentaho.reporting.engine.classic.core.modules.output.pageable.xml.Encoding", "UTF-8" );

    LogicalPageBox logicalPageBox = DebugReportRunner.layoutPage( report, 0 );
    assertNull( MatchFactory.match( logicalPageBox.getFooterArea(), new FooterTextMatcher( "Label" ) ) );
    assertNotNull( MatchFactory.match( logicalPageBox.getFooterArea(), new FooterTextMatcher( "XASDAS" ) ) );
  }

  public void testRunStickyMasterFooter() throws Exception {
    final URL url = getClass().getResource( "Prd-2974-2.prpt" );
    assertNotNull( url );
    final ResourceManager resourceManager = new ResourceManager();
    resourceManager.registerDefaults();
    final Resource directly = resourceManager.createDirectly( url, MasterReport.class );
    final MasterReport report = (MasterReport) directly.getResource();
    report.getPageFooter().setSticky( true );
    report.getPageFooter().addElement( createLabel() );
    report.getPageFooter().setName( "Master-Footer" );
    report.getReportHeader().getSubReport( 0 ).getPageFooter().setName( "Subreport-Footer" );
    report.getReportHeader().getSubReport( 0 ).getPageFooter().setSticky( false );
    report.getReportHeader().getSubReport( 0 ).getReportHeader().addElement( createLabel( "ReportHeader-label" ) );
    report.getReportConfiguration().setConfigProperty(
        "org.pentaho.reporting.engine.classic.core.modules.output.pageable.xml.Encoding", "UTF-8" );

    LogicalPageBox logicalPageBox = DebugReportRunner.layoutPage( report, 0 );

    assertNull( MatchFactory.match( logicalPageBox.getFooterArea(), new FooterTextMatcher( "Label" ) ) );
    assertNotNull( MatchFactory.match( logicalPageBox.getFooterArea(), new FooterTextMatcher( "XASDAS" ) ) );
  }

  private Element createLabel() {
    return createLabel( "XASDAS" );
  }

  private Element createLabel( final String text ) {
    final Element element = new Element();
    element.setElementType( LabelType.INSTANCE );
    element.getStyle().setStyleProperty( ElementStyleKeys.MIN_HEIGHT, new Float( 20 ) );
    element.getStyle().setStyleProperty( ElementStyleKeys.MIN_WIDTH, new Float( 200 ) );
    element.setAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.VALUE, text );
    return element;
  }

  public void testTwoPageReport() throws Exception {
    final URL url = getClass().getResource( "Prd-2974-2.prpt" );
    assertNotNull( url );
    final ResourceManager resourceManager = new ResourceManager();
    resourceManager.registerDefaults();
    final Resource directly = resourceManager.createDirectly( url, MasterReport.class );
    final MasterReport report = (MasterReport) directly.getResource();
    report.getPageFooter().setSticky( false );
    report.getPageFooter().addElement( createLabel( "PageFooter-Label" ) );
    report.getPageFooter().setName( "Master-Footer" );
    report.getReportHeader().getSubReport( 0 ).getPageFooter().setName( "Subreport-Footer" );
    report.getReportHeader().getSubReport( 0 ).getPageFooter().setSticky( true );
    report.getReportHeader().getSubReport( 0 ).getReportHeader().addElement( createLabel( "ReportHeader-label" ) );

    report.getReportFooter().addElement( createLabel( "ReportFooter-label" ) );
    report.getReportFooter().setPagebreakBeforePrint( true );

    report.getReportConfiguration().setConfigProperty(
        "org.pentaho.reporting.engine.classic.core.modules.output.pageable.xml.Encoding", "UTF-8" );

    LogicalPageBox logicalPageBox = DebugReportRunner.layoutPage( report, 0 );
    assertNotNull( MatchFactory.match( logicalPageBox.getFooterArea(), new FooterTextMatcher( "Label" ) ) );
    assertNull( MatchFactory.match( logicalPageBox.getFooterArea(), new FooterTextMatcher( "PageFooter-Label" ) ) );
    // TESTBUG: this is not a true structural test at all.
  }

}

// Fix for PRD-2709 makes totally empty subreport behave like empty bands. They do not produce empty pages anymore.
// therefore this test must be changed so that the subreport is printing at least one element.
