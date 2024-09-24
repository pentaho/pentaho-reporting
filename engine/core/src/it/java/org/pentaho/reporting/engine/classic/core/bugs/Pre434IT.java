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

package org.pentaho.reporting.engine.classic.core.bugs;

import junit.framework.TestCase;
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
import org.pentaho.reporting.engine.classic.core.testsupport.DebugReportRunner;
import org.pentaho.reporting.libraries.base.util.FloatDimension;

import java.awt.geom.Point2D;
import java.awt.print.PageFormat;

public class Pre434IT extends TestCase {
  public Pre434IT() {
  }

  public Pre434IT( final String s ) {
    super( s );
  }

  protected void setUp() throws Exception {
    ClassicEngineBoot.getInstance().start();
  }

  public void testLeftAlignment() throws Exception {
    final MasterReport report = new MasterReport();
    // enforce letter-size pages; usable width = 468, usable height = 648
    report.setPageDefinition( new SimplePageDefinition( new PageFormat() ) );

    final LabelElementFactory labelFactory = new LabelElementFactory();
    labelFactory.setText( "Free / Open Source Software Survey" );
    labelFactory.setFontName( "SansSerif" );
    labelFactory.setFontSize( new Integer( 18 ) );
    labelFactory.setBold( Boolean.TRUE );
    labelFactory.setAbsolutePosition( new Point2D.Double( 300, 10.0 ) );
    labelFactory.setMinimumSize( new FloatDimension( 300, 24 ) );
    labelFactory.setHorizontalAlignment( ElementAlignment.LEFT );

    // watermark has a implicit rule that limits the height of the band to the size of the usable page-area.
    report.getReportHeader().addElement( labelFactory.createElement() );
    validate( report );
  }

  public void testCenterAlignment() throws Exception {
    final MasterReport report = new MasterReport();
    // enforce letter-size pages; usable width = 468, usable height = 648
    report.setPageDefinition( new SimplePageDefinition( new PageFormat() ) );

    final LabelElementFactory labelFactory = new LabelElementFactory();
    labelFactory.setText( "Free / Open Source Software Survey" );
    labelFactory.setFontName( "SansSerif" );
    labelFactory.setFontSize( new Integer( 18 ) );
    labelFactory.setBold( Boolean.TRUE );
    labelFactory.setAbsolutePosition( new Point2D.Double( 300, 10.0 ) );
    labelFactory.setMinimumSize( new FloatDimension( 300, 24 ) );
    labelFactory.setHorizontalAlignment( ElementAlignment.CENTER );

    // watermark has a implicit rule that limits the height of the band to the size of the usable page-area.
    report.getReportHeader().addElement( labelFactory.createElement() );
    validate( report );
  }

  public void testRightAlignment() throws Exception {
    final MasterReport report = new MasterReport();
    // enforce letter-size pages; usable width = 468, usable height = 648
    report.setPageDefinition( new SimplePageDefinition( new PageFormat() ) );

    final LabelElementFactory labelFactory = new LabelElementFactory();
    labelFactory.setText( "Free / Open Source Software Survey" );
    labelFactory.setFontName( "SansSerif" );
    labelFactory.setFontSize( new Integer( 18 ) );
    labelFactory.setBold( Boolean.TRUE );
    labelFactory.setAbsolutePosition( new Point2D.Double( 300, 10.0 ) );
    labelFactory.setMinimumSize( new FloatDimension( 300, 24 ) );
    labelFactory.setHorizontalAlignment( ElementAlignment.RIGHT );

    // watermark has a implicit rule that limits the height of the band to the size of the usable page-area.
    report.getReportHeader().addElement( labelFactory.createElement() );
    validate( report );
  }

  private void validate( final MasterReport report ) throws Exception {
    final LogicalPageBox logicalPageBox =
        DebugReportRunner.layoutSingleBand( report, report.getPageHeader(), false, false );
    // simple test, we assert that all paragraph-poolboxes are on either 485000 or 400000
    // and that only two lines exist for each
    new ValidateRunner().startValidation( logicalPageBox );
  }

  private static class ValidateRunner extends IterateStructuralProcessStep {
    protected void processParagraphChilds( final ParagraphRenderBox box ) {
      final long x = box.getX();
      if ( x != 300000 ) {
        TestCase.fail( "X position is wrong: " + x );
      }
      if ( box.getWidth() != 300000 ) {
        TestCase.fail( "Width position is wrong: " + x );
      }

      processBoxChilds( box );
    }

    protected boolean startInlineBox( final InlineRenderBox box ) {
      if ( box instanceof ParagraphPoolBox ) {
        final long x = box.getX();
        if ( x != 300000 ) {
          TestCase.fail( "X position is wrong: " + x );
        }
        if ( box.getWidth() != 300000 ) {
          TestCase.fail( "Width position is wrong: " + x );
        }
      }
      return super.startInlineBox( box );
    }

    public void startValidation( final LogicalPageBox logicalPageBox ) {
      startProcessing( logicalPageBox );
    }
  }

}
