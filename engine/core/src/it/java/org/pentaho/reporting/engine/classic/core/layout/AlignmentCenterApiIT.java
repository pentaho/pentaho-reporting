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

package org.pentaho.reporting.engine.classic.core.layout;

import junit.framework.TestCase;
import org.pentaho.reporting.engine.classic.core.Band;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.ElementAlignment;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.SimplePageDefinition;
import org.pentaho.reporting.engine.classic.core.elementfactory.LabelElementFactory;
import org.pentaho.reporting.engine.classic.core.layout.model.InlineRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.LogicalPageBox;
import org.pentaho.reporting.engine.classic.core.layout.model.ParagraphPoolBox;
import org.pentaho.reporting.engine.classic.core.layout.model.ParagraphRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.process.IterateStructuralProcessStep;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleKeys;
import org.pentaho.reporting.engine.classic.core.testsupport.DebugReportRunner;
import org.pentaho.reporting.engine.classic.core.util.PageFormatFactory;
import org.pentaho.reporting.engine.classic.core.util.PageSize;
import org.pentaho.reporting.libraries.base.util.FloatDimension;

import java.awt.geom.Point2D;
import java.awt.print.PageFormat;
import java.awt.print.Paper;

/**
 * Creation-Date: 14.04.2007, 15:18:02
 *
 * @author Thomas Morgner
 */
public class AlignmentCenterApiIT extends TestCase {

  public AlignmentCenterApiIT() {
  }

  public AlignmentCenterApiIT( final String s ) {
    super( s );
  }

  protected void setUp() throws Exception {
    ClassicEngineBoot.getInstance().start();
  }

  public void testAlignmentCenter() throws Exception {
    final MasterReport report = new MasterReport();
    final PageFormatFactory pff = PageFormatFactory.getInstance();
    final Paper paper = pff.createPaper( PageSize.A4 );
    pff.setBorders( paper, 36.0f, 36.0f, 36.0f, 36.0f );
    final PageFormat format = pff.createPageFormat( paper, PageFormat.PORTRAIT );
    report.setPageDefinition( new SimplePageDefinition( format ) );

    final Band pageHeader = report.getPageHeader();
    pageHeader.getStyle().setStyleProperty( ElementStyleKeys.MIN_HEIGHT, 10.0f );

    // the main heading is just a fixed label centered on the page...
    final LabelElementFactory labelFactory = new LabelElementFactory();
    labelFactory.setText( "Free / Open Source Software Survey" );
    labelFactory.setFontName( "SansSerif" );
    labelFactory.setFontSize( new Integer( 18 ) );
    labelFactory.setBold( Boolean.TRUE );
    labelFactory.setAbsolutePosition( new Point2D.Double( 15, 10.0 ) );
    labelFactory.setMinimumSize( new FloatDimension( 565, 28.0f ) );
    labelFactory.setHorizontalAlignment( ElementAlignment.CENTER );
    pageHeader.addElement( labelFactory.createElement() );

    final LogicalPageBox logicalPageBox =
        DebugReportRunner.layoutSingleBand( report, report.getPageHeader(), false, false );
    // simple test, we assert that all paragraph-poolboxes are on either 485000 or 400000
    // and that only two lines exist for each
    new ValidateRunner().startValidation( logicalPageBox );
  }

  private static class ValidateRunner extends IterateStructuralProcessStep {
    private int count;

    protected void processParagraphChilds( final ParagraphRenderBox box ) {
      count = 0;
      processBoxChilds( box );
      TestCase.assertEquals( "Line-Count", 1, count );
    }

    protected boolean startInlineBox( final InlineRenderBox box ) {
      if ( box instanceof ParagraphPoolBox ) {
        count += 1;
        final long x = box.getX();
        if ( x == 0 ) {
          TestCase.fail( "X position is wrong: " + x );
        }
      }
      return super.startInlineBox( box );
    }

    public void startValidation( final LogicalPageBox logicalPageBox ) {
      startProcessing( logicalPageBox );
    }
  }
}
