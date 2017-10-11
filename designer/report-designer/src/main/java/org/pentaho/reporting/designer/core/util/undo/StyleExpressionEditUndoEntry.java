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
import org.pentaho.reporting.engine.classic.core.style.StyleKey;
import org.pentaho.reporting.engine.classic.core.util.InstanceID;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;

/**
 * Todo: Document Me
 *
 * @author Thomas Morgner
 */
public class StyleExpressionEditUndoEntry implements UndoEntry {
  private InstanceID target;
  private StyleKey styleKey;
  private Expression newValue;
  private Expression oldValue;

  public StyleExpressionEditUndoEntry( final InstanceID target,
                                       final StyleKey styleKey,
                                       final Expression oldValue,
                                       final Expression newValue ) {
    this.target = target;
    this.styleKey = styleKey;
    this.oldValue = oldValue;
    this.newValue = newValue;
  }

  public void undo( final ReportDocumentContext renderContext ) {
    final ReportElement elementById = ModelUtility.findElementById( renderContext.getReportDefinition(), target );
    elementById.setStyleExpression( styleKey, oldValue );
  }

  public void redo( final ReportDocumentContext renderContext ) {
    final ReportElement elementById = ModelUtility.findElementById( renderContext.getReportDefinition(), target );
    elementById.setStyleExpression( styleKey, newValue );
  }

  public UndoEntry merge( final UndoEntry newEntry ) {
    if ( newEntry instanceof StyleExpressionEditUndoEntry == false ) {
      return null;
    }

    final StyleExpressionEditUndoEntry entry = (StyleExpressionEditUndoEntry) newEntry;
    if ( entry.target == target &&
      ObjectUtilities.equal( entry.styleKey, styleKey ) ) {
      return newEntry;
    }
    return null;
  }
}
