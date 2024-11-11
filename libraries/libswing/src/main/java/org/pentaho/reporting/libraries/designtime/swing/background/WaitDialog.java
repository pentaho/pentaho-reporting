/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


package org.pentaho.reporting.libraries.designtime.swing.background;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.libraries.designtime.swing.LibSwingUtil;
import org.pentaho.reporting.libraries.designtime.swing.Messages;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * A dialog which will indicate to the user that an operation is being performed and will provide them an opportunity to
 * try and cancel that operation. This will create and run in a separate thread so that the user and the background
 * operation will not have to wait for completion.
 */
public class WaitDialog extends JDialog {
  /**
   * Class which handles the processing of a cancel request. It will notify all listeners waiting to handle the Cancel
   * request.
   */
  private class CancelActionListener extends AbstractAction {
    /**
     * Defines an <code>Action</code> object with a default description string and default icon.
     */
    private CancelActionListener() {
      putValue( Action.NAME, Messages.getInstance().getString( "WaitDialog.CANCEL" ) );
    }

    public void actionPerformed( final ActionEvent e ) {
      final CancelEvent event = new CancelEvent( this );
      for ( final CancelListener cancelListener : cancelListeners ) {
        try {
          if ( cancelListener != null ) {
            logger.debug( "Passing cancel action along to cancel listener [" + cancelListener + "]" );
            cancelListener.cancelProcessing( event );
          }
        } catch ( final Throwable ignored ) {
          logger.warn(
            Messages.getInstance().formatMessage( "WaitDialog.CANCEL_EXCEPTION", ignored.getLocalizedMessage() ) );
        }
      }
    }
  }

  private class DisposeTask implements Runnable {
    public void run() {
      setVisible( false );
      dispose();
    }
  }

  private static final int PADDING = 8;
  private static final Log logger = LogFactory.getLog( WaitDialog.class );
  private List<CancelListener> cancelListeners = new ArrayList<CancelListener>();
  private JLabel message;
  private JProgressBar progressBar;
  private JPanel contentPane;

  private boolean showProgressbar;
  private boolean showCancelButton;

  public WaitDialog() {
    super();
    init();
  }

  public WaitDialog( final Dialog parent ) {
    super( parent );
    init();
  }

  public WaitDialog( final Frame parent ) {
    super( parent );
    init();
  }

  public void addCancelListener( final CancelListener listener ) {
    cancelListeners.add( listener );
  }

  public void removeCancelListener( final CancelListener listener ) {
    cancelListeners.remove( listener );
  }

  private void init() {
    logger.debug( "Initializing the Wait Dialog" );
    setModal( true );
    setTitle( Messages.getInstance().getString( "WaitDialog.TITLE" ) );

    message = new JLabel( Messages.getInstance().getString( "WaitDialog.MESSAGE" ) );
    progressBar = new JProgressBar( 0, 1000 );

    contentPane = new JPanel();
    contentPane.setLayout( new BorderLayout( PADDING, PADDING ) );
    setContentPane( contentPane );
  }

  public void reinitComponents() {
    contentPane.removeAll();

    final JPanel contentPanel = new JPanel( new FlowLayout( FlowLayout.CENTER ) );
    contentPanel.add( message );
    contentPane.add( contentPanel, BorderLayout.CENTER );

    final JPanel progressPanel = new JPanel( new FlowLayout( FlowLayout.CENTER ) );
    progressPanel.add( progressBar );
    contentPane.add( progressPanel, BorderLayout.NORTH );

    if ( showCancelButton ) {
      final JPanel buttonPanel = new JPanel( new FlowLayout( FlowLayout.CENTER, PADDING, PADDING ) );
      final JButton cancelButton = new JButton( new CancelActionListener() );
      buttonPanel.add( cancelButton );
      contentPane.add( buttonPanel, BorderLayout.SOUTH );
    }

    contentPane.invalidate();
    pack();

    // Since the parent dialog has not yet been centered, we will just center on the screen
    LibSwingUtil.centerDialogInParent( this );
  }

  private class UpdateProgressTask implements Runnable {
    private double progress;

    private UpdateProgressTask( final double progress ) {
      this.progress = progress;
    }

    public void run() {
      try {
        setProgress( this.progress );
      } catch ( Exception e ) {
        e.printStackTrace();
      }
    }
  }

  public void showDialog( final boolean showCancel, final boolean showProgress ) {
    this.showCancelButton = showCancel;
    this.showProgressbar = showProgress;

    this.progressBar.setIndeterminate( showProgress == false );
  }

  public boolean isShowProgressbar() {
    return showProgressbar;
  }

  public void setShowProgressbar( final boolean showProgressbar ) {
    this.showProgressbar = showProgressbar;
  }

  public boolean isShowCancelButton() {
    return showCancelButton;
  }

  public void setShowCancelButton( final boolean showCancelButton ) {
    this.showCancelButton = showCancelButton;
  }

  public double getProgress() {
    return progressBar.getPercentComplete();
  }

  public void setProgress( final double p ) {
    progressBar.setValue( (int) ( Math.max( Math.min( 1, p ), 0 ) * 1000 ) );
  }

  public void updateProgress( final double p ) {
    SwingUtilities.invokeLater( new UpdateProgressTask( p ) );
  }

  public void setMessage( final String message ) {
    this.message.setText( message );
  }

  /**
   * Indicates that the thread processing is complete and this dialog should close
   */
  public void exit() {
    SwingUtilities.invokeLater( new DisposeTask() );
  }


}
