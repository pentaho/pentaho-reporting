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

import org.pentaho.reporting.engine.classic.core.metadata.MaturityLevel;
import org.pentaho.reporting.engine.classic.extensions.drilldown.DrillDownProfile;
import org.pentaho.reporting.libraries.designtime.swing.CommonDialog;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

public abstract class AbstractDrillDownEditor extends CommonDialog {
  private JTextField nameTextField;
  private JTextField bundleTextField;
  private JCheckBox expertBox;
  private JCheckBox hiddenBox;
  private JCheckBox deprecatedBox;
  private JCheckBox preferredBox;


  public AbstractDrillDownEditor() {
    init();
  }

  public AbstractDrillDownEditor( final Dialog parent ) {
    super( parent );
    init();
  }

  public AbstractDrillDownEditor( final Frame parent ) {
    super( parent );
    init();
  }

  protected void init() {
    nameTextField = new JTextField();
    bundleTextField = new JTextField();

    expertBox = new JCheckBox( "Expert" );
    hiddenBox = new JCheckBox( "Hidden" );
    deprecatedBox = new JCheckBox( "Deprecated" );
    preferredBox = new JCheckBox( "Preferred" );
    super.init();
  }

  protected Component createContentPane() {
    final JPanel panel = new JPanel();
    panel.setLayout( new GridBagLayout() );

    GridBagConstraints gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 0;
    panel.add( new JLabel( "Name" ), gbc );

    gbc = new GridBagConstraints();
    gbc.gridx = 1;
    gbc.gridy = 0;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.weightx = 1;
    panel.add( nameTextField, gbc );

    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 1;
    panel.add( new JLabel( "Bundle" ), gbc );

    gbc = new GridBagConstraints();
    gbc.gridx = 1;
    gbc.gridy = 1;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.weightx = 1;
    panel.add( bundleTextField, gbc );

    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 2;
    panel.add( expertBox, gbc );

    gbc = new GridBagConstraints();
    gbc.gridx = 1;
    gbc.gridy = 2;
    panel.add( hiddenBox, gbc );

    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 3;
    panel.add( deprecatedBox, gbc );

    gbc = new GridBagConstraints();
    gbc.gridx = 1;
    gbc.gridy = 3;
    panel.add( preferredBox, gbc );

    final JPanel contentPane = new JPanel();
    contentPane.setLayout( new BorderLayout() );
    contentPane.add( panel, BorderLayout.NORTH );
    contentPane.add( createDetailPane() );
    return contentPane;
  }

  protected abstract Component createDetailPane();

  public boolean isExpert() {
    return expertBox.isSelected();
  }

  public void setExpert( final boolean b ) {
    expertBox.setSelected( b );
  }

  public boolean isHidden() {
    return hiddenBox.isSelected();
  }

  public void setHidden( final boolean b ) {
    hiddenBox.setSelected( b );
  }

  public boolean isDeprecated() {
    return deprecatedBox.isSelected();
  }

  public void setDeprecated( final boolean b ) {
    deprecatedBox.setSelected( b );
  }

  public boolean isPreferred() {
    return preferredBox.isSelected();
  }

  public void setPreferred( final boolean b ) {
    preferredBox.setSelected( b );
  }

  public String getProfileName() {
    return nameTextField.getText();
  }

  public void setProfileName( final String name ) {
    nameTextField.setText( name );
  }

  public String getBundleName() {
    return bundleTextField.getText();
  }

  public void setBundleName( final String name ) {
    bundleTextField.setText( name );
  }

  public void updateUi( final DrillDownProfile profile ) {
    if ( profile == null ) {
      setBundleName( null );
      setProfileName( null );
      setDeprecated( false );
      setHidden( false );
      setPreferred( false );
      setExpert( false );
      return;
    }

    setBundleName( profile.getBundleLocation() );
    setProfileName( profile.getName() );
    setDeprecated( profile.isDeprecated() );
    setHidden( profile.isHidden() );
    setPreferred( profile.isPreferred() );
    setExpert( profile.isExpert() );
  }

  protected abstract Class getCustomizerType();

  public DrillDownProfile createFromUI() {
    return new DrillDownProfile( getProfileName(), getBundleName(), getProfileName() + ".",
      isExpert(), isPreferred(), isHidden(), isDeprecated(), getCustomizerType(), getAttributes(),
      MaturityLevel.Production, -1 );
  }

  protected abstract Map<String, String> getAttributes();

  public DrillDownProfile performEdit( final DrillDownProfile profile ) {
    updateUi( profile );
    if ( super.performEdit() ) {
      return createFromUI();
    }
    return null;
  }
}
