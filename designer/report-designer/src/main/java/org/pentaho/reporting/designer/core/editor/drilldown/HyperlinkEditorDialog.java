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

package org.pentaho.reporting.designer.core.editor.drilldown;

import org.pentaho.reporting.designer.core.Messages;
import org.pentaho.reporting.designer.core.ReportDesignerContext;
import org.pentaho.reporting.designer.core.editor.format.EditableStyleSheet;
import org.pentaho.reporting.designer.core.util.undo.ElementFormatUndoEntry;
import org.pentaho.reporting.engine.classic.core.function.Expression;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleSheet;
import org.pentaho.reporting.engine.classic.core.style.StyleKey;
import org.pentaho.reporting.libraries.designtime.swing.CommonDialog;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class HyperlinkEditorDialog extends CommonDialog {
  private HyperlinkEditorPane hyperlinksPane;

  public HyperlinkEditorDialog()
    throws HeadlessException {
    init();
  }

  public HyperlinkEditorDialog( final Frame owner )
    throws HeadlessException {
    super( owner );
    init();
  }

  public HyperlinkEditorDialog( final Dialog owner )
    throws HeadlessException {
    super( owner );
    init();
  }

  protected void init() {
    setTitle( Messages.getString( "HyperlinkEditorDialog.Title" ) );

    hyperlinksPane = new HyperlinkEditorPane();
    super.init();
  }

  protected String getDialogId() {
    return "ReportDesigner.Core.HyperlinkEditor";
  }

  protected Component createContentPane() {
    return hyperlinksPane;
  }

  public ElementFormatUndoEntry.EditResult performEdit( final ReportDesignerContext designerContext,
                                                        final ElementStyleSheet element,
                                                        final Map<StyleKey, Expression> styleExpressions ) {
    if ( styleExpressions == null ) {
      throw new NullPointerException();
    }

    final EditableStyleSheet styleSheet = new EditableStyleSheet();
    styleSheet.copyParentValues( element );

    final HashMap<StyleKey, Expression> editableStyleExpressions =
      new HashMap<StyleKey, Expression>( styleExpressions );

    hyperlinksPane.initializeFromStyle( styleSheet, editableStyleExpressions, designerContext );

    if ( performEdit() == false ) {
      return null;
    }

    hyperlinksPane.commitValues( styleSheet, editableStyleExpressions );

    // do something ..
    return new ElementFormatUndoEntry.EditResult( styleSheet, editableStyleExpressions );
  }
}

