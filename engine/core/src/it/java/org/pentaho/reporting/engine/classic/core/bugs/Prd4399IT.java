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
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.filter.types.HorizontalLineType;
import org.pentaho.reporting.engine.classic.core.filter.types.LabelType;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleKeys;
import org.pentaho.reporting.engine.classic.core.testsupport.DebugReportRunner;

import java.awt.*;
import java.io.IOException;

public class Prd4399IT extends TestCase {
  public Prd4399IT() {
  }

  protected void setUp() throws Exception {
    ClassicEngineBoot.getInstance().start();
  }

  public void testReport() throws ReportProcessingException, IOException {
    MasterReport report = new MasterReport();
    report.getReportHeader().addElement( createLabel() );
    report.getReportFooter().addElement( createLabel() );
    report.getReportFooter().addElement( createHorizontalLine() );

    DebugReportRunner.createPDF( report );
    // PdfReportUtil.createPDF(report, "/tmp/prd-4399-40.pdf");
  }

  private Element createLabel() {
    final Element label = new Element();
    label.setElementType( LabelType.INSTANCE );
    label.setAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.VALUE, "Label" );
    label.getStyle().setStyleProperty( ElementStyleKeys.MIN_WIDTH, 100f );
    label.getStyle().setStyleProperty( ElementStyleKeys.MIN_HEIGHT, 20f );
    return label;
  }

  private Element createHorizontalLine() {
    final Element label = new Element();
    label.setElementType( HorizontalLineType.INSTANCE );
    label.getStyle().setStyleProperty( ElementStyleKeys.MIN_WIDTH, 100f );
    label.getStyle().setStyleProperty( ElementStyleKeys.MIN_HEIGHT, 0f );
    label.getStyle().setStyleProperty( ElementStyleKeys.DRAW_SHAPE, true );
    label.getStyle().setStyleProperty( ElementStyleKeys.STROKE, new BasicStroke( 0.2f ) );
    return label;
  }
}
