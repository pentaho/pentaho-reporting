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
import org.pentaho.reporting.designer.core.util.table.GroupingModel;
import org.pentaho.reporting.designer.core.util.undo.AttributeEditUndoEntry;
import org.pentaho.reporting.designer.core.util.undo.AttributeExpressionEditUndoEntry;
import org.pentaho.reporting.designer.core.util.undo.CompoundUndoEntry;
import org.pentaho.reporting.designer.core.util.undo.UndoEntry;
import org.pentaho.reporting.designer.core.util.undo.UndoManager;
import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.ReportElement;
import org.pentaho.reporting.engine.classic.core.function.Expression;
import org.pentaho.reporting.engine.classic.core.metadata.AttributeMetaData;
import org.pentaho.reporting.engine.classic.core.metadata.ElementType;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;

import javax.swing.*;
import java.beans.PropertyEditor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Todo: Document me!
 *
 * @author Thomas Morgner
 */
public class VisualAttributeTableModel extends AbstractAttributeTableModel implements GroupingModel {
  private static final Log logger = LogFactory.getLog( VisualAttributeTableModel.class );

  protected static final Object[] EMPTY_VALUES = new Object[ 0 ];
  protected static final Object NULL_INDICATOR = new Object();
  protected static final Element[] EMPTY_ELEMENTS = new Element[ 0 ];
  protected static final ElementType[] EMPTY_ELEMENT_TYPES = new ElementType[ 0 ];
  private static final String[] EMPTY_FIELDS = new String[ 0 ];

  private static class AttributeDataBackend extends DataBackend {
    private Element[] elements;
    private Object[] fullValues;
    private Object[] propertyEditors;
    private Object[] expressionValues;
    private ElementType[] elementTypes;

    private AttributeDataBackend() {
      elements = EMPTY_ELEMENTS;
      propertyEditors = EMPTY_VALUES;
      fullValues = EMPTY_VALUES;
      expressionValues = EMPTY_VALUES;
      elementTypes = EMPTY_ELEMENT_TYPES;
    }

    private AttributeDataBackend( final AttributeMetaData[] metaData,
                                  final GroupingHeader[] groupings,
                                  final Element[] elements,
                                  final ElementType[] elementTypes ) {
      super( metaData, groupings );
      if ( elements == null ) {
        throw new NullPointerException();
      }
      if ( elementTypes == null ) {
        throw new NullPointerException();
      }
      this.elements = elements;
      this.fullValues = new Object[ metaData.length ];
      this.propertyEditors = new Object[ metaData.length ];
      this.expressionValues = new Object[ metaData.length ];
      this.elementTypes = elementTypes;
    }

    public Object[] getExpressionValues() {
      return expressionValues;
    }

    public Element[] getData() {
      return elements.clone();
    }

    public Object[] getFullValues() {
      return fullValues;
    }

    public void resetCache() {
      Arrays.fill( fullValues, null );
      Arrays.fill( expressionValues, null );
    }

    public Object[] getPropertyEditors() {
      return propertyEditors;
    }

    public ElementType[] getElementTypes() {
      return elementTypes.clone();
    }
  }

  private ExecutorService pool;

  public VisualAttributeTableModel() {
    setDataBackend( new AttributeDataBackend() );
    pool = Executors.newSingleThreadExecutor();
  }

  public void setData( final Element[] elements ) {
    if ( isSameElements( elements, getData(), getElementTypes() ) ) {
      SwingUtilities.invokeLater( new SameElementsUpdateDataTask( getDataBackend() ) );
      return;
    }

    pool.submit( new UpdateDataTask( elements ) );
  }

  protected DataBackend createDataBackend( final GroupingHeader[] headers,
                                           final AttributeMetaData[] metaData,
                                           final ReportElement[] elements,
                                           final ElementType[] elementTypes ) {
    super.createDataBackend( headers, metaData, elements, elementTypes );
    return new AttributeDataBackend( metaData, headers, (Element[]) elements, elementTypes );
  }

  protected void refreshData() {
    pool.submit( new UpdateDataTask( getAttributeDataBackend().getData() ) );
  }


  protected AttributeDataBackend getAttributeDataBackend() {
    return (AttributeDataBackend) getDataBackend();
  }

  public Element[] getData() {
    return getAttributeDataBackend().getData();
  }

  public ElementType[] getElementTypes() {
    return getAttributeDataBackend().getElementTypes();
  }

  public int getColumnCount() {
    return 3;
  }

  public String getColumnName( final int column ) {
    switch( column ) {
      case 0:
        return Messages.getString( "VisualAttributeTableModel.NameColumn" );
      case 1:
        return Messages.getString( "VisualAttributeTableModel.ValueColumn" );
      case 2:
        return Messages.getString( "VisualAttributeTableModel.FormulaColumn" );
      default:
        throw new IllegalArgumentException();
    }
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
      case 2:
        return computeExpressionValue( metaData, rowIndex );
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
        return "ElementType".equals( metaData.getValueRole() ) == false; // $NON-NLS$
      case 2: {
        if ( "ElementType".equals( metaData.getValueRole() ) )// $NON-NLS$
        {
          return false;
        }
        if ( metaData.isDesignTimeValue() ) {
          return false;
        }
        return true;
      }
      default:
        throw new IndexOutOfBoundsException();
    }
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
      case 2: {
        if ( aValue != null && aValue instanceof Expression == false ) {
          return;
        }
        if ( defineExpressionValue( metaData, (Expression) aValue ) ) {
          final AttributeDataBackend db = (AttributeDataBackend) getDataBackend();
          db.expressionValues[ rowIndex ] = null;
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
      logger.warn( "Invalid type: " + value + " but expected " + metaData.getTargetType() );// $NON-NLS$
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
    undo.addChange( Messages.getString( "VisualAttributeTableModel.UndoName" ),
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


  private boolean defineExpressionValue( final AttributeMetaData metaData,
                                         final Expression value ) {
    boolean changed = false;
    final Element[] elements = getAttributeDataBackend().getData();
    for ( int i = 0; i < elements.length; i++ ) {
      final Element element = elements[ i ];
      final Expression attribute = element.getAttributeExpression
        ( metaData.getNameSpace(), metaData.getName() );
      if ( ( ObjectUtilities.equal( attribute, value ) ) == false ) {
        changed = true;
      }
    }

    if ( changed ) {
      final ReportDocumentContext reportRenderContext = getReportRenderContext();
      if ( reportRenderContext == null ) {
        throw new IllegalStateException( "No report render context? Thats bad." );
      }
      final UndoManager undo = reportRenderContext.getUndo();

      final ArrayList<UndoEntry> undos = new ArrayList<UndoEntry>();
      for ( int i = 0; i < elements.length; i++ ) {
        final Element element = elements[ i ];
        final Expression attribute = element.getAttributeExpression
          ( metaData.getNameSpace(), metaData.getName() );
        if ( value != null ) {
          final Expression expression = value.getInstance();
          undos.add( new AttributeExpressionEditUndoEntry
            ( element.getObjectID(), metaData.getNameSpace(), metaData.getName(), attribute, expression ) );
          element.setAttributeExpression( metaData.getNameSpace(), metaData.getName(), expression );
        } else {
          undos.add( new AttributeExpressionEditUndoEntry
            ( element.getObjectID(), metaData.getNameSpace(), metaData.getName(), attribute, null ) );
          element.setAttributeExpression( metaData.getNameSpace(), metaData.getName(), null );
        }
      }
      undo.addChange( Messages.getString( "VisualAttributeTableModel.UndoNameExpression" ),
        new CompoundUndoEntry( (UndoEntry[]) undos.toArray( new UndoEntry[ undos.size() ] ) ) );

    }
    return changed;
  }

  private Expression computeExpressionValue( final AttributeMetaData metaData,
                                             final int row ) {
    final AttributeDataBackend dataBackend1 = getAttributeDataBackend();
    final Object[] expressionValues = dataBackend1.getExpressionValues();

    final Object o = expressionValues[ row ];
    if ( o == NULL_INDICATOR ) {
      return null;
    }
    if ( o != null ) {
      return (Expression) o;
    }

    if ( metaData.isDesignTimeValue() ) {
      expressionValues[ row ] = NULL_INDICATOR;
      return null;
    }

    Expression lastElement = null;
    final Element[] elements = dataBackend1.getData();
    if ( elements.length > 0 ) {
      final Element element = elements[ 0 ];
      lastElement = element.getAttributeExpression( metaData.getNameSpace(), metaData.getName() );
    }
    if ( lastElement != null ) {
      expressionValues[ row ] = lastElement;
    } else {
      expressionValues[ row ] = NULL_INDICATOR;
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
      case 2:
        if ( metaData.isDesignTimeValue() ) {
          // disables the expression-editor.
          return Object.class;
        }
        return Expression.class;
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
      case 2:
        return null;
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

  public GroupingHeader getGroupHeader( final int index ) {
    return getGroupings( index );
  }

  public boolean isHeaderRow( final int index ) {
    return getDataBackend().getMetaData( index ) == null;
  }

}
