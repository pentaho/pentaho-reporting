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
