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

import org.pentaho.reporting.designer.core.util.HyperLink;
import org.pentaho.reporting.designer.core.widgets.HyperlinkHandler;
import org.pentaho.reporting.libraries.designtime.swing.CommonDialog;

import javax.swing.*;
import java.awt.*;

public class VersionCheckerEnableDialog extends CommonDialog {
  public VersionCheckerEnableDialog( final Frame owner )
    throws HeadlessException {
    super( owner );
    init();
  }

  public VersionCheckerEnableDialog( final Dialog owner )
    throws HeadlessException {
    super( owner );
    init();
  }

  public VersionCheckerEnableDialog() {
    init();
  }

  protected void init() {
    setTitle( Messages.getInstance().getString( "VersionCheckerUtility.Title" ) );
    setModal( true );

    // build button panel
    super.init();

    getConfirmAction()
      .putValue( Action.NAME, Messages.getInstance().getString( "VersionCheckerUtility.Yes" ) );// NON-NLS
    getCancelAction().putValue( Action.NAME, Messages.getInstance().getString( "VersionCheckerUtility.No" ) );// NON-NLS
  }

  protected String getDialogId() {
    return "ReportDesigner.Core.VersionCheckerEnable";
  }

  protected Component createContentPane() {
    Font font = UIManager.getFont( "Label.font" ); // NON-NLS
    if ( font == null ) {
      font = new Font( "Dialog", Font.PLAIN, 10 );// NON-NLS
    }

    final JPanel contentPane = new JPanel();
    contentPane.setLayout( new GridBagLayout() );
    final JTextPane messageLabel = new JTextPane();
    messageLabel.setFont( font.deriveFont( Font.BOLD ) );
    messageLabel.setText( Messages.getInstance().getString( "VersionCheckerUtility.Message" ) );
    messageLabel.setFocusable( false );
    messageLabel.setBackground( null );

    final String url = Messages.getInstance().getString( "VersionCheckerUtility.URL" );
    final HyperLink linkLbl = new HyperLink( url );
    linkLbl.addMouseListener( new HyperlinkHandler( url, linkLbl ) );

    final String questionText = Messages.getInstance().getString( "VersionCheckerUtility.Question" );
    final JTextPane questionLabel = new JTextPane();
    questionLabel.setFont( font.deriveFont( Font.BOLD ) );
    questionLabel.setText( questionText );
    questionLabel.setBackground( null );
    questionLabel.setFocusable( false );

    final GridBagConstraints c = new GridBagConstraints();
    c.insets = new Insets( 10, 10, 0, 10 );
    c.gridx = 0;
    c.gridy = 0;
    c.anchor = GridBagConstraints.SOUTHWEST;
    contentPane.add( messageLabel, c );

    c.insets = new Insets( 0, 15, 0, 10 );
    c.gridx = 0;
    c.gridy = 1;
    c.anchor = GridBagConstraints.NORTHWEST;
    contentPane.add( linkLbl, c );

    c.insets = new Insets( 0, 10, 10, 10 );
    c.gridx = 0;
    c.gridy = 2;
    c.anchor = GridBagConstraints.SOUTHWEST;
    contentPane.add( questionLabel, c );

    return contentPane;
  }

  public boolean performEdit() {
    return super.performEdit();
  }
}
