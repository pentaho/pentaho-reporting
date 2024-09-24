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

package gui;

import org.pentaho.reporting.libraries.designtime.swing.CommonDialog;

import javax.swing.*;
import java.awt.*;

public class ShowTextDialog extends CommonDialog {
  private JTextArea textArea;

  public ShowTextDialog() {
    init();
  }

  public ShowTextDialog( final Frame owner )
    throws HeadlessException {
    super( owner );
    init();
  }

  public ShowTextDialog( final Dialog owner )
    throws HeadlessException {
    super( owner );
    init();
  }

  protected void init() {
    super.init();
    pack();
    setSize( 800, 600 );
  }

  protected String getDialogId() {
    return getClass().getName();
  }

  protected Component createContentPane() {
    textArea = new JTextArea();
    textArea.setFont( new Font( Font.MONOSPACED, Font.PLAIN, 14 ) );
    textArea.setLineWrap( false );
    textArea.setEditable( true );

    final JPanel panel = new JPanel();
    panel.setLayout( new BorderLayout() );
    panel.add( new JScrollPane( textArea ), BorderLayout.CENTER );
    return panel;
  }

  protected boolean hasCancelButton() {
    return false;
  }

  public void showText( String text ) {
    textArea.setText( text );
    setModal( false );
    setVisible( true );
  }
}
