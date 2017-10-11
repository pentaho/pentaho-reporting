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

package org.pentaho.reporting.designer.core.editor.styles;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.designer.core.util.FastPropertyEditorManager;
import org.pentaho.reporting.designer.core.util.exceptions.UncaughtExceptionsModel;
import org.pentaho.reporting.designer.core.util.table.ElementMetaDataTableModel;
import org.pentaho.reporting.designer.core.util.table.GroupedName;
import org.pentaho.reporting.designer.core.util.table.GroupingHeader;
import org.pentaho.reporting.designer.core.util.table.GroupingModel;
import org.pentaho.reporting.designer.core.util.table.TableStyle;
import org.pentaho.reporting.engine.classic.core.metadata.AttributeMetaData;
import org.pentaho.reporting.engine.classic.core.metadata.StyleMetaData;
import org.pentaho.reporting.engine.classic.core.style.ResolverStyleSheet;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.beans.PropertyEditor;

public abstract class AbstractStyleTableModel<T extends StyleDataBackend>
  extends AbstractTableModel implements ElementMetaDataTableModel, GroupingModel {
  protected class SameElementsUpdateDataTask implements Runnable {
    private T dataBackend;
    private boolean synchronous;

    protected SameElementsUpdateDataTask( final T elements,
                                          final boolean synchronous ) {
      this.dataBackend = elements;
      this.synchronous = synchronous;
    }

    public void run() {
      dataBackend.resetCache();
      try {
        if ( synchronous || SwingUtilities.isEventDispatchThread() ) {
          setDataBackend( dataBackend );
          fireTableDataChanged();
        } else {
          SwingUtilities.invokeAndWait( new NotifyChangeTask( dataBackend ) );
        }
      } catch ( Exception e ) {
        UncaughtExceptionsModel.getInstance().addException( e );
      }
    }
  }

  protected class NotifyChangeTask implements Runnable {
    private T dataBackend;

    protected NotifyChangeTask( final T dataBackend ) {
      this.dataBackend = dataBackend;
    }

    public void run() {
      setDataBackend( dataBackend );
      fireTableDataChanged();
    }
  }

  private static final Log logger = LogFactory.getLog( AbstractStyleTableModel.class );
  private static final String[] EXTRA_FIELDS = new String[ 0 ];

  private TableStyle tableStyle;
  private T dataBackend;
  private boolean synchronous;

  public AbstractStyleTableModel() {
    tableStyle = TableStyle.GROUPED;
  }

  public boolean isSynchronous() {
    return synchronous;
  }

  public void setSynchronous( final boolean synchronous ) {
    this.synchronous = synchronous;
  }

  protected synchronized T getDataBackend() {
    return dataBackend;
  }

  protected synchronized void setDataBackend( final T dataBackend ) {
    this.dataBackend = dataBackend;
  }

  public int getRowCount() {
    return dataBackend.getRowCount();
  }

  protected StyleMetaData getMetaData( final int row ) {
    return getDataBackend().getMetaData( row );
  }

  protected GroupingHeader getGroupings( final int row ) {
    return getDataBackend().getGroupings( row );
  }

  public TableStyle getTableStyle() {
    return tableStyle;
  }

  public void setTableStyle( final TableStyle tableStyle ) {
    if ( tableStyle == null ) {
      throw new NullPointerException();
    }
    this.tableStyle = tableStyle;
  }


  /**
   * Uses the name of the old groupings to set the collapse status of the new groupings so that when a user makes a
   * selection not all of the groups return to the expanded state.  In essence makes group collapses "sticky" where the
   * group heading hasn't changed.
   *
   * @param groupings
   * @param oldGroupings
   */
  protected GroupingHeader[] reconcileState( final GroupingHeader[] groupings,
                                             final GroupingHeader[] oldGroupings ) {
    if ( oldGroupings == null ) {
      return groupings;
    }

    for ( int i = 0; i < groupings.length; i++ ) {
      final GroupingHeader header = groupings[ i ];
      if ( header == null ) {
        continue;
      }

      final GroupingHeader oldHeader = findFirstOccurrenceOfHeaderTitle( oldGroupings, header.getHeaderText() );
      if ( oldHeader != null ) {
        header.setCollapsed( oldHeader.isCollapsed() );
      }
    }
    return groupings;
  }

  private GroupingHeader findFirstOccurrenceOfHeaderTitle( final GroupingHeader[] headerArray,
                                                           final String headerTitle ) {
    for ( final GroupingHeader header : headerArray ) {
      if ( header == null ) {
        continue;
      }
      if ( ObjectUtilities.equal( header.getHeaderText(), headerTitle ) ) {
        return header;
      }
    }
    return null;
  }


  public int getColumnCount() {
    return 3;
  }

  public String getColumnName( final int column ) {
    switch( column ) {
      case 0:
        return Messages.getString( "StyleTableModel.NameColumn" );
      case 1:
        return Messages.getString( "StyleTableModel.InheritColumn" );
      case 2:
        return Messages.getString( "StyleTableModel.ValueColumn" );
      default:
        throw new IllegalArgumentException();
    }
  }

  public Object getValueAt( final int rowIndex, final int columnIndex ) {
    final StyleMetaData metaData = getMetaData( rowIndex );
    if ( metaData == null ) {
      return getGroupings( rowIndex );
    }
    switch( columnIndex ) {
      case 0:
        return new GroupedName( metaData );
      case 1:
        return computeInheritValue( metaData, rowIndex );
      case 2:
        return computeFullValue( metaData, rowIndex );
      default:
        throw new IndexOutOfBoundsException();
    }
  }

  public boolean isCellEditable( final int rowIndex, final int columnIndex ) {
    final StyleMetaData metaData = getMetaData( rowIndex );
    if ( metaData == null ) {
      return false;
    }

    switch( columnIndex ) {
      case 0:
        return false;
      case 1:
      case 2:
        return true;
      default:
        throw new IndexOutOfBoundsException();
    }
  }


  public void setValueAt( final Object aValue, final int rowIndex, final int columnIndex ) {
    final StyleMetaData metaData = getMetaData( rowIndex );
    if ( metaData == null ) {
      return;
    }

    switch( columnIndex ) {
      case 0:
        return;
      case 1: {
        if ( Boolean.TRUE.equals( aValue ) ) {
          if ( defineFullValue( metaData, null ) ) {
            getDataBackend().clearCache( rowIndex );
            fireTableDataChanged();
          }
        }
        break;
      }
      case 2: {
        if ( defineFullValue( metaData, aValue ) ) {
          getDataBackend().clearCache( rowIndex );
          fireTableDataChanged();
        }
        break;
      }
      default:
        throw new IndexOutOfBoundsException();
    }
  }

  protected abstract Object computeInheritValue( final StyleMetaData metaData,
                                                 final int rowIndex );

  protected abstract boolean defineFullValue( final StyleMetaData metaData, final Object value );

  protected Object computeFullValue( final StyleMetaData metaData,
                                     final int row ) {
    final StyleDataBackend dataBackend1 = getDataBackend();
    final Object[] fullValues = dataBackend1.getFullValues();
    final Object o = fullValues[ row ];
    if ( o == StyleDataBackend.NULL_INDICATOR ) {
      return null;
    }
    if ( o != null ) {
      return o;
    }

    final ResolverStyleSheet styleSheet = dataBackend1.getResolvedStyle();
    final Object lastElement = styleSheet.getStyleProperty( metaData.getStyleKey() );
    if ( lastElement != null ) {
      fullValues[ row ] = lastElement;
    } else {
      fullValues[ row ] = StyleDataBackend.NULL_INDICATOR;
    }

    return lastElement;
  }

  public Class getClassForCell( final int rowIndex, final int columnIndex ) {
    final StyleMetaData metaData = getMetaData( rowIndex );
    if ( metaData == null ) {
      return GroupingHeader.class;
    }

    switch( columnIndex ) {
      case 0:
        return GroupedName.class;
      case 1:
        return Boolean.class;
      case 2:
        return metaData.getTargetType();
      default:
        throw new IndexOutOfBoundsException();
    }
  }

  public PropertyEditor getEditorForCell( final int rowIndex, final int columnIndex ) {
    final StyleMetaData metaData = getMetaData( rowIndex );
    if ( metaData == null ) {
      return null;
    }

    switch( columnIndex ) {
      case 0:
        return null;
      case 1:
        return null;
      case 2:
        return computeEditor( metaData, rowIndex );
      default:
        throw new IndexOutOfBoundsException();
    }
  }

  protected PropertyEditor computeEditor( final StyleMetaData metaData,
                                          final int row ) {
    final Object[] propertyEditors = getDataBackend().getPropertyEditors();
    final Object o = propertyEditors[ row ];
    if ( o == StyleDataBackend.NULL_INDICATOR ) {
      return null;
    }
    if ( o != null ) {
      return (PropertyEditor) o;
    }

    PropertyEditor propertyEditor = metaData.getEditor();
    if ( propertyEditor == null ) {
      propertyEditor = getDefaultEditor( metaData.getTargetType() );
    }
    if ( propertyEditor == null ) {
      propertyEditors[ row ] = StyleDataBackend.NULL_INDICATOR;
    } else {
      propertyEditors[ row ] = propertyEditor;
    }
    return propertyEditor;
  }

  protected PropertyEditor getDefaultEditor( final Class type ) {
    if ( String.class.equals( type ) ) {
      return null;
    }
    return FastPropertyEditorManager.findEditor( type );
  }

  public String getValueRole( final int row, final int column ) {
    return AttributeMetaData.VALUEROLE_VALUE;
  }

  public String[] getExtraFields( final int row, final int column ) {
    return EXTRA_FIELDS;
  }

  public GroupingHeader getGroupHeader( final int index ) {
    return getGroupings( index );
  }

  public boolean isHeaderRow( final int index ) {
    return dataBackend.getMetaData( index ) == null;
  }

}
