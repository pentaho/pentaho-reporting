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
import org.pentaho.reporting.engine.classic.core.function.Expression;
import org.pentaho.reporting.engine.classic.core.util.InstanceID;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;

/**
 * Todo: Document Me
 *
 * @author Thomas Morgner
 */
public class AttributeExpressionEditUndoEntry implements UndoEntry {
  private InstanceID target;
  private String attributeNamespace;
  private String attributeName;
  private Expression newValue;
  private Expression oldValue;

  public AttributeExpressionEditUndoEntry( final InstanceID target,
                                           final String attributeNamespace,
                                           final String attributeName,
                                           final Expression oldValue,
                                           final Expression newValue ) {
    this.target = target;
    this.attributeNamespace = attributeNamespace;
    this.attributeName = attributeName;
    this.newValue = newValue;
    this.oldValue = oldValue;
  }

  public void undo( final ReportDocumentContext renderContext ) {
    final ReportElement elementById = ModelUtility.findElementById( renderContext.getReportDefinition(), target );
    elementById.setAttributeExpression( attributeNamespace, attributeName, oldValue );
  }

  public void redo( final ReportDocumentContext renderContext ) {
    final ReportElement elementById = ModelUtility.findElementById( renderContext.getReportDefinition(), target );
    elementById.setAttributeExpression( attributeNamespace, attributeName, newValue );

  }

  public UndoEntry merge( final UndoEntry newEntry ) {
    if ( newEntry instanceof AttributeExpressionEditUndoEntry == false ) {
      return null;
    }

    final AttributeExpressionEditUndoEntry entry = (AttributeExpressionEditUndoEntry) newEntry;
    if ( entry.target == target &&
      ObjectUtilities.equal( entry.attributeNamespace, attributeNamespace ) &&
      ObjectUtilities.equal( entry.attributeName, attributeName ) ) {
      return newEntry;
    }
    return null;
  }

}
