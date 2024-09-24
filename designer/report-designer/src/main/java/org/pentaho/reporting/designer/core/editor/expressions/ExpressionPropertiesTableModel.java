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

package org.pentaho.reporting.designer.core.editor.expressions;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.designer.core.editor.ReportDocumentContext;
import org.pentaho.reporting.designer.core.settings.WorkspaceSettings;
import org.pentaho.reporting.designer.core.util.FastPropertyEditorManager;
import org.pentaho.reporting.designer.core.util.exceptions.UncaughtExceptionsModel;
import org.pentaho.reporting.designer.core.util.table.ElementMetaDataTableModel;
import org.pentaho.reporting.designer.core.util.table.GroupedName;
import org.pentaho.reporting.designer.core.util.table.GroupingHeader;
import org.pentaho.reporting.designer.core.util.table.GroupingModel;
import org.pentaho.reporting.designer.core.util.table.TableStyle;
import org.pentaho.reporting.designer.core.util.undo.CompoundUndoEntry;
import org.pentaho.reporting.designer.core.util.undo.ExpressionPropertyChangeUndoEntry;
import org.pentaho.reporting.designer.core.util.undo.UndoEntry;
import org.pentaho.reporting.designer.core.util.undo.UndoManager;
import org.pentaho.reporting.engine.classic.core.AbstractReportDefinition;
import org.pentaho.reporting.engine.classic.core.event.ReportModelEvent;
import org.pentaho.reporting.engine.classic.core.function.Expression;
import org.pentaho.reporting.engine.classic.core.metadata.ExpressionMetaData;
import org.pentaho.reporting.engine.classic.core.metadata.ExpressionPropertyMetaData;
import org.pentaho.reporting.engine.classic.core.metadata.ExpressionRegistry;
import org.pentaho.reporting.engine.classic.core.metadata.GroupedMetaDataComparator;
import org.pentaho.reporting.engine.classic.core.metadata.PlainMetaDataComparator;
import org.pentaho.reporting.engine.classic.core.util.beans.BeanException;
import org.pentaho.reporting.engine.classic.core.util.beans.BeanUtility;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;

import javax.swing.table.AbstractTableModel;
import java.beans.IntrospectionException;
import java.beans.PropertyEditor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;

public class ExpressionPropertiesTableModel
  extends AbstractTableModel implements ElementMetaDataTableModel, GroupingModel {
  private static final Log logger = LogFactory.getLog( ExpressionPropertiesTableModel.class );

  private static final Expression[] EMPTY_ELEMENTS = new Expression[ 0 ];
  private static final ExpressionPropertyMetaData[] EMPTY_METADATA = new ExpressionPropertyMetaData[ 0 ];
  private static final GroupingHeader[] EMPTY_GROUPINGS = new GroupingHeader[ 0 ];
  private static final BeanUtility[] EMPTY_EDITORS = new BeanUtility[ 0 ];

  private ExpressionPropertyMetaData[] metaData;
  private GroupingHeader[] groupings;
  private TableStyle tableStyle;
  private Expression[] elements;
  private BeanUtility[] editors;
  private ReportDocumentContext activeContext;
  private boolean filterInlineExpressionProperty;

  public ExpressionPropertiesTableModel() {
    tableStyle = TableStyle.GROUPED;
    this.elements = EMPTY_ELEMENTS;
    this.metaData = EMPTY_METADATA;
    this.groupings = EMPTY_GROUPINGS;
    this.editors = EMPTY_EDITORS;
  }

  public boolean isFilterInlineExpressionProperty() {
    return filterInlineExpressionProperty;
  }

  public void setFilterInlineExpressionProperty( final boolean filterInlineExpressionProperty ) {
    this.filterInlineExpressionProperty = filterInlineExpressionProperty;
  }

  public ReportDocumentContext getActiveContext() {
    return activeContext;
  }

  public void setActiveContext( final ReportDocumentContext activeContext ) {
    this.activeContext = activeContext;
  }

  public int getRowCount() {
    return metaData.length;
  }

  protected ExpressionPropertyMetaData getMetaData( final int row ) {
    return metaData[ row ];
  }

  protected GroupingHeader getGroupings( final int row ) {
    return groupings[ row ];
  }

  public TableStyle getTableStyle() {
    return tableStyle;
  }

  public void setTableStyle( final TableStyle tableStyle ) {
    if ( tableStyle == null ) {
      throw new NullPointerException();
    }
    this.tableStyle = tableStyle;
    try {
      updateData( getData() );
    } catch ( IntrospectionException e ) {
      UncaughtExceptionsModel.getInstance().addException( e );
      try {
        updateData( EMPTY_ELEMENTS );
      } catch ( IntrospectionException e1 ) {
        // now this cannot happen ..
        UncaughtExceptionsModel.getInstance().addException( e );
      }
    }
  }

  protected void updateData( final Expression[] elements ) throws IntrospectionException {
    this.elements = elements.clone();
    this.editors = new BeanUtility[ elements.length ];
    for ( int i = 0; i < elements.length; i++ ) {
      this.editors[ i ] = new BeanUtility( elements[ i ] );
    }

    final ExpressionPropertyMetaData[] metaData = selectCommonAttributes();
    if ( tableStyle == TableStyle.ASCENDING ) {
      Arrays.sort( metaData, new PlainMetaDataComparator() );
      this.groupings = new GroupingHeader[ metaData.length ];
      this.metaData = metaData;
    } else if ( tableStyle == TableStyle.DESCENDING ) {
      Arrays.sort( metaData, Collections.reverseOrder( new PlainMetaDataComparator() ) );
      this.groupings = new GroupingHeader[ metaData.length ];
      this.metaData = metaData;
    } else {
      Arrays.sort( metaData, new GroupedMetaDataComparator() );

      int groupCount = 0;
      final Locale locale = Locale.getDefault();
      if ( metaData.length > 0 ) {
        String oldValue = null;

        for ( int i = 0; i < metaData.length; i++ ) {
          if ( groupCount == 0 ) {
            groupCount = 1;
            final ExpressionPropertyMetaData firstdata = metaData[ i ];
            oldValue = firstdata.getGrouping( locale );
            continue;
          }

          final ExpressionPropertyMetaData data = metaData[ i ];
          final String grouping = data.getGrouping( locale );
          if ( ( ObjectUtilities.equal( oldValue, grouping ) ) == false ) {
            oldValue = grouping;
            groupCount += 1;
          }
        }
      }

      final ExpressionPropertyMetaData[] groupedMetaData =
        new ExpressionPropertyMetaData[ metaData.length + groupCount ];
      this.groupings = new GroupingHeader[ groupedMetaData.length ];
      int targetIdx = 0;
      GroupingHeader group = null;
      for ( int sourceIdx = 0; sourceIdx < metaData.length; sourceIdx++ ) {
        final ExpressionPropertyMetaData data = metaData[ sourceIdx ];
        if ( sourceIdx == 0 ) {
          group = new GroupingHeader( data.getGrouping( locale ) );
          groupings[ targetIdx ] = group;
          targetIdx += 1;
        } else {
          final String newgroup = data.getGrouping( locale );
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

      this.metaData = groupedMetaData;
    }

    fireTableDataChanged();
  }

  protected boolean isFiltered( final ExpressionPropertyMetaData metaData ) {
    if ( metaData.isHidden() ) {
      return true;
    }
    if ( !WorkspaceSettings.getInstance().isVisible( metaData ) ) {
      return true;
    }
    if ( isFilterInlineExpressionProperty() == false ) {
      return false;
    }

    if ( "name".equals( metaData.getName() ) ) // NON-NLS
    {
      return true;
    }
    if ( "dependencyLevel".equals( metaData.getName() ) ) // NON-NLS
    {
      return true;
    }
    return false;
  }

  protected ExpressionPropertyMetaData[] selectCommonAttributes() {
    final HashMap<String, Boolean> attributes = new HashMap<String, Boolean>();
    final ArrayList<ExpressionPropertyMetaData> selectedArrays = new ArrayList<ExpressionPropertyMetaData>();
    for ( int elementIdx = 0; elementIdx < elements.length; elementIdx++ ) {
      final Expression element = elements[ elementIdx ];
      final String key = element.getClass().getName();
      if ( ExpressionRegistry.getInstance().isExpressionRegistered( key ) == false ) {
        // we cannot even attempt to edit such unknown expressions.
        return new ExpressionPropertyMetaData[ 0 ];
      }

      final ExpressionMetaData metaData =
        ExpressionRegistry.getInstance().getExpressionMetaData( key );

      final ExpressionPropertyMetaData[] datas = metaData.getPropertyDescriptions();
      for ( int styleIdx = 0; styleIdx < datas.length; styleIdx++ ) {
        final ExpressionPropertyMetaData data = datas[ styleIdx ];
        if ( isFiltered( data ) ) {
          continue;
        }

        final String name = data.getName();
        final Object attribute = attributes.get( name );
        if ( Boolean.TRUE.equals( attribute ) ) {
          // fine, we already have a value for it.
        } else if ( attribute == null ) {
          // add it ..
          if ( elementIdx == 0 ) {
            selectedArrays.add( data );
            attributes.put( name, Boolean.TRUE );
          } else {
            attributes.put( name, Boolean.FALSE );
          }
        }

      }
    }

    return selectedArrays.toArray( new ExpressionPropertyMetaData[ selectedArrays.size() ] );
  }


  public void setData( final Expression[] elements ) {
    try {
      updateData( elements );
    } catch ( Exception e ) {
      UncaughtExceptionsModel.getInstance().addException( e );
      try {
        updateData( EMPTY_ELEMENTS );
      } catch ( IntrospectionException e1 ) {
        // this time it will not happen.
      }
    }
  }

  public Expression[] getData() {
    return elements.clone();
  }

  public int getColumnCount() {
    return 2;
  }

  public String getColumnName( final int column ) {
    switch( column ) {
      case 0:
        return EditorExpressionsMessages.getString( "ExpressionPropertiesTableModel.NameColumn" );
      case 1:
        return EditorExpressionsMessages.getString( "ExpressionPropertiesTableModel.ValueColumn" );
      default:
        throw new IllegalArgumentException();
    }
  }

  public Object getValueAt( final int rowIndex, final int columnIndex ) {
    final ExpressionPropertyMetaData metaData = getMetaData( rowIndex );
    if ( metaData == null ) {
      return getGroupings( rowIndex );
    }

    switch( columnIndex ) {
      case 0:
        return new GroupedName( metaData );
      case 1:
        return computeFullValue( metaData );
      default:
        throw new IndexOutOfBoundsException();
    }
  }

  public boolean isCellEditable( final int rowIndex, final int columnIndex ) {
    final ExpressionPropertyMetaData metaData = getMetaData( rowIndex );
    if ( metaData == null ) {
      return false;
    }

    switch( columnIndex ) {
      case 0:
        return false;
      case 1:
        return true;
      default:
        throw new IndexOutOfBoundsException();
    }
  }


  public void setValueAt( final Object aValue, final int rowIndex, final int columnIndex ) {
    final ExpressionPropertyMetaData metaData = getMetaData( rowIndex );
    if ( metaData == null ) {
      return;
    }

    switch( columnIndex ) {
      case 0:
        return;
      case 1: {
        if ( defineFullValue( metaData, aValue ) ) {
          if ( activeContext != null ) {
            final AbstractReportDefinition abstractReportDefinition = activeContext.getReportDefinition();
            for ( int i = 0; i < elements.length; i++ ) {
              final Expression expression = elements[ i ];
              abstractReportDefinition.fireModelLayoutChanged
                ( abstractReportDefinition, ReportModelEvent.NODE_PROPERTIES_CHANGED, expression );
            }
          }
          fireTableDataChanged();
        }
        break;
      }
      default:
        throw new IndexOutOfBoundsException();
    }

  }

  private boolean defineFullValue( final ExpressionPropertyMetaData metaData,
                                   final Object value ) {
    boolean changed = false;
    try {
      for ( int i = 0; i < editors.length; i++ ) {
        final BeanUtility element = editors[ i ];
        final Object attribute = element.getProperty( metaData.getName() );
        if ( ( ObjectUtilities.equal( attribute, value ) ) == false ) {
          changed = true;
        }
      }

      if ( changed ) {
        final ReportDocumentContext activeContext1 = getActiveContext();
        final ArrayList<UndoEntry> undos = new ArrayList<UndoEntry>();

        for ( int i = 0; i < elements.length; i++ ) {
          final BeanUtility element = editors[ i ];
          final String name = metaData.getName();
          if ( activeContext1 != null ) {
            final Object oldValue = element.getProperty( name );
            undos.add( new ExpressionPropertyChangeUndoEntry( elements[ i ], name, oldValue, value ) );
          }
          element.setProperty( name, value );
        }
        if ( activeContext1 != null ) {
          final UndoManager undo = activeContext1.getUndo();
          undo.addChange( EditorExpressionsMessages.getString( "ExpressionPropertiesTableModel.UndoName" ),
            new CompoundUndoEntry( (UndoEntry[]) undos.toArray( new UndoEntry[ undos.size() ] ) ) );
        }
      }
    } catch ( BeanException e ) {
      UncaughtExceptionsModel.getInstance().addException( e );
    }

    return changed;
  }

  private Object computeFullValue( final ExpressionPropertyMetaData metaData ) {
    try {
      Object lastElement = null;
      if ( elements.length > 0 ) {
        final BeanUtility element = editors[ 0 ];
        lastElement = element.getProperty( metaData.getName() );
      }
      return lastElement;
    } catch ( BeanException e ) {
      UncaughtExceptionsModel.getInstance().addException( e );
      return null;
    }
  }

  public Class getClassForCell( final int rowIndex, final int columnIndex ) {
    final ExpressionPropertyMetaData metaData = getMetaData( rowIndex );
    if ( metaData == null ) {
      return GroupingHeader.class;
    }

    switch( columnIndex ) {
      case 0:
        return GroupedName.class;
      case 1:
        return metaData.getPropertyType();
      default:
        throw new IndexOutOfBoundsException();
    }
  }

  public PropertyEditor getEditorForCell( final int aRowIndex, final int aColumnIndex ) {
    final ExpressionPropertyMetaData metaData = getMetaData( aRowIndex );
    if ( metaData == null ) {
      // a header row
      return null;
    }

    try {
      switch( aColumnIndex ) {
        case 0:
          return null;
        case 1:
          final PropertyEditor editor = metaData.getEditor();
          if ( editor != null ) {
            return editor;
          }

          final Class editorClass = metaData.getBeanDescriptor().getPropertyEditorClass();
          if ( editorClass != null ) {
            return (PropertyEditor) editorClass.newInstance();
          }

          if ( String.class.equals( metaData.getPropertyType() ) ) {
            return null;
          }

          return FastPropertyEditorManager.findEditor( metaData.getPropertyType() );
        default:
          throw new IndexOutOfBoundsException();
      }
    } catch ( Exception e ) {
      if ( logger.isTraceEnabled() ) {
        logger.trace( "Failed to create property-editor", e ); // NON-NLS
      }
      return null;
    }
  }

  public String getValueRole( final int row, final int column ) {
    if ( column != 1 ) {
      return null;
    }
    final ExpressionPropertyMetaData metaData = getMetaData( row );
    if ( metaData == null ) {
      return null;
    }
    return metaData.getPropertyRole();
  }

  public String[] getExtraFields( final int row, final int column ) {
    if ( column == 0 ) {
      return null;
    }
    final ExpressionPropertyMetaData metaData = getMetaData( row );
    if ( metaData == null ) {
      return null;
    }
    return metaData.getExtraCalculationFields();
  }

  public GroupingHeader getGroupHeader( final int index ) {
    return getGroupings( index );
  }

  public boolean isHeaderRow( final int index ) {
    return metaData[ index ] == null;
  }
}
