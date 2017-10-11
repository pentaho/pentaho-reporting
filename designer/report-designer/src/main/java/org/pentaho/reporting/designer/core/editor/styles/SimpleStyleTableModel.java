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
import org.pentaho.reporting.designer.core.settings.WorkspaceSettings;
import org.pentaho.reporting.designer.core.util.exceptions.UncaughtExceptionsModel;
import org.pentaho.reporting.designer.core.util.table.GroupingHeader;
import org.pentaho.reporting.designer.core.util.table.TableStyle;
import org.pentaho.reporting.engine.classic.core.metadata.ElementMetaData;
import org.pentaho.reporting.engine.classic.core.metadata.ElementTypeRegistry;
import org.pentaho.reporting.engine.classic.core.metadata.GroupedMetaDataComparator;
import org.pentaho.reporting.engine.classic.core.metadata.PlainMetaDataComparator;
import org.pentaho.reporting.engine.classic.core.metadata.StyleMetaData;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleSheet;
import org.pentaho.reporting.engine.classic.core.style.StyleKey;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Locale;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class SimpleStyleTableModel extends AbstractStyleTableModel<SimpleStyleTableModel.SimpleStyleDataBackend> {
  private static final Log logger = LogFactory.getLog( SimpleStyleTableModel.class );

  public static class SimpleStyleDataBackend extends AbstractStyleDataBackend {
    private ElementStyleSheet styleSheet;

    public SimpleStyleDataBackend() {
    }

    public SimpleStyleDataBackend( final StyleMetaData[] metaData,
                                   final GroupingHeader[] groupings,
                                   final ElementStyleSheet styleSheet ) {
      super( metaData, groupings );
      this.styleSheet = styleSheet;

      if ( this.styleSheet != null ) {
        getResolvedStyle().copyFrom( styleSheet );
      }
    }

    public void resetCache() {
      super.resetCache();
      if ( this.styleSheet != null ) {
        getResolvedStyle().copyFrom( styleSheet );
      }
    }

    public ElementStyleSheet getStyleSheet() {
      return styleSheet;
    }
  }

  private class UpdateDataTask implements Runnable {
    private ElementStyleSheet elements;
    private boolean synchronous;

    private UpdateDataTask( final ElementStyleSheet elements,
                            final boolean synchronous ) {
      this.elements = elements;
      this.synchronous = synchronous;
    }

    public void run() {
      try {
        final SimpleStyleDataBackend dataBackend = updateData( elements );
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

  private SimpleStyleDataBackend oldDataBackend;
  private Executor pool;

  public SimpleStyleTableModel() {
    pool = Executors.newSingleThreadExecutor();
    super.setDataBackend( new SimpleStyleDataBackend() );
  }

  public void setTableStyle( final TableStyle tableStyle ) {
    super.setTableStyle( tableStyle );
    pool.execute( new UpdateDataTask( getData(), isSynchronous() ) );
  }

  public void setData( final ElementStyleSheet elements ) {
    final SimpleStyleDataBackend backend = this.getDataBackend();
    if ( isSameStyleSheet( elements, backend.getStyleSheet() ) ) {
      SwingUtilities.invokeLater( new SameElementsUpdateDataTask( backend, isSynchronous() ) );
      return;
    }

    pool.execute( new UpdateDataTask( elements, isSynchronous() ) );
  }

  private boolean isSameStyleSheet( final ElementStyleSheet elements, final ElementStyleSheet styleSheet ) {
    if ( elements == styleSheet ) {
      return true;
    }
    if ( elements == null ) {
      return false;
    }
    if ( styleSheet == null ) {
      return false;
    }
    return styleSheet.getId() == elements.getId();
  }

  public ElementStyleSheet getData() {
    return getDataBackend().getStyleSheet();
  }

  protected synchronized void setDataBackend( final SimpleStyleDataBackend dataBackend ) {
    this.oldDataBackend = getDataBackend();
    super.setDataBackend( dataBackend );
  }

  protected SimpleStyleDataBackend updateData( final ElementStyleSheet styleSheet ) {
    final StyleMetaData[] metaData = selectCommonAttributes();
    final TableStyle tableStyle = getTableStyle();
    if ( tableStyle == TableStyle.ASCENDING ) {
      Arrays.sort( metaData, new PlainMetaDataComparator() );
      return ( new SimpleStyleDataBackend( metaData, new GroupingHeader[ metaData.length ], styleSheet ) );
    } else if ( tableStyle == TableStyle.DESCENDING ) {
      Arrays.sort( metaData, Collections.reverseOrder( new PlainMetaDataComparator() ) );
      return ( new SimpleStyleDataBackend( metaData, new GroupingHeader[ metaData.length ], styleSheet ) );
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
            oldValue = data.getGrouping( locale );
            continue;
          }

          final String grouping = data.getGrouping( locale );
          if ( ( ObjectUtilities.equal( oldValue, grouping ) ) == false ) {
            groupCount += 1;
            oldValue = grouping;
            continue;
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


      return new SimpleStyleDataBackend( groupedMetaData, groupings, styleSheet );
    }
  }

  private StyleMetaData[] selectCommonAttributes() {
    final HashSet<StyleKey> seenKeys = new HashSet<StyleKey>();
    final ArrayList<StyleMetaData> result = new ArrayList<StyleMetaData>();

    final ElementMetaData[] allElementTypes = ElementTypeRegistry.getInstance().getAllElementTypes();
    for ( int i = 0; i < allElementTypes.length; i++ ) {
      final ElementMetaData elementType = allElementTypes[ i ];
      final StyleMetaData[] datas = elementType.getStyleDescriptions();
      for ( int j = 0; j < datas.length; j++ ) {
        final StyleMetaData data = datas[ j ];

        if ( seenKeys.add( data.getStyleKey() ) ) {
          result.add( data );
        }
      }
    }
    return result.toArray( new StyleMetaData[ result.size() ] );
  }

  protected Object computeInheritValue( final StyleMetaData metaData, final int rowIndex ) {
    final ElementStyleSheet styleSheet = getDataBackend().getStyleSheet();
    if ( styleSheet == null ) {
      return null;
    }

    return styleSheet.isLocalKey( metaData.getStyleKey() ) == false;
  }

  protected boolean defineFullValue( final StyleMetaData metaData, final Object value ) {
    if ( value != null && metaData.getTargetType().isInstance( value ) == false ) {
      // not the correct type
      logger.warn( "Invalid type: " + value + "(" + value.getClass() + ") but expected " +  // NON-NLS
        metaData.getTargetType() );
      return false;
    }

    final ElementStyleSheet styleSheet = getDataBackend().getStyleSheet();
    if ( styleSheet == null ) {
      return false;
    }

    final long changeTrackerHash = styleSheet.getChangeTrackerHash();
    styleSheet.setStyleProperty( metaData.getStyleKey(), value );
    return changeTrackerHash != styleSheet.getChangeTrackerHash();
  }
}
