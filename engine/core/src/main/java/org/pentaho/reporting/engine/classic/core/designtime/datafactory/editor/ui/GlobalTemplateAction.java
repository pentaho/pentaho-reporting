/*
 * This program is free software; you can redistribute it and/or modify it under the
 *  terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
 *  Foundation.
 *
 *  You should have received a copy of the GNU Lesser General Public License along with this
 *  program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 *  or from the Free Software Foundation, Inc.,
 *  51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 *  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *  See the GNU Lesser General Public License for more details.
 *
 *  Copyright (c) 2006 - 2017 Hitachi Vantara..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.designtime.datafactory.editor.ui;

import java.awt.Component;

import org.pentaho.reporting.engine.classic.core.designtime.datafactory.editor.model.QueryDialogModel;

public class GlobalTemplateAction<T> extends ScriptTemplateAction {
  private final Component parentComponent;
  private final QueryDialogModel<T> dialogModel;

  public GlobalTemplateAction( final Component parentComponent, final QueryDialogModel<T> dialogModel ) {
    super( true );
    this.parentComponent = parentComponent;
    this.dialogModel = dialogModel;
  }

  protected Component getParentComponent() {
    return parentComponent;
  }

  protected String getText() {
    return dialogModel.getGlobalScript();
  }

  protected void setText( final String text ) {
    dialogModel.setGlobalScripting( dialogModel.getGlobalScriptLanguage(), text );
  }
}
