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

package org.pentaho.reporting.engine.classic.extensions.drilldown.devtools;

import org.pentaho.reporting.engine.classic.extensions.drilldown.DrillDownProfile;
import org.pentaho.reporting.engine.classic.extensions.drilldown.PatternLinkCustomizer;
import org.pentaho.reporting.libraries.base.util.StringUtils;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class PatternDrillDownEditor extends AbstractDrillDownEditor {
  private JTextArea patternField;
  private JTextField extensionField;

  public PatternDrillDownEditor( final Dialog owner ) {
    super( owner );
    setModal( true );
  }

  protected void init() {
    patternField = new JTextArea();
    extensionField = new JTextField();
    super.init();
  }

  protected String getDialogId() {
    return "extensions.drilldown.PatternDrillDownEditor";
  }

  protected Component createDetailPane() {
    final JPanel panel = new JPanel();
    panel.setLayout( new GridBagLayout() );

    GridBagConstraints gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 0;
    panel.add( new JLabel( "Pattern" ), gbc );

    gbc = new GridBagConstraints();
    gbc.gridx = 1;
    gbc.gridy = 0;
    gbc.fill = GridBagConstraints.BOTH;
    gbc.weightx = 1;
    gbc.weighty = 1;
    panel.add( new JScrollPane( patternField ), gbc );

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
    return PatternLinkCustomizer.class;
  }

  public void updateUi( final DrillDownProfile profile ) {
    if ( profile == null ) {
      patternField.setText( null );
      extensionField.setText( null );
    } else {
      patternField.setText( profile.getAttribute( "pattern" ) );
      extensionField.setText( profile.getAttribute( "extension" ) );
    }
    super.updateUi( profile );
  }

  protected Map<String, String> getAttributes() {
    final HashMap<String, String> map = new HashMap<String, String>();
    if ( StringUtils.isEmpty( patternField.getText() ) == false ) {
      map.put( "pattern", patternField.getText() );
    }

    if ( StringUtils.isEmpty( extensionField.getText() ) == false ) {
      map.put( "extension", extensionField.getText() );
    }
    return map;
  }
}
