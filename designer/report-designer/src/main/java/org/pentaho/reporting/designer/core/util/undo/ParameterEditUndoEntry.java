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
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.parameters.ModifiableReportParameterDefinition;
import org.pentaho.reporting.engine.classic.core.parameters.ParameterDefinitionEntry;
import org.pentaho.reporting.engine.classic.core.parameters.ReportParameterDefinition;

/**
 * Handles insert, remove and replacement of elements. Insert: old is null, remove: new is null.
 *
 * @author Thomas Morgner
 */
public class ParameterEditUndoEntry implements UndoEntry {
  private int position;
  private ParameterDefinitionEntry oldElement;
  private ParameterDefinitionEntry newElement;

  public ParameterEditUndoEntry( final int position,
                                 final ParameterDefinitionEntry oldElement,
                                 final ParameterDefinitionEntry newElement ) {
    this.position = position;
    this.oldElement = oldElement;
    this.newElement = newElement;
  }

  public void undo( final ReportDocumentContext renderContext ) {
    final AbstractReportDefinition abstractReportDefinition = renderContext.getReportDefinition();
    if ( abstractReportDefinition instanceof MasterReport == false ) {
      return;
    }
    final MasterReport report = (MasterReport) abstractReportDefinition;
    final ReportParameterDefinition definition = report.getParameterDefinition();
    if ( definition instanceof ModifiableReportParameterDefinition == false ) {
      return;
    }

    final ModifiableReportParameterDefinition mdef = (ModifiableReportParameterDefinition) definition;
    if ( newElement != null ) {
      mdef.removeParameterDefinition( position );
      clearParameterValues( report );
      report.notifyNodeChildRemoved( newElement );
    }
    if ( oldElement != null ) {
      mdef.addParameterDefinition( position, oldElement );
      clearParameterValues( report );
      report.notifyNodeChildAdded( oldElement );
    }
  }

  public void redo( final ReportDocumentContext renderContext ) {
    final AbstractReportDefinition abstractReportDefinition = renderContext.getReportDefinition();
    if ( abstractReportDefinition instanceof MasterReport == false ) {
      return;
    }
    final MasterReport report = (MasterReport) abstractReportDefinition;
    final ReportParameterDefinition definition = report.getParameterDefinition();
    if ( definition instanceof ModifiableReportParameterDefinition == false ) {
      return;
    }

    final ModifiableReportParameterDefinition mdef = (ModifiableReportParameterDefinition) definition;
    if ( oldElement != null ) {
      mdef.removeParameterDefinition( position );
      clearParameterValues( report );
      report.notifyNodeChildRemoved( oldElement );
    }
    if ( newElement != null ) {
      mdef.addParameterDefinition( position, newElement );
      clearParameterValues( report );
      report.notifyNodeChildAdded( newElement );
    }
  }

  private void clearParameterValues( final MasterReport report ) {
    final String[] columnNames = report.getParameterValues().getColumnNames();
    for ( int i = 0; i < columnNames.length; i++ ) {
      final String columnName = columnNames[ i ];
      report.getParameterValues().put( columnName, null );
    }
  }

  public UndoEntry merge( final UndoEntry newEntry ) {
    return null;
  }
}
