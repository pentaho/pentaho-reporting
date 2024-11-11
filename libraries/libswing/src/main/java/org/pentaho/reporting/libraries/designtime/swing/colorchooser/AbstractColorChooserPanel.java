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


package org.pentaho.reporting.libraries.designtime.swing.colorchooser;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;

public abstract class AbstractColorChooserPanel extends JComponent {
  private class ColorSelectionListener implements ChangeListener {
    private ColorSelectionListener() {
    }

    public void stateChanged( final ChangeEvent e ) {
      colorUpdated();
    }
  }

  private ExtendedColorModel colorSelectionModel;
  private ColorSelectionListener colorSelectionListener;

  protected AbstractColorChooserPanel() {
    colorSelectionListener = new ColorSelectionListener();
  }

  public ExtendedColorModel getColorSelectionModel() {
    return colorSelectionModel;
  }

  public abstract String getDisplayName();

  public abstract Icon getSmallDisplayIcon();

  public int getMnemonic() {
    return 0;
  }

  public int getDisplayedMnemonicIndex() {
    return -1;
  }

  public void installChooserPanel( final ExtendedColorModel colorSelectionModel ) {
    if ( colorSelectionModel == null ) {
      throw new NullPointerException();
    }
    if ( this.colorSelectionModel != null ) {
      this.colorSelectionModel.removeChangeListener( colorSelectionListener );
    }
    this.colorSelectionModel = colorSelectionModel;
    this.colorSelectionModel.addChangeListener( colorSelectionListener );
  }

  public void uninstallChooserPanel() {
    if ( this.colorSelectionModel != null ) {
      this.colorSelectionModel.removeChangeListener( colorSelectionListener );
    }
    this.colorSelectionModel = null;
  }

  protected Color getColorFromModel() {
    if ( colorSelectionModel == null ) {
      return null;
    }
    return colorSelectionModel.getSelectedColor();
  }

  protected void colorUpdated() {

  }
}
