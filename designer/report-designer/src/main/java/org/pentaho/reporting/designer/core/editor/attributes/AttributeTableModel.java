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

package org.pentaho.reporting.designer.core.editor.attributes;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.designer.core.Messages;
import org.pentaho.reporting.designer.core.editor.ReportDocumentContext;
import org.pentaho.reporting.designer.core.util.table.GroupedName;
import org.pentaho.reporting.designer.core.util.table.GroupingHeader;
import org.pentaho.reporting.designer.core.util.undo.AttributeEditUndoEntry;
import org.pentaho.reporting.designer.core.util.undo.CompoundUndoEntry;
import org.pentaho.reporting.designer.core.util.undo.UndoEntry;
import org.pentaho.reporting.designer.core.util.undo.UndoManager;
import org.pentaho.reporting.engine.classic.core.ReportElement;
import org.pentaho.reporting.engine.classic.core.metadata.AttributeMetaData;
import org.pentaho.reporting.engine.classic.core.metadata.ElementType;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;

import javax.swing.*;
import java.beans.PropertyEditor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AttributeTableModel extends AbstractAttributeTableModel {
  private static final Log logger = LogFactory.getLog( AttributeTableModel.class );

  private static final ReportElement[] EMPTY = new ReportElement[ 0 ];
  private static final Object[] EMPTY_VALUES = new Object[ 0 ];
  private static final Object NULL_INDICATOR = new Object();

  private static class AttributeDataBackend extends DataBackend {
    private ReportElement[] elements;
    private Object[] fullValues;
    private Object[] propertyEditors;

    private AttributeDataBackend() {
      elements = EMPTY;
      propertyEditors = EMPTY_VALUES;
      fullValues = EMPTY_VALUES;
    }

    private AttributeDataBackend( final AttributeMetaData[] metaData,
                                  final GroupingHeader[] groupings,
                                  final ReportElement[] elements ) {
      super( metaData, groupings );
      this.elements = elements;
      this.fullValues = new Object[ metaData.length ];
      this.propertyEditors = new Object[ metaData.length ];
    }

    public ReportElement[] getData() {
      return elements.clone();
    }

    public Object[] getFullValues() {
      return fullValues;
    }

    public Object[] getPropertyEditors() {
      return propertyEditors;
    }

    public void resetCache() {
      Arrays.fill( fullValues, null );
    }
  }

  private ExecutorService pool;
  private static final String[] EMPTY_FIELDS = new String[ 0 ];

  public AttributeTableModel() {
    pool = Executors.newSingleThreadExecutor();
    setDataBackend( new AttributeDataBackend() );
  }

  protected AttributeDataBackend getAttributeDataBackend() {
    return (AttributeDataBackend) getDataBackend();
  }

  public ReportElement[] getData() {
    return getAttributeDataBackend().getData();
  }

  public void setData( final ReportElement[] elements ) {
    // thats fast, we only compare the ids ..
    if ( isSameElements( elements, getData(), null ) ) {
      SwingUtilities.invokeLater( new SameElementsUpdateDataTask( getDataBackend() ) );
      return;
    }

    pool.submit( new UpdateDataTask( elements ) );
  }

  protected void refreshData() {
    final ReportElement[] data = getAttributeDataBackend().getData();
    setDataBackend( updateData( data ) );
  }

  protected DataBackend createDataBackend( final GroupingHeader[] headers,
                                           final AttributeMetaData[] metaData,
                                           final ReportElement[] elements,
                                           final ElementType[] elementTypes ) {
    super.createDataBackend( headers, metaData, elements, elementTypes );
    return new AttributeDataBackend( metaData, headers, elements );
  }

  public int getColumnCount() {
    return 2;
  }

  public Object getValueAt( final int rowIndex, final int columnIndex ) {
    final AttributeMetaData metaData = getMetaData( rowIndex );
    if ( metaData == null ) {
      return getGroupings( rowIndex );
    }

    switch( columnIndex ) {
      case 0:
        return new GroupedName( metaData );
      case 1:
        return computeFullValue( metaData, rowIndex );
      default:
        throw new IndexOutOfBoundsException();
    }
  }

  public boolean isCellEditable( final int rowIndex, final int columnIndex ) {
    final AttributeMetaData metaData = getMetaData( rowIndex );
    if ( metaData == null ) {
      return false;
    }

    switch( columnIndex ) {
      case 0:
        return false;
      case 1:
        return "ElementType".equals( metaData.getValueRole() ) == false; // NON-NLS
      default:
        throw new IndexOutOfBoundsException();
    }
  }

  public String getValueRole( final int row, final int column ) {
    if ( column != 1 ) {
      return null;
    }

    final AttributeMetaData metaData = getMetaData( row );
    if ( metaData == null ) {
      return null;
    }
    return metaData.getValueRole();
  }

  public String[] getExtraFields( final int row, final int column ) {
    if ( column == 0 ) {
      return EMPTY_FIELDS;
    }

    final AttributeMetaData metaData = getMetaData( row );
    if ( metaData == null ) {
      return EMPTY_FIELDS;
    }
    return metaData.getExtraCalculationFields();
  }

  public void setValueAt( final Object aValue, final int rowIndex, final int columnIndex ) {
    final AttributeMetaData metaData = getMetaData( rowIndex );
    if ( metaData == null ) {
      return;
    }

    switch( columnIndex ) {
      case 0:
        return;
      case 1: {
        if ( defineFullValue( metaData, aValue ) ) {
          final AttributeDataBackend db = (AttributeDataBackend) getDataBackend();
          db.fullValues[ rowIndex ] = null;
          fireTableDataChanged();
        }
        break;
      }
      default:
        throw new IndexOutOfBoundsException();
    }

  }

  private boolean defineFullValue( final AttributeMetaData metaData,
                                   final Object value ) {
    if ( value != null && metaData.getTargetType().isInstance( value ) == false ) {
      // not the correct type
      logger.warn( "Invalid type: " + value + " but expected " + metaData.getTargetType() ); // NON-NLS
      return false;
    }

    final ReportDocumentContext reportRenderContext = getReportRenderContext();
    if ( reportRenderContext == null ) {
      throw new IllegalStateException( "No report render context? Thats bad." );
    }
    final UndoManager undo = reportRenderContext.getUndo();

    boolean changed = false;
    final ReportElement[] elements = getAttributeDataBackend().getData();
    final ArrayList<UndoEntry> undos = new ArrayList<UndoEntry>();
    for ( int i = 0; i < elements.length; i++ ) {
      final ReportElement element = elements[ i ];
      final Object attribute = element.getAttribute( metaData.getNameSpace(), metaData.getName() );
      if ( ( ObjectUtilities.equal( attribute, value ) ) == false ) {
        undos.add( new AttributeEditUndoEntry
          ( element.getObjectID(), metaData.getNameSpace(), metaData.getName(), attribute, value ) );
        element.setAttribute( metaData.getNameSpace(), metaData.getName(), value );
        changed = true;
      }
    }
    undo.addChange( Messages.getString( "AttributeTableModel.UndoName" ),
      new CompoundUndoEntry( (UndoEntry[]) undos.toArray( new UndoEntry[ undos.size() ] ) ) );

    return changed;
  }

  private Object computeFullValue( final AttributeMetaData metaData, final int row ) {
    final AttributeDataBackend dataBackend = getAttributeDataBackend();
    final Object[] fullValues = dataBackend.getFullValues();
    final Object o = fullValues[ row ];
    if ( o == NULL_INDICATOR ) {
      return null;
    }
    if ( o != null ) {
      return o;
    }

    Object lastElement = null;
    final ReportElement[] elements = dataBackend.getData();
    if ( elements.length > 0 ) {
      final ReportElement element = elements[ 0 ];
      lastElement = element.getAttribute( metaData.getNameSpace(), metaData.getName() );
    }
    if ( lastElement != null ) {
      fullValues[ row ] = lastElement;
    } else {
      fullValues[ row ] = NULL_INDICATOR;
    }
    return lastElement;
  }

  public Class getClassForCell( final int rowIndex, final int columnIndex ) {
    final AttributeMetaData metaData = getMetaData( rowIndex );
    if ( metaData == null ) {
      return GroupingHeader.class;
    }

    switch( columnIndex ) {
      case 0:
        return GroupedName.class;
      case 1:
        return metaData.getTargetType();
      default:
        throw new IndexOutOfBoundsException();
    }
  }

  public PropertyEditor getEditorForCell( final int rowIndex, final int columnIndex ) {
    final AttributeMetaData metaData = getMetaData( rowIndex );
    if ( metaData == null ) {
      return null;
    }

    switch( columnIndex ) {
      case 0:
        return null;
      case 1:
        return computeEditor( metaData, rowIndex );
      default:
        throw new IndexOutOfBoundsException();
    }
  }

  private PropertyEditor computeEditor( final AttributeMetaData metaData, final int row ) {
    final Object[] propertyEditors = getAttributeDataBackend().getPropertyEditors();

    final Object o = propertyEditors[ row ];
    if ( o == NULL_INDICATOR ) {
      return null;
    }
    if ( o != null ) {
      return (PropertyEditor) o;
    }
    PropertyEditor propertyEditor = metaData.getEditor();
    if ( propertyEditor == null ) {
      propertyEditor = getDefaultEditor( metaData.getTargetType(), metaData.getValueRole() );
    }
    if ( propertyEditor == null ) {
      propertyEditors[ row ] = NULL_INDICATOR;
    } else {
      propertyEditors[ row ] = propertyEditor;
    }
    return propertyEditor;
  }

  public String getColumnName( final int column ) {
    switch( column ) {
      case 0:
        return Messages.getString( "AttributeTableModel.NameColumn" );
      case 1:
        return Messages.getString( "AttributeTableModel.ValueColumn" );
      default:
        throw new IllegalArgumentException();
    }
  }
}
