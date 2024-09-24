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
import org.pentaho.reporting.engine.classic.core.ElementAlignment;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ReportHeader;
import org.pentaho.reporting.engine.classic.core.filter.types.LabelType;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleKeys;
import org.pentaho.reporting.engine.classic.core.style.TextStyleKeys;
import org.pentaho.reporting.engine.classic.core.testsupport.DebugReportRunner;

public class Pre511IT extends TestCase {
  public Pre511IT() {
  }

  public Pre511IT( final String s ) {
    super( s );
  }

  protected void setUp() throws Exception {
    ClassicEngineBoot.getInstance().start();
  }

  public void testCanvasBug() throws Exception {
    final MasterReport report = new MasterReport();
    final ReportHeader header = report.getReportHeader();
    header.getStyle().setStyleProperty( TextStyleKeys.FONT, "Monospaced" );
    header.getStyle().setStyleProperty( TextStyleKeys.FONTSIZE, new Integer( 6 ) );
    header.getStyle().setStyleProperty( ElementStyleKeys.MIN_HEIGHT, new Float( 50 ) );
    header.getStyle().setStyleProperty( ElementStyleKeys.VALIGNMENT, ElementAlignment.MIDDLE );

    final Element label1 = new Element();
    label1.setElementType( LabelType.INSTANCE );
    label1.setAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.VALUE, "COST" );
    label1.setAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.NAME, "COST" );
    label1.getStyle().setStyleProperty( ElementStyleKeys.ALIGNMENT, ElementAlignment.LEFT );
    label1.getStyle().setStyleProperty( ElementStyleKeys.MIN_HEIGHT, new Float( 12 ) );
    label1.getStyle().setStyleProperty( ElementStyleKeys.MIN_WIDTH, new Float( 100 ) );
    label1.getStyle().setStyleProperty( ElementStyleKeys.PADDING_LEFT, new Float( 2 ) );
    label1.getStyle().setStyleProperty( ElementStyleKeys.PADDING_RIGHT, new Float( 2 ) );
    label1.getStyle().setStyleProperty( ElementStyleKeys.PADDING_TOP, new Float( 2 ) );
    label1.getStyle().setStyleProperty( ElementStyleKeys.PADDING_BOTTOM, new Float( 2 ) );
    label1.getStyle().setStyleProperty( ElementStyleKeys.POS_X, new Float( 0 ) );
    label1.getStyle().setStyleProperty( ElementStyleKeys.POS_Y, new Float( 0 ) );
    header.addElement( label1 );

    final Element label2 = new Element();
    label2.setElementType( LabelType.INSTANCE );
    label2.setAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.VALUE, "DROPPED" );
    label2.setAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.NAME, "DROPPED" );
    label2.getStyle().setStyleProperty( ElementStyleKeys.ALIGNMENT, ElementAlignment.LEFT );
    label2.getStyle().setStyleProperty( ElementStyleKeys.MIN_HEIGHT, new Float( 12 ) );
    label2.getStyle().setStyleProperty( ElementStyleKeys.MIN_WIDTH, new Float( 100 ) );
    label2.getStyle().setStyleProperty( ElementStyleKeys.PADDING_LEFT, new Float( 2 ) );
    label2.getStyle().setStyleProperty( ElementStyleKeys.PADDING_RIGHT, new Float( 2 ) );
    label2.getStyle().setStyleProperty( ElementStyleKeys.PADDING_TOP, new Float( 2 ) );
    label2.getStyle().setStyleProperty( ElementStyleKeys.PADDING_BOTTOM, new Float( 2 ) );
    label2.getStyle().setStyleProperty( ElementStyleKeys.POS_X, new Float( 100 ) );
    label2.getStyle().setStyleProperty( ElementStyleKeys.POS_Y, new Float( 0 ) );
    header.addElement( label2 );

    final Element label3 = new Element();
    label3.setElementType( LabelType.INSTANCE );
    label3.setAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.VALUE, "DROPPED" );
    label3.setAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.NAME, "DROPPED" );
    label3.getStyle().setStyleProperty( ElementStyleKeys.ALIGNMENT, ElementAlignment.LEFT );
    label3.getStyle().setStyleProperty( ElementStyleKeys.MIN_HEIGHT, new Float( 12 ) );
    label3.getStyle().setStyleProperty( ElementStyleKeys.MIN_WIDTH, new Float( 100 ) );
    label3.getStyle().setStyleProperty( ElementStyleKeys.PADDING_LEFT, new Float( 2 ) );
    label3.getStyle().setStyleProperty( ElementStyleKeys.PADDING_RIGHT, new Float( 2 ) );
    label3.getStyle().setStyleProperty( ElementStyleKeys.PADDING_TOP, new Float( 2 ) );
    label3.getStyle().setStyleProperty( ElementStyleKeys.PADDING_BOTTOM, new Float( 2 ) );
    label3.getStyle().setStyleProperty( ElementStyleKeys.POS_X, new Float( 200 ) );
    label3.getStyle().setStyleProperty( ElementStyleKeys.POS_Y, new Float( 0 ) );
    header.addElement( label3 );

    final Element labelA1 = new Element();
    labelA1.setElementType( LabelType.INSTANCE );
    labelA1.setAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.VALUE, "COST" );
    labelA1.setAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.NAME, "COST" );
    labelA1.getStyle().setStyleProperty( ElementStyleKeys.ALIGNMENT, ElementAlignment.LEFT );
    labelA1.getStyle().setStyleProperty( ElementStyleKeys.MIN_HEIGHT, new Float( 12 ) );
    labelA1.getStyle().setStyleProperty( ElementStyleKeys.MIN_WIDTH, new Float( 100 ) );
    labelA1.getStyle().setStyleProperty( ElementStyleKeys.PADDING_LEFT, new Float( 2 ) );
    labelA1.getStyle().setStyleProperty( ElementStyleKeys.PADDING_RIGHT, new Float( 2 ) );
    labelA1.getStyle().setStyleProperty( ElementStyleKeys.PADDING_TOP, new Float( 2 ) );
    labelA1.getStyle().setStyleProperty( ElementStyleKeys.PADDING_BOTTOM, new Float( 2 ) );
    labelA1.getStyle().setStyleProperty( ElementStyleKeys.POS_X, new Float( 0 ) );
    labelA1.getStyle().setStyleProperty( ElementStyleKeys.POS_Y, new Float( 12 ) );
    header.addElement( labelA1 );

    final Element labelA2 = new Element();
    labelA2.setElementType( LabelType.INSTANCE );
    labelA2.setAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.VALUE, "DROPPED" );
    labelA2.setAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.NAME, "DROPPED" );
    labelA2.getStyle().setStyleProperty( ElementStyleKeys.ALIGNMENT, ElementAlignment.LEFT );
    labelA2.getStyle().setStyleProperty( ElementStyleKeys.MIN_HEIGHT, new Float( 12 ) );
    labelA2.getStyle().setStyleProperty( ElementStyleKeys.MIN_WIDTH, new Float( 100 ) );
    labelA2.getStyle().setStyleProperty( ElementStyleKeys.PADDING_LEFT, new Float( 2 ) );
    labelA2.getStyle().setStyleProperty( ElementStyleKeys.PADDING_RIGHT, new Float( 2 ) );
    labelA2.getStyle().setStyleProperty( ElementStyleKeys.PADDING_TOP, new Float( 2 ) );
    labelA2.getStyle().setStyleProperty( ElementStyleKeys.PADDING_BOTTOM, new Float( 2 ) );
    labelA2.getStyle().setStyleProperty( ElementStyleKeys.POS_X, new Float( 100 ) );
    labelA2.getStyle().setStyleProperty( ElementStyleKeys.POS_Y, new Float( 12 ) );
    header.addElement( labelA2 );

    final Element labelA3 = new Element();
    labelA3.setElementType( LabelType.INSTANCE );
    labelA3.setAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.VALUE, "DROPPED" );
    labelA3.setAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.NAME, "DROPPED" );
    labelA3.getStyle().setStyleProperty( ElementStyleKeys.ALIGNMENT, ElementAlignment.LEFT );
    labelA3.getStyle().setStyleProperty( ElementStyleKeys.MIN_HEIGHT, new Float( 12 ) );
    labelA3.getStyle().setStyleProperty( ElementStyleKeys.MIN_WIDTH, new Float( 100 ) );
    labelA3.getStyle().setStyleProperty( ElementStyleKeys.PADDING_LEFT, new Float( 2 ) );
    labelA3.getStyle().setStyleProperty( ElementStyleKeys.PADDING_RIGHT, new Float( 2 ) );
    labelA3.getStyle().setStyleProperty( ElementStyleKeys.PADDING_TOP, new Float( 2 ) );
    labelA3.getStyle().setStyleProperty( ElementStyleKeys.PADDING_BOTTOM, new Float( 2 ) );
    labelA3.getStyle().setStyleProperty( ElementStyleKeys.POS_X, new Float( 200 ) );
    labelA3.getStyle().setStyleProperty( ElementStyleKeys.POS_Y, new Float( 12 ) );
    header.addElement( labelA3 );

    final Element labelB1 = new Element();
    labelB1.setElementType( LabelType.INSTANCE );
    labelB1.setAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.VALUE, "COST" );
    labelB1.setAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.NAME, "COST" );
    labelB1.getStyle().setStyleProperty( ElementStyleKeys.ALIGNMENT, ElementAlignment.LEFT );
    labelB1.getStyle().setStyleProperty( ElementStyleKeys.MIN_HEIGHT, new Float( 12 ) );
    labelB1.getStyle().setStyleProperty( ElementStyleKeys.MIN_WIDTH, new Float( 100 ) );
    labelB1.getStyle().setStyleProperty( ElementStyleKeys.PADDING_LEFT, new Float( 2 ) );
    labelB1.getStyle().setStyleProperty( ElementStyleKeys.PADDING_RIGHT, new Float( 2 ) );
    labelB1.getStyle().setStyleProperty( ElementStyleKeys.PADDING_TOP, new Float( 2 ) );
    labelB1.getStyle().setStyleProperty( ElementStyleKeys.PADDING_BOTTOM, new Float( 2 ) );
    labelB1.getStyle().setStyleProperty( ElementStyleKeys.POS_X, new Float( 0 ) );
    labelB1.getStyle().setStyleProperty( ElementStyleKeys.POS_Y, new Float( 24 ) );
    header.addElement( labelB1 );

    final Element labelB2 = new Element();
    labelB2.setElementType( LabelType.INSTANCE );
    labelB2.setAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.VALUE, "DROPPED" );
    labelB2.setAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.NAME, "DROPPED" );
    labelB2.getStyle().setStyleProperty( ElementStyleKeys.ALIGNMENT, ElementAlignment.LEFT );
    labelB2.getStyle().setStyleProperty( ElementStyleKeys.MIN_HEIGHT, new Float( 12 ) );
    labelB2.getStyle().setStyleProperty( ElementStyleKeys.MIN_WIDTH, new Float( 100 ) );
    labelB2.getStyle().setStyleProperty( ElementStyleKeys.PADDING_LEFT, new Float( 2 ) );
    labelB2.getStyle().setStyleProperty( ElementStyleKeys.PADDING_RIGHT, new Float( 2 ) );
    labelB2.getStyle().setStyleProperty( ElementStyleKeys.PADDING_TOP, new Float( 2 ) );
    labelB2.getStyle().setStyleProperty( ElementStyleKeys.PADDING_BOTTOM, new Float( 2 ) );
    labelB2.getStyle().setStyleProperty( ElementStyleKeys.POS_X, new Float( 100 ) );
    labelB2.getStyle().setStyleProperty( ElementStyleKeys.POS_Y, new Float( 24 ) );
    header.addElement( labelB2 );

    final Element labelB3 = new Element();
    labelB3.setElementType( LabelType.INSTANCE );
    labelB3.setAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.VALUE, "DROPPED" );
    labelB3.setAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.NAME, "DROPPED" );
    labelB3.getStyle().setStyleProperty( ElementStyleKeys.ALIGNMENT, ElementAlignment.LEFT );
    labelB3.getStyle().setStyleProperty( ElementStyleKeys.MIN_HEIGHT, new Float( 12 ) );
    labelB3.getStyle().setStyleProperty( ElementStyleKeys.MIN_WIDTH, new Float( 100 ) );
    labelB3.getStyle().setStyleProperty( ElementStyleKeys.PADDING_LEFT, new Float( 2 ) );
    labelB3.getStyle().setStyleProperty( ElementStyleKeys.PADDING_RIGHT, new Float( 2 ) );
    labelB3.getStyle().setStyleProperty( ElementStyleKeys.PADDING_TOP, new Float( 2 ) );
    labelB3.getStyle().setStyleProperty( ElementStyleKeys.PADDING_BOTTOM, new Float( 2 ) );
    labelB3.getStyle().setStyleProperty( ElementStyleKeys.POS_X, new Float( 200 ) );
    labelB3.getStyle().setStyleProperty( ElementStyleKeys.POS_Y, new Float( 24 ) );
    header.addElement( labelB3 );

    DebugReportRunner.createStreamHTML( report );
  }

}
