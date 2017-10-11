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

package org.pentaho.reporting.designer.core.util.undo;

import org.pentaho.reporting.designer.core.editor.ReportDocumentContext;
import org.pentaho.reporting.engine.classic.core.AbstractReportDefinition;
import org.pentaho.reporting.engine.classic.core.CompoundDataFactory;
import org.pentaho.reporting.engine.classic.core.DataFactory;

/**
 * Todo: Document Me
 *
 * @author Thomas Morgner
 */
public class DataSourceEditUndoEntry implements UndoEntry {
  private int position;
  private DataFactory oldElement;
  private DataFactory newElement;

  public DataSourceEditUndoEntry( final int position,
                                  final DataFactory oldElement,
                                  final DataFactory newElement ) {
    this.position = position;
    this.oldElement = oldElement;
    this.newElement = newElement;
  }

  public void undo( final ReportDocumentContext renderContext ) {
    final AbstractReportDefinition abstractReportDefinition = renderContext.getReportDefinition();
    final DataFactory dataFactory = abstractReportDefinition.getDataFactory();
    if ( dataFactory instanceof CompoundDataFactory == false ) {
      throw new IllegalStateException();
    }
    final CompoundDataFactory cdf = (CompoundDataFactory) dataFactory;
    if ( newElement != null ) {
      cdf.remove( position );
      abstractReportDefinition.notifyNodeChildRemoved( newElement );
    }
    if ( oldElement != null ) {
      cdf.add( position, oldElement );
      abstractReportDefinition.notifyNodeChildAdded( oldElement );
    }
  }

  public void redo( final ReportDocumentContext renderContext ) {
    final AbstractReportDefinition abstractReportDefinition = renderContext.getReportDefinition();
    final DataFactory dataFactory = abstractReportDefinition.getDataFactory();
    if ( dataFactory instanceof CompoundDataFactory == false ) {
      throw new IllegalStateException();
    }
    final CompoundDataFactory cdf = (CompoundDataFactory) dataFactory;
    if ( oldElement != null ) {
      cdf.remove( position );
      abstractReportDefinition.notifyNodeChildRemoved( oldElement );
    }
    if ( newElement != null ) {
      cdf.add( position, newElement );
      abstractReportDefinition.notifyNodeChildAdded( newElement );
    }
  }

  public UndoEntry merge( final UndoEntry newEntry ) {
    return null;
  }

}
