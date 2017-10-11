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

package org.pentaho.reporting.engine.classic.core.crosstab;

import junit.framework.Assert;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.ParameterDataRow;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.function.ProcessingContext;
import org.pentaho.reporting.engine.classic.core.layout.output.DefaultProcessingContext;
import org.pentaho.reporting.engine.classic.core.states.ReportStateKey;
import org.pentaho.reporting.engine.classic.core.states.crosstab.CrosstabSpecification;
import org.pentaho.reporting.engine.classic.core.states.crosstab.OrderedMergeCrosstabSpecification;
import org.pentaho.reporting.engine.classic.core.states.crosstab.SortedMergeCrosstabSpecification;
import org.pentaho.reporting.engine.classic.core.states.datarow.GlobalMasterRow;
import org.pentaho.reporting.engine.classic.core.states.datarow.MasterDataRow;
import org.pentaho.reporting.engine.classic.core.wizard.DefaultDataSchemaDefinition;
import org.pentaho.reporting.libraries.base.util.DebugLog;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;

import javax.swing.table.TableModel;
import java.util.Arrays;

public class CrosstabTestUtil {
  private static final Log logger = LogFactory.getLog( CrosstabTestUtil.class );

  public static int advanceCrosstab( final CrosstabSpecification specification, final TableModel data,
      final String[][] valData ) {
    // second run. Now with padding ..
    final ProcessingContext prc = new DefaultProcessingContext();
    final GlobalMasterRow gmr =
        GlobalMasterRow.createReportRow( prc, new DefaultDataSchemaDefinition(), new ParameterDataRow() );
    gmr.requireStructuralProcessing();
    MasterDataRow wdata = gmr.deriveWithQueryData( data );
    int advanceCount = 1;
    wdata = wdata.startCrosstabMode( specification );
    logger.debug( "Rows:  " + wdata.getGlobalView().get( "Rows" ) );
    logger.debug( "Cols:  " + wdata.getGlobalView().get( "Cols" ) );
    logger.debug( "Data: " + wdata.getGlobalView().get( "Data" ) );

    Assert.assertEquals( valData[0][0], wdata.getGlobalView().get( "Rows" ) );
    Assert.assertEquals( valData[0][1], wdata.getGlobalView().get( "Cols" ) );

    Object grpVal = wdata.getGlobalView().get( "Rows" );
    while ( wdata.isAdvanceable() ) {
      logger.debug( "-- Advance -- " + advanceCount );
      MasterDataRow nextdata = wdata.advance();
      final Object rows = nextdata.getGlobalView().get( "Rows" );
      if ( ObjectUtilities.equal( grpVal, rows ) == false ) {
        nextdata = nextdata.resetRowCursor();
      }

      logger.debug( "Do Advance Count: " + nextdata.getReportDataRow().getCursor() );
      logger.debug( "Rows:  " + rows );
      final Object cols = nextdata.getGlobalView().get( "Cols" );
      logger.debug( "Cols:  " + cols );
      logger.debug( "Data: " + nextdata.getGlobalView().get( "Data" ) );

      Assert.assertEquals( valData[advanceCount][0], rows );
      Assert.assertEquals( valData[advanceCount][1], cols );

      advanceCount += 1;
      wdata = nextdata;
      grpVal = rows;
    }
    return advanceCount;
  }

  public static CrosstabSpecification fillOrderedCrosstabSpec( final TableModel model )
    throws ReportProcessingException {
    final CrosstabSpecification spec =
        new OrderedMergeCrosstabSpecification( new ReportStateKey(), new String[] { "Cols" }, new String[] { "Rows" } );
    return fillCrosstabSpec( model, spec );
  }

  public static CrosstabSpecification fillSortedCrosstabSpec( final TableModel model ) throws ReportProcessingException {
    final CrosstabSpecification spec =
        new SortedMergeCrosstabSpecification( new ReportStateKey(), new String[] { "Cols" }, new String[] { "Rows" } );
    return fillCrosstabSpec( model, spec );
  }

  private static CrosstabSpecification fillCrosstabSpec( final TableModel model, final CrosstabSpecification spec )
    throws ReportProcessingException {
    final TableModelDataRow dr = new TableModelDataRow( model );

    Object rowKey = dr.get( "Rows" );
    spec.startRow();
    for ( int i = 0; i < model.getRowCount(); i += 1 ) {
      dr.setCurrentRow( i );
      Object row = dr.get( "Rows" );
      if ( ObjectUtilities.equal( row, rowKey ) == false ) {
        DebugLog.log( "R: " + rowKey + " -> " + row );
        spec.endRow();
        spec.startRow();
        rowKey = row;
      }
      spec.add( dr );
    }
    spec.endRow();
    spec.endCrosstab();
    return spec;
  }

  public static void assertEqualsArray( final Object[] objects, final Object[] o2 ) {
    if ( ObjectUtilities.equalArray( objects, o2 ) == false ) {
      Assert.fail( Arrays.asList( objects ) + " vs. " + Arrays.asList( o2 ) );
    }
  }

}
