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
 * Copyright (c) 2001 - 2013 Object Refinery Ltd, Hitachi Vantara and Contributors..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.modules.gui.base.internal;

import java.awt.Component;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ResourceBundle;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.pentaho.reporting.engine.classic.core.modules.gui.commonswing.FormValidator;
import org.pentaho.reporting.engine.classic.core.modules.gui.commonswing.NumericDocument;
import org.pentaho.reporting.engine.classic.core.modules.gui.commonswing.SwingCommonModule;
import org.pentaho.reporting.libraries.designtime.swing.LibSwingUtil;

/**
 * Creation-Date: 29.10.2007, 18:36:13
 *
 * @author Thomas Morgner
 */
public class NumericInputDialog extends JDialog {
  private class SyncValuesHandler implements DocumentListener, ChangeListener {
    private boolean inStateChange;

    private SyncValuesHandler() {
    }

    public void insertUpdate( final DocumentEvent e ) {
      if ( inStateChange ) {
        return;
      }
      inStateChange = true;
      try {
        final Integer i = getInputValue();
        if ( i != null ) {
          valueSlider.setValue( i.intValue() );
          valueMessage.setText( String.valueOf( i ) );
        }
      } finally {
        inStateChange = false;
      }
    }

    public void removeUpdate( final DocumentEvent e ) {
      if ( inStateChange ) {
        return;
      }
      inStateChange = true;
      try {
        final Integer i = getInputValue();
        if ( i != null ) {
          valueSlider.setValue( i.intValue() );
          valueMessage.setText( String.valueOf( i ) );
        }
      } finally {
        inStateChange = false;
      }
    }

    public void changedUpdate( final DocumentEvent e ) {
      if ( inStateChange ) {
        return;
      }
      inStateChange = true;
      try {
        final Integer i = getInputValue();
        if ( i != null ) {
          valueSlider.setValue( i.intValue() );
          valueMessage.setText( String.valueOf( i ) );
        }
      } finally {
        inStateChange = false;
      }
    }

    public void stateChanged( final ChangeEvent e ) {
      if ( inStateChange ) {
        return;
      }
      inStateChange = true;
      try {
        final String text = String.valueOf( valueSlider.getValue() );
        textField.setText( text );
        valueMessage.setText( text );
      } finally {
        inStateChange = false;
      }
    }
  }

  /**
   * Internal action class to confirm the dialog and to validate the input.
   */
  private class ConfirmAction extends AbstractAction {
    /**
     * Default constructor.
     */
    protected ConfirmAction( final ResourceBundle resources ) {
      putValue( Action.NAME, resources.getString( "OptionPane.okButtonText" ) ); //$NON-NLS-1$
    }

    /**
     * Receives notification that the action has occurred.
     *
     * @param e
     *          the action event.
     */
    public void actionPerformed( final ActionEvent e ) {
      if ( performValidate() ) {
        setConfirmed( true );
        setVisible( false );
      }
    }
  }

  /**
   * Internal action class to cancel the report processing.
   */
  private class CancelAction extends AbstractAction {
    /**
     * Default constructor.
     */
    protected CancelAction( final ResourceBundle resources ) {
      putValue( Action.NAME, resources.getString( "OptionPane.cancelButtonText" ) ); //$NON-NLS-1$
      putValue( Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke( KeyEvent.VK_ESCAPE, 0 ) );
    }

    /**
     * Receives notification that the action has occurred.
     *
     * @param e
     *          the action event.
     */
    public void actionPerformed( final ActionEvent e ) {
      setConfirmed( false );
      setVisible( false );
    }
  }

  private class DialogValidator extends FormValidator {
    protected DialogValidator() {
    }

    public boolean performValidate() {
      return NumericInputDialog.this.performValidate();
    }

    public Action getConfirmAction() {
      return NumericInputDialog.this.getConfirmAction();
    }
  }

  private class WindowCloseHandler extends WindowAdapter {
    protected WindowCloseHandler() {
    }

    /**
     * Invoked when a window is in the process of being closed. The close operation can be overridden at this point.
     */
    public void windowClosing( final WindowEvent e ) {
      final Action cancelAction = getCancelAction();
      if ( cancelAction != null ) {
        cancelAction.actionPerformed( null );
      } else {
        setConfirmed( false );
        setVisible( false );
      }
    }
  }

  private Action cancelAction;
  private Action confirmAction;
  private boolean boundedRange;

  private JLabel icon;
  private JLabel message;
  private JLabel valueMessage;
  private JSlider valueSlider;
  private JTextField textField;
  private boolean confirmed;
  private FormValidator formValidator;

  public NumericInputDialog() {
    initialize();
  }

  public NumericInputDialog( final Frame owner ) {
    super( owner );
    initialize();
  }

  public NumericInputDialog( final Frame owner, final boolean modal ) {
    super( owner, modal );
    initialize();
  }

  public NumericInputDialog( final Frame owner, final String title ) {
    super( owner, title );
    initialize();
  }

  public NumericInputDialog( final Dialog owner ) {
    super( owner );
    initialize();
  }

  public NumericInputDialog( final Dialog owner, final boolean modal ) {
    super( owner, modal );
    initialize();
  }

  public NumericInputDialog( final Dialog owner, final String title ) {
    super( owner, title );
    initialize();
  }

  public NumericInputDialog( final Frame owner, final String title, final boolean modal ) {
    super( owner, title, modal );
    initialize();
  }

  public NumericInputDialog( final Dialog owner, final String title, final boolean modal ) {
    super( owner, title, modal );
    initialize();
  }

  private void initialize() {
    final ResourceBundle resources = ResourceBundle.getBundle( SwingCommonModule.BUNDLE_NAME );
    setDefaultCloseOperation( JDialog.DISPOSE_ON_CLOSE );

    addWindowListener( new WindowCloseHandler() );

    cancelAction = new CancelAction( resources );
    confirmAction = new ConfirmAction( resources );

    final SyncValuesHandler syncValuesHandler = new SyncValuesHandler();
    message = new JLabel();
    icon = new JLabel();
    icon.setVisible( false );
    icon.setBorder( BorderFactory.createEmptyBorder( 0, 0, 0, 5 ) );

    valueMessage = new JLabel();
    valueMessage.setVisible( false );

    valueSlider = new JSlider();
    textField = new JTextField();
    textField.setDocument( new NumericDocument() );
    textField.setColumns( 10 );
    textField.setHorizontalAlignment( SwingConstants.TRAILING );

    textField.getDocument().addDocumentListener( syncValuesHandler );
    valueSlider.addChangeListener( syncValuesHandler );

    final JPanel contentPane = new JPanel();
    contentPane.setLayout( new GridBagLayout() );
    contentPane.setBorder( BorderFactory.createEmptyBorder( 5, 5, 5, 5 ) );

    GridBagConstraints gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.gridheight = 5;
    gbc.gridwidth = 1;
    gbc.anchor = GridBagConstraints.CENTER;
    contentPane.add( icon, gbc );

    gbc = new GridBagConstraints();
    gbc.gridx = 1;
    gbc.gridy = 0;
    gbc.gridheight = 1;
    gbc.gridwidth = 1;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    contentPane.add( message, gbc );

    gbc = new GridBagConstraints();
    gbc.gridx = 1;
    gbc.gridy = 1;
    gbc.gridheight = 1;
    gbc.gridwidth = 1;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    contentPane.add( valueSlider, gbc );

    gbc = new GridBagConstraints();
    gbc.gridx = 1;
    gbc.gridy = 2;
    gbc.gridheight = 1;
    gbc.gridwidth = 1;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    contentPane.add( textField, gbc );

    gbc = new GridBagConstraints();
    gbc.gridx = 1;
    gbc.gridy = 3;
    gbc.gridheight = 1;
    gbc.gridwidth = 1;
    gbc.anchor = GridBagConstraints.EAST;
    contentPane.add( valueMessage, gbc );

    gbc = new GridBagConstraints();
    gbc.gridx = 1;
    gbc.gridy = 4;
    gbc.gridheight = 1;
    gbc.gridwidth = 1;
    contentPane.add( createButtonPanel(), gbc );

    formValidator = new DialogValidator();
    formValidator.setEnabled( true );
    formValidator.registerTextField( textField );

    setContentPane( contentPane );
  }

  private JPanel createButtonPanel() {
    final JButton btnCancel = new JButton( getCancelAction() );
    final JButton btnConfirm = new JButton( getConfirmAction() );
    final JPanel buttonPanel = new JPanel();
    buttonPanel.setLayout( new GridLayout( 1, 2, 5, 5 ) );
    buttonPanel.add( btnConfirm );
    buttonPanel.add( btnCancel );
    btnConfirm.setDefaultCapable( true );
    getRootPane().setDefaultButton( btnConfirm );
    buttonPanel.registerKeyboardAction( getConfirmAction(), KeyStroke.getKeyStroke( KeyEvent.VK_ENTER, 0 ),
        JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT );

    final JPanel buttonCarrier = new JPanel();
    buttonCarrier.setLayout( new FlowLayout( FlowLayout.RIGHT ) );
    buttonCarrier.add( buttonPanel );
    return buttonCarrier;
  }

  public boolean getPaintTicks() {
    return valueSlider.getPaintTicks();
  }

  public void setPaintTicks( final boolean b ) {
    valueSlider.setPaintTicks( b );
  }

  public void setSnapToTicks( final boolean b ) {
    valueSlider.setSnapToTicks( b );
  }

  public boolean getSnapToTicks() {
    return valueSlider.getSnapToTicks();
  }

  public Action getCancelAction() {
    return cancelAction;
  }

  public Action getConfirmAction() {
    return confirmAction;
  }

  public void setIcon( final Icon icon ) {
    this.icon.setIcon( icon );
    this.icon.setVisible( icon != null );
  }

  public Icon getIcon() {
    return icon.getIcon();
  }

  public String getMessage() {
    return message.getText();
  }

  public void setMessage( final String message ) {
    this.message.setText( message );
  }

  public int getSliderValue() {
    return valueSlider.getValue();
  }

  public void setSliderValue( final int n ) {
    valueSlider.setValue( n );
  }

  public int getMinimum() {
    return valueSlider.getMinimum();
  }

  public void setMinimum( final int minimum ) {
    valueSlider.setMinimum( minimum );
  }

  public int getMaximum() {
    return valueSlider.getMaximum();
  }

  public void setMaximum( final int maximum ) {
    valueSlider.setMaximum( maximum );
  }

  public boolean isConfirmed() {
    return confirmed;
  }

  public void setConfirmed( final boolean confirmed ) {
    this.confirmed = confirmed;
  }

  protected boolean performValidate() {
    final Integer value = getInputValue();
    if ( value == null ) {
      return false;
    }
    if ( isBoundedRange() == false ) {
      return true;
    }
    final int iVal = value.intValue();
    if ( iVal < valueSlider.getMinimum() || iVal > valueSlider.getMaximum() ) {
      return false;
    }
    return true;
  }

  public void setInputValue( final Integer value ) {
    if ( value == null ) {
      textField.setText( "" );
    } else {
      textField.setText( String.valueOf( value ) );
    }
  }

  public Integer getInputValue() {
    try {
      return new Integer( textField.getText() );
    } catch ( NumberFormatException nfe ) {
      return null;
    }
  }

  public boolean isBoundedRange() {
    return boundedRange;
  }

  public void setBoundedRange( final boolean boundedRange ) {
    this.boundedRange = boundedRange;
  }

  public boolean isSliderVisible() {
    return valueSlider.isVisible();
  }

  public void setSliderVisible( final boolean b ) {
    valueSlider.setVisible( b );
  }

  public boolean isTextInputVisible() {
    return textField.isVisible();
  }

  public void setTextInputVisible( final boolean b ) {
    textField.setVisible( b );
    valueMessage.setVisible( !b );
  }

  private static NumericInputDialog createDialog( final Component parent ) {
    if ( parent != null ) {
      final Window window = LibSwingUtil.getWindowAncestor( parent );
      if ( window instanceof Dialog ) {
        return new NumericInputDialog( (Dialog) window, true );
      }
      if ( window instanceof Frame ) {
        return new NumericInputDialog( (Frame) window, true );
      }
    }

    final NumericInputDialog dialog = new NumericInputDialog();
    dialog.setModal( true );
    return dialog;
  }

  public static Integer showInputDialog( final Component parent, final int icon, final String title,
      final String message, final int minimum, final int maximum, final int initialValue, final boolean bounded ) {
    final NumericInputDialog dialog = createDialog( parent );
    if ( title != null ) {
      dialog.setTitle( title );
    }
    if ( message != null ) {
      dialog.setMessage( message );
    }
    dialog.setIcon( createDefaultIcon( icon ) );
    dialog.setMinimum( minimum );
    dialog.setMaximum( maximum );
    dialog.setInputValue( new Integer( initialValue ) );
    dialog.setBoundedRange( bounded );
    dialog.setPaintTicks( true );
    dialog.pack();
    LibSwingUtil.centerDialogInParent( dialog );
    dialog.setVisible( true );
    if ( dialog.isConfirmed() ) {
      return dialog.getInputValue();
    }
    return null;
  }

  public static Integer showInputDialog( final Component parent, final int icon, final String title,
      final String message, final int initialValue, final boolean bounded ) {
    final NumericInputDialog dialog = createDialog( parent );
    if ( title != null ) {
      dialog.setTitle( title );
    }
    if ( message != null ) {
      dialog.setMessage( message );
    }
    dialog.setIcon( createDefaultIcon( icon ) );
    dialog.setInputValue( new Integer( initialValue ) );
    dialog.setSliderVisible( false );
    dialog.setBoundedRange( bounded );
    dialog.pack();
    LibSwingUtil.centerDialogInParent( dialog );
    dialog.setVisible( true );
    if ( dialog.isConfirmed() ) {
      return dialog.getInputValue();
    }
    return null;
  }

  public static Integer showSliderDialog( final Component parent, final int icon, final String title,
      final String message, final int minimum, final int maximum, final int initialValue ) {
    final NumericInputDialog dialog = createDialog( parent );
    if ( title != null ) {
      dialog.setTitle( title );
    }
    if ( message != null ) {
      dialog.setMessage( message );
    }
    dialog.setIcon( createDefaultIcon( icon ) );
    dialog.setMinimum( minimum );
    dialog.setMaximum( maximum );
    dialog.setInputValue( new Integer( initialValue ) );
    dialog.setBoundedRange( true );
    dialog.setTextInputVisible( false );
    dialog.pack();
    LibSwingUtil.centerDialogInParent( dialog );
    dialog.setVisible( true );
    if ( dialog.isConfirmed() ) {
      return dialog.getInputValue();
    }
    return null;
  }

  /**
   * Returns the icon to use for the passed in type.
   */
  private static Icon createDefaultIcon( final int messageType ) {
    final String propertyName;
    switch ( messageType ) {
      case 0:
        propertyName = "OptionPane.errorIcon"; //$NON-NLS-1$
        break;
      case 1:
        propertyName = "OptionPane.informationIcon"; //$NON-NLS-1$
        break;
      case 2:
        propertyName = "OptionPane.warningIcon"; //$NON-NLS-1$
        break;
      case 3:
        propertyName = "OptionPane.questionIcon"; //$NON-NLS-1$
        break;
      default:
        return null;
    }
    return UIManager.getIcon( propertyName );
  }

}
