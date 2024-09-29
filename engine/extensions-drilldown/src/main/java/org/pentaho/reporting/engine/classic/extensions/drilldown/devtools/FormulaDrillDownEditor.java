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


package org.pentaho.reporting.engine.classic.extensions.drilldown.devtools;

import org.pentaho.reporting.engine.classic.extensions.drilldown.DrillDownProfile;
import org.pentaho.reporting.engine.classic.extensions.drilldown.FormulaLinkCustomizer;
import org.pentaho.reporting.libraries.base.util.StringUtils;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class FormulaDrillDownEditor extends AbstractDrillDownEditor {
  private JTextArea formulaField;
  private JTextField extensionField;

  public FormulaDrillDownEditor( final Dialog owner ) {
    super( owner );
    setModal( true );
  }

  protected void init() {
    formulaField = new JTextArea();
    extensionField = new JTextField();
    super.init();
  }

  protected String getDialogId() {
    return "extensions.drilldown.FormulaDrillDownEditor";
  }

  protected Component createDetailPane() {
    final JPanel panel = new JPanel();
    panel.setLayout( new GridBagLayout() );

    GridBagConstraints gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 0;
    panel.add( new JLabel( "Formula" ), gbc );

    gbc = new GridBagConstraints();
    gbc.gridx = 1;
    gbc.gridy = 0;
    gbc.fill = GridBagConstraints.BOTH;
    gbc.weightx = 1;
    gbc.weighty = 1;
    panel.add( new JScrollPane( formulaField ), gbc );

    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 1;
    panel.add( new JLabel( "Extension" ), gbc );

    gbc = new GridBagConstraints();
    gbc.gridx = 1;
    gbc.gridy = 1;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.weightx = 1;
    panel.add( extensionField, gbc );

    return panel;
  }

  protected Class getCustomizerType() {
    return FormulaLinkCustomizer.class;
  }

  public void updateUi( final DrillDownProfile profile ) {
    if ( profile == null ) {
      formulaField.setText( null );
      extensionField.setText( null );
    } else {
      formulaField.setText( profile.getAttribute( "formula" ) );
      extensionField.setText( profile.getAttribute( "extension" ) );
    }
    super.updateUi( profile );
  }

  protected Map<String, String> getAttributes() {
    final HashMap<String, String> map = new HashMap<String, String>();
    if ( StringUtils.isEmpty( formulaField.getText() ) == false ) {
      map.put( "formula", formulaField.getText() );
    }

    if ( StringUtils.isEmpty( extensionField.getText() ) == false ) {
      map.put( "extension", extensionField.getText() );
    }
    return map;
  }
}
