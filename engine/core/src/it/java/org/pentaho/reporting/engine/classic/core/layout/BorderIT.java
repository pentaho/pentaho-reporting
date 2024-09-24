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

package org.pentaho.reporting.engine.classic.core.layout;

import junit.framework.TestCase;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.ElementAlignment;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.TableDataFactory;
import org.pentaho.reporting.engine.classic.core.elementfactory.LabelElementFactory;
import org.pentaho.reporting.engine.classic.core.elementfactory.TextFieldElementFactory;
import org.pentaho.reporting.engine.classic.core.style.BorderStyle;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleKeys;
import org.pentaho.reporting.engine.classic.core.style.FontDefinition;
import org.pentaho.reporting.engine.classic.core.testsupport.DebugReportRunner;

import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.geom.Rectangle2D;

public class BorderIT extends TestCase {
  public BorderIT() {
  }

  public BorderIT( final String s ) {
    super( s );
  }

  protected void setUp() throws Exception {
    ClassicEngineBoot.getInstance().start();
  }

  public void testFailure() throws Exception {
    final Object[] columnNames = new Object[] { "Customer", "City", "Number" };

    final DefaultTableModel reportTableModel =
        new DefaultTableModel( new Object[][] {
          { "Customer_ASDFSDFSDFSDFSaasdasdasdasweruzweurzwiezrwieuzriweuzriweu", "Bern", "123" },
          { "Hugo", "Z?rich", "2234" }, }, columnNames );

    final MasterReport report = new MasterReport();

    report.setName( "BorderTest" );

    report.getItemBand().addElement(
        LabelElementFactory.createLabelElement( "CustomerLabel", new Rectangle2D.Double( 0, 0, 200, 100 ), Color.RED,
            ElementAlignment.LEFT, new FontDefinition( "Arial", 12 ), "CustomerLabel" ) );

    final Element element =
        TextFieldElementFactory.createStringElement( "CustomerField", new Rectangle2D.Double( 110, 0, 250, 50 ),
            Color.black, ElementAlignment.LEFT, ElementAlignment.TOP, null, // font
            "-", // null string
            "Customer" );

    element.getStyle().setStyleProperty( ElementStyleKeys.BORDER_TOP_COLOR, Color.RED );
    element.getStyle().setStyleProperty( ElementStyleKeys.BORDER_TOP_WIDTH, new Float( 5 ) );
    element.getStyle().setStyleProperty( ElementStyleKeys.BORDER_TOP_STYLE, BorderStyle.SOLID );

    element.getStyle().setStyleProperty( ElementStyleKeys.BORDER_LEFT_COLOR, Color.GREEN );
    element.getStyle().setStyleProperty( ElementStyleKeys.BORDER_LEFT_WIDTH, new Float( 5 ) );
    element.getStyle().setStyleProperty( ElementStyleKeys.BORDER_LEFT_STYLE, BorderStyle.SOLID );

    element.getStyle().setStyleProperty( ElementStyleKeys.BORDER_RIGHT_COLOR, Color.YELLOW );
    element.getStyle().setStyleProperty( ElementStyleKeys.BORDER_RIGHT_WIDTH, new Float( 5 ) );
    element.getStyle().setStyleProperty( ElementStyleKeys.BORDER_RIGHT_STYLE, BorderStyle.SOLID );

    element.getStyle().setStyleProperty( ElementStyleKeys.BORDER_BOTTOM_COLOR, Color.CYAN );
    element.getStyle().setStyleProperty( ElementStyleKeys.BORDER_BOTTOM_WIDTH, new Float( 5 ) );
    element.getStyle().setStyleProperty( ElementStyleKeys.BORDER_BOTTOM_STYLE, BorderStyle.SOLID );
    element.getStyle().setStyleProperty( ElementStyleKeys.BACKGROUND_COLOR, new Color( 255, 127, 127, 120 ) );
    element.getStyle().setStyleProperty( ElementStyleKeys.PADDING_LEFT, new Float( 5 ) );
    element.getStyle().setStyleProperty( ElementStyleKeys.PADDING_TOP, new Float( 5 ) );

    report.getItemBand().addElement( element );

    report.setDataFactory( new TableDataFactory( "default", reportTableModel ) );
    DebugReportRunner.executeAll( report );

  }

}
