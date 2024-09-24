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

package org.pentaho.reporting.libraries.designtime.swing.background;

import org.pentaho.reporting.libraries.designtime.swing.LibSwingUtil;
import org.pentaho.reporting.libraries.designtime.swing.Messages;

import javax.swing.*;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

public class DataPreviewDialog extends JDialog {
  private class CloseAction extends AbstractAction {
    /**
     * Defines an <code>Action</code> object with a default description string and default icon.
     */
    private CloseAction() {
      putValue( Action.NAME, Messages.getInstance().getString( "PreviewDialog.Close" ) );
    }

    /**
     * Invoked when an action occurs.
     */
    public void actionPerformed( final ActionEvent e ) {
      dispose();
    }
  }

  private JTable table;

  public DataPreviewDialog()
    throws HeadlessException {
    init();
  }

  public DataPreviewDialog( final Frame owner )
    throws HeadlessException {
    super( owner );
    init();
  }

  public DataPreviewDialog( final Dialog owner )
    throws HeadlessException {
    super( owner );
    init();
  }

  private void init() {

    setModal( true );
    setTitle( Messages.getInstance().getString( "PreviewDialog.Title" ) );

    final JPanel mainPanel = new JPanel( new BorderLayout() );
    setContentPane( mainPanel );

    table = new JTable();
    mainPanel.add( new JScrollPane( table ), BorderLayout.CENTER );

    final JPanel buttonsPanel = new JPanel( new FlowLayout( FlowLayout.RIGHT ) );
    buttonsPanel.setBorder( BorderFactory.createMatteBorder( 1, 0, 0, 0, Color.LIGHT_GRAY ) );
    final JButton closeButton = new JButton( new CloseAction() );
    buttonsPanel.add( closeButton );
    mainPanel.add( buttonsPanel, BorderLayout.SOUTH );

    final JComponent contentPane = (JComponent) getContentPane();
    final InputMap inputMap = contentPane.getInputMap();
    final ActionMap actionMap = contentPane.getActionMap();

    inputMap.put( KeyStroke.getKeyStroke( KeyEvent.VK_ESCAPE, 0 ), "cancel" ); // NON-NLS
    actionMap.put( "cancel", new CloseAction() ); // NON-NLS

    setResizable( true );
    setDefaultCloseOperation( JFrame.DISPOSE_ON_CLOSE );
    setSize( 800, 600 );
  }

  public void showData( final PreviewWorker previewWorker ) {
    // Run the query in a separate thread so that we can display a cancel dialog
    final Thread qt = new Thread( previewWorker );
    BackgroundCancellableProcessHelper.executeProcessWithCancelDialog
      ( qt, previewWorker, this, Messages.getInstance().getString( "PreviewDialog.PreviewDataTask" ) );

    // Get the results ... or throw the exception that was generated
    final TableModel rawTableModel = previewWorker.getResultTableModel();
    if ( showData( rawTableModel ) ) {
      return;
    }
    previewWorker.close();
  }

  public boolean showData( final TableModel rawTableModel ) {
    if ( rawTableModel == null ) {
      // User must have hit cancel
      return true;
    }

    LibSwingUtil.centerDialogInParent( this );
    table.setModel( rawTableModel );

    setVisible( true );
    return false;
  }
}
