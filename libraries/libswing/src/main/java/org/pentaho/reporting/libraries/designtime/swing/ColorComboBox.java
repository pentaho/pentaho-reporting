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

package org.pentaho.reporting.libraries.designtime.swing;

import javax.swing.Action;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.plaf.basic.BasicComboBoxEditor;
import javax.swing.UIManager;
import javax.swing.border.EtchedBorder;
import javax.swing.border.MatteBorder;
import javax.swing.BorderFactory;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.Color;
import java.awt.Component;

/**
 * A predefined combobox that contains the predefined excel color pallette.
 */
public class ColorComboBox extends SmartComboBox {
  /**
   * Creates a new color combobox and populates it with the excel colors.
   */
  public ColorComboBox() {
    final DefaultComboBoxModel model = new DefaultComboBoxModel( ColorUtility.getPredefinedExcelColors() );
    model.insertElementAt( null, 0 );
    model.setSelectedItem( null );

    setModel( model );
    setRenderer( new ColorCellRenderer() );
    final int height1 = getPreferredSize().height;
    setMaximumSize( new Dimension( height1 * 4, height1 ) );
    setFocusable( false );

    final boolean isGTK = isGTKLookAndFeel();
    setEditable( isGTK );

    // if it's GTK LnF, we have to customize the combo box since GTK Lnf ignores the <i>setBackground</i>
    if ( isGTK ) {
      setEditor( new CustomGTKComboBoxEditor() );
    }
  }

  /**
   * Custom GTK ComboBox Editor since GTK ignores background color setting of JTextField (PRD-6098)
   */
  private class CustomGTKComboBoxEditor extends BasicComboBoxEditor {
    private JLabel label = new JLabel();
    private JPanel panel = new JPanel();
    private Object selectedItem;

    public CustomGTKComboBoxEditor() {
      label.setOpaque( false );

      panel.setLayout( new GridBagLayout() );
      panel.add( label );
      panel.setOpaque( true );

      panel.setBorder(
        BorderFactory.createCompoundBorder(
          new MatteBorder( 1, 1, 1, 1, Color.LIGHT_GRAY ),
          new EtchedBorder()
        )
      );
    }

    @Override
    public Component getEditorComponent() {
      return this.panel;
    }

    @Override
    public Object getItem() {
      return this.selectedItem;
    }

    @Override
    public void setItem( Object item ) {
      this.selectedItem = item;
      Color color = (Color) item;
      String txt = ( color == null ) ? Messages.getInstance().getString( "ColorCellRenderer.Automatic" )
        : ColorUtility.toAttributeValue( color );
      label.setText( txt );

      if ( color == null ) {
        color = Color.WHITE; // Automatic
      }
      label.setBackground( color );

      label.setForeground(
        ColorUtility.getBrightness( color ) > ColorUtility.BRIGHTNESS_THRESHOLD ? Color.BLACK : Color.WHITE
      );

      panel.setBackground( color );
    }
  }

  /**
   * Defines the selected value without firing a action event.
   *
   * @param o the new selected value.
   */
  public void setValueFromModel( final Color o ) {
    final Action old = getAction();
    setAction( null );
    setSelectedItem( o );
    setAction( old );
  }

  /**
   * Returns the currently selected value from the model, or null if no value is selected.
   *
   * @return the selected color.
   */
  public Color getValueFromModel() {
    return (Color) getSelectedItem();
  }

  /**
   * Determines if report designer is running with the GTK Look and Feel. Typically some linux distros
   *
   * @return whether is GTK or not
   */
  private boolean isGTKLookAndFeel() {
    return "GTK".equalsIgnoreCase( UIManager.getLookAndFeel().getID() );
  }

}
