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

package org.pentaho.reporting.libraries.designtime.swing;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.EventListenerList;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.util.Arrays;


/**
 * A panel that edits the basic font properties.
 * <p/>
 * <ul> <li>Font-Family</li> <li>Font-Style (bold, italics)</li> <li>Font-Size</li> <li>underline</li>
 * <li>strikethrough</li> <li>aliasing</li></ul>
 *
 * @author Thomas Morgner
 */
public class BasicFontPropertiesPane extends JPanel {
  private class FontSizeUpdateHandler implements ListSelectionListener, DocumentListener {
    private boolean inUpdate;

    private FontSizeUpdateHandler() {
    }

    public void valueChanged( final ListSelectionEvent e ) {
      if ( inUpdate ) {
        return;
      }
      try {
        inUpdate = true;
        final Object value = fontSizeList.getSelectedValue();
        if ( value != null ) {
          fontSizeTextBox.setText( String.valueOf( value ) );
        }
        fireChangeEvent();
      } finally {
        inUpdate = false;
      }
    }

    private void updateFromTextField() {
      if ( inUpdate ) {
        return;
      }
      try {
        inUpdate = true;
        final String value = fontSizeTextBox.getText();
        if ( value != null && value.length() != 0 ) {
          try {
            fontSizeList.setSelectedValue( new Integer( value ), true );
          } catch ( final NumberFormatException nfe ) {
            // ignore
          }
          fireChangeEvent();
        }
      } finally {
        inUpdate = false;
      }

    }

    public void insertUpdate( final DocumentEvent e ) {
      updateFromTextField();
    }

    public void removeUpdate( final DocumentEvent e ) {
      updateFromTextField();
    }

    public void changedUpdate( final DocumentEvent e ) {
      updateFromTextField();
    }
  }

  private class FontNameUpdateHandler implements ListSelectionListener, DocumentListener {
    private boolean inUpdate;

    private FontNameUpdateHandler() {
    }

    public void valueChanged( final ListSelectionEvent e ) {
      if ( inUpdate ) {
        return;
      }
      try {
        inUpdate = true;
        final Object value = fontFamilyList.getSelectedValue();
        if ( value != null ) {
          fontFamilyTextBox.setText( (String) value );
          fireChangeEvent();
        }
      } finally {
        inUpdate = false;
      }
    }

    private void updateFromTextField() {
      if ( inUpdate ) {
        return;
      }
      try {
        inUpdate = true;
        final String value = fontFamilyTextBox.getText();
        if ( value != null && value.length() != 0 ) {
          fontFamilyList.setSelectedValue( value, true );
        }
        fireChangeEvent();
      } finally {
        inUpdate = false;
      }

    }

    public void insertUpdate( final DocumentEvent e ) {
      updateFromTextField();
    }

    public void removeUpdate( final DocumentEvent e ) {
      updateFromTextField();
    }

    public void changedUpdate( final DocumentEvent e ) {
      updateFromTextField();
    }
  }

  private class FontStyleUpdateHandler implements ListSelectionListener, ChangeListener {
    private FontStyleUpdateHandler() {
    }

    /**
     * Called whenever the value of the selection changes.
     *
     * @param e the event that characterizes the change.
     */
    public void valueChanged( final ListSelectionEvent e ) {
      fireChangeEvent();
    }

    /**
     * Invoked when the target of the listener has changed its state.
     *
     * @param e a ChangeEvent object
     */
    public void stateChanged( final ChangeEvent e ) {
      fireChangeEvent();
    }
  }

  private JTextField fontFamilyTextBox;
  private JTextField fontSizeTextBox;
  private JList fontFamilyList;
  private JList fontSizeList;
  private JList fontStyleList;
  private EventListenerList eventListenerList;
  private JCheckBox underlineCheckbox;
  private JCheckBox strikethroughCheckbox;
  private boolean extendedFontPropertiesShowing;

  public BasicFontPropertiesPane() {
    eventListenerList = new EventListenerList();

    final FontNameUpdateHandler nameUpdateHandler = new FontNameUpdateHandler();
    fontFamilyTextBox = new JTextField();
    fontFamilyTextBox.getDocument().addDocumentListener( nameUpdateHandler );
    fontFamilyList = new JList( createFontNameModel() );
    fontFamilyList.addListSelectionListener( nameUpdateHandler );

    final FontSizeUpdateHandler sizeUpdateHandler = new FontSizeUpdateHandler();
    fontSizeTextBox = new JTextField();
    fontSizeTextBox.getDocument().addDocumentListener( sizeUpdateHandler );
    fontSizeList = new JList( createFontSizeModel() );
    fontSizeList.addListSelectionListener( sizeUpdateHandler );

    fontStyleList = new JList( createFontStyleModel() );
    fontStyleList.getSelectionModel().addListSelectionListener( new FontStyleUpdateHandler() );

    extendedFontPropertiesShowing = true;
    underlineCheckbox = new JCheckBox( Messages.getInstance().getString( "BasicFontPropertiesPane.Underline" ) );
    underlineCheckbox.addChangeListener( new FontStyleUpdateHandler() );
    strikethroughCheckbox =
      new JCheckBox( Messages.getInstance().getString( "BasicFontPropertiesPane.Strikethrough" ) );
    strikethroughCheckbox.addChangeListener( new FontStyleUpdateHandler() );
  }

  protected boolean isExtendedFontPropertiesShowing() {
    return extendedFontPropertiesShowing;
  }

  public void setExtendedFontPropertiesShowing( final boolean extendedFontPropertiesShowing ) {
    this.extendedFontPropertiesShowing = extendedFontPropertiesShowing;
    underlineCheckbox.setVisible( extendedFontPropertiesShowing );
    strikethroughCheckbox.setVisible( extendedFontPropertiesShowing );
  }

  /**
   * @noinspection ReuseOfLocalVariable
   */
  public void init() {
    setLayout( new GridBagLayout() );

    GridBagConstraints gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.insets = new Insets( 5, 5, 0, 5 );
    add( new JLabel( Messages.getInstance().getString( "BasicFontPropertiesPane.FontFamily" ) ), gbc );

    gbc = new GridBagConstraints();
    gbc.gridx = 1;
    gbc.gridy = 0;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.insets = new Insets( 5, 5, 0, 5 );
    add( new JLabel( Messages.getInstance().getString( "BasicFontPropertiesPane.FontStyle" ) ), gbc );

    gbc = new GridBagConstraints();
    gbc.gridx = 2;
    gbc.gridy = 0;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.insets = new Insets( 5, 5, 0, 5 );
    add( new JLabel( Messages.getInstance().getString( "BasicFontPropertiesPane.FontSize" ) ), gbc );

    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 1;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.weightx = 1;
    gbc.insets = new Insets( 5, 5, 0, 5 );
    add( fontFamilyTextBox, gbc );

    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 2;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.fill = GridBagConstraints.BOTH;
    gbc.weightx = 1;
    gbc.weighty = 1;
    gbc.insets = new Insets( 0, 5, 5, 5 );
    add( new JScrollPane( fontFamilyList ), gbc );

    gbc = new GridBagConstraints();
    gbc.gridx = 1;
    gbc.gridy = 1;
    gbc.gridheight = 2;
    gbc.fill = GridBagConstraints.BOTH;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.weightx = 1;
    gbc.weighty = 1;
    gbc.insets = new Insets( 5, 5, 5, 5 );
    add( new JScrollPane( fontStyleList ), gbc );

    gbc = new GridBagConstraints();
    gbc.gridx = 2;
    gbc.gridy = 1;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.weightx = 1;
    gbc.insets = new Insets( 5, 5, 0, 5 );
    add( fontSizeTextBox, gbc );

    gbc = new GridBagConstraints();
    gbc.gridx = 2;
    gbc.gridy = 2;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.fill = GridBagConstraints.BOTH;
    gbc.weightx = 1;
    gbc.weighty = 1;
    gbc.insets = new Insets( 0, 5, 5, 5 );
    add( new JScrollPane( fontSizeList ), gbc );

    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 3;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.insets = new Insets( 0, 5, 0, 5 );
    add( underlineCheckbox, gbc );

    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 4;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.insets = new Insets( 0, 5, 0, 5 );
    add( strikethroughCheckbox, gbc );

    final Component aliasPane = createAliasPanel();
    if ( aliasPane != null ) {
      gbc = new GridBagConstraints();
      gbc.gridx = 1;
      gbc.gridy = 4;
      gbc.gridwidth = 2;
      gbc.anchor = GridBagConstraints.WEST;
      gbc.insets = new Insets( 0, 5, 0, 5 );
      add( aliasPane, gbc );
    }
    final JComponent previewPane = createPreviewPane();
    if ( previewPane != null ) {
      gbc = new GridBagConstraints();
      gbc.gridx = 0;
      gbc.gridy = 5;
      gbc.gridwidth = 3;
      gbc.anchor = GridBagConstraints.WEST;
      gbc.weightx = 1;
      gbc.weighty = 1;
      gbc.fill = GridBagConstraints.BOTH;
      gbc.insets = new Insets( 5, 5, 5, 5 );
      add( previewPane, gbc );
    }
  }

  public String getFontFamily() {
    return fontFamilyTextBox.getText();
  }

  public void setFontFamily( final String fontFamily ) {
    this.fontFamilyTextBox.setText( fontFamily );
  }

  public int getFontSize() {
    try {
      return Integer.parseInt( fontSizeTextBox.getText() );

    } catch ( final NumberFormatException nfe ) {
      // ignore exception
      return 10;
    }
  }

  public void setFontSize( final int fontSize ) {
    this.fontSizeTextBox.setText( String.valueOf( fontSize ) );
  }

  public int getFontStyle() {
    final int index = fontStyleList.getSelectedIndex();
    if ( index < 0 || index > 3 ) {
      return 0;
    }
    return index;
  }

  public void setFontStyle( final int fontStyle ) {
    if ( fontStyle < 0 || fontStyle > 3 ) {
      this.fontStyleList.setSelectedIndex( 0 );
    } else {
      this.fontStyleList.setSelectedIndex( fontStyle );
    }
  }

  public boolean isUnderlined() {
    return underlineCheckbox.isSelected();
  }

  public void setUnderlined( final boolean underlined ) {
    this.underlineCheckbox.setSelected( underlined );
  }

  public boolean isStrikeThrough() {
    return strikethroughCheckbox.isSelected();
  }

  public void setStrikeThrough( final boolean strikeThrough ) {
    this.strikethroughCheckbox.setSelected( strikeThrough );
  }

  protected JComponent createPreviewPane() {
    return null;
  }

  protected Component createAliasPanel() {
    return null;
  }

  public void addChangeListener( final ChangeListener changeListener ) {
    if ( changeListener == null ) {
      throw new NullPointerException();
    }
    eventListenerList.add( ChangeListener.class, changeListener );
  }

  public void removeChangeListener( final ChangeListener changeListener ) {
    if ( changeListener == null ) {
      throw new NullPointerException();
    }
    eventListenerList.remove( ChangeListener.class, changeListener );
  }

  protected void fireChangeEvent() {
    final ChangeEvent event = new ChangeEvent( this );
    final ChangeListener[] changeListeners = eventListenerList.getListeners( ChangeListener.class );
    for ( int i = 0; i < changeListeners.length; i++ ) {
      final ChangeListener changeListener = changeListeners[ i ];
      changeListener.stateChanged( event );
    }
  }

  /**
   * The model's item index matches the AWT-font-style flags.
   *
   * @return
   */
  private DefaultListModel createFontStyleModel() {
    final DefaultListModel model = new DefaultListModel();
    model.addElement( Messages.getInstance().getString( "BasicFontPropertiesPane.FontStylePlain" ) );
    model.addElement( Messages.getInstance().getString( "BasicFontPropertiesPane.FontStyleBold" ) );
    model.addElement( Messages.getInstance().getString( "BasicFontPropertiesPane.FontStyleItalics" ) );
    model.addElement( Messages.getInstance().getString( "BasicFontPropertiesPane.FontStyleBoldItalics" ) );
    return model;
  }

  private DefaultListModel createFontNameModel() {
    final String[] availableFontFamilyNames =
      GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
    Arrays.sort( availableFontFamilyNames );
    final DefaultListModel retval = new DefaultListModel();
    for ( int i = 0; i < availableFontFamilyNames.length; i++ ) {
      final String familyName = availableFontFamilyNames[ i ];
      retval.addElement( familyName );
    }
    return retval;
  }

  private DefaultListModel createFontSizeModel() {
    final Integer[] fontSizes = new Integer[] { 6, 8, 10, 12, 14, 16, 18, 20, 24, 28, 32, 36, 48, 72 };
    final DefaultListModel retval = new DefaultListModel();
    for ( int i = 0; i < fontSizes.length; i++ ) {
      final Integer fontSize = fontSizes[ i ];
      retval.addElement( fontSize );
    }
    return retval;
  }
}
