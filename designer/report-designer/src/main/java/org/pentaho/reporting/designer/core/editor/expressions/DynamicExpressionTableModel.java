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

import java.beans.PropertyEditor;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import org.pentaho.plugin.jfreereport.reportcharts.AbstractChartExpression;
import org.pentaho.plugin.jfreereport.reportcharts.ChartExpression;
import org.pentaho.reporting.designer.core.editor.ReportDocumentContext;
import org.pentaho.reporting.designer.core.util.FastPropertyEditorManager;
import org.pentaho.reporting.designer.core.util.exceptions.UncaughtExceptionsModel;
import org.pentaho.reporting.designer.core.util.table.GroupedName;
import org.pentaho.reporting.designer.core.util.table.GroupingHeader;
import org.pentaho.reporting.designer.core.util.undo.CompoundUndoEntry;
import org.pentaho.reporting.designer.core.util.undo.UndoEntry;
import org.pentaho.reporting.designer.core.util.undo.ExpressionPropertyChangeUndoEntry;
import org.pentaho.reporting.designer.core.util.undo.UndoManager;
import org.pentaho.reporting.engine.classic.core.AbstractReportDefinition;
import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.event.ReportModelEvent;
import org.pentaho.reporting.engine.classic.core.function.Expression;
import org.pentaho.reporting.engine.classic.core.function.FormulaExpression;
import org.pentaho.reporting.engine.classic.core.metadata.ExpressionPropertyMetaData;
import org.pentaho.reporting.engine.classic.core.util.beans.BeanException;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;

public class DynamicExpressionTableModel extends ExpressionPropertiesTableModel {

  public DynamicExpressionTableModel() {
    setFilterInlineExpressionProperty( true );
  }

  protected boolean isFiltered( final ExpressionPropertyMetaData metaData ) {
    if ( "linesDataSource".equals( metaData.getName() ) ) // NON-NLS
    {
      return true;
    }
    if ( "secondaryDataSet".equals( metaData.getName() ) ) // NON-NLS
    {
      return true;
    }
    if ( "dataSource".equals( metaData.getName() ) ) // NON-NLS
    {
      return true;
    }
    return super.isFiltered( metaData );
  }

  public int getColumnCount() {
    return 3;
  }

  public String getColumnName( final int column ) {
    switch( column ) {
      case 0:
        return super.getColumnName( column );
      case 1:
        return super.getColumnName( column );
      case 2:
        return EditorExpressionsMessages.getString( "ExpressionPropertiesTableModel.ExpressionColumn" );
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
        return super.getValueAt( rowIndex, columnIndex );
      case 1:
        return super.getValueAt( rowIndex, columnIndex );
      case 2:
        for ( int i = 0; i < getData().length; i++ ) {
          final Expression expression = getData()[ i ];

          if ( expression instanceof ChartExpression ) {
            AbstractChartExpression exp = ( AbstractChartExpression ) expression;
            LinkedHashMap<String, Expression> exprMap = ( LinkedHashMap<String, Expression> ) exp.getExpressionMap();
            if ( null != exprMap.get( metaData.getName() ) ) {
              FormulaExpression fe = ( FormulaExpression ) exprMap.get( metaData.getName() );
              return fe;
            }
          }
        }
        return null;
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
      case 2:
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
        super.setValueAt( aValue, rowIndex, columnIndex );
        break;
      }
      case 2: {
        if ( defineExpressionValue( metaData, aValue ) ) {
          if ( getActiveContext() != null ) {
            final AbstractReportDefinition abstractReportDefinition = getActiveContext().getReportDefinition();
            for ( int i = 0; i < getData().length; i++ ) {
              final Expression expression = getData()[ i ];
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

  private boolean defineExpressionValue( final ExpressionPropertyMetaData metaData,
          final Object value ) {
    boolean changed = false;
    FormulaExpression newExpression = ( FormulaExpression ) value;
    for ( int i = 0; i < getData().length; i++ ) {
      final Expression expression = getData()[ i ];
      AbstractChartExpression exp = ( AbstractChartExpression ) expression;
      FormulaExpression oldExpression = ( FormulaExpression ) exp.getExpressionMap().get( metaData.getName() );
      String oldFormulaExpression;
      if ( null == oldExpression ) {
        oldFormulaExpression = "";
      } else {
        oldFormulaExpression = oldExpression.getFormulaExpression();
      }
      String newFormulaExpression;
      if ( null == newExpression ) {
        newFormulaExpression = "";
      } else {
        newFormulaExpression = newExpression.getFormulaExpression();
      }
      if ( ( ObjectUtilities.equal( oldFormulaExpression, newFormulaExpression ) ) == false ) {
        changed = true;
      }
    }

    if ( changed ) {
      final ReportDocumentContext activeContext1 = getActiveContext();
      final ArrayList<UndoEntry> undos = new ArrayList<UndoEntry>();

      for ( int i = 0; i < getData().length; i++ ) {
        final Expression expression = getData()[ i ];
        final String name = metaData.getName();
        if ( activeContext1 != null ) {
          AbstractChartExpression exp = ( AbstractChartExpression ) expression;
          final Object oldValue = exp.getExpressionMap().get( name );
          undos.add( new ExpressionPropertyChangeUndoEntry( getData()[ i ], name, oldValue, value ) );
        }
        AbstractChartExpression exp = ( AbstractChartExpression ) expression;
        if ( null == value ) {
          exp.removeExpression( name );              
        } else {
          FormulaExpression fe = ( FormulaExpression ) value;
          exp.addExpression( metaData.getName(), fe );
        }
      }
      if ( activeContext1 != null ) {
        final UndoManager undo = activeContext1.getUndo();
        undo.addChange( EditorExpressionsMessages.getString( "ExpressionPropertiesTableModel.UndoName" ),
          new CompoundUndoEntry( (UndoEntry[]) undos.toArray( new UndoEntry[ undos.size() ] ) ) );
      }
    }

    return changed;
  }

  public PropertyEditor getEditorForCell( final int aRowIndex, final int aColumnIndex ) {
    final ExpressionPropertyMetaData metaData = getMetaData( aRowIndex );
    if ( metaData == null ) {
      // a header row
      return null;
    }

    switch( aColumnIndex ) {
      case 0:
        return null;
      case 1:
        return super.getEditorForCell( aRowIndex, aColumnIndex );
      case 2:
        return null;
      default:
        throw new IndexOutOfBoundsException();
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
        return super.getClassForCell( rowIndex, columnIndex );
      case 2:
        if ( metaData.isDesignTimeProperty() ) {
          // disables the expression-editor.
          return Object.class;
        }
        return Expression.class;
      default:
        throw new IndexOutOfBoundsException();
    }
  }
}
