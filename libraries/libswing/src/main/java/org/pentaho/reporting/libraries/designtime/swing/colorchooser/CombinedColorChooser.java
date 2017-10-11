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

import org.pentaho.reporting.libraries.designtime.swing.VerticalLayout;
import org.pentaho.reporting.libraries.designtime.swing.event.DocumentChangeHandler;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CombinedColorChooser extends AbstractColorChooserPanel {
  private class ChooseRGBComponentActionHandler implements ActionListener {
    private RGBColorSelectorPanel.ColorComponents component;

    private ChooseRGBComponentActionHandler( final RGBColorSelectorPanel.ColorComponents component ) {
      this.component = component;
    }

    public void actionPerformed( final ActionEvent e ) {
      rgbColorSelectorPanel.setComponent( component );
      colorSelectorLayout.last( colorSelectorPanel );
      reconfigureSlider( component );
    }
  }

  private class ChooseHSBComponentActionHandler implements ActionListener {
    private HSBColorSelectorPanel.ColorComponents component;

    private ChooseHSBComponentActionHandler( final HSBColorSelectorPanel.ColorComponents component ) {
      this.component = component;
    }

    public void actionPerformed( final ActionEvent e ) {
      hsbColorSelectorPanel.setComponent( component );
      colorSelectorLayout.first( colorSelectorPanel );
      reconfigureSlider( component );
    }
  }

  private class ColorSelectionHandler implements ChangeListener {
    private ColorSelectionHandler() {
    }

    public void stateChanged( final ChangeEvent e ) {
      final Color color = getColorFromModel();
      colorModel.setSelectedColor( color );
    }

  }

  private class ExtColorHandler implements ChangeListener {
    private ExtColorHandler() {
    }

    private String colorToHex( final Color c ) {
      final String color = Integer.toHexString( c.getRGB() & 0x00ffffff );
      final StringBuffer retval = new StringBuffer( 6 );
      final int fillUp = 6 - color.length();
      for ( int i = 0; i < fillUp; i++ ) {
        retval.append( '0' );
      }

      retval.append( color );
      return retval.toString().toUpperCase();
    }

    public void stateChanged( final ChangeEvent e ) {
      if ( processExternalEvent ) {
        return;
      }
      try {
        processExternalEvent = true;
        final Color color = colorModel.getSelectedColor();
        getColorSelectionModel().copyInto( colorModel );

        red.setValue( colorModel.getRed() );
        green.setValue( colorModel.getGreen() );
        blue.setValue( colorModel.getBlue() );
        hue.setValue( colorModel.getHue() );
        saturation.setValue( colorModel.getSaturation() );
        value.setValue( colorModel.getValue() );
        colorCode.setText( colorToHex( color ) );
        previewPanel.setCurrent( color );
      } finally {
        processExternalEvent = false;
      }
    }
  }

  private class HSBKeyboardInputHandler implements ChangeListener {
    public void stateChanged( final ChangeEvent e ) {
      if ( processExternalEvent ) {
        return;
      }
      colorModel.setHSB( (Integer) hue.getValue(), (Integer) saturation.getValue(), (Integer) value.getValue() );
    }
  }

  private class RGBKeyboardInputHandler implements ChangeListener {
    public void stateChanged( final ChangeEvent e ) {
      if ( processExternalEvent ) {
        return;
      }
      colorModel.setRGB( (Integer) red.getValue(), (Integer) green.getValue(), (Integer) blue.getValue() );
    }
  }

  private class ColorCodeInputHandler extends DocumentChangeHandler {
    private ColorCodeInputHandler() {
    }

    protected void handleChange( final DocumentEvent e ) {
      if ( processExternalEvent ) {
        return;
      }

      final String text = colorCode.getText();
      try {
        final int value = Integer.parseInt( text, 16 );
        colorModel.setSelectedColor( new Color( value ) );
      } catch ( Exception ex ) {
        // ignore ..
      }
    }
  }

  private class SliderChangeHandler implements ChangeListener {
    private SliderChangeHandler() {
    }

    public void stateChanged( final ChangeEvent e ) {
      if ( sliderSelector == null ) {
        return;
      }
      final Object component = sliderSelector;
      if ( HSBColorSelectorPanel.ColorComponents.BRIGHTNESS.equals( component ) ) {
        colorModel.setHSB( colorModel.getHue(), colorModel.getSaturation(), selectedValueSlider.getValue() );
      }
      if ( HSBColorSelectorPanel.ColorComponents.SATURATION.equals( component ) ) {
        colorModel.setHSB( colorModel.getHue(), selectedValueSlider.getValue(), colorModel.getValue() );
      }
      if ( HSBColorSelectorPanel.ColorComponents.HUE.equals( component ) ) {
        colorModel.setHSB( selectedValueSlider.getValue(), colorModel.getSaturation(), colorModel.getValue() );
      }
      if ( RGBColorSelectorPanel.ColorComponents.RED.equals( component ) ) {
        colorModel.setRGB( selectedValueSlider.getValue(), colorModel.getGreen(), colorModel.getBlue() );
      }
      if ( RGBColorSelectorPanel.ColorComponents.GREEN.equals( component ) ) {
        colorModel.setRGB( colorModel.getRed(), selectedValueSlider.getValue(), colorModel.getBlue() );
      }
      if ( RGBColorSelectorPanel.ColorComponents.BLUE.equals( component ) ) {
        colorModel.setRGB( colorModel.getRed(), colorModel.getGreen(), selectedValueSlider.getValue() );
      }
    }
  }

  private ExtendedColorModel colorModel;
  private ColorSelectionHandler selectionHandler;
  private JRadioButton hueSelector;
  private JRadioButton saturationSelector;
  private JRadioButton valueSelector;
  private ColorPreviewPanel previewPanel;

  private JRadioButton redSelector;
  private JRadioButton greenSelector;
  private JRadioButton blueSelector;

  private JSpinner hue;
  private JSpinner saturation;
  private JSpinner value;

  private JSpinner red;
  private JSpinner green;
  private JSpinner blue;
  private JTextField colorCode;

  private JPanel colorSelectorPanel;
  private CardLayout colorSelectorLayout;
  private HSBColorSelectorPanel hsbColorSelectorPanel;
  private RGBColorSelectorPanel rgbColorSelectorPanel;
  private boolean processExternalEvent;
  private JSlider selectedValueSlider;
  private Object sliderSelector;

  public CombinedColorChooser() {
    previewPanel = new ColorPreviewPanel();

    colorModel = new ExtendedColorModel();
    colorModel.addChangeListener( new ExtColorHandler() );

    selectionHandler = new ColorSelectionHandler();

    selectedValueSlider = new JSlider();
    selectedValueSlider.setOrientation( JSlider.VERTICAL );
    selectedValueSlider.addChangeListener( new SliderChangeHandler() );


    final ColorChooserMessages messages = ColorChooserMessages.getInstance();

    hueSelector = new JRadioButton( messages.getString( "Hue" ) );
    hueSelector.setSelected( true );
    hueSelector.addActionListener( new ChooseHSBComponentActionHandler( HSBColorSelectorPanel.ColorComponents.HUE ) );
    saturationSelector = new JRadioButton( messages.getString( "Saturation" ) );
    saturationSelector
      .addActionListener( new ChooseHSBComponentActionHandler( HSBColorSelectorPanel.ColorComponents.SATURATION ) );
    valueSelector = new JRadioButton( messages.getString( "Brightness" ) );
    valueSelector
      .addActionListener( new ChooseHSBComponentActionHandler( HSBColorSelectorPanel.ColorComponents.BRIGHTNESS ) );

    redSelector = new JRadioButton( messages.getString( "Red" ) );
    redSelector.addActionListener( new ChooseRGBComponentActionHandler( RGBColorSelectorPanel.ColorComponents.RED ) );
    greenSelector = new JRadioButton( messages.getString( "Green" ) );
    greenSelector
      .addActionListener( new ChooseRGBComponentActionHandler( RGBColorSelectorPanel.ColorComponents.GREEN ) );
    blueSelector = new JRadioButton( messages.getString( "Blue" ) );
    blueSelector.addActionListener( new ChooseRGBComponentActionHandler( RGBColorSelectorPanel.ColorComponents.BLUE ) );

    final ButtonGroup bg = new ButtonGroup();
    bg.add( hueSelector );
    bg.add( saturationSelector );
    bg.add( valueSelector );
    bg.add( redSelector );
    bg.add( greenSelector );
    bg.add( blueSelector );

    hue = new JSpinner( new SpinnerNumberModel( 0, 0, 359, 1 ) );
    hue.addChangeListener( new HSBKeyboardInputHandler() );
    saturation = new JSpinner( new SpinnerNumberModel( 0, 0, 100, 1 ) );
    saturation.addChangeListener( new HSBKeyboardInputHandler() );
    value = new JSpinner( new SpinnerNumberModel( 0, 0, 100, 1 ) );
    value.addChangeListener( new HSBKeyboardInputHandler() );

    red = new JSpinner( new SpinnerNumberModel( 0, 0, 255, 1 ) );
    red.addChangeListener( new RGBKeyboardInputHandler() );
    green = new JSpinner( new SpinnerNumberModel( 0, 0, 255, 1 ) );
    green.addChangeListener( new RGBKeyboardInputHandler() );
    blue = new JSpinner( new SpinnerNumberModel( 0, 0, 255, 1 ) );
    blue.addChangeListener( new RGBKeyboardInputHandler() );

    colorCode = new JTextField();
    colorCode.setColumns( 6 );
    colorCode.getDocument().addDocumentListener( new ColorCodeInputHandler() );

    hsbColorSelectorPanel = new HSBColorSelectorPanel();
    rgbColorSelectorPanel = new RGBColorSelectorPanel();

    colorSelectorLayout = new CardLayout();

    colorSelectorPanel = new JPanel();
    colorSelectorPanel.setLayout( colorSelectorLayout );
    colorSelectorPanel.add( "HSB", hsbColorSelectorPanel ); // NON-NLS
    colorSelectorPanel.add( "RGB", rgbColorSelectorPanel ); // NON-NLS

    colorSelectorLayout.first( colorSelectorPanel );

    buildChooser();

    reconfigureSlider( HSBColorSelectorPanel.ColorComponents.HUE );
  }

  private JPanel createValueInputPanel() {

    final JPanel leftPanel = new JPanel();
    leftPanel.setLayout( new GridBagLayout() );
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.gridwidth = 2;
    gbc.gridheight = 1;
    leftPanel.add( hueSelector, gbc );

    gbc = new GridBagConstraints();
    gbc.gridx = 2;
    gbc.gridy = 0;
    gbc.gridwidth = 1;
    gbc.gridheight = 1;
    leftPanel.add( hue, gbc );

    gbc = new GridBagConstraints();
    gbc.gridx = 3;
    gbc.gridy = 0;
    gbc.gridwidth = 1;
    gbc.gridheight = 1;
    leftPanel.add( new JLabel( "\u00b0" ), gbc );

    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 1;
    gbc.gridwidth = 2;
    gbc.gridheight = 1;
    leftPanel.add( saturationSelector, gbc );

    gbc = new GridBagConstraints();
    gbc.gridx = 2;
    gbc.gridy = 1;
    gbc.gridwidth = 1;
    gbc.gridheight = 1;
    leftPanel.add( saturation, gbc );

    gbc = new GridBagConstraints();
    gbc.gridx = 3;
    gbc.gridy = 1;
    gbc.gridwidth = 1;
    gbc.gridheight = 1;
    leftPanel.add( new JLabel( "%" ), gbc );

    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 2;
    gbc.gridwidth = 2;
    gbc.gridheight = 1;
    leftPanel.add( valueSelector, gbc );

    gbc = new GridBagConstraints();
    gbc.gridx = 2;
    gbc.gridy = 2;
    gbc.gridwidth = 1;
    gbc.gridheight = 1;
    leftPanel.add( value, gbc );

    gbc = new GridBagConstraints();
    gbc.gridx = 3;
    gbc.gridy = 2;
    gbc.gridwidth = 1;
    gbc.gridheight = 1;
    leftPanel.add( new JLabel( "%" ), gbc );

    gbc = new GridBagConstraints();
    gbc.gridx = 1;
    gbc.gridy = 3;
    gbc.gridwidth = 3;
    gbc.gridheight = 1;
    leftPanel.add( Box.createRigidArea( new Dimension( 20, 20 ) ), gbc );

    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 4;
    gbc.gridwidth = 2;
    gbc.gridheight = 1;
    leftPanel.add( redSelector, gbc );

    gbc = new GridBagConstraints();
    gbc.gridx = 2;
    gbc.gridy = 4;
    gbc.gridwidth = 1;
    gbc.gridheight = 1;
    leftPanel.add( red, gbc );

    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 5;
    gbc.gridwidth = 2;
    gbc.gridheight = 1;
    leftPanel.add( greenSelector, gbc );

    gbc = new GridBagConstraints();
    gbc.gridx = 2;
    gbc.gridy = 5;
    gbc.gridwidth = 1;
    gbc.gridheight = 1;
    leftPanel.add( green, gbc );

    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 6;
    gbc.gridwidth = 2;
    gbc.gridheight = 1;
    leftPanel.add( blueSelector, gbc );

    gbc = new GridBagConstraints();
    gbc.gridx = 2;
    gbc.gridy = 6;
    gbc.gridwidth = 1;
    gbc.gridheight = 1;
    leftPanel.add( blue, gbc );

    gbc = new GridBagConstraints();
    gbc.gridx = 1;
    gbc.gridy = 7;
    gbc.gridwidth = 3;
    gbc.gridheight = 1;
    leftPanel.add( Box.createRigidArea( new Dimension( 20, 20 ) ), gbc );

    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 8;
    gbc.gridwidth = 1;
    gbc.gridheight = 1;
    leftPanel.add( new JLabel( "#" ), gbc );

    gbc = new GridBagConstraints();
    gbc.gridx = 1;
    gbc.gridy = 8;
    gbc.gridwidth = 3;
    gbc.gridheight = 1;
    leftPanel.add( colorCode, gbc );

    return leftPanel;
  }

  protected void buildChooser() {
    setLayout( new GridBagLayout() );
    setBorder( new EmptyBorder( 5, 5, 5, 5 ) );

    GridBagConstraints gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.gridwidth = 1;
    gbc.gridheight = 3;
    gbc.weightx = 1;
    gbc.weighty = 1;
    gbc.fill = GridBagConstraints.BOTH;
    add( colorSelectorPanel, gbc );

    gbc = new GridBagConstraints();
    gbc.gridx = 1;
    gbc.gridy = 0;
    gbc.gridwidth = 1;
    gbc.gridheight = 3;
    gbc.weighty = 1;
    gbc.fill = GridBagConstraints.BOTH;
    add( selectedValueSlider, gbc );

    gbc = new GridBagConstraints();
    gbc.gridx = 2;
    gbc.gridy = 0;
    gbc.gridwidth = 1;
    gbc.gridheight = 1;
    add( createPreviewPanel(), gbc );

    gbc = new GridBagConstraints();
    gbc.gridx = 2;
    gbc.gridy = 1;
    gbc.gridwidth = 1;
    gbc.gridheight = 1;
    add( createValueInputPanel(), gbc );
  }

  private JComponent createPreviewPanel() {
    final ColorChooserMessages messages = ColorChooserMessages.getInstance();
    final JPanel p = new JPanel();
    p.setLayout( new VerticalLayout() );
    p.add( new JLabel( messages.getString( "New" ) ) );
    p.add( previewPanel );
    p.add( new JLabel( messages.getString( "Old" ) ) );
    return p;
  }

  public String getDisplayName() {
    final ColorChooserMessages messages = ColorChooserMessages.getInstance();
    return messages.getString( "CombinedTitle" );
  }

  public Icon getSmallDisplayIcon() {
    return null;
  }

  public void installChooserPanel( final ExtendedColorModel enclosingChooser ) {
    super.installChooserPanel( enclosingChooser );
    rgbColorSelectorPanel.setColorSelectionModel( colorModel );
    hsbColorSelectorPanel.setColorSelectionModel( colorModel );
    final ExtendedColorModel colorSelectionModel = getColorSelectionModel();
    if ( colorSelectionModel != null ) {
      colorSelectionModel.addChangeListener( selectionHandler );
      selectionHandler.stateChanged( null );
      previewPanel.setPrevious( colorSelectionModel.getSelectedColor() );
    }
  }

  public void uninstallChooserPanel() {
    final ExtendedColorModel colorSelectionModel = getColorSelectionModel();
    if ( colorSelectionModel != null ) {
      colorSelectionModel.removeChangeListener( selectionHandler );
    }
    super.uninstallChooserPanel();
    rgbColorSelectorPanel.setColorSelectionModel( null );
    hsbColorSelectorPanel.setColorSelectionModel( null );
  }

  private void reconfigureSlider( final Object component ) {
    sliderSelector = null;
    if ( HSBColorSelectorPanel.ColorComponents.BRIGHTNESS.equals( component ) ) {
      selectedValueSlider.setMinimum( 0 );
      selectedValueSlider.setMaximum( 100 );
      selectedValueSlider.setValue( colorModel.getValue() );
    }
    if ( HSBColorSelectorPanel.ColorComponents.SATURATION.equals( component ) ) {
      selectedValueSlider.setMinimum( 0 );
      selectedValueSlider.setMaximum( 100 );
      selectedValueSlider.setValue( colorModel.getSaturation() );
    }
    if ( HSBColorSelectorPanel.ColorComponents.HUE.equals( component ) ) {
      selectedValueSlider.setMinimum( 0 );
      selectedValueSlider.setMaximum( 359 );
      selectedValueSlider.setValue( colorModel.getHue() );
    }
    if ( RGBColorSelectorPanel.ColorComponents.RED.equals( component ) ) {
      selectedValueSlider.setMinimum( 0 );
      selectedValueSlider.setMaximum( 255 );
      selectedValueSlider.setValue( colorModel.getRed() );
    }
    if ( RGBColorSelectorPanel.ColorComponents.GREEN.equals( component ) ) {
      selectedValueSlider.setMinimum( 0 );
      selectedValueSlider.setMaximum( 255 );
      selectedValueSlider.setValue( colorModel.getGreen() );
    }
    if ( RGBColorSelectorPanel.ColorComponents.BLUE.equals( component ) ) {
      selectedValueSlider.setMinimum( 0 );
      selectedValueSlider.setMaximum( 255 );
      selectedValueSlider.setValue( colorModel.getBlue() );
    }
    sliderSelector = component;
  }


}
