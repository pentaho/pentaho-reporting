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
import org.pentaho.reporting.designer.core.editor.ReportDocumentContext;
import org.pentaho.reporting.designer.core.settings.WorkspaceSettings;
import org.pentaho.reporting.designer.core.util.exceptions.UncaughtExceptionsModel;
import org.pentaho.reporting.designer.core.util.table.GroupedName;
import org.pentaho.reporting.designer.core.util.table.GroupingHeader;
import org.pentaho.reporting.designer.core.util.table.TableStyle;
import org.pentaho.reporting.designer.core.util.undo.CompoundUndoEntry;
import org.pentaho.reporting.designer.core.util.undo.StyleEditUndoEntry;
import org.pentaho.reporting.designer.core.util.undo.StyleExpressionEditUndoEntry;
import org.pentaho.reporting.designer.core.util.undo.UndoEntry;
import org.pentaho.reporting.designer.core.util.undo.UndoManager;
import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.function.Expression;
import org.pentaho.reporting.engine.classic.core.metadata.ElementType;
import org.pentaho.reporting.engine.classic.core.metadata.GroupedMetaDataComparator;
import org.pentaho.reporting.engine.classic.core.metadata.PlainMetaDataComparator;
import org.pentaho.reporting.engine.classic.core.metadata.StyleMetaData;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleSheet;
import org.pentaho.reporting.engine.classic.core.style.ResolverStyleSheet;
import org.pentaho.reporting.engine.classic.core.style.resolver.SimpleStyleResolver;
import org.pentaho.reporting.engine.classic.core.style.resolver.StyleResolver;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;

import javax.swing.*;
import java.beans.PropertyEditor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class StyleTableModel extends AbstractStyleTableModel<StyleTableModel.DefaultStyleDataBackend> {
  private static final Log logger = LogFactory.getLog( StyleTableModel.class );

  private static final Object NULL_INDICATOR = new Object();
  private static final Element[] EMPTY_ELEMENTS = new Element[ 0 ];
  private static final ElementType[] EMPTY_ELEMENT_TYPES = new ElementType[ 0 ];

  private static final Object[] EMPTY_VALUES = new Object[ 0 ];

  protected static class DefaultStyleDataBackend extends AbstractStyleDataBackend {
    private Element[] elements;
    private ElementType[] elementTypes;
    private Object[] inheritValues;
    private Object[] expressionValues;

    private DefaultStyleDataBackend() {
      this.elements = EMPTY_ELEMENTS;
      this.elementTypes = EMPTY_ELEMENT_TYPES;
      this.inheritValues = EMPTY_VALUES;
      this.expressionValues = EMPTY_VALUES;
    }

    private DefaultStyleDataBackend( final StyleMetaData[] metaData,
                                     final GroupingHeader[] groupings,
                                     final Element[] elements ) {
      super( metaData, groupings );
      this.elements = elements;
      this.elementTypes = new ElementType[ elements.length ];

      for ( int i = 0; i < elements.length; i++ ) {
        final Element element = elements[ i ];
        elementTypes[ i ] = element.getElementType();
      }

      final ResolverStyleSheet resolverStyleSheet = getResolvedStyle();
      if ( elements.length > 0 ) {
        final StyleResolver resolver = new SimpleStyleResolver( true );
        resolver.resolve( elements[ 0 ], resolverStyleSheet );
      }

      this.inheritValues = new Object[ metaData.length ];
      this.expressionValues = new Object[ metaData.length ];
    }

    public void clearCache( final int rowIndex ) {
      super.clearCache( rowIndex );
      inheritValues[ rowIndex ] = null;
    }

    public void resetCache() {
      super.resetCache();
      Arrays.fill( inheritValues, null );
      Arrays.fill( expressionValues, null );

      final ResolverStyleSheet resolverStyleSheet = getResolvedStyle();
      if ( elements.length > 0 ) {
        final StyleResolver resolver = new SimpleStyleResolver( true );
        resolver.resolve( elements[ 0 ], resolverStyleSheet );
      } else {
        resolverStyleSheet.clear();
      }
    }

    public Element[] getData() {
      return elements;
    }

    public void clearExpressionsCache( final int rowIndex ) {
      expressionValues[ rowIndex ] = null;
    }

    public Object[] getInheritValues() {
      return inheritValues;
    }

    public Object[] getExpressionValues() {
      return expressionValues;
    }

    public ElementType[] getElementTypes() {
      return elementTypes;
    }
  }

  private class UpdateDataTask implements Runnable {
    private Element[] elements;
    private boolean synchronous;

    private UpdateDataTask( final Element[] elements,
                            final boolean synchronous ) {
      this.synchronous = synchronous;
      this.elements = elements.clone();
    }

    public void run() {
      try {
        final DefaultStyleDataBackend dataBackend = updateData( elements );
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

  private Executor pool;
  private DefaultStyleDataBackend oldDataBackend;
  private ReportDocumentContext reportRenderContext;

  public StyleTableModel() {
    this( Executors.newSingleThreadExecutor() );
  }

  public StyleTableModel( final Executor pool ) {
    if ( pool == null ) {
      throw new NullPointerException();
    }
    this.pool = pool;
    super.setDataBackend( new DefaultStyleDataBackend() );

  }

  public void setTableStyle( final TableStyle tableStyle ) {
    super.setTableStyle( tableStyle );
    pool.execute( new UpdateDataTask( getData(), isSynchronous() ) );
  }

  public synchronized void setDataBackend( final DefaultStyleDataBackend dataBackend ) {
    this.oldDataBackend = getDataBackend();
    super.setDataBackend( dataBackend );
  }

  protected DefaultStyleDataBackend updateData( final Element[] elements ) {
    final StyleMetaData[] metaData = selectCommonAttributes( elements );
    final TableStyle tableStyle = getTableStyle();
    if ( tableStyle == TableStyle.ASCENDING ) {
      Arrays.sort( metaData, new PlainMetaDataComparator() );
      return ( new DefaultStyleDataBackend( metaData, new GroupingHeader[ metaData.length ], elements ) );
    } else if ( tableStyle == TableStyle.DESCENDING ) {
      Arrays.sort( metaData, Collections.reverseOrder( new PlainMetaDataComparator() ) );
      return ( new DefaultStyleDataBackend( metaData, new GroupingHeader[ metaData.length ], elements ) );
    } else {
      Arrays.sort( metaData, new GroupedMetaDataComparator() );
      final Locale locale = Locale.getDefault();
      int groupCount = 0;
      int metaDataCount = 0;

      if ( metaData.length > 0 ) {
        String oldValue = null;

        for ( int i = 0; i < metaData.length; i++ ) {
          final StyleMetaData data = metaData[ i ];
          if ( data.isHidden() ) {
            continue;
          }
          if ( !WorkspaceSettings.getInstance().isVisible( data ) ) {
            continue;
          }

          metaDataCount += 1;

          if ( groupCount == 0 ) {
            groupCount = 1;
            final StyleMetaData firstdata = metaData[ i ];
            oldValue = firstdata.getGrouping( locale );
            continue;
          }

          final String grouping = data.getGrouping( locale );
          if ( ( ObjectUtilities.equal( oldValue, grouping ) ) == false ) {
            oldValue = grouping;
            groupCount += 1;
          }
        }
      }

      final StyleMetaData[] groupedMetaData = new StyleMetaData[ metaDataCount + groupCount ];
      int targetIdx = 0;
      GroupingHeader[] groupings = new GroupingHeader[ groupedMetaData.length ];
      GroupingHeader group = null;
      for ( int sourceIdx = 0; sourceIdx < metaData.length; sourceIdx++ ) {
        final StyleMetaData data = metaData[ sourceIdx ];
        if ( data.isHidden() ) {
          continue;
        }
        if ( !WorkspaceSettings.getInstance().isVisible( data ) ) {
          continue;
        }

        if ( targetIdx == 0 ) {
          group = new GroupingHeader( data.getGrouping( locale ) );
          groupings[ targetIdx ] = group;
          targetIdx += 1;
        } else {
          final String newgroup = data.getGrouping( locale );
          //noinspection ConstantConditions
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

      if ( oldDataBackend != null ) {
        groupings = reconcileState( groupings, oldDataBackend.getGroupings() );
      }


      return new DefaultStyleDataBackend( groupedMetaData, groupings, elements );
    }
  }

  private static boolean isSameElements( final Element[] elements,
                                         final ElementType[] elementTypes,
                                         final Element[] oldElements ) {
    if ( elements.length != oldElements.length ) {
      // that is easy!
      return false;
    }

    for ( int i = 0; i < elements.length; i++ ) {
      final Element element = elements[ i ];
      if ( oldElements[ i ].getObjectID() != element.getObjectID() ) {
        return false;
      }
      if ( oldElements[ i ].getElementType() != elementTypes[ i ] ) {
        return false;
      }
    }
    return true;
  }

  protected static StyleMetaData[] selectCommonAttributes( final Element[] elements ) {
    final HashMap<String, Boolean> attributes = new HashMap<String, Boolean>();
    final ArrayList<StyleMetaData> selectedArrays = new ArrayList<StyleMetaData>();
    for ( int elementIdx = 0; elementIdx < elements.length; elementIdx++ ) {
      final Element element = elements[ elementIdx ];
      final StyleMetaData[] datas = element.getMetaData().getStyleDescriptions();
      for ( int styleIdx = 0; styleIdx < datas.length; styleIdx++ ) {
        final StyleMetaData data = datas[ styleIdx ];
        final String name = data.getName();

        if ( data.isHidden() ) {
          attributes.put( name, Boolean.FALSE );
          continue;
        }
        if ( !WorkspaceSettings.getInstance().isVisible( data ) ) {
          attributes.put( name, Boolean.FALSE );
          continue;
        }

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

    return selectedArrays.toArray( new StyleMetaData[ selectedArrays.size() ] );
  }

  public void setData( final Element[] elements ) {
    final DefaultStyleDataBackend backend = this.getDataBackend();
    if ( isSameElements( elements, backend.getElementTypes(), backend.getData() ) ) {
      if ( isSynchronous() ) {
        new SameElementsUpdateDataTask( backend, isSynchronous() ).run();
      } else {
        SwingUtilities.invokeLater( new SameElementsUpdateDataTask( backend, isSynchronous() ) );
      }
      return;
    }

    pool.execute( new UpdateDataTask( elements, isSynchronous() ) );
  }

  public Element[] getData() {
    return getDataBackend().getData();
  }

  public int getColumnCount() {
    return 4;
  }

  public String getColumnName( final int column ) {
    switch( column ) {
      case 0:
        return Messages.getString( "StyleTableModel.NameColumn" );
      case 1:
        return Messages.getString( "StyleTableModel.InheritColumn" );
      case 2:
        return Messages.getString( "StyleTableModel.ValueColumn" );
      case 3:
        return Messages.getString( "StyleTableModel.FormulaColumn" );
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
      case 3:
        return computeExpressionValue( metaData, rowIndex );
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
        return true;
      case 2:
        return true;
      case 3:
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
      case 3: {
        if ( aValue != null && aValue instanceof Expression == false ) {
          return;
        }
        if ( defineExpressionValue( metaData, (Expression) aValue ) ) {
          getDataBackend().clearExpressionsCache( rowIndex );
          fireTableDataChanged();
        }
        break;
      }
      default:
        throw new IndexOutOfBoundsException();
    }
  }

  protected boolean defineFullValue( final StyleMetaData metaData,
                                     final Object value ) {
    if ( value != null && metaData.getTargetType().isInstance( value ) == false ) {
      // not the correct type
      logger.warn( "Invalid type: " + value + "(" + value.getClass() + ") but expected " +  // NON-NLS
        metaData.getTargetType() );
      return false;
    }

    boolean changed = false;
    final Element[] elements = getDataBackend().getData();
    for ( int i = 0; i < elements.length; i++ ) {
      final Element element = elements[ i ];
      final ElementStyleSheet styleSheet = element.getStyle();
      final Object attribute = styleSheet.getStyleProperty( metaData.getStyleKey() );
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
        final ElementStyleSheet styleSheet = element.getStyle();
        final Object attribute = styleSheet.getStyleProperty( metaData.getStyleKey() );
        undos.add( new StyleEditUndoEntry
          ( element.getObjectID(), metaData.getStyleKey(), attribute, value ) );
        styleSheet.setStyleProperty( metaData.getStyleKey(), value );
      }
      undo.addChange( Messages.getString( "StyleChange" ),
        new CompoundUndoEntry( (UndoEntry[]) undos.toArray( new UndoEntry[ undos.size() ] ) ) );
    }
    return changed;
  }

  protected Object computeInheritValue( final StyleMetaData metaData,
                                        final int rowIndex ) {
    final DefaultStyleDataBackend dataBackend1 = getDataBackend();
    final Object[] inheritValues = dataBackend1.getInheritValues();
    final Object o = inheritValues[ rowIndex ];
    if ( o == StyleDataBackend.NULL_INDICATOR ) {
      return null;
    }
    if ( o != null ) {
      return o;
    }

    boolean allLocalKeys = true;
    boolean allInheritedKeys = true;
    final Element[] elements = dataBackend1.getData();
    if ( elements.length > 0 ) {
      final Element element = elements[ 0 ];
      final ElementStyleSheet styleSheet = element.getStyle();
      final boolean localKey = styleSheet.isLocalKey( metaData.getStyleKey() );
      allLocalKeys = allLocalKeys & localKey;
      allInheritedKeys = ( localKey == false );
    }
    final Object retval;
    if ( allLocalKeys == true && allInheritedKeys == true ) {
      retval = null;
    } else if ( allInheritedKeys == true ) {
      retval = Boolean.TRUE;
    } else if ( allLocalKeys == true ) {
      retval = Boolean.FALSE;
    } else {
      retval = null;
    }
    if ( retval == null ) {
      inheritValues[ rowIndex ] = StyleDataBackend.NULL_INDICATOR;
    } else {
      inheritValues[ rowIndex ] = retval;
    }
    return retval;
  }

  private boolean defineExpressionValue( final StyleMetaData metaData,
                                         final Expression value ) {
    boolean changed = false;
    final Element[] elements = getDataBackend().getData();
    for ( int i = 0; i < elements.length; i++ ) {
      final Element element = elements[ i ];
      final Expression attribute = element.getStyleExpression( metaData.getStyleKey() );
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
        final Expression attribute = element.getStyleExpression( metaData.getStyleKey() );
        if ( value == null ) {
          undos.add( new StyleExpressionEditUndoEntry
            ( element.getObjectID(), metaData.getStyleKey(), attribute, null ) );
          element.setStyleExpression( metaData.getStyleKey(), null );
          element.notifyNodePropertiesChanged();
        } else {
          final Expression expression = value.getInstance();
          undos.add( new StyleExpressionEditUndoEntry
            ( element.getObjectID(), metaData.getStyleKey(), attribute, expression ) );
          element.setStyleExpression( metaData.getStyleKey(), expression );
          element.notifyNodePropertiesChanged();
        }
      }
      undo.addChange( Messages.getString( "StyleChange" ),
        new CompoundUndoEntry( (UndoEntry[]) undos.toArray( new UndoEntry[ undos.size() ] ) ) );

    }
    return changed;
  }

  private Expression computeExpressionValue( final StyleMetaData metaData,
                                             final int row ) {
    final DefaultStyleDataBackend dataBackend1 = getDataBackend();
    final Object[] expressionValues = dataBackend1.getExpressionValues();
    final Object o = expressionValues[ row ];
    if ( o == NULL_INDICATOR ) {
      return null;
    }
    if ( o != null ) {
      return (Expression) o;
    }


    Expression lastElement = null;
    final Element[] elements = dataBackend1.getData();
    if ( elements.length > 0 ) {
      final Element element = elements[ 0 ];
      lastElement = element.getStyleExpression( metaData.getStyleKey() );
    }
    if ( lastElement != null ) {
      expressionValues[ row ] = lastElement;
    } else {
      expressionValues[ row ] = NULL_INDICATOR;
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
      case 3:
        return Expression.class;
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
      case 3:
        return null;
      default:
        throw new IndexOutOfBoundsException();
    }
  }

  public ReportDocumentContext getReportRenderContext() {
    return reportRenderContext;
  }

  public void setReportRenderContext( final ReportDocumentContext reportRenderContext ) {
    this.reportRenderContext = reportRenderContext;
  }
}
