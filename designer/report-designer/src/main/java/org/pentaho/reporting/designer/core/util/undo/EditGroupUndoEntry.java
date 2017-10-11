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
import org.pentaho.reporting.designer.core.model.ModelUtility;
import org.pentaho.reporting.engine.classic.core.RelationalGroup;
import org.pentaho.reporting.engine.classic.core.util.InstanceID;

/**
 * Todo: Document Me
 *
 * @author Thomas Morgner
 */
public class EditGroupUndoEntry implements UndoEntry {
  private InstanceID group;
  private String oldName;
  private String newName;
  private String[] oldFields;
  private String[] newFields;

  public EditGroupUndoEntry( final InstanceID group,
                             final String oldName,
                             final String newName,
                             final String[] oldFields,
                             final String[] newFields ) {
    this.group = group;
    this.oldName = oldName;
    this.newName = newName;
    this.oldFields = oldFields.clone();
    this.newFields = newFields.clone();
  }

  public String getNewName() {
    return newName;
  }

  public String[] getNewFields() {
    return newFields.clone();
  }

  public void undo( final ReportDocumentContext renderContext ) {
    final RelationalGroup elementById =
      (RelationalGroup) ModelUtility.findElementById( renderContext.getReportDefinition(), group );
    elementById.setName( oldName );
    elementById.setFieldsArray( oldFields );
  }

  public void redo( final ReportDocumentContext renderContext ) {
    final RelationalGroup elementById =
      (RelationalGroup) ModelUtility.findElementById( renderContext.getReportDefinition(), group );
    elementById.setName( newName );
    elementById.setFieldsArray( newFields );
  }

  public UndoEntry merge( final UndoEntry newEntry ) {
    return null;
  }
}
