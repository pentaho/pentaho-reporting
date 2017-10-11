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
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.SimplePageDefinition;
import org.pentaho.reporting.engine.classic.core.layout.model.InlineRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.LogicalPageBox;
import org.pentaho.reporting.engine.classic.core.layout.model.ParagraphPoolBox;
import org.pentaho.reporting.engine.classic.core.layout.model.ParagraphRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.process.IterateStructuralProcessStep;
import org.pentaho.reporting.engine.classic.core.testsupport.DebugReportRunner;
import org.pentaho.reporting.engine.classic.core.util.geom.StrictGeomUtility;
import org.pentaho.reporting.libraries.resourceloader.Resource;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

import java.awt.print.PageFormat;
import java.net.URL;

public class NestedRowsIT extends TestCase {
  public NestedRowsIT() {
  }

  public NestedRowsIT( final String s ) {
    super( s );
  }

  protected void setUp() throws Exception {
    ClassicEngineBoot.getInstance().start();
  }

  public void testNestedRows() throws Exception {
    final MasterReport basereport = new MasterReport();
    basereport.setPageDefinition( new SimplePageDefinition( new PageFormat() ) );
    final URL target = LayoutIT.class.getResource( "nested-rows.xml" );
    final ResourceManager rm = new ResourceManager();
    rm.registerDefaults();
    final Resource directly = rm.createDirectly( target, MasterReport.class );
    final MasterReport report = (MasterReport) directly.getResource();

    final LogicalPageBox logicalPageBox =
        DebugReportRunner.layoutSingleBand( basereport, report.getReportHeader(), false, false );
    // simple test, we assert that all paragraph-poolboxes are on either 485000 or 400000
    // and that only two lines exist for each

    // This test works on some invalid assumptions and therefore cannot validate properly.
    // within a canvas context there is no inherent size for elements and thus the labels are not visible at all.

    // new ValidateRunner().startValidation(logicalPageBox);
  }

  private static class ValidateRunner extends IterateStructuralProcessStep {
    private int count;

    protected void processParagraphChilds( final ParagraphRenderBox box ) {
      count = 0;
      processBoxChilds( box );
      if ( box.getX() == StrictGeomUtility.toInternalValue( 485 ) ) {
        TestCase.assertEquals( "Line-Count", 2, count );
      } else {
        TestCase.assertEquals( "Line-Count", 1, count );
      }
    }

    protected boolean startInlineBox( final InlineRenderBox box ) {
      if ( box instanceof ParagraphPoolBox ) {
        count += 1;
        final long x = box.getX();
        if ( "A".equals( box.getName() ) ) {
          if ( x < StrictGeomUtility.toInternalValue( 400 ) || x > StrictGeomUtility.toInternalValue( 485 ) ) {
            TestCase.fail( "X position is wrong: " + x );
          }
        }
        if ( "B".equals( box.getName() ) ) {
          if ( x < StrictGeomUtility.toInternalValue( 485 ) || x > StrictGeomUtility.toInternalValue( 560 ) ) {
            TestCase.fail( "X position is wrong: " + x );
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
