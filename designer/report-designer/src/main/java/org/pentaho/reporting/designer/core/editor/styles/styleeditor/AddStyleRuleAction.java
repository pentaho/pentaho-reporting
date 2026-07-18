/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 - 2026 by Pentaho Canada Inc. : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2030-06-15
 ******************************************************************************/



package org.pentaho.reporting.designer.core.editor.styles.styleeditor;

import org.pentaho.reporting.engine.classic.core.style.css.ElementStyleRule;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class AddStyleRuleAction extends AbstractAction {
  private StyleDefinitionEditorContext context;

  public AddStyleRuleAction( final StyleDefinitionEditorContext context ) {
    this.context = context;
    putValue( Action.NAME, "Add Style-Rule" );
  }

  public void actionPerformed( final ActionEvent e ) {
    context.addStyleRule( new ElementStyleRule() );
  }
}
