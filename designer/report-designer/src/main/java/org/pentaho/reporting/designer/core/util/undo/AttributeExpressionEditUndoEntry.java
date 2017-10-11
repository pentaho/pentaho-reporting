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
