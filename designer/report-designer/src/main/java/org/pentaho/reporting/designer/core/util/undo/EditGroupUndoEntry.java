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
