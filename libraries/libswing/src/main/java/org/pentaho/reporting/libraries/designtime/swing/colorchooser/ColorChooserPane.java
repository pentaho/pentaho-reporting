/*
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
* Copyright (c) 2000 - 2017 Hitachi Vantara and Contributors...
* All rights reserved.
*/

package org.pentaho.reporting.libraries.designtime.swing.colorchooser;

import javax.swing.*;
import javax.swing.colorchooser.ColorChooserComponentFactory;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;

public class ColorChooserPane extends JComponent {
  private class PreviewColorUpdater implements ChangeListener {
    private PreviewColorUpdater() {
    }

    public void stateChanged( final ChangeEvent e ) {
      previewPane.setForeground( colorModel.getSelectedColor() );
    }
  }

  private JComponent previewPane;
  private JTabbedPane multiSelectorPane;
  private ExtendedColorModel colorModel;
  private SwatchColorChooser swatchColorChooser;

  public ColorChooserPane() {
    colorModel = new ExtendedColorModel();
    colorModel.addChangeListener( new PreviewColorUpdater() );
    previewPane = ColorChooserComponentFactory.getPreviewPanel();
    multiSelectorPane = new JTabbedPane();
    swatchColorChooser = new SwatchColorChooser();

    addColorChooserPanel( new CombinedColorChooser() );
    addColorChooserPanel( swatchColorChooser );

    setLayout( new BorderLayout() );
    add( previewPane, BorderLayout.SOUTH );
    add( multiSelectorPane, BorderLayout.CENTER );
  }

  public JComponent getPreviewPane() {
    return previewPane;
  }

  public void addSwatches( final ColorSchema colorSchema ) {
    swatchColorChooser.addSwatches( colorSchema );
  }

  public void clearSwatches() {
    swatchColorChooser.clearSwatches();
  }

  public void removeSwatches( final ColorSchema colorSchema ) {
    swatchColorChooser.removeSwatches( colorSchema );
  }

  public SwatchColorChooser getSwatchColorChooser() {
    return swatchColorChooser;
  }

  public ExtendedColorModel getModel() {
    return colorModel;
  }

  public void addColorChooserPanel( final AbstractColorChooserPanel panel ) {
    multiSelectorPane.addTab( panel.getDisplayName(), panel.getSmallDisplayIcon(), panel );
    multiSelectorPane.setMnemonicAt( multiSelectorPane.getTabCount() - 1, panel.getMnemonic() );
    multiSelectorPane
      .setDisplayedMnemonicIndexAt( multiSelectorPane.getTabCount() - 1, panel.getDisplayedMnemonicIndex() );

    panel.installChooserPanel( colorModel );
  }

  public void removeColorChooserPanel( final AbstractColorChooserPanel panel ) {
    panel.uninstallChooserPanel();
    multiSelectorPane.remove( panel );
  }

  public AbstractColorChooserPanel getColorChooserPanel( final int index ) {
    return (AbstractColorChooserPanel) multiSelectorPane.getComponentAt( index );
  }

  public void removeAllColorChooserPanels() {
    final int count = getColorChooserPanelCount();
    for ( int i = 0; i < count; i++ ) {
      getColorChooserPanel( i ).uninstallChooserPanel();
    }
    multiSelectorPane.removeAll();
  }

  public int getColorChooserPanelCount() {
    return multiSelectorPane.getTabCount();
  }

  public void setColor( final Color value ) {
    colorModel.setSelectedColor( value );
  }
}
