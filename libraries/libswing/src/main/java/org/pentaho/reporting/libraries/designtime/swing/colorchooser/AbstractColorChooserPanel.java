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
