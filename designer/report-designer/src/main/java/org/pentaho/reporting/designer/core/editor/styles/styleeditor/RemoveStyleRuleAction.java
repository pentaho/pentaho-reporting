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


package org.pentaho.reporting.designer.core.editor.styles.styleeditor;

import org.pentaho.reporting.engine.classic.core.style.css.ElementStyleRule;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class RemoveStyleRuleAction extends AbstractAction {
  private StyleDefinitionEditorContext context;
  private ElementStyleRule rule;

  public RemoveStyleRuleAction( final StyleDefinitionEditorContext context,
                                final ElementStyleRule rule ) {
    this.context = context;
    this.rule = rule;
    putValue( Action.NAME, "Remove Style-Rule" );
  }

  public void actionPerformed( final ActionEvent e ) {
    context.removeStyleRule( rule );
  }

}
