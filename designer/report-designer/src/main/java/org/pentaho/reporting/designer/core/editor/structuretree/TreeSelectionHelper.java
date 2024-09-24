/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2028-08-13
 ******************************************************************************/

package org.pentaho.reporting.designer.core.editor.structuretree;

import org.pentaho.reporting.engine.classic.core.AbstractReportDefinition;
import org.pentaho.reporting.engine.classic.core.DataFactory;
import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.ReportElement;

import javax.swing.tree.TreePath;
import java.util.ArrayList;

public class TreeSelectionHelper {
  private TreeSelectionHelper() {
  }

  public static TreePath getPathForNode( final ReportStructureTreeModel treeModel, final Object currentSelection ) {
    if ( currentSelection instanceof Element ) {
      return getBasePathForNode( (Element) currentSelection, treeModel.getReport() );
    }
    if ( currentSelection instanceof DataFactory ) {
      final DataFactory params = treeModel.getDataFactoryElement();
      if ( treeModel.getIndexOfChild( params, currentSelection ) < 0 ) {
        return null;
      }
      return new TreePath( new Object[] { treeModel.getRoot(), params, currentSelection } );
    }
    return null;
  }

  private static TreePath getBasePathForNode( final Element node, final AbstractReportDefinition stopOnReport ) {
    final ArrayList<ReportElement> list = new ArrayList<ReportElement>();
    ReportElement re = node;
    while ( re != null ) {
      list.add( re );
      if ( re == stopOnReport ) {
        break;
      }

      re = re.getParentSection();
    }
    final Object[] data = new Object[ list.size() ];
    for ( int i = 0; i < list.size(); i++ ) {
      final ReportElement element = list.get( i );
      data[ data.length - 1 - i ] = element;
    }

    return new TreePath( data );
  }
}
