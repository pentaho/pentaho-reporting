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


package org.pentaho.reporting.designer.core.actions.report;

import org.pentaho.reporting.designer.core.actions.ActionMessages;
import org.pentaho.reporting.designer.core.actions.elements.EditCrosstabAction;

import javax.swing.*;

public class MenuEditCrosstabAction extends EditCrosstabAction {
  public MenuEditCrosstabAction() {
    super();
    putValue( Action.NAME, ActionMessages.getString( "MenuEditCrosstabAction.Text" ) );
    putValue( Action.MNEMONIC_KEY, ActionMessages.getString( "MenuEditCrosstabAction.Mnemonic" ) );
  }
}
