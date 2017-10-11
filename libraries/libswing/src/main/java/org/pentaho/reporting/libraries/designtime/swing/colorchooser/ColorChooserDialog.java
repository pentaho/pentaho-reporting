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

import org.pentaho.reporting.libraries.designtime.swing.CommonDialog;

import java.awt.*;

public class ColorChooserDialog extends CommonDialog {
  private ColorChooserPane colorChooserPane;

  public ColorChooserDialog() {
    init();
  }

  public ColorChooserDialog( final Frame owner ) throws HeadlessException {
    super( owner );
    init();
  }

  public ColorChooserDialog( final Dialog owner ) throws HeadlessException {
    super( owner );
    init();
  }

  public void addColorChooserPanel( final AbstractColorChooserPanel panel ) {
    colorChooserPane.addColorChooserPanel( panel );
  }

  public AbstractColorChooserPanel getColorChooserPanel( final int index ) {
    return colorChooserPane.getColorChooserPanel( index );
  }

  public void clearSwatches() {
    colorChooserPane.clearSwatches();
  }

  public SwatchColorChooser getSwatchColorChooser() {
    return colorChooserPane.getSwatchColorChooser();
  }

  public int getColorChooserPanelCount() {
    return colorChooserPane.getColorChooserPanelCount();
  }

  public void removeAllColorChooserPanels() {
    colorChooserPane.removeAllColorChooserPanels();
  }

  public void removeColorChooserPanel( final AbstractColorChooserPanel panel ) {
    colorChooserPane.removeColorChooserPanel( panel );
  }

  public ExtendedColorModel getModel() {
    return colorChooserPane.getModel();
  }

  protected ColorChooserPane getColorChooserPane() {
    return colorChooserPane;
  }

  protected void init() {
    setTitle( ColorChooserMessages.getInstance().getString( "ColorChooserDialog.Title" ) );
    colorChooserPane = new ColorChooserPane();
    super.init();
  }


  protected String getDialogId() {
    return "LibSwing.ColorChooser";
  }

  protected Component createContentPane() {
    return colorChooserPane;
  }

  public Color performEdit( final Color color, final ColorSchema[] colorSchemas ) {
    if ( colorSchemas != null ) {
      colorChooserPane.clearSwatches();
      for ( final ColorSchema colorSchema : colorSchemas ) {
        colorChooserPane.addSwatches( colorSchema );
      }
    }
    getModel().setSelectedColor( color );
    if ( performEdit() ) {
      return getModel().getSelectedColor();
    }
    return null;
  }
}
