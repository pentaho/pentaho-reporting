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

package org.pentaho.reporting.designer.core.editor.drilldown;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.designer.core.Messages;
import org.pentaho.reporting.designer.core.editor.drilldown.model.DrillDownParameter;
import org.pentaho.reporting.designer.core.util.table.ElementMetaDataTableModel;
import org.pentaho.reporting.designer.core.util.table.GroupedName;
import org.pentaho.reporting.designer.core.util.table.GroupingHeader;
import org.pentaho.reporting.designer.core.util.table.GroupingModel;
import org.pentaho.reporting.designer.core.util.table.TableStyle;
import org.pentaho.reporting.engine.classic.core.metadata.AttributeMetaData;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;

import javax.swing.table.AbstractTableModel;
import java.beans.PropertyEditor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;

public class DrillDownParameterTableModel extends AbstractTableModel
  implements ElementMetaDataTableModel, GroupingModel {
  private static class PlainParameterComparator implements Comparator {
    public int compare( final Object o1, final Object o2 ) {
      final DrillDownParameter parameter1 = (DrillDownParameter) o1;
      final DrillDownParameter parameter2 = (DrillDownParameter) o2;
      if ( parameter1 == null && parameter2 == null ) {
        return 0;
      }
      if ( parameter1 == null ) {
        return -1;
      }
      if ( parameter2 == null ) {
        return 1;
      }

      if ( parameter1.getPosition() < parameter2.getPosition() ) {
        return -1;
      }
      if ( parameter1.getPosition() > parameter2.getPosition() ) {
        return 1;
      }
      return parameter1.getName().compareTo( parameter2.getName() );
    }
  }

  private static class GroupedParameterComparator implements Comparator {
    public int compare( final Object o1, final Object o2 ) {
      final DrillDownParameter parameter1 = (DrillDownParameter) o1;
      final DrillDownParameter parameter2 = (DrillDownParameter) o2;
      if ( parameter1 == null && parameter2 == null ) {
        return 0;
      }
      if ( parameter1 == null ) {
        return -1;
      }
      if ( parameter2 == null ) {
        return 1;
      }
      final DrillDownParameter.Type type1 = parameter1.getType();
      final DrillDownParameter.Type type2 = parameter2.getType();
      final int compareType = type1.compareTo( type2 );
      if ( compareType != 0 ) {
        return compareType;
      }

      if ( parameter1.getPosition() < parameter2.getPosition() ) {
        return -1;
      }
      if ( parameter1.getPosition() > parameter2.getPosition() ) {
        return 1;
      }
      return parameter1.getName().compareTo( parameter2.getName() );
    }
  }

  private static final Log logger = LogFactory.getLog( DrillDownParameterTableModel.class );

  private static final DrillDownParameter[] EMPTY_ELEMENTS = new DrillDownParameter[ 0 ];
  private static final GroupingHeader[] EMPTY_GROUPINGS = new GroupingHeader[ 0 ];

  private HashSet filteredParameterNames;
  private String[] filteredParameterNamesArray;

  private GroupingHeader[] groupings;
  private TableStyle tableStyle;
  private DrillDownParameter[] elements;
  private DrillDownParameter[] groupedElements;
  private String[] extraFields;

  /**
   * Constructs a default <code>DefaultTableModel</code> which is a table of zero columns and zero rows.
   */
  public DrillDownParameterTableModel() {
    this.filteredParameterNamesArray = new String[ 0 ];
    this.filteredParameterNames = new HashSet();
    this.tableStyle = TableStyle.GROUPED;
    this.elements = EMPTY_ELEMENTS;
    this.groupings = EMPTY_GROUPINGS;
    this.groupedElements = EMPTY_ELEMENTS;
    this.extraFields = new String[ 0 ];
  }

  public String[] getExtraFields() {
    return extraFields.clone();
  }

  public void setExtraFields( final String[] extraFields ) {
    this.extraFields = extraFields.clone();
  }

  /**
   * Returns the number of rows in the model. A <code>JTable</code> uses this method to determine how many rows it
   * should display.  This method should be quick, as it is called frequently during rendering.
   *
   * @return the number of rows in the model
   * @see #getColumnCount
   */
  public int getRowCount() {
    return groupedElements.length;
  }

  /**
   * Returns the number of columns in the model. A <code>JTable</code> uses this method to determine how many columns it
   * should create and display by default.
   *
   * @return the number of columns in the model
   * @see #getRowCount
   */
  public int getColumnCount() {
    return 2;
  }

  /**
   * Returns a default name for the column using spreadsheet conventions: A, B, C, ... Z, AA, AB, etc.  If
   * <code>column</code> cannot be found, returns an empty string.
   *
   * @param column the column being queried
   * @return a string containing the default name of <code>column</code>
   */
  public String getColumnName( final int column ) {
    if ( column == 0 ) {
      return Messages.getString( "DrillDownParameterTable.ParameterName" );
    }
    return Messages.getString( "DrillDownParameterTable.ParameterValue" );
  }


  public TableStyle getTableStyle() {
    return tableStyle;
  }

  public void setTableStyle( final TableStyle tableStyle ) {
    if ( tableStyle == null ) {
      throw new NullPointerException();
    }
    this.tableStyle = tableStyle;
    updateData( getData() );
  }

  private DrillDownParameter[] filter( final DrillDownParameter[] elements ) {
    final ArrayList<DrillDownParameter> retval = new ArrayList<DrillDownParameter>( elements.length );
    for ( int i = 0; i < elements.length; i++ ) {
      final DrillDownParameter element = elements[ i ];
      if ( filteredParameterNames.contains( element.getName() ) ) {
        continue;
      }
      retval.add( element );
    }
    return retval.toArray( new DrillDownParameter[ retval.size() ] );
  }

  protected void updateData( final DrillDownParameter[] elements ) {
    this.elements = elements.clone();

    final DrillDownParameter[] metaData = filter( elements );
    if ( tableStyle == TableStyle.ASCENDING ) {
      Arrays.sort( metaData, new PlainParameterComparator() );
      this.groupings = new GroupingHeader[ metaData.length ];
      this.groupedElements = metaData;
    } else if ( tableStyle == TableStyle.DESCENDING ) {
      Arrays.sort( metaData, Collections.reverseOrder( new PlainParameterComparator() ) );
      this.groupings = new GroupingHeader[ metaData.length ];
      this.groupedElements = metaData;
    } else {
      Arrays.sort( metaData, new GroupedParameterComparator() );

      int groupCount = 0;
      if ( metaData.length > 0 ) {
        DrillDownParameter.Type oldValue = null;

        for ( int i = 0; i < metaData.length; i++ ) {
          if ( groupCount == 0 ) {
            groupCount = 1;
            final DrillDownParameter firstdata = metaData[ i ];
            oldValue = firstdata.getType();
            continue;
          }

          final DrillDownParameter data = metaData[ i ];
          final DrillDownParameter.Type grouping = data.getType();
          if ( ( ObjectUtilities.equal( oldValue, grouping ) ) == false ) {
            oldValue = grouping;
            groupCount += 1;
          }
        }
      }

      final DrillDownParameter[] groupedMetaData = new DrillDownParameter[ metaData.length + groupCount ];
      this.groupings = new GroupingHeader[ groupedMetaData.length ];
      int targetIdx = 0;
      GroupingHeader group = null;
      for ( int sourceIdx = 0; sourceIdx < metaData.length; sourceIdx++ ) {
        final DrillDownParameter data = metaData[ sourceIdx ];
        if ( sourceIdx == 0 ) {
          group = new GroupingHeader( data.getType().toString() );
          groupings[ targetIdx ] = group;
          targetIdx += 1;
        } else {
          final String newgroup = data.getType().toString();
          if ( ( ObjectUtilities.equal( newgroup, group.getHeaderText() ) ) == false ) {
            group = new GroupingHeader( newgroup );
            groupings[ targetIdx ] = group;
            targetIdx += 1;
          }
        }

        groupings[ targetIdx ] = group;
        groupedMetaData[ targetIdx ] = data;
        targetIdx += 1;
      }
      this.groupedElements = groupedMetaData;
    }

    fireTableDataChanged();
  }

  /**
   * Returns the value for the cell at <code>columnIndex</code> and <code>rowIndex</code>.
   *
   * @param rowIndex    the row whose value is to be queried
   * @param columnIndex the column whose value is to be queried
   * @return the value Object at the specified cell
   */
  public Object getValueAt( final int rowIndex, final int columnIndex ) {
    final DrillDownParameter metaData = groupedElements[ rowIndex ];
    if ( metaData == null ) {
      return groupings[ rowIndex ];
    }

    switch( columnIndex ) {
      case 0:
        return new GroupedName( metaData, metaData.getName(), metaData.getType().toString() );
      case 1:
        return metaData.getFormulaFragment();
      default:
        throw new IndexOutOfBoundsException();
    }
  }

  /**
   * Returns false.  This is the default implementation for all cells.
   *
   * @param rowIndex    the row being queried
   * @param columnIndex the column being queried
   * @return false
   */
  public boolean isCellEditable( final int rowIndex, final int columnIndex ) {
    final DrillDownParameter metaData = groupedElements[ rowIndex ];
    if ( metaData == null ) {
      return false;
    }

    switch( columnIndex ) {
      case 0:
        return metaData.getType() == DrillDownParameter.Type.MANUAL;
      case 1:
        return true;
      default:
        throw new IndexOutOfBoundsException();
    }
  }


  public void setValueAt( final Object aValue, final int rowIndex, final int columnIndex ) {
    final DrillDownParameter metaData = groupedElements[ rowIndex ];
    if ( metaData == null ) {
      return;
    }

    switch( columnIndex ) {
      case 0:
        if ( aValue instanceof GroupedName ) {
          final GroupedName name = (GroupedName) aValue;
          metaData.setName( name.getName() );
          fireTableDataChanged();
        }
        return;
      case 1: {
        if ( aValue == null ) {
          metaData.setFormulaFragment( null );
        } else {
          metaData.setFormulaFragment( String.valueOf( aValue ) );
        }
        fireTableDataChanged();
        break;
      }
      default:
        throw new IndexOutOfBoundsException();
    }

  }

  public Class getClassForCell( final int row, final int column ) {
    final DrillDownParameter metaData = groupedElements[ row ];
    if ( metaData == null ) {
      return GroupingHeader.class;
    }

    if ( column == 0 ) {
      return GroupedName.class;
    }

    return String.class;
  }

  public PropertyEditor getEditorForCell( final int row, final int column ) {
    return null;
  }

  public String getValueRole( final int row, final int column ) {
    if ( column == 0 ) {
      return AttributeMetaData.VALUEROLE_VALUE; // NON-NLS
    }
    return AttributeMetaData.VALUEROLE_FORMULA;// NON-NLS
  }

  public String[] getExtraFields( final int row, final int column ) {
    return extraFields;
  }

  public GroupingHeader getGroupHeader( final int index ) {
    return groupings[ index ];
  }

  public boolean isHeaderRow( final int index ) {
    return groupedElements[ index ] == null;
  }

  public String[] getFilteredParameterNames() {
    return filteredParameterNamesArray.clone();
  }

  public void setFilteredParameterNames( final String[] names ) {
    this.filteredParameterNamesArray = names.clone();
    this.filteredParameterNames.clear();
    this.filteredParameterNames.addAll( Arrays.asList( names ) );

    updateData( elements );
  }

  public void setData( final DrillDownParameter[] parameter ) {
    updateData( parameter );
  }

  public DrillDownParameter[] getData() {
    return elements.clone();
  }

  public DrillDownParameter[] getGroupedData() {
    return groupedElements.clone();
  }

  public DrillDownParameter.Type getParameterType( final int row ) {
    final DrillDownParameter downParameter = groupedElements[ row ];
    if ( downParameter != null ) {
      return downParameter.getType();
    }
    return null;
  }

  public boolean isPreferred( final int row ) {
    final DrillDownParameter downParameter = groupedElements[ row ];
    if ( downParameter != null ) {
      return downParameter.isPreferred();
    }
    return true;
  }
}
