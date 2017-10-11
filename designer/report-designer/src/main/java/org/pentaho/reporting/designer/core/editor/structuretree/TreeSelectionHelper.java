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
