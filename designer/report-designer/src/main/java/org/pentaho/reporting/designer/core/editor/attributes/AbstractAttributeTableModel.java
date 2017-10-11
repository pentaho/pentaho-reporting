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

import org.pentaho.reporting.designer.core.editor.ReportDocumentContext;
import org.pentaho.reporting.designer.core.settings.WorkspaceSettings;
import org.pentaho.reporting.designer.core.util.FastPropertyEditorManager;
import org.pentaho.reporting.designer.core.util.exceptions.UncaughtExceptionsModel;
import org.pentaho.reporting.designer.core.util.table.ElementMetaDataTableModel;
import org.pentaho.reporting.designer.core.util.table.GroupingHeader;
import org.pentaho.reporting.designer.core.util.table.ResourcePropertyEditor;
import org.pentaho.reporting.designer.core.util.table.TableStyle;
import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.ReportElement;
import org.pentaho.reporting.engine.classic.core.metadata.AttributeMetaData;
import org.pentaho.reporting.engine.classic.core.metadata.ElementType;
import org.pentaho.reporting.engine.classic.core.metadata.GroupedMetaDataComparator;
import org.pentaho.reporting.engine.classic.core.metadata.PlainMetaDataComparator;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.libraries.xmlns.common.AttributeMap;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.beans.PropertyEditor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Locale;

/**
 * Todo: Document me!
 *
 * @author Thomas Morgner
 */
public abstract class AbstractAttributeTableModel
  extends AbstractTableModel implements ElementMetaDataTableModel

{
  protected static final GroupingHeader[] EMPTY_GROUPINGS = new GroupingHeader[ 0 ];
  protected static final AttributeMetaData[] EMPTY_METADATA = new AttributeMetaData[ 0 ];

  protected class NotifyChangeTask implements Runnable {
    private DataBackend dataBackend;

    protected NotifyChangeTask( final DataBackend dataBackend ) {
      this.dataBackend = dataBackend;
    }

    public void run() {
      setDataBackend( dataBackend );
      fireTableDataChanged();
    }
  }

  protected class SameElementsUpdateDataTask implements Runnable {
    private DataBackend dataBackend;

    protected SameElementsUpdateDataTask( final DataBackend elements ) {
      this.dataBackend = elements;
    }

    public void run() {
      dataBackend.resetCache();
      try {
        if ( SwingUtilities.isEventDispatchThread() ) {
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

  protected class UpdateDataTask implements Runnable {
    private ReportElement[] elements;

    protected UpdateDataTask( final ReportElement[] elements ) {
      this.elements = elements.clone();
    }

    public void run() {
      try {
        final DataBackend dataBackend = updateData( elements );
        if ( SwingUtilities.isEventDispatchThread() ) {
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

  protected abstract static class DataBackend {
    private AttributeMetaData[] metaData;
    private GroupingHeader[] groupings;

    public DataBackend() {
      groupings = EMPTY_GROUPINGS;
      metaData = EMPTY_METADATA;
    }

    public abstract void resetCache();

    public DataBackend( final AttributeMetaData[] metaData, final GroupingHeader[] groupings ) {
      this.metaData = metaData;
      this.groupings = groupings;
    }

    public int getRowCount() {
      return metaData.length;
    }

    protected AttributeMetaData getMetaData( final int row ) {
      //noinspection ReturnOfCollectionOrArrayField, as this is for internal use only
      return metaData[ row ];
    }

    protected GroupingHeader getGroupings( final int row ) {
      //noinspection ReturnOfCollectionOrArrayField, as this is for internal use only
      return groupings[ row ];
    }

    protected GroupingHeader[] getGroupings() {
      return groupings;
    }
  }

  private DataBackend dataBackend, oldDataBackend;
  private TableStyle tableStyle;
  private ReportDocumentContext reportRenderContext;

  protected AbstractAttributeTableModel() {
    tableStyle = TableStyle.GROUPED;
  }

  public int getRowCount() {
    return dataBackend.getRowCount();
  }

  protected AttributeMetaData getMetaData( final int row ) {
    return dataBackend.getMetaData( row );
  }

  protected GroupingHeader getGroupings( final int row ) {
    return dataBackend.getGroupings( row );
  }

  public TableStyle getTableStyle() {
    return tableStyle;
  }

  public void setTableStyle( final TableStyle tableStyle ) {
    if ( tableStyle == null ) {
      throw new NullPointerException();
    }
    this.tableStyle = tableStyle;
    refreshData();
  }

  protected abstract void refreshData();

  protected static boolean isSameElements( final ReportElement[] elements,
                                           final ReportElement[] existingElements,
                                           final ElementType[] elementTypes ) {
/*
    if (elements == existingElements)
    {
      return true;
    }
    */
    if ( elements.length != existingElements.length ) {
      // that is easy!
      return false;
    }

    for ( int i = 0; i < elements.length; i++ ) {
      final Element element = (Element) elements[ i ];
      if ( existingElements[ i ].getObjectID() != element.getObjectID() ) {
        return false;
      }
      if ( elementTypes != null ) {
        if ( !element.getElementType().getClass().equals( elementTypes[ i ].getClass() ) ) {
          return false;
        }
      }
    }
    return true;
  }

  public synchronized DataBackend getDataBackend() {
    return dataBackend;
  }

  public synchronized void setDataBackend( final DataBackend dataBackend ) {
    this.dataBackend = dataBackend;
  }

  /**
   * @param headers
   * @param metaData
   * @param elements
   * @return null - Concrete implementations MUST override this method and call super.createDataBackend(headers,
   * metaData, elements) BEFORE any other code is executed.  Then they must return a implementation of Databackend
   */
  protected DataBackend createDataBackend( final GroupingHeader[] headers,
                                           final AttributeMetaData[] metaData,
                                           final ReportElement[] elements,
                                           final ElementType[] elementTypes ) {
    oldDataBackend = this.getDataBackend();
    return null;
  }

  protected DataBackend updateData( final ReportElement[] elements ) {
    final AttributeMetaData[] metaData = selectCommonAttributes( elements );
    final ArrayList<ElementType> elementTypesArray = new ArrayList<ElementType>();
    for ( int i = 0; i < elements.length; i++ ) {
      final Element element = (Element) elements[ i ];
      elementTypesArray.add( element.getElementType() );
    }
    final ElementType[] elementTypes = elementTypesArray.toArray( new ElementType[ elementTypesArray.size() ] );

    if ( tableStyle == TableStyle.ASCENDING ) {
      Arrays.sort( metaData, new PlainMetaDataComparator() );
      return ( createDataBackend( new GroupingHeader[ metaData.length ], metaData, elements, elementTypes ) );
    } else if ( tableStyle == TableStyle.DESCENDING ) {
      Arrays.sort( metaData, Collections.reverseOrder( new PlainMetaDataComparator() ) );
      return ( createDataBackend( new GroupingHeader[ metaData.length ], metaData, elements, elementTypes ) );
    } else {
      GroupingHeader[] groupings;
      Arrays.sort( metaData, new GroupedMetaDataComparator() );

      int groupCount = 0;
      int metaDataCount = 0;
      final Locale locale = Locale.getDefault();
      if ( metaData.length > 0 ) {
        String oldValue = null;
        for ( int i = 0; i < metaData.length; i++ ) {
          final AttributeMetaData data = metaData[ i ];
          if ( data.isHidden() ) {
            continue;
          }
          if ( !WorkspaceSettings.getInstance().isVisible( data ) ) {
            continue;
          }

          metaDataCount += 1;

          if ( groupCount == 0 ) {
            groupCount = 1;
            final AttributeMetaData firstdata = metaData[ i ];
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

      final AttributeMetaData[] groupedMetaData = new AttributeMetaData[ metaDataCount + groupCount ];
      int targetIdx = 0;
      groupings = new GroupingHeader[ groupedMetaData.length ];
      GroupingHeader group = null;
      for ( int sourceIdx = 0; sourceIdx < metaData.length; sourceIdx++ ) {
        final AttributeMetaData data = metaData[ sourceIdx ];
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

      return ( createDataBackend( groupings, groupedMetaData, elements, elementTypes ) );
    }
  }

  /**
   * Uses the name of the old groupings to set the collapse status of the new groupings so that when a user makes a
   * selection not all of the groups return to the expanded state.  In essence makes group collapses "sticky" where the
   * group heading hasn't changed.
   *
   * @param groupings
   * @param oldGroupings
   */
  private GroupingHeader[] reconcileState( final GroupingHeader[] groupings, final GroupingHeader[] oldGroupings ) {
    for ( final GroupingHeader header : groupings ) {
      final GroupingHeader oldHeader = findFirstOccuranceOfHeaderTitle( oldGroupings, header.getHeaderText() );
      if ( oldHeader != null ) {
        header.setCollapsed( oldHeader.isCollapsed() );
      }
    }
    return groupings;
  }

  private GroupingHeader findFirstOccuranceOfHeaderTitle( final GroupingHeader[] headerArray,
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

  private static AttributeMetaData[] selectCommonAttributes( final ReportElement[] elements ) {
    final AttributeMap<Object> attributes = new AttributeMap<Object>();
    final ArrayList<AttributeMetaData> selectedArrays = new ArrayList<AttributeMetaData>();
    for ( int elementCount = 0; elementCount < elements.length; elementCount++ ) {
      final ReportElement element = elements[ elementCount ];
      final AttributeMetaData[] datas = element.getMetaData().getAttributeDescriptions();
      for ( int j = 0; j < datas.length; j++ ) {
        final AttributeMetaData data = datas[ j ];

        final String name = data.getName();
        final String namespace = data.getNameSpace();

        if ( data.isHidden() ) {
          attributes.setAttribute( namespace, name, Boolean.FALSE );
          continue;
        }
        if ( !WorkspaceSettings.getInstance().isVisible( data ) ) {
          continue;
        }

        final Object attribute = attributes.getAttribute( namespace, name );
        if ( Boolean.TRUE.equals( attribute ) ) {
          // fine, we already have a value for it.
        } else if ( attribute == null ) {
          // add it ..
          if ( elementCount == 0 ) {
            attributes.setAttribute( namespace, name, Boolean.TRUE );
          } else {
            attributes.setAttribute( namespace, name, Boolean.FALSE );
          }
        }
      }
    }

    final String[] namespaces = attributes.getNameSpaces();
    for ( int nsIdx = 0; nsIdx < namespaces.length; nsIdx++ ) {
      final String namespace = namespaces[ nsIdx ];
      final String[] names = attributes.getNames( namespace );
      for ( int namesIdx = 0; namesIdx < names.length; namesIdx++ ) {
        final String name = names[ namesIdx ];
        final Object attribute = attributes.getAttribute( namespace, name );
        if ( Boolean.TRUE.equals( attribute ) ) {
          selectedArrays.add( find( elements[ 0 ].getMetaData().getAttributeDescriptions(), namespace, name ) );
        }
      }
    }

    return selectedArrays.toArray( new AttributeMetaData[ selectedArrays.size() ] );
  }

  private static AttributeMetaData find( final AttributeMetaData[] data, final String namespace, final String name ) {
    for ( int i = 0; i < data.length; i++ ) {
      final AttributeMetaData attributeMetaData = data[ i ];
      if ( attributeMetaData.getName().equals( name ) && attributeMetaData.getNameSpace().equals( namespace ) ) {
        return attributeMetaData;
      }
    }
    return null;
  }


  protected PropertyEditor getDefaultEditor( final Class type, final String valueRole ) {
    if ( String.class.equals( type ) ) {
      return null;
    }
    if ( AttributeMetaData.VALUEROLE_RESOURCE.equals( valueRole ) ) {
      return new ResourcePropertyEditor( reportRenderContext );
    }

    return FastPropertyEditorManager.findEditor( type );
  }

  public ReportDocumentContext getReportRenderContext() {
    return reportRenderContext;
  }

  public void setReportRenderContext( final ReportDocumentContext reportRenderContext ) {
    this.reportRenderContext = reportRenderContext;
  }
}
