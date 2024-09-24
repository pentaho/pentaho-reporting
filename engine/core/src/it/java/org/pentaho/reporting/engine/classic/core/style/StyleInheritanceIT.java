/*
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
 * Copyright (c) 2001 - 2013 Object Refinery Ltd, Hitachi Vantara and Contributors..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.style;

import junit.framework.TestCase;
import org.pentaho.reporting.engine.classic.core.AttributeNames;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ReportHeader;
import org.pentaho.reporting.engine.classic.core.SubReport;
import org.pentaho.reporting.engine.classic.core.filter.types.LabelType;
import org.pentaho.reporting.engine.classic.core.layout.model.LogicalPageBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderNode;
import org.pentaho.reporting.engine.classic.core.style.css.ElementStyleDefinition;
import org.pentaho.reporting.engine.classic.core.style.css.ElementStyleRule;
import org.pentaho.reporting.engine.classic.core.style.css.selector.CSSSelector;
import org.pentaho.reporting.engine.classic.core.style.css.selector.CSSSelectorFactory;
import org.pentaho.reporting.engine.classic.core.testsupport.DebugReportRunner;
import org.pentaho.reporting.engine.classic.core.testsupport.selector.MatchFactory;

public class StyleInheritanceIT extends TestCase {
  public StyleInheritanceIT() {
  }

  protected void setUp() throws Exception {
    ClassicEngineBoot.getInstance().start();
  }

  private Element createLabel( final String text ) {
    final Element element = new Element();
    element.setName( text );
    element.setElementType( LabelType.INSTANCE );
    element.getStyle().setStyleProperty( ElementStyleKeys.MIN_HEIGHT, new Float( 20 ) );
    element.getStyle().setStyleProperty( ElementStyleKeys.MIN_WIDTH, new Float( 200 ) );
    element.setAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.VALUE, text );
    return element;
  }

  public void testStyleInheritance() throws Exception {
    MasterReport report = new MasterReport();
    ReportHeader reportHeader = report.getReportHeader();
    reportHeader.addElement( createLabel( "Master-Report-Header-Label" ) );
    report.setStyleDefinition( createStyleDefinition( "selected-font" ) );

    LogicalPageBox box = DebugReportRunner.layoutPage( report, 0 );
    RenderNode elementByName = MatchFactory.findElementByName( box, "Master-Report-Header-Label" );
    assertNotNull( elementByName );
    assertEquals( "selected-font", elementByName.getStyleSheet().getStyleProperty( TextStyleKeys.FONT ) );
  }

  public void testStyleInheritanceOnSubReport() throws Exception {
    SubReport bandedSr = new SubReport();
    bandedSr.getReportHeader().addElement( createLabel( "Banded-SubReport-Header-Label" ) );

    SubReport inlineSr = new SubReport();
    inlineSr.getReportHeader().addElement( createLabel( "Inline-SubReport-Header-Label" ) );

    MasterReport report = new MasterReport();
    report.getReportFooter().addElement( inlineSr );
    report.getReportFooter().addElement( bandedSr );
    report.setStyleDefinition( createStyleDefinition( "selected-font" ) );

    LogicalPageBox box = DebugReportRunner.layoutPage( report, 0 );

    RenderNode inlineElement = MatchFactory.findElementByName( box, "Inline-SubReport-Header-Label" );
    assertNotNull( inlineElement );
    assertEquals( "selected-font", inlineElement.getStyleSheet().getStyleProperty( TextStyleKeys.FONT ) );

    RenderNode bandedElement = MatchFactory.findElementByName( box, "Banded-SubReport-Header-Label" );
    assertNotNull( bandedElement );
    assertEquals( "selected-font", bandedElement.getStyleSheet().getStyleProperty( TextStyleKeys.FONT ) );
  }

  public void testStylesOnSubreportAreNotSupported() throws Exception {
    SubReport bandedSr = new SubReport();
    bandedSr.getReportHeader().addElement( createLabel( "Banded-SubReport-Header-Label" ) );
    bandedSr.setAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.STYLE_SHEET,
        createStyleDefinition( "selected-font-banded" ) );

    SubReport inlineSr = new SubReport();
    inlineSr.getReportHeader().addElement( createLabel( "Inline-SubReport-Header-Label" ) );
    inlineSr.setAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.STYLE_SHEET,
        createStyleDefinition( "selected-font-inline" ) );

    MasterReport report = new MasterReport();
    report.getReportFooter().addElement( inlineSr );
    report.getReportFooter().addElement( bandedSr );
    report.setStyleDefinition( createStyleDefinition( "selected-font" ) );

    LogicalPageBox box = DebugReportRunner.layoutPage( report, 0 );

    RenderNode inlineElement = MatchFactory.findElementByName( box, "Inline-SubReport-Header-Label" );
    assertNotNull( inlineElement );
    assertEquals( "selected-font", inlineElement.getStyleSheet().getStyleProperty( TextStyleKeys.FONT ) );

    RenderNode bandedElement = MatchFactory.findElementByName( box, "Banded-SubReport-Header-Label" );
    assertNotNull( bandedElement );
    assertEquals( "selected-font", bandedElement.getStyleSheet().getStyleProperty( TextStyleKeys.FONT ) );
  }

  private ElementStyleDefinition createStyleDefinition( final String targetName ) {
    CSSSelectorFactory factory = new CSSSelectorFactory();

    ElementStyleRule rule = new ElementStyleRule();
    rule.addSelector( (CSSSelector) factory.createElementSelector( null, "label" ) );
    rule.setStyleProperty( TextStyleKeys.FONT, targetName );

    ElementStyleDefinition def = new ElementStyleDefinition();
    def.addRule( rule );
    return def;
  }

}
