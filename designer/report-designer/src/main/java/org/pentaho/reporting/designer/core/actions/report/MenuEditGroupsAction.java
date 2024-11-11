/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


package org.pentaho.reporting.designer.core.actions.report;

import org.pentaho.reporting.designer.core.actions.ActionMessages;

import javax.swing.*;

public class MenuEditGroupsAction extends EditGroupsAction {
  public MenuEditGroupsAction() {
    super();
    putValue( Action.NAME, ActionMessages.getString( "MenuEditGroupsAction.Text" ) );
    putValue( Action.MNEMONIC_KEY, ActionMessages.getString( "MenuEditGroupsAction.Mnemonic" ) );
  }
}
