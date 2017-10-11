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
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.ElementAlignment;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ReportHeader;
import org.pentaho.reporting.engine.classic.core.elementfactory.LabelElementFactory;
import org.pentaho.reporting.engine.classic.core.layout.model.InlineRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.LogicalPageBox;
import org.pentaho.reporting.engine.classic.core.layout.model.ParagraphPoolBox;
import org.pentaho.reporting.engine.classic.core.layout.model.ParagraphRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderNode;
import org.pentaho.reporting.engine.classic.core.layout.model.SpacerRenderNode;
import org.pentaho.reporting.engine.classic.core.layout.process.IterateStructuralProcessStep;
import org.pentaho.reporting.engine.classic.core.testsupport.DebugReportRunner;

import java.awt.*;
import java.awt.geom.Rectangle2D;

/**
 * Creation-Date: 14.04.2007, 15:18:02
 *
 * @author Thomas Morgner
 */
public class AlignmentJustifiedIT extends TestCase {
  private static final String longText = "A rich variety of Victorian paintings can be seen as you enter the "
      + "Gallery, newly re-displayed in the original nineteenth century style. The collection was developed by "
      + "the Gallery's first Director, Sir Alfred Temple. An energetic and dynamic man, he sought out contemporary "
      + "works to set the newly built Guildhall Art Gallery on the map in the 1880s. Several private owners were "
      + "persuaded to donate their collections, including Charles Gassiot, a City wine merchant. Gassiot?s bequest "
      + "forms the core of the Victorian collections and includes key works such as Alma Tadema's The Pyrrhic Dance, "
      + "Millais?s My First Sermon and My Second Sermon (right) and Landseer?s The First Leap. Temple also acquired "
      + "Millais? The Woodman?s Daughter, Lord Frederic Leighton?s The Music Lesson, William Holman Hunt?s Eve of St "
      + "Agnes, Sir E J Poynter?s Israel in Egypt and Dante Gabriel Rossetti's La Ghirlandata.";

  public AlignmentJustifiedIT() {
  }

  public AlignmentJustifiedIT( final String s ) {
    super( s );
  }

  protected void setUp() throws Exception {
    ClassicEngineBoot.getInstance().start();
  }

  public void testAlignmentCenter() throws Exception {
    final MasterReport report = new MasterReport();
    ReportHeader reportHeader = report.getReportHeader();
    reportHeader.addElement( LabelElementFactory.createLabelElement( "CustomerLabel", new Rectangle2D.Double( 0, 0,
        203, 157 ), Color.RED, ElementAlignment.JUSTIFY, null, longText ) );

    final LogicalPageBox logicalPageBox = DebugReportRunner.layoutSingleBand( report, reportHeader );
    // simple test, we assert that all paragraph-poolboxes are on either 485000 or 400000
    // and that only two lines exist for each
    // ModelPrinter.print(logicalPageBox);
    new ValidateRunner().startValidation( logicalPageBox );
  }

  private static class ValidateRunner extends IterateStructuralProcessStep {
    protected void processParagraphChilds( final ParagraphRenderBox box ) {
      processBoxChilds( box );
    }

    protected boolean startInlineBox( final InlineRenderBox box ) {
      if ( box instanceof ParagraphPoolBox ) {
        if ( box.getNext() == null ) {
          boolean lastWasSpacer = true;
          RenderNode child = box.getFirstChild();
          while ( child != null ) {
            if ( lastWasSpacer ) {
              assertTrue( child instanceof SpacerRenderNode == false );
              lastWasSpacer = false;
            } else {
              assertTrue( child instanceof SpacerRenderNode );
              lastWasSpacer = true;
            }
            child = child.getNext();
          }
        }

      }
      return super.startInlineBox( box );
    }

    public void startValidation( final LogicalPageBox logicalPageBox ) {
      startProcessing( logicalPageBox );
    }
  }
}
