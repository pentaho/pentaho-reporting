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
 * Copyright (c) 2001 - 2017 Object Refinery Ltd, Hitachi Vantara and Contributors..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.modules.gui.commonswing;

import java.awt.Dialog;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.MessageFormat;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import org.pentaho.reporting.engine.classic.core.event.ReportProgressEvent;
import org.pentaho.reporting.engine.classic.core.event.ReportProgressListener;
import org.pentaho.reporting.libraries.base.util.Messages;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.libraries.designtime.swing.LibSwingUtil;

/**
 * A progress monitor dialog component that visualizes the report processing progress. It will receive update events
 * from the report processors and updates the UI according to the latest event data.
 * <p/>
 * The progress will be computed according to the currently processed table row. This approach provides relativly
 * accurate data, but assumes that processing all bands consumes roughly the same time.
 *
 * @author Thomas Morgner
 */
public class ReportProgressDialog extends JDialog implements ReportProgressListener {
  /**
   * Handles the update event processing as a separate thread
   *
   * @author Thomas Morgner
   */
  private class ScreenUpdateRunnable implements Runnable {
    /**
     * The event upon which this update event processing will occur
     */
    private ReportProgressEvent event;

    /**
     * Initializes the update event processing thread with the event information
     */
    protected ScreenUpdateRunnable() {
    }

    /**
     * Performs the process of updating all the pieces of the progress dialog with the update event information.
     */
    public synchronized void run() {
      if ( event == null ) {
        return;
      }
      updatePageMessage( event.getPage() );
      updateRowsMessage( event.getRow(), event.getMaximumRow() );
      updateActivityMessage( event.getActivity() );
      updateProgressBar( event );
      this.event = null;
    }

    public synchronized boolean update( final ReportProgressEvent event ) {
      final boolean retval = ( this.event == null );
      this.event = event;
      return retval;
    }
  }

  /**
   * Event handles that will ensure this dialog will remain on top
   *
   * @author Thomas Morgner
   */
  private static class ToFrontHandler extends WindowAdapter {
    protected ToFrontHandler() {
    }

    /**
     * Invoked when a window has been opened.
     */
    public void windowOpened( final WindowEvent e ) {
      e.getWindow().toFront();
    }
  }

  /**
   * A label that carries the global message that describes the current task.
   */
  private JLabel messageCarrier;

  /**
   * A label containing the report processing pass count.
   */
  private JLabel passCountMessage;

  /**
   * A label containing the current page.
   */
  private JLabel pageCountMessage;

  /**
   * A label containing the currently processed row.
   */
  private JLabel rowCountMessage;

  /**
   * The progress bar that is used to visualize the progress.
   */
  private JProgressBar progressBar;

  /**
   * The reusable message format for the page label.
   */
  private MessageFormat pageMessageFormatter;

  /**
   * The reusable message format for the rows label.
   */
  private MessageFormat rowsMessageFormatter;

  /**
   * The reusable message format for the pass label.
   */
  private MessageFormat passMessageFormatter;

  /**
   * The last page received.
   */
  private int lastPage;

  /**
   * The last pass values received.
   */
  private int lastActivity;

  /**
   * The last max-row received.
   */
  private int lastMaxRow;

  /**
   * the cached value for the max-row value as integer.
   */
  private Integer lastMaxRowInteger; // this values doesnt change much, so reduce GC work

  /**
   * a text which describes the layouting process.
   */
  private String layoutText;

  /**
   * a text that describes the export phase of the report processing.
   */
  private String outputText;

  /**
   * Localized messages.
   */
  private Messages messages;

  private boolean onlyPagination;

  private ScreenUpdateRunnable updateRunnable;

  /**
   * Creates a non-modal dialog without a title and with the specified Dialog owner.
   *
   * @param dialog
   *          the owner of the dialog
   */
  public ReportProgressDialog( final Dialog dialog ) {
    super( dialog );
    setLocale( dialog.getLocale() );
    initConstructor();
  }

  /**
   * Creates a non-modal dialog without a title and with the specified Frame owner.
   *
   * @param frame
   *          the owner of the dialog
   */
  public ReportProgressDialog( final Frame frame ) {
    super( frame );
    setLocale( frame.getLocale() );
    initConstructor();
  }

  /**
   * Creates a non-modal dialog without a title and without a specified Frame owner. A shared, hidden frame will be set
   * as the owner of the Dialog.
   */
  public ReportProgressDialog() {
    initConstructor();
  }

  public boolean isOnlyPagination() {
    return onlyPagination;
  }

  public void setOnlyPagination( final boolean onlyPagination ) {
    this.onlyPagination = onlyPagination;
  }

  /**
   * Initializes the dialog (Non-GUI stuff).
   */
  private void initConstructor() {
    updateRunnable = new ScreenUpdateRunnable();
    messages =
        new Messages( getLocale(), SwingCommonModule.BUNDLE_NAME, ObjectUtilities
            .getClassLoader( SwingCommonModule.class ) );
    initialize();
    addWindowListener( new ToFrontHandler() );

    setOutputText( messages.getString( "progress-dialog.perform-output" ) ); //$NON-NLS-1$
    setLayoutText( messages.getString( "progress-dialog.prepare-layout" ) ); //$NON-NLS-1$

    lastActivity = -1;
    lastMaxRow = -1;
    lastPage = -1;

    this.setDefaultCloseOperation( JFrame.DISPOSE_ON_CLOSE );
    pack();
    LibSwingUtil.centerDialogInParent( this );
  }

  /**
   * Initializes the GUI components of this dialog.
   */
  private void initialize() {
    final JPanel contentPane = new JPanel();
    contentPane.setBorder( new EmptyBorder( 5, 5, 5, 5 ) );
    contentPane.setLayout( new GridBagLayout() );

    pageMessageFormatter = new MessageFormat( messages.getString( "progress-dialog.page-label" ) ); //$NON-NLS-1$
    rowsMessageFormatter = new MessageFormat( messages.getString( "progress-dialog.rows-label" ) ); //$NON-NLS-1$
    passMessageFormatter = new MessageFormat( messages.getString( "progress-dialog.pass-label-0" ) ); //$NON-NLS-1$

    messageCarrier = new JLabel( " " ); //$NON-NLS-1$
    passCountMessage = new JLabel( " " ); //$NON-NLS-1$
    rowCountMessage = new JLabel( " " ); //$NON-NLS-1$
    pageCountMessage = new JLabel( " " ); //$NON-NLS-1$
    progressBar = new JProgressBar( SwingConstants.HORIZONTAL, 0, 100 );
    progressBar.setStringPainted( true );

    GridBagConstraints gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.gridwidth = 2;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.weightx = 1;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.insets = new Insets( 3, 1, 5, 1 );
    gbc.ipadx = 200;
    contentPane.add( messageCarrier, gbc );

    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 1;
    gbc.gridwidth = 2;
    gbc.anchor = GridBagConstraints.SOUTHWEST;
    gbc.insets = new Insets( 3, 1, 1, 1 );
    contentPane.add( passCountMessage, gbc );

    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 2;
    gbc.gridwidth = 2;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.weightx = 1;
    gbc.insets = new Insets( 3, 1, 1, 1 );
    contentPane.add( progressBar, gbc );

    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 3;
    gbc.gridwidth = 1;
    gbc.weighty = 1;
    gbc.anchor = GridBagConstraints.NORTHWEST;
    gbc.insets = new Insets( 3, 1, 1, 1 );
    contentPane.add( pageCountMessage, gbc );

    gbc = new GridBagConstraints();
    gbc.gridx = 1;
    gbc.gridy = 3;
    gbc.gridwidth = 1;
    gbc.anchor = GridBagConstraints.NORTHWEST;
    gbc.insets = new Insets( 3, 10, 1, 1 );
    gbc.weightx = 1;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    contentPane.add( rowCountMessage, gbc );

    setContentPane( contentPane );
  }

  /**
   * Returns the current message.
   *
   * @return the current global message.
   */
  public String getMessage() {
    return messageCarrier.getText();
  }

  /**
   * Defines the current message.
   *
   * @param message
   *          the current global message.
   */
  public void setMessage( final String message ) {
    messageCarrier.setText( message );
  }

  /**
   * Updates the page message label if the current page has changed.
   *
   * @param page
   *          the new page parameter.
   */
  protected void updatePageMessage( final int page ) {
    if ( lastPage != page ) {
      final Object[] parameters = new Object[] { new Integer( page ) };
      pageCountMessage.setText( pageMessageFormatter.format( parameters ) );
      lastPage = page;
    }
  }

  /**
   * Updates the rows message label if either the rows or maxrows changed.
   *
   * @param rows
   *          the currently processed rows.
   * @param maxRows
   *          the maximum number of rows in the report.
   */
  protected void updateRowsMessage( final int rows, final int maxRows ) {
    if ( maxRows != lastMaxRow ) {
      lastMaxRowInteger = new Integer( maxRows );
      lastMaxRow = maxRows;
    }
    final Object[] parameters = new Object[] { new Integer( rows ), lastMaxRowInteger };
    rowCountMessage.setText( rowsMessageFormatter.format( parameters ) );
  }

  /**
   * Updates the pass message label if either the pass or prepare state changed. The pass reflects the current
   * processing level, one level for every function dependency level.
   *
   * @param activity
   *          the current reporting pass.
   */
  protected void updateActivityMessage( final int activity ) {
    if ( lastActivity != activity ) {
      lastActivity = activity;
      final Object[] parameters = new Object[] { new Integer( activity ) };
      passCountMessage.setText( passMessageFormatter.format( parameters ) );
    }
  }

  /**
   * Updates the progress bar to show the current progress
   *
   * @param event
   *          the event data used to update the progress bar
   */
  protected void updateProgressBar( final ReportProgressEvent event ) {
    progressBar.setValue( (int) ReportProgressEvent.computePercentageComplete( event, isOnlyPagination() ) );
  }

  /**
   * Returns the current pass message component.
   *
   * @return the pass message component.
   */
  protected final JLabel getPassCountMessage() {
    return passCountMessage;
  }

  /**
   * Returns the current pagecount message component.
   *
   * @return the page message component.
   */
  protected final JLabel getPageCountMessage() {
    return pageCountMessage;
  }

  /**
   * Returns the current row message component.
   *
   * @return the row message component.
   */
  protected final JLabel getRowCountMessage() {
    return rowCountMessage;
  }

  /**
   * Returns the current pass message component.
   *
   * @return the pass message component.
   */
  protected final MessageFormat getPageMessageFormatter() {
    return pageMessageFormatter;
  }

  /**
   * Returns the current pass message component.
   *
   * @return the pass message component.
   */
  protected final MessageFormat getRowsMessageFormatter() {
    return rowsMessageFormatter;
  }

  /**
   * Returns the current pass message component.
   *
   * @return the pass message component.
   */
  protected final MessageFormat getPassMessageFormatter() {
    return passMessageFormatter;
  }

  /**
   * Returns the output text message. This text describes the export phases of the report processing.
   *
   * @return the output phase description.
   */
  public String getOutputText() {
    return outputText;
  }

  /**
   * Defines the output text message. This text describes the export phases of the report processing.
   *
   * @param outputText
   *          the output message.
   */
  public void setOutputText( final String outputText ) {
    if ( outputText == null ) {
      throw new NullPointerException( messages.getErrorString( "ReportProgressDialog.ERROR_0001_OUTPUT_TEXT_NULL" ) ); //$NON-NLS-1$
    }
    this.outputText = outputText;
  }

  /**
   * Returns the layout text. This text describes the prepare phases of the report processing.
   *
   * @return the layout text.
   */
  public String getLayoutText() {
    return layoutText;
  }

  /**
   * Defines the layout text message. This text describes the prepare phases of the report processing.
   *
   * @param layoutText
   *          the layout message.
   */
  public void setLayoutText( final String layoutText ) {
    if ( layoutText == null ) {
      throw new NullPointerException( messages.getErrorString( "ReportProgressDialog.ERROR_0002_LAYOUT_TEXT_NULL" ) ); //$NON-NLS-1$
    }
    this.layoutText = layoutText;
  }

  protected boolean isSameMaxRow( final int row ) {
    return lastMaxRow == row;
  }

  public void reportProcessingStarted( final ReportProgressEvent event ) {
    postUpdate( event );
  }

  public void reportProcessingUpdate( final ReportProgressEvent event ) {
    postUpdate( event );
  }

  public void reportProcessingFinished( final ReportProgressEvent event ) {
    postUpdate( event );
  }

  private void postUpdate( final ReportProgressEvent event ) {
    synchronized ( this.updateRunnable ) {
      if ( this.updateRunnable.update( event ) ) {
        if ( SwingUtilities.isEventDispatchThread() ) {
          this.updateRunnable.run();
        } else {
          SwingUtilities.invokeLater( this.updateRunnable );
        }
      }
    }
  }

  public void setVisibleInEDT( final boolean b ) {
    if ( SwingUtilities.isEventDispatchThread() ) {
      if ( b ) {
        setVisible( true );
      } else {
        setVisible( false );
      }
    } else {
      try {
        SwingUtilities.invokeAndWait( new ShowHideTask( this, b ) );
      } catch ( Exception e ) {
        // something has to be done!
        if ( b ) {
          setVisible( true );
        } else {
          setVisible( false );
        }
      }
    }
  }

  private static class ShowHideTask implements Runnable {
    private Dialog parent;
    private boolean visible;

    private ShowHideTask( final Dialog parent, final boolean visible ) {
      this.parent = parent;
      this.visible = visible;
    }

    /**
     * When an object implementing interface <code>Runnable</code> is used to create a thread, starting the thread
     * causes the object's <code>run</code> method to be called in that separately executing thread.
     * <p/>
     * The general contract of the method <code>run</code> is that it may take any action whatsoever.
     *
     * @see Thread#run()
     */
    public void run() {
      if ( visible ) {
        parent.setVisible( true );
      } else {
        parent.setVisible( false );
      }
    }
  }

}
