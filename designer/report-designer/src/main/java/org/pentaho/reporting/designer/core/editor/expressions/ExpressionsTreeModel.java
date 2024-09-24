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
import org.pentaho.reporting.designer.core.ReportDesignerBoot;
import org.pentaho.reporting.designer.core.settings.WorkspaceSettings;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.function.StructureFunction;
import org.pentaho.reporting.engine.classic.core.metadata.ExpressionMetaData;
import org.pentaho.reporting.engine.classic.core.metadata.ExpressionRegistry;
import org.pentaho.reporting.engine.classic.core.metadata.GroupedMetaDataComparator;
import org.pentaho.reporting.libraries.base.util.HashNMap;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;

import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;

public class ExpressionsTreeModel implements TreeModel {
  private static class ExpressionGroupingRoot {
    private ExpressionGroupingRoot() {
    }

    public String toString() {
      return "<Root> You should not see this </root>"; // NON-NLS
    }
  }

  private static ExpressionsTreeModel model;

  public static ExpressionsTreeModel getTreeModel() {
    if ( model != null ) {
      return model;
    }

    if ( ClassicEngineBoot.getInstance().isBootDone() ) {
      model = new ExpressionsTreeModel();
      return model;
    }

    // not booted yet, return a temporary object.
    throw new IllegalStateException( "ExpressionsTree: Requesting tree without booting is not a sane thing to do." );
  }

  private static Log logger = LogFactory.getLog( ExpressionsTreeModel.class );
  private ExpressionGroupingRoot root;
  private String[] groupings;
  private HashNMap<String, ExpressionMetaData> expressionsByGroup;

  private ExpressionsTreeModel() {
    final ExpressionMetaData[] metaData = ExpressionRegistry.getInstance().getAllExpressionMetaDatas();

    Arrays.sort( metaData, new GroupedMetaDataComparator() );

    final Locale locale = Locale.getDefault();
    final ArrayList<String> groupingsList = new ArrayList<String>( metaData.length );
    final HashMap<String, String> diagnosticMap = new HashMap<String, String>();
    String group = null;
    for ( int sourceIdx = 0; sourceIdx < metaData.length; sourceIdx++ ) {
      final ExpressionMetaData data = metaData[ sourceIdx ];
      if ( data.isHidden() ) {
        continue;
      }
      if ( !WorkspaceSettings.getInstance().isVisible( data ) ) {
        continue;
      }
      if ( StructureFunction.class.isAssignableFrom( data.getExpressionType() ) ) {
        continue;
      }

      if ( logger.isDebugEnabled() ) {
        logger.debug(
          "Grouping : " + data.getGrouping( locale ) + " - " + data.getGroupingOrdinal( locale ) + " -> " + data
            .getExpressionType() );
      }
      final String diagnosticGroup = group;
      if ( sourceIdx == 0 ) {
        group = data.getGrouping( locale );
        groupingsList.add( group );
      } else {
        final String newgroup = data.getGrouping( locale );
        if ( ( ObjectUtilities.equal( newgroup, group ) ) == false ) {
          if ( groupingsList.contains( newgroup ) == false ) {
            group = newgroup;
            groupingsList.add( newgroup );
          } else {
            logger.warn( "Warning: Misconfigured Expression-metadata: Group already processed: '" +
              newgroup + "' - " +  // NON-NLS
              data.getExpressionType() + " - Previous: " + diagnosticMap.get( diagnosticGroup ) );
          }
        }
      }

      diagnosticMap.put( group, data.getExpressionType().getName() );
    }

    root = new ExpressionGroupingRoot();
    groupings = groupingsList.toArray( new String[ groupingsList.size() ] );

    expressionsByGroup = new HashNMap<String, ExpressionMetaData>();
    for ( int i = 0; i < metaData.length; i++ ) {
      final ExpressionMetaData exMetaData = metaData[ i ];
      if ( StructureFunction.class.isAssignableFrom( exMetaData.getExpressionType() ) ) {
        continue;
      }

      if ( exMetaData.isHidden() ) {
        continue;
      }
      if ( WorkspaceSettings.getInstance().isShowExpertItems() == false && exMetaData.isExpert() ) {
        continue;
      }
      if ( WorkspaceSettings.getInstance().isShowDeprecatedItems() == false && exMetaData.isDeprecated() ) {
        continue;
      }
      expressionsByGroup.add( exMetaData.getGrouping( Locale.getDefault() ), exMetaData );
    }
  }

  public Object getRoot() {
    return root;
  }

  public Object getChild( final Object parent, final int index ) {
    if ( parent == root ) {
      return groupings[ index ];
    } else if ( parent instanceof String ) {
      return expressionsByGroup.get( (String) parent, index );
    }
    return null;
  }

  public int getChildCount( final Object parent ) {
    if ( parent == root ) {
      return groupings.length;
    } else if ( parent instanceof String ) {
      return expressionsByGroup.getValueCount( (String) parent );
    }
    return 0;
  }

  public boolean isLeaf( final Object node ) {
    return node instanceof ExpressionMetaData;
  }

  public void valueForPathChanged( final TreePath path,
                                   final Object newValue ) {
    // cannot happen, we are not editable ..
  }

  public int getIndexOfChild( final Object parent, final Object child ) {
    if ( parent == null ) {
      return -1;
    }
    if ( parent == root && child instanceof String ) {
      final int idx = Arrays.binarySearch( groupings, child );
      if ( idx < 0 || idx >= groupings.length ) {
        return -1;
      }
      return idx;
    } else if ( parent instanceof String ) {
      final Object[] metas = expressionsByGroup.toArray( (String) parent );
      if ( metas == null ) {
        return -1;
      }
      for ( int i = 0; i < metas.length; i++ ) {
        if ( child == metas[ i ] ) {
          return i;
        }
      }
      return -1;
    }
    return -1;
  }

  public void addTreeModelListener( final TreeModelListener l ) {
  }

  public void removeTreeModelListener( final TreeModelListener l ) {
  }

  public static void main( String[] args ) {
    ReportDesignerBoot.getInstance().start();
    ExpressionsTreeModel.getTreeModel();
  }
}
