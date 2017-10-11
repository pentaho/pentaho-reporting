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

package org.pentaho.reporting.designer.core.versionchecker;

import org.pentaho.reporting.designer.core.settings.WorkspaceSettings;
import org.pentaho.reporting.designer.core.util.ExternalToolLauncher;
import org.pentaho.reporting.libraries.designtime.swing.LibSwingUtil;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;

/**
 * Todo: Document me!
 *
 * @author : Thomas Morgner
 */
public class VersionConfirmationDialog extends JDialog {
  private class MouseHandler extends MouseAdapter {
    public void mouseClicked( final MouseEvent e ) {
      if ( e.getClickCount() > 1 && e.getButton() == MouseEvent.BUTTON1 && isSelectionActive() ) {
        performLaunch();
      }
    }

  }

  private class UpdateAction extends AbstractAction {
    private UpdateAction() {
      putValue( Action.NAME, Messages.getInstance().getString( "VersionConfirmationDialog.Update" ) );// NON-NLS
      setEnabled( false );
    }

    public void actionPerformed( final ActionEvent e ) {
      performLaunch();
      setVisible( false );
    }
  }

  private class UpdateSelectionHandler implements ListSelectionListener {
    private UpdateSelectionHandler() {
    }

    public void valueChanged( final ListSelectionEvent e ) {
      getYesAction().setEnabled( isSelectionActive() );
    }
  }

  private class IgnoreAction extends AbstractAction {
    private IgnoreAction() {
      putValue( Action.NAME, Messages.getInstance().getString( "VersionConfirmationDialog.Ignore" ) );// NON-NLS
    }

    public void actionPerformed( final ActionEvent e ) {

      final UpdateInfo[] updateInfos = getUpdates();
      if ( updateInfos.length > 0 ) {
        WorkspaceSettings.getInstance()
          .setLastPromptedVersionUpdate( updateInfos[ updateInfos.length - 1 ].getVersion() );
      }
      setVisible( false );
    }

  }

  private Action yesAction;
  private JTable updateList;
  private UpdateInfo[] updates;
  private boolean exitOnLaunch;

  public VersionConfirmationDialog( final UpdateInfo[] updates,
                                    final boolean exitOnLaunch )
    throws HeadlessException {
    init( updates, exitOnLaunch );
  }

  public VersionConfirmationDialog( final Frame owner,
                                    final UpdateInfo[] updates,
                                    final boolean exitOnLaunch )
    throws HeadlessException {
    super( owner );
    init( updates, exitOnLaunch );
  }

  public VersionConfirmationDialog( final Dialog owner,
                                    final UpdateInfo[] updates,
                                    final boolean exitOnLaunch )
    throws HeadlessException {
    super( owner );
    init( updates, exitOnLaunch );
  }

  private void init( final UpdateInfo[] updates,
                     final boolean exitOnLaunch ) {
    this.updates = updates.clone();
    this.exitOnLaunch = exitOnLaunch;
    setTitle( Messages.getInstance().getString( "VersionConfirmationDialog.Title" ) );// NON-NLS
    setModal( true );
    setResizable( false );
    getContentPane().setLayout( new GridBagLayout() );


    updateList = new JTable( new UpdateTableModel( updates ) );
    updateList.addMouseListener( new MouseHandler() );
    updateList.getSelectionModel().addListSelectionListener( new UpdateSelectionHandler() );

    final JScrollPane updateListScroller = new JScrollPane( updateList );
    updateListScroller.setPreferredSize( new Dimension( 320, 120 ) );
    final GridBagConstraints c = new GridBagConstraints();
    c.insets = new Insets( 10, 10, 10, 10 );
    c.gridx = 0;
    c.gridy = 0;
    getContentPane().add( updateListScroller, c );

    c.insets = new Insets( 0, 10, 10, 10 );
    c.gridx = 0;
    c.gridy = 1;
    c.anchor = GridBagConstraints.CENTER;
    c.fill = GridBagConstraints.HORIZONTAL;
    getContentPane().add( createButtons(), c );


    final JComponent contentPane = (JComponent) getContentPane();
    final InputMap inputMap = contentPane.getInputMap();
    final ActionMap actionMap = contentPane.getActionMap();

    inputMap.put( KeyStroke.getKeyStroke( KeyEvent.VK_ESCAPE, 0 ), "cancel" ); // NON-NLS
    actionMap.put( "cancel", new IgnoreAction() ); // NON-NLS

  }

  protected Action getYesAction() {
    return yesAction;
  }

  public boolean isSelectionActive() {
    return updateList.getSelectedRow() != -1;
  }

  public UpdateInfo[] getUpdates() {
    return updates.clone();
  }

  private JPanel createButtons() {
    yesAction = new UpdateAction();

    final JButton yesButton = new JButton( yesAction );
    final JButton noButton = new JButton( new IgnoreAction() );

    final JPanel buttonPanel = new JPanel( new GridBagLayout() );
    final GridBagConstraints c = new GridBagConstraints();
    c.insets = new Insets( 0, 0, 0, 5 );
    c.gridx = 0;
    c.gridy = 0;

    buttonPanel.add( yesButton, c );
    c.gridx = 1;
    c.weightx = 0;
    c.insets = new Insets( 0, 0, 0, 0 );
    buttonPanel.add( noButton, c );

    return buttonPanel;
  }

  protected void performLaunch() {
    try {
      final int selectedRow = updateList.getSelectedRow();
      final String url = updates[ selectedRow ].getUrl();
      ExternalToolLauncher.openURL( url );
      if ( exitOnLaunch ) {
        WorkspaceSettings.getInstance().flush();
        System.exit( 0 );
      }
    } catch ( IOException e1 ) {
      // ignored 
    }

  }

  public static void performUpdateAvailable( final Component parent,
                                             final UpdateInfo[] updates,
                                             final boolean exitOnLaunch ) {
    final VersionConfirmationDialog dialog;
    if ( parent == null ) {
      dialog = new VersionConfirmationDialog( updates, exitOnLaunch );
    } else {
      final Window window = LibSwingUtil.getWindowAncestor( parent );
      if ( window instanceof Frame ) {
        dialog = new VersionConfirmationDialog( (Frame) window, updates, exitOnLaunch );
      } else if ( window instanceof Dialog ) {
        dialog = new VersionConfirmationDialog( (Dialog) window, updates, exitOnLaunch );
      } else {
        dialog = new VersionConfirmationDialog( updates, exitOnLaunch );
      }
    }

    dialog.pack();
    LibSwingUtil.centerFrameOnScreen( dialog );
    dialog.setVisible( true );
  }
}
