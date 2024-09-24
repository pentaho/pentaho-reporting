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

package org.pentaho.reporting.designer.core.util;

import junit.framework.TestCase;
import org.pentaho.reporting.designer.core.util.table.GroupedTableModel;
import org.pentaho.reporting.designer.core.util.table.GroupingHeader;
import org.pentaho.reporting.designer.core.util.table.GroupingModel;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;

/**
 * Todo: Document Me
 *
 * @author Thomas Morgner
 * @noinspection HardCodedStringLiteral
 */
public class GroupedTableModelTest extends TestCase {
  private static class GroupModel extends AbstractTableModel implements GroupingModel {
    private ArrayList headers;
    private ArrayList data;

    private GroupModel() {
      headers = new ArrayList();
      data = new ArrayList();
    }

    public void add( final GroupingHeader header, final Object data ) {
      headers.add( header );
      this.data.add( data );
    }

    public int getRowCount() {
      return data.size();
    }

    public int getColumnCount() {
      return 2;
    }

    public Object getValueAt( final int rowIndex, final int columnIndex ) {
      if ( columnIndex == 0 ) {
        return headers.get( rowIndex );
      }
      return data.get( rowIndex );
    }

    public GroupingHeader getGroupHeader( final int index ) {
      return (GroupingHeader) headers.get( index );
    }

    public boolean isHeaderRow( final int index ) {
      return data.get( index ) == null;
    }
  }

  protected static class TestGroupedTableModel extends GroupedTableModel {
    protected TestGroupedTableModel( final GroupingModel parent ) {
      super( parent );
    }

    @Override
    public int mapFromModel( final int row ) {
      return super.mapFromModel( row );
    }

    @Override
    public int mapToModel( final int row ) {
      return super.mapToModel( row );
    }
  }

  public GroupedTableModelTest() {
  }

  public GroupedTableModelTest( final String s ) {
    super( s );
  }

  @Override
  protected void setUp() throws Exception {
    super.setUp();
  }

  public void testEmptyMapping() {
    final GroupModel model = new GroupModel();
    final TestGroupedTableModel groupedTableModel = new TestGroupedTableModel( model );
    assertEquals( groupedTableModel.getRowCount(), model.getRowCount() );

  }

  public void testSimpleMapping() {
    final GroupModel model = new GroupModel();
    final GroupingHeader header1 = new GroupingHeader( "1" );
    final GroupingHeader header2 = new GroupingHeader( "2" );
    final GroupingHeader header3 = new GroupingHeader( "3" );
    final GroupingHeader header4 = new GroupingHeader( "4" );

    model.add( header1, null );
    model.add( header2, null );
    model.add( header2, "2a" );
    model.add( header2, "2b" );
    model.add( header2, "2c" );
    model.add( header3, null );
    model.add( header3, "3a" );
    model.add( header3, "3b" );
    model.add( header4, null );

    final TestGroupedTableModel groupedTableModel = new TestGroupedTableModel( model );
    assertEquals( groupedTableModel.getRowCount(), model.getRowCount() );
    for ( int row = 0; row < model.getRowCount(); row++ ) {
      assertEquals( groupedTableModel.getValueAt( row, 0 ), model.getValueAt( row, 0 ) );
      assertEquals( groupedTableModel.getValueAt( row, 1 ), model.getValueAt( row, 1 ) );
      assertEquals( groupedTableModel.mapFromModel( row ), row );
    }
  }

  public void testInnerCollapseMapping() {
    final GroupModel model = new GroupModel();
    final GroupingHeader header1 = new GroupingHeader( "1" );
    final GroupingHeader header2 = new GroupingHeader( "2" );
    header2.setCollapsed( true );
    final GroupingHeader header3 = new GroupingHeader( "3" );
    final GroupingHeader header4 = new GroupingHeader( "4" );

    model.add( header1, null );
    model.add( header1, "1a" );
    model.add( header2, null );
    model.add( header2, "2a" );
    model.add( header2, "2b" );
    model.add( header2, "2c" );
    model.add( header3, null );
    model.add( header3, "3a" );
    model.add( header3, "3b" );
    model.add( header4, null );
    model.add( header4, "4a" );

    final TestGroupedTableModel groupedTableModel = new TestGroupedTableModel( model );
    assertEquals( "Row-Count", 8, groupedTableModel.getRowCount() );

    assertEquals( "Header1", header1, groupedTableModel.getValueAt( 0, 0 ) );
    assertEquals( "Header1", header1, groupedTableModel.getValueAt( 1, 0 ) );
    assertEquals( "Header2", header2, groupedTableModel.getValueAt( 2, 0 ) );
    assertEquals( "Header3", header3, groupedTableModel.getValueAt( 3, 0 ) );
    assertEquals( "Header3", header3, groupedTableModel.getValueAt( 4, 0 ) );
    assertEquals( "Header3", header3, groupedTableModel.getValueAt( 5, 0 ) );
    assertEquals( "Header4", header4, groupedTableModel.getValueAt( 6, 0 ) );
    assertEquals( "Header4", header4, groupedTableModel.getValueAt( 7, 0 ) );

    assertEquals( "Back-Mapping", 0, groupedTableModel.mapFromModel( 0 ) );
    assertEquals( "Back-Mapping", 1, groupedTableModel.mapFromModel( 1 ) );
    assertEquals( "Back-Mapping", 2, groupedTableModel.mapFromModel( 2 ) );
    assertEquals( "Back-Mapping", 2, groupedTableModel.mapFromModel( 3 ) );
    assertEquals( "Back-Mapping", 2, groupedTableModel.mapFromModel( 4 ) );
    assertEquals( "Back-Mapping", 2, groupedTableModel.mapFromModel( 5 ) );
    assertEquals( "Back-Mapping", 3, groupedTableModel.mapFromModel( 6 ) );
    assertEquals( "Back-Mapping", 4, groupedTableModel.mapFromModel( 7 ) );
    assertEquals( "Back-Mapping", 5, groupedTableModel.mapFromModel( 8 ) );
    assertEquals( "Back-Mapping", 6, groupedTableModel.mapFromModel( 9 ) );
    assertEquals( "Back-Mapping", 7, groupedTableModel.mapFromModel( 10 ) );
  }

  public void testFirstCollapseMapping() {
    final GroupModel model = new GroupModel();
    final GroupingHeader header1 = new GroupingHeader( "1" );
    header1.setCollapsed( true );
    final GroupingHeader header2 = new GroupingHeader( "2" );
    final GroupingHeader header3 = new GroupingHeader( "3" );
    final GroupingHeader header4 = new GroupingHeader( "4" );

    model.add( header1, null );
    model.add( header1, "1a" );
    model.add( header2, null );
    model.add( header2, "2a" );
    model.add( header2, "2b" );
    model.add( header2, "2c" );
    model.add( header3, null );
    model.add( header3, "3a" );
    model.add( header3, "3b" );
    model.add( header4, null );
    model.add( header4, "4a" );

    final TestGroupedTableModel groupedTableModel = new TestGroupedTableModel( model );
    assertEquals( "Row-Count", 10, groupedTableModel.getRowCount() );

    assertEquals( "Header1", header1, groupedTableModel.getValueAt( 0, 0 ) );
    assertEquals( "Header2", header2, groupedTableModel.getValueAt( 1, 0 ) );
    assertEquals( "Header2", header2, groupedTableModel.getValueAt( 2, 0 ) );
    assertEquals( "Header2", header2, groupedTableModel.getValueAt( 3, 0 ) );
    assertEquals( "Header2", header2, groupedTableModel.getValueAt( 4, 0 ) );
    assertEquals( "Header3", header3, groupedTableModel.getValueAt( 5, 0 ) );
    assertEquals( "Header3", header3, groupedTableModel.getValueAt( 6, 0 ) );
    assertEquals( "Header3", header3, groupedTableModel.getValueAt( 7, 0 ) );
    assertEquals( "Header4", header4, groupedTableModel.getValueAt( 8, 0 ) );
    assertEquals( "Header4", header4, groupedTableModel.getValueAt( 9, 0 ) );

    assertEquals( "Back-Mapping", 0, groupedTableModel.mapFromModel( 0 ) );
    assertEquals( "Back-Mapping", 0, groupedTableModel.mapFromModel( 1 ) );
    assertEquals( "Back-Mapping", 1, groupedTableModel.mapFromModel( 2 ) );
    assertEquals( "Back-Mapping", 2, groupedTableModel.mapFromModel( 3 ) );
    assertEquals( "Back-Mapping", 3, groupedTableModel.mapFromModel( 4 ) );
    assertEquals( "Back-Mapping", 4, groupedTableModel.mapFromModel( 5 ) );
    assertEquals( "Back-Mapping", 5, groupedTableModel.mapFromModel( 6 ) );
    assertEquals( "Back-Mapping", 6, groupedTableModel.mapFromModel( 7 ) );
    assertEquals( "Back-Mapping", 7, groupedTableModel.mapFromModel( 8 ) );
    assertEquals( "Back-Mapping", 8, groupedTableModel.mapFromModel( 9 ) );
    assertEquals( "Back-Mapping", 9, groupedTableModel.mapFromModel( 10 ) );
  }

  public void testLastCollapseMapping() {
    final GroupModel model = new GroupModel();
    final GroupingHeader header1 = new GroupingHeader( "1" );
    final GroupingHeader header2 = new GroupingHeader( "2" );
    final GroupingHeader header3 = new GroupingHeader( "3" );
    final GroupingHeader header4 = new GroupingHeader( "4" );
    header4.setCollapsed( true );

    model.add( header1, null );
    model.add( header1, "1a" );
    model.add( header2, null );
    model.add( header2, "2a" );
    model.add( header2, "2b" );
    model.add( header2, "2c" );
    model.add( header3, null );
    model.add( header3, "3a" );
    model.add( header3, "3b" );
    model.add( header4, null );
    model.add( header4, "4a" );
    model.add( header4, "4b" );

    final TestGroupedTableModel groupedTableModel = new TestGroupedTableModel( model );
    assertEquals( "Row-Count", 10, groupedTableModel.getRowCount() );

    assertEquals( "Header1", header1, groupedTableModel.getValueAt( 0, 0 ) );
    assertEquals( "Header1", header1, groupedTableModel.getValueAt( 1, 0 ) );
    assertEquals( "Header2", header2, groupedTableModel.getValueAt( 2, 0 ) );
    assertEquals( "Header2", header2, groupedTableModel.getValueAt( 3, 0 ) );
    assertEquals( "Header2", header2, groupedTableModel.getValueAt( 4, 0 ) );
    assertEquals( "Header2", header2, groupedTableModel.getValueAt( 5, 0 ) );
    assertEquals( "Header3", header3, groupedTableModel.getValueAt( 6, 0 ) );
    assertEquals( "Header3", header3, groupedTableModel.getValueAt( 7, 0 ) );
    assertEquals( "Header3", header3, groupedTableModel.getValueAt( 8, 0 ) );
    assertEquals( "Header4", header4, groupedTableModel.getValueAt( 9, 0 ) );

    assertEquals( "Back-Mapping", 0, groupedTableModel.mapFromModel( 0 ) );
    assertEquals( "Back-Mapping", 1, groupedTableModel.mapFromModel( 1 ) );
    assertEquals( "Back-Mapping", 2, groupedTableModel.mapFromModel( 2 ) );
    assertEquals( "Back-Mapping", 3, groupedTableModel.mapFromModel( 3 ) );
    assertEquals( "Back-Mapping", 4, groupedTableModel.mapFromModel( 4 ) );
    assertEquals( "Back-Mapping", 5, groupedTableModel.mapFromModel( 5 ) );
    assertEquals( "Back-Mapping", 6, groupedTableModel.mapFromModel( 6 ) );
    assertEquals( "Back-Mapping", 7, groupedTableModel.mapFromModel( 7 ) );
    assertEquals( "Back-Mapping", 8, groupedTableModel.mapFromModel( 8 ) );
    assertEquals( "Back-Mapping", 9, groupedTableModel.mapFromModel( 9 ) );
    assertEquals( "Back-Mapping", 9, groupedTableModel.mapFromModel( 10 ) );
    assertEquals( "Back-Mapping", 9, groupedTableModel.mapFromModel( 11 ) );
  }

  public void testAllCollapseMapping() {
    final GroupModel model = new GroupModel();
    final GroupingHeader header1 = new GroupingHeader( "1" );
    header1.setCollapsed( true );
    final GroupingHeader header2 = new GroupingHeader( "2" );
    header2.setCollapsed( true );
    final GroupingHeader header3 = new GroupingHeader( "3" );
    header3.setCollapsed( true );
    final GroupingHeader header4 = new GroupingHeader( "4" );
    header4.setCollapsed( true );

    model.add( header1, null );
    model.add( header1, "1a" );
    model.add( header2, null );
    model.add( header2, "2a" );
    model.add( header2, "2b" );
    model.add( header2, "2c" );
    model.add( header3, null );
    model.add( header3, "3a" );
    model.add( header3, "3b" );
    model.add( header4, null );
    model.add( header4, "4a" );

    final TestGroupedTableModel groupedTableModel = new TestGroupedTableModel( model );
    assertEquals( "Row-Count", 4, groupedTableModel.getRowCount() );

    assertEquals( "Header1", header1, groupedTableModel.getValueAt( 0, 0 ) );
    assertEquals( "Header2", header2, groupedTableModel.getValueAt( 1, 0 ) );
    assertEquals( "Header3", header3, groupedTableModel.getValueAt( 2, 0 ) );
    assertEquals( "Header4", header4, groupedTableModel.getValueAt( 3, 0 ) );

    assertEquals( "Back-Mapping", 0, groupedTableModel.mapFromModel( 0 ) );
    assertEquals( "Back-Mapping", 0, groupedTableModel.mapFromModel( 1 ) );
    assertEquals( "Back-Mapping", 1, groupedTableModel.mapFromModel( 2 ) );
    assertEquals( "Back-Mapping", 1, groupedTableModel.mapFromModel( 3 ) );
    assertEquals( "Back-Mapping", 1, groupedTableModel.mapFromModel( 4 ) );
    assertEquals( "Back-Mapping", 1, groupedTableModel.mapFromModel( 5 ) );
    assertEquals( "Back-Mapping", 2, groupedTableModel.mapFromModel( 6 ) );
    assertEquals( "Back-Mapping", 2, groupedTableModel.mapFromModel( 7 ) );
    assertEquals( "Back-Mapping", 2, groupedTableModel.mapFromModel( 8 ) );
    assertEquals( "Back-Mapping", 3, groupedTableModel.mapFromModel( 9 ) );
    assertEquals( "Back-Mapping", 3, groupedTableModel.mapFromModel( 10 ) );
  }
}
