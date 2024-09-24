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

import org.pentaho.reporting.libraries.designtime.swing.settings.DialogSizeSettings;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

/**
 * A modal dialog with a ok and cancel buttons.
 *
 * @author Thomas Morgner
 */
public abstract class CommonDialog extends JDialog {
  private class OKAction extends AbstractAction {
    /**
     * Defines an <code>Action</code> object with a default description string and default icon.
     */
    private OKAction() {
      putValue( Action.NAME, Messages.getInstance().getString( "OK" ) );
    }

    /**
     * Invoked when an action occurs.
     */
    public void actionPerformed( final ActionEvent e ) {
      if ( validateInputs( true ) ) {
        setConfirmed( true );
        dispose();
      }
    }
  }

  private class CancelAction extends AbstractAction {
    /**
     * Defines an <code>Action</code> object with a default description string and default icon.
     */
    private CancelAction() {
      putValue( Action.NAME, Messages.getInstance().getString( "CANCEL" ) );
    }

    /**
     * Invoked when an action occurs.
     */
    public void actionPerformed( final ActionEvent e ) {
      dispose();
    }
  }

  private boolean confirmed;
  private Action confirmAction;
  private CancelAction cancelAction;
  private JComponent buttonPanel;
  private boolean buttonPaneVisible;
  private DialogSizeSettings dialogSizeSettings;

  /**
   * Creates a new modal dialog.
   */
  protected CommonDialog() {
    setModal( true );
  }

  protected CommonDialog( final Frame owner )
    throws HeadlessException {
    super( owner, true );
  }

  protected CommonDialog( final Dialog owner )
    throws HeadlessException {
    super( owner, true );
  }

  public boolean isConfirmed() {
    return confirmed;
  }

  public void setConfirmed( final boolean confirmed ) {
    this.confirmed = confirmed;
  }

  protected void init() {
    dialogSizeSettings = new DialogSizeSettings();
    buttonPaneVisible = true;

    confirmAction = new OKAction();
    cancelAction = new CancelAction();

    setDefaultCloseOperation( DISPOSE_ON_CLOSE );
    final JPanel contentPane = new JPanel();
    contentPane.setLayout( new BorderLayout() );

    buttonPanel = createButtonsPane();

    contentPane.add( createContentPane(), BorderLayout.CENTER );
    contentPane.add( buttonPanel, BorderLayout.SOUTH );
    setContentPane( contentPane );

    final InputMap inputMap = contentPane.getInputMap( JComponent.WHEN_IN_FOCUSED_WINDOW );
    final ActionMap actionMap = contentPane.getActionMap();

    inputMap.put( KeyStroke.getKeyStroke( KeyEvent.VK_ENTER, 0 ), "confirm" ); // NON-NLS
    inputMap.put( KeyStroke.getKeyStroke( KeyEvent.VK_ESCAPE, 0 ), "cancel" ); // NON-NLS
    actionMap.put( "confirm", getConfirmAction() ); // NON-NLS
    actionMap.put( "cancel", getCancelAction() ); // NON-NLS

    resizeDialog();
  }

  protected abstract String getDialogId();

  protected void resizeDialog() {
    final Rectangle rectangle = dialogSizeSettings.get( getDialogId() );
    if ( rectangle != null ) {
      if ( LibSwingUtil.safeRestoreWindow( this, rectangle ) ) {
        // paranoid safety net to ensure that we have sensible sizes
        if ( getWidth() > 50 && getHeight() > 50 ) {
          return;
        }
      }
    }

    performInitialResize();
  }

  protected void performInitialResize() {
    pack();
    LibSwingUtil.centerDialogInParent( this );
  }

  protected Action getCancelAction() {
    return cancelAction;
  }

  protected abstract Component createContentPane();

  protected boolean performEdit() {
    confirmed = false;
    setModal( true );
    setVisible( true );
    return confirmed;
  }


  public boolean isButtonPaneVisible() {
    return buttonPaneVisible;
  }

  public void setButtonPaneVisible( final boolean buttonPaneVisible ) {
    this.buttonPaneVisible = buttonPaneVisible;
    if ( buttonPaneVisible ) {
      getContentPane().add( buttonPanel, BorderLayout.SOUTH );
    } else {
      getContentPane().remove( buttonPanel );
    }
  }

  protected boolean hasCancelButton() {
    return true;
  }

  protected Action[] getExtraActions() {
    return new Action[ 0 ];
  }

  protected JPanel createButtonsPane() {
    final JButton button = new JButton( getConfirmAction() );
    button.setDefaultCapable( true );

    final JPanel buttonsPanel = new JPanel();
    buttonsPanel.setLayout( new FlowLayout( FlowLayout.RIGHT, 5, 5 ) );
    if ( !MacOSXIntegration.MAC_OS_X ) {
      buttonsPanel.add( button );
    }
    if ( hasCancelButton() ) {
      buttonsPanel.add( new JButton( getCancelAction() ) );
    }
    if ( MacOSXIntegration.MAC_OS_X ) {
      buttonsPanel.add( button );
    }

    final JPanel extraPanel = new JPanel();
    extraPanel.setLayout( new FlowLayout( FlowLayout.LEFT, 5, 5 ) );
    final Action[] extraActions = getExtraActions();
    for ( int i = 0; i < extraActions.length; i++ ) {
      final Action action = extraActions[ i ];
      extraPanel.add( new JButton( action ) );
    }

    final JPanel buttonsCarrierPanel = new JPanel();
    buttonsCarrierPanel.setLayout( new BorderLayout( 5, 5 ) );
    buttonsCarrierPanel.add( buttonsPanel, BorderLayout.EAST );
    buttonsCarrierPanel.add( extraPanel, BorderLayout.WEST );
    return buttonsCarrierPanel;
  }

  protected Action getConfirmAction() {
    return confirmAction;
  }

  @SuppressWarnings( "deprecation" )
  public void hide() {
    // called from dispose() ...
    dialogSizeSettings.put( getDialogId(), getBounds() );
    super.hide();
  }

  public void setVisible( final boolean b ) {
    if ( b == false ) {
      dialogSizeSettings.put( getDialogId(), getBounds() );
    }
    super.setVisible( b );
  }

  protected boolean validateInputs( final boolean onConfirm ) {
    return true;
  }
}
