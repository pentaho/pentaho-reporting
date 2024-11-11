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


package org.pentaho.reporting.designer.core.status;

import org.pentaho.reporting.designer.core.Messages;
import org.pentaho.reporting.designer.core.util.dnd.ClipboardManager;
import org.pentaho.reporting.designer.core.util.exceptions.UncaughtExceptionsModel;
import org.pentaho.reporting.libraries.designtime.swing.LibSwingUtil;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Todo: Document Me
 *
 * @author Thomas Morgner
 */
public class ExceptionDialog extends JDialog {
  private class CopyAction extends AbstractAction {
    private CopyAction() {
      putValue( Action.NAME, Messages.getString( "ExceptionDialog.CopyToClipboard" ) ); // NON-NLS
    }

    public void actionPerformed( final ActionEvent e ) {
      final StringWriter b = new StringWriter();
      final PrintWriter pw = new PrintWriter( b );
      final ExceptionsListModel dataModel = getDataModel();
      final int size = dataModel.getSize();
      for ( int i = 0; i < size; i += 1 ) {
        if ( i != 0 ) {
          pw.println();
          pw.println();
        }

        final Throwable at = (Throwable) dataModel.getElementAt( i );
        at.printStackTrace( pw );
      }

      pw.close();

      ClipboardManager.getManager().setRawContent( new StringSelection( b.toString() ) );
    }
  }


  private class ClearAction extends AbstractAction {
    private ClearAction() {
      putValue( Action.NAME, Messages.getString( "ExceptionDialog.Clear" ) ); // NON-NLS
    }

    public void actionPerformed( final ActionEvent e ) {
      UncaughtExceptionsModel.getInstance().clearExceptions();
      final ExceptionsListModel dataModel = getDataModel();
      dataModel.refresh();
      setStracktraceText( null );
    }
  }

  private class ExceptionSelectionListener implements ListSelectionListener {
    private ExceptionSelectionListener() {
    }

    public void valueChanged( final ListSelectionEvent e ) {
      if ( !e.getValueIsAdjusting() ) {
        final Throwable selectedValue = getSelectedThrowable();
        setStracktraceText( selectedValue );
      }
    }
  }

  private class CloseAction extends AbstractAction {
    /**
     * Defines an <code>Action</code> object with a default description string and default icon.
     */
    private CloseAction() {
      putValue( Action.NAME, Messages.getString( "ExceptionDialog.Close" ) ); // NON-NLS
    }

    /**
     * Invoked when an action occurs.
     */
    public void actionPerformed( final ActionEvent e ) {
      dispose();
    }
  }

  private ExceptionsListModel dataModel;
  private JTextArea stacktraceTextArea;
  private JList list;

  public ExceptionDialog() {
    init();
  }

  public ExceptionDialog( final Frame owner ) {
    super( owner );
    init();
  }

  public ExceptionDialog( final Dialog owner ) {
    super( owner );
    init();
  }

  private void init() {
    setTitle( Messages.getString( "ExceptionDialog.Title" ) ); // NON-NLS
    setDefaultCloseOperation( JDialog.DISPOSE_ON_CLOSE );

    final JPanel contentPane = new JPanel();
    contentPane.setLayout( new BorderLayout() );

    dataModel = new ExceptionsListModel();
    list = new JList( dataModel );
    list.getSelectionModel().setSelectionMode( ListSelectionModel.SINGLE_SELECTION );
    list.setVisibleRowCount( 10 );

    stacktraceTextArea = new JTextArea();
    stacktraceTextArea.setEditable( false );
    stacktraceTextArea.setColumns( 60 );
    stacktraceTextArea.setRows( 10 );

    final JPanel buttonPanel = new JPanel();
    buttonPanel.setLayout( new GridLayout( 1, 3, 5, 5 ) );
    buttonPanel.add( new JButton( new CloseAction() ) );
    buttonPanel.add( new JButton( new ClearAction() ) );
    buttonPanel.add( new JButton( new CopyAction() ) );

    final JPanel buttonCarrier = new JPanel( new FlowLayout( FlowLayout.RIGHT ) );
    buttonCarrier.add( buttonPanel );
    contentPane.add( buttonCarrier, BorderLayout.SOUTH );

    list.addListSelectionListener( new ExceptionSelectionListener() );
    if ( dataModel.getSize() > 0 ) {
      list.setSelectedIndex( dataModel.getSize() - 1 );
    }


    final JSplitPane splitPane =
      new JSplitPane( JSplitPane.VERTICAL_SPLIT, true, new JScrollPane( list ), new JScrollPane(
        stacktraceTextArea ) );
    splitPane.setDividerLocation( 150 );
    contentPane.add( splitPane, BorderLayout.CENTER );

    setContentPane( contentPane );
    pack();

    LibSwingUtil.centerFrameOnScreen( this );


    final InputMap inputMap = contentPane.getInputMap();
    final ActionMap actionMap = contentPane.getActionMap();

    inputMap.put( KeyStroke.getKeyStroke( KeyEvent.VK_ESCAPE, 0 ), "cancel" ); // NON-NLS
    actionMap.put( "cancel", new CloseAction() ); // NON-NLS
  }

  protected ExceptionsListModel getDataModel() {
    return dataModel;
  }

  protected Throwable getSelectedThrowable() {
    return (Throwable) list.getSelectedValue();
  }

  protected static String getStacktraceText( final Throwable throwableInfo ) {
    final StringWriter sw = new StringWriter();
    PrintWriter pw = null;
    try {
      //noinspection IOResourceOpenedButNotSafelyClosed
      pw = new PrintWriter( sw );
      throwableInfo.printStackTrace( pw );
      return sw.getBuffer().toString();
    } finally {
      if ( pw != null ) {
        pw.close();
      }
    }
  }

  protected void setStracktraceText( final Throwable t ) {
    if ( t == null ) {
      stacktraceTextArea.setText( "" );
    } else {
      final String text = getStacktraceText( t );
      stacktraceTextArea.setText( text );
      stacktraceTextArea.setCaretPosition( 0 );
    }
  }

  public void showDialog() {
    dataModel.refresh();
    setVisible( true );
  }

  public static void showDialog( final Component parent ) {
    final ExceptionDialog exceptionDialog;
    final Window window = LibSwingUtil.getWindowAncestor( parent );
    if ( window instanceof Dialog ) {
      exceptionDialog = new ExceptionDialog( (Dialog) window );
    } else if ( window instanceof Frame ) {
      exceptionDialog = new ExceptionDialog( (Frame) window );
    } else {
      exceptionDialog = new ExceptionDialog();
    }
    exceptionDialog.showDialog();

  }
}
