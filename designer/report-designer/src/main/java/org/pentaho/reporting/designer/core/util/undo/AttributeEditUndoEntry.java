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
import org.pentaho.reporting.engine.classic.core.ReportElement;
import org.pentaho.reporting.engine.classic.core.util.InstanceID;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;

public class AttributeEditUndoEntry implements UndoEntry {
  private InstanceID target;
  private String attributeNamespace;
  private String attributeName;
  private Object oldValue;
  private Object newValue;

  public AttributeEditUndoEntry( final InstanceID target,
                                 final String attributeNamespace,
                                 final String attributeName,
                                 final Object oldValue,
                                 final Object newValue ) {
    this.target = target;
    this.attributeNamespace = attributeNamespace;
    this.attributeName = attributeName;
    this.oldValue = oldValue;
    this.newValue = newValue;
  }

  public void undo( final ReportDocumentContext renderContext ) {
    final ReportElement elementById = ModelUtility.findElementById( renderContext.getReportDefinition(), target );
    elementById.setAttribute( attributeNamespace, attributeName, oldValue );
  }

  public void redo( final ReportDocumentContext renderContext ) {
    final ReportElement elementById = ModelUtility.findElementById( renderContext.getReportDefinition(), target );
    elementById.setAttribute( attributeNamespace, attributeName, newValue );
  }

  public UndoEntry merge( final UndoEntry newEntry ) {
    if ( newEntry instanceof AttributeEditUndoEntry == false ) {
      return null;
    }

    final AttributeEditUndoEntry entry = (AttributeEditUndoEntry) newEntry;
    if ( entry.target == target &&
      ObjectUtilities.equal( entry.attributeNamespace, attributeNamespace ) &&
      ObjectUtilities.equal( entry.attributeName, attributeName ) ) {
      return newEntry;
    }
    return null;
  }
}
