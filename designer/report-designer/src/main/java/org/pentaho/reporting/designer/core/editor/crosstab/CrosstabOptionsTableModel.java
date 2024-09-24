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

package org.pentaho.reporting.designer.core.editor.crosstab;

import org.pentaho.reporting.designer.core.Messages;
import org.pentaho.reporting.designer.core.util.FastPropertyEditorManager;
import org.pentaho.reporting.designer.core.util.table.ElementMetaDataTableModel;
import org.pentaho.reporting.designer.core.util.table.GroupedName;
import org.pentaho.reporting.designer.core.util.table.GroupingHeader;
import org.pentaho.reporting.designer.core.util.table.GroupingModel;
import org.pentaho.reporting.designer.core.util.table.TableStyle;
import org.pentaho.reporting.engine.classic.core.ReportElement;
import org.pentaho.reporting.engine.classic.core.metadata.AttributeMetaData;
import org.pentaho.reporting.engine.classic.core.metadata.ElementMetaData;
import org.pentaho.reporting.engine.classic.core.metadata.ElementType;
import org.pentaho.reporting.engine.classic.core.metadata.MetaData;
import org.pentaho.reporting.engine.classic.core.metadata.StyleMetaData;
import org.pentaho.reporting.engine.classic.core.style.StyleKey;

import javax.swing.table.AbstractTableModel;
import java.beans.PropertyEditor;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Does not support sorting. Table-style is ignored.
 */
public class CrosstabOptionsTableModel extends AbstractTableModel
  implements ElementMetaDataTableModel, GroupingModel {
  private class CrosstabOption {
    private GroupingHeader groupingHeader;
    private boolean headerRow;
    private String valueRole;
    private Class type;
    private GroupedName name;
    private Object value;
    private PropertyEditor propertyEditor;
    private ElementType elementType;

    private CrosstabOption( final GroupingHeader groupingHeader ) {
      if ( groupingHeader == null ) {
        throw new NullPointerException();
      }
      this.groupingHeader = groupingHeader;
      this.headerRow = true;
      this.type = GroupingHeader.class;
    }

    private CrosstabOption( final GroupingHeader groupingHeader,
                            final ElementType elementType,
                            final GroupedName name,
                            final Class type,
                            final String valueRole,
                            final PropertyEditor propertyEditor,
                            final Object value ) {
      if ( groupingHeader == null ) {
        throw new NullPointerException();
      }
      if ( name == null ) {
        throw new NullPointerException();
      }
      if ( type == null ) {
        throw new NullPointerException();
      }
      if ( valueRole == null ) {
        throw new NullPointerException();
      }
      this.elementType = elementType;
      this.propertyEditor = propertyEditor;
      this.groupingHeader = groupingHeader;
      this.headerRow = false;
      this.name = name;
      this.type = type;
      this.valueRole = valueRole;
      this.value = value;
    }

    private ElementType getElementType() {
      return elementType;
    }

    public MetaData getMetaData() {
      return name.getMetaData();
    }

    private GroupedName getName() {
      return name;
    }

    private PropertyEditor getPropertyEditor() {
      return propertyEditor;
    }

    private Object getValue() {
      return value;
    }

    private Class getType() {
      return type;
    }

    private boolean isHeaderRow() {
      return headerRow;
    }

    private String getValueRole() {
      return valueRole;
    }

    private GroupingHeader getGroupingHeader() {
      return groupingHeader;
    }

    public void setValue( final Object value ) {
      this.value = value;
    }
  }

  private ArrayList<CrosstabOption> backend;
  private TableStyle tableStyle;

  public CrosstabOptionsTableModel() {
    backend = new ArrayList<CrosstabOption>();
    tableStyle = TableStyle.GROUPED;
  }

  public void addCrosstabOptionGroup( final GroupingHeader header ) {
    backend.add( new CrosstabOption( header ) );
    fireTableRowsInserted( backend.size() - 1, backend.size() - 1 );
  }

  public void addAttributeOption( final ElementType type,
                                  final String nameSpace,
                                  final String name,
                                  final Object value ) {
    final ElementMetaData metaData = type.getMetaData();
    final AttributeMetaData attributeDescription = metaData.getAttributeDescription( nameSpace, name );
    if ( attributeDescription == null ) {
      return;
    }

    final String valueRole = attributeDescription.getValueRole();
    final Class valueType = attributeDescription.getTargetType();
    final PropertyEditor propertyEditor = attributeDescription.getEditor();
    final GroupedName groupedName = new GroupedName( attributeDescription );
    final GroupingHeader groupingHeader = createGroupingHeader( attributeDescription );
    backend.add( new CrosstabOption( groupingHeader, type, groupedName, valueType, valueRole, propertyEditor, value ) );

    fireTableRowsInserted( backend.size() - 1, backend.size() - 1 );
  }

  public void addStyleOption( final ElementType type,
                              final StyleKey styleKey,
                              final Object value ) {
    final ElementMetaData metaData = type.getMetaData();
    final StyleMetaData styleDescription = metaData.getStyleDescription( styleKey );
    if ( styleDescription == null ) {
      return;
    }

    final String valueRole = "Value";
    final Class valueType = styleDescription.getTargetType();
    final PropertyEditor propertyEditor = styleDescription.getEditor();
    final GroupedName groupedName = new GroupedName( styleDescription );
    final GroupingHeader groupingHeader = createGroupingHeader( styleDescription );
    backend.add( new CrosstabOption( groupingHeader, type, groupedName, valueType, valueRole, propertyEditor, value ) );

    fireTableRowsInserted( backend.size() - 1, backend.size() - 1 );
  }

  public void setStyleOption( final ElementType type, final StyleKey key, final Object value ) {
    final String elementTypeName = type.getMetaData().getName();
    for ( int i = 0; i < backend.size(); i++ ) {
      final CrosstabOption crosstabOption = backend.get( i );
      if ( crosstabOption.isHeaderRow() ) {
        continue;
      }

      if ( elementTypeName.equals( crosstabOption.getElementType().getMetaData().getName() ) == false ) {
        continue;
      }

      final MetaData metaData = crosstabOption.getMetaData();
      if ( metaData instanceof StyleMetaData ) {
        final StyleMetaData attributeMetaData = (StyleMetaData) metaData;
        if ( key.equals( attributeMetaData.getStyleKey() ) ) {
          crosstabOption.setValue( value );
          fireTableCellUpdated( i, 1 );
          return;
        }
      }
    }
  }

  public Object getStyleOption( final ElementType type, final StyleKey key ) {
    final String elementTypeName = type.getMetaData().getName();
    for ( int i = 0; i < backend.size(); i++ ) {
      final CrosstabOption crosstabOption = backend.get( i );
      if ( crosstabOption.isHeaderRow() ) {
        continue;
      }

      if ( elementTypeName.equals( crosstabOption.getElementType().getMetaData().getName() ) == false ) {
        continue;
      }

      final MetaData metaData = crosstabOption.getMetaData();
      if ( metaData instanceof StyleMetaData ) {
        final StyleMetaData attributeMetaData = (StyleMetaData) metaData;
        if ( key.equals( attributeMetaData.getStyleKey() ) ) {
          return crosstabOption.getValue();
        }
      }
    }
    return null;
  }


  public void setAttributeOption( final ElementType type, final String namespace, final String name,
                                  final Object value ) {
    final String elementTypeName = type.getMetaData().getName();
    for ( int i = 0; i < backend.size(); i++ ) {
      final CrosstabOption crosstabOption = backend.get( i );
      if ( crosstabOption.isHeaderRow() ) {
        continue;
      }

      if ( elementTypeName.equals( crosstabOption.getElementType().getMetaData().getName() ) == false ) {
        continue;
      }

      final MetaData metaData = crosstabOption.getMetaData();
      if ( metaData instanceof AttributeMetaData ) {
        final AttributeMetaData attributeMetaData = (AttributeMetaData) metaData;
        if ( namespace.equals( attributeMetaData.getNameSpace() ) &&
          name.equals( attributeMetaData.getName() ) ) {
          crosstabOption.setValue( value );
          fireTableCellUpdated( i, 1 );
          return;
        }
      }
    }
  }

  public Object getAttributeOption( final ElementType type, final String namespace, final String name ) {
    final String elementTypeName = type.getMetaData().getName();
    for ( int i = 0; i < backend.size(); i++ ) {
      final CrosstabOption crosstabOption = backend.get( i );
      if ( crosstabOption.isHeaderRow() ) {
        continue;
      }

      if ( elementTypeName.equals( crosstabOption.getElementType().getMetaData().getName() ) == false ) {
        continue;
      }

      final MetaData metaData = crosstabOption.getMetaData();
      if ( metaData instanceof AttributeMetaData ) {
        final AttributeMetaData attributeMetaData = (AttributeMetaData) metaData;
        if ( namespace.equals( attributeMetaData.getNameSpace() ) &&
          name.equals( attributeMetaData.getName() ) ) {
          return crosstabOption.getValue();
        }
      }
    }
    return null;
  }

  private GroupingHeader createGroupingHeader( final MetaData metaData ) {
    if ( backend.isEmpty() ) {
      final GroupingHeader grHeader = new GroupingHeader( metaData.getGrouping( Locale.getDefault() ) );
      addCrosstabOptionGroup( grHeader );
      return grHeader;
    } else {
      final CrosstabOption last = backend.get( backend.size() - 1 );
      final GroupingHeader groupingHeader = last.getGroupingHeader();
      final String groupingText = metaData.getGrouping( Locale.getDefault() );
      if ( groupingHeader.getHeaderText().equals( groupingText ) == false ) {
        final GroupingHeader grHeader = new GroupingHeader( metaData.getGrouping( Locale.getDefault() ) );
        addCrosstabOptionGroup( grHeader );
        return grHeader;
      } else {
        return groupingHeader;
      }
    }
  }

  public void copyFrom( final ReportElement e ) {
    final String elementTypeName = e.getElementType().getMetaData().getName();
    for ( int i = 0; i < backend.size(); i++ ) {
      final CrosstabOption crosstabOption = backend.get( i );
      if ( crosstabOption.isHeaderRow() ) {
        continue;
      }

      if ( elementTypeName.equals( crosstabOption.getElementType().getMetaData().getName() ) == false ) {
        continue;
      }

      final MetaData metaData = crosstabOption.getMetaData();
      if ( metaData instanceof AttributeMetaData ) {
        final AttributeMetaData attributeMetaData = (AttributeMetaData) metaData;
        final Object value = e.getAttribute( attributeMetaData.getNameSpace(), attributeMetaData.getName() );
        crosstabOption.setValue( value );
      }
    }
    fireTableDataChanged();
  }

  public void copyInto( final ReportElement e ) {
    final String elementTypeName = e.getElementType().getMetaData().getName();
    for ( int i = 0; i < backend.size(); i++ ) {
      final CrosstabOption crosstabOption = backend.get( i );
      if ( crosstabOption.isHeaderRow() ) {
        continue;
      }

      if ( elementTypeName.equals( crosstabOption.getElementType().getMetaData().getName() ) == false ) {
        continue;
      }

      final MetaData metaData = crosstabOption.getMetaData();
      if ( metaData instanceof AttributeMetaData ) {
        final AttributeMetaData attributeMetaData = (AttributeMetaData) metaData;
        e.setAttribute( attributeMetaData.getNameSpace(), attributeMetaData.getName(), crosstabOption.getValue() );
      }
    }
  }


  public String getValueRole( final int row, final int column ) {
    final CrosstabOption crosstabOption = backend.get( row );
    return crosstabOption.getValueRole();
  }

  public String[] getExtraFields( final int row, final int column ) {
    return new String[ 0 ];
  }

  public GroupingHeader getGroupHeader( final int index ) {
    final CrosstabOption crosstabOption = backend.get( index );
    return crosstabOption.getGroupingHeader();
  }

  public boolean isHeaderRow( final int index ) {
    final CrosstabOption crosstabOption = backend.get( index );
    return crosstabOption.isHeaderRow();
  }

  public Class getClassForCell( final int row, final int col ) {
    final CrosstabOption crosstabOption = backend.get( row );
    if ( crosstabOption.isHeaderRow() ) {
      return crosstabOption.getType();
    } else if ( col == 0 ) {
      return GroupedName.class;
    } else {
      return crosstabOption.getType();
    }
  }

  public PropertyEditor getEditorForCell( final int row, final int column ) {
    if ( column == 0 ) {
      return null;
    }
    final CrosstabOption crosstabOption = backend.get( row );
    if ( crosstabOption.isHeaderRow() ) {
      return null;
    }

    final PropertyEditor propertyEditor = crosstabOption.getPropertyEditor();
    if ( propertyEditor != null ) {
      return propertyEditor;
    }
    return getDefaultEditor( crosstabOption.getType() );
  }

  public void setTableStyle( final TableStyle tableStyle ) {
    this.tableStyle = tableStyle;
  }

  public TableStyle getTableStyle() {
    return tableStyle;
  }

  public int getRowCount() {
    return backend.size();
  }

  public int getColumnCount() {
    return 2;
  }

  public String getColumnName( final int column ) {
    switch( column ) {
      case 0:
        return Messages.getString( "CrosstabOptionsTableModel.NameColumn" );
      case 1:
        return Messages.getString( "CrosstabOptionsTableModel.ValueColumn" );
      default:
        throw new IllegalArgumentException();
    }
  }

  public Object getValueAt( final int row, final int column ) {
    final CrosstabOption crosstabOption = backend.get( row );
    if ( crosstabOption.isHeaderRow() ) {
      return crosstabOption.getGroupingHeader();
    }

    switch( column ) {
      case 0:
        return crosstabOption.getName();
      case 1:
        return crosstabOption.getValue();
      default:
        throw new IllegalArgumentException();
    }
  }

  public boolean isCellEditable( final int rowIndex, final int columnIndex ) {
    if ( columnIndex != 1 ) {
      return false;
    }

    final CrosstabOption crosstabOption = backend.get( rowIndex );
    if ( crosstabOption.isHeaderRow() ) {
      return false;
    }
    return true;
  }

  public void setValueAt( final Object aValue, final int rowIndex, final int columnIndex ) {
    if ( columnIndex != 1 ) {
      return;
    }
    final CrosstabOption crosstabOption = backend.get( rowIndex );
    if ( crosstabOption.isHeaderRow() ) {
      return;
    }

    crosstabOption.setValue( aValue );
    fireTableCellUpdated( rowIndex, columnIndex );
  }

  protected PropertyEditor getDefaultEditor( final Class type ) {
    if ( String.class.equals( type ) ) {
      return null;
    }
    return FastPropertyEditorManager.findEditor( type );
  }

}
