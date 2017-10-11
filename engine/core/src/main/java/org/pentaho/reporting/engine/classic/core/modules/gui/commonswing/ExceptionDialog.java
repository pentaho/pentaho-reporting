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

package org.pentaho.reporting.engine.classic.core.modules.gui.commonswing;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.io.PrintWriter;
import java.util.Locale;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.UIManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.libraries.base.util.MemoryStringWriter;
import org.pentaho.reporting.libraries.base.util.Messages;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.libraries.designtime.swing.LibSwingUtil;

/**
 * The exception dialog is used to display an exception and the exceptions stacktrace to the user.
 *
 * @author Thomas Morgner
 */
public class ExceptionDialog extends JDialog {
  private static final Log logger = LogFactory.getLog( ExceptionDialog.class );

  /**
   * OK action.
   */
  private final class OKAction extends AbstractAction {
    /**
     * Default constructor.
     */
    private OKAction() {
      putValue( Action.NAME, UIManager.getDefaults().getString( "OptionPane.okButtonText" ) ); //$NON-NLS-1$
    }

    /**
     * Receives notification that an action event has occurred.
     *
     * @param event
     *          the action event.
     */
    public void actionPerformed( final ActionEvent event ) {
      setVisible( false );
    }
  }

  /**
   * Details action.
   */
  private final class DetailsAction extends AbstractAction {
    /**
     * Default constructor.
     */
    private DetailsAction() {
      putValue( Action.NAME, ">>" ); //$NON-NLS-1$
    }

    /**
     * Receives notification that an action event has occurred.
     *
     * @param event
     *          the action event.
     */
    public void actionPerformed( final ActionEvent event ) {
      setScrollerVisible( !( isScrollerVisible() ) );
      if ( isScrollerVisible() ) {
        putValue( Action.NAME, "<<" ); //$NON-NLS-1$
      } else {
        putValue( Action.NAME, ">>" ); //$NON-NLS-1$
      }
      adjustSize();
    }
  }

  /**
   * A UI component for displaying the stack trace.
   */
  private JTextArea backtraceArea;

  /**
   * A UI component for displaying the message.
   */
  private JLabel messageLabel;

  /**
   * The exception.
   */
  private Exception currentEx;

  /**
   * An action associated with the 'Details' button.
   */
  private DetailsAction detailsAction;

  /**
   * A scroll pane.
   */
  private JScrollPane scroller;

  /**
   * A filler panel.
   */
  private JPanel filler;

  /**
   * Localized message class
   */
  private Messages messages;

  /**
   * Creates a new ExceptionDialog.
   */
  public ExceptionDialog() {
    init();
  }

  public ExceptionDialog( final Frame parent ) {
    super( parent );
    init();
  }

  public ExceptionDialog( final Dialog parent ) {
    super( parent );
    init();
  }

  private void init() {
    messages =
        new Messages( getLocale(), SwingCommonModule.BUNDLE_NAME, ObjectUtilities
            .getClassLoader( SwingCommonModule.class ) );
    setModal( true );
    detailsAction = new DetailsAction();

    messageLabel = new JLabel();
    backtraceArea = new JTextArea();

    scroller = new JScrollPane( backtraceArea );
    scroller.setVisible( false );

    final JPanel detailPane = new JPanel();
    detailPane.setLayout( new GridBagLayout() );
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.anchor = GridBagConstraints.CENTER;
    gbc.fill = GridBagConstraints.NONE;
    gbc.weightx = 0;
    gbc.weighty = 0;
    gbc.gridx = 0;
    gbc.gridy = 0;
    final JLabel icon = new JLabel( UIManager.getDefaults().getIcon( "OptionPane.errorIcon" ) ); //$NON-NLS-1$
    icon.setBorder( BorderFactory.createEmptyBorder( 10, 10, 10, 10 ) );
    detailPane.add( icon, gbc );

    gbc = new GridBagConstraints();
    gbc.anchor = GridBagConstraints.WEST;
    gbc.fill = GridBagConstraints.NONE;
    gbc.weightx = 1;
    gbc.weighty = 1;
    gbc.gridx = 1;
    gbc.gridy = 0;
    detailPane.add( messageLabel );

    gbc = new GridBagConstraints();
    gbc.anchor = GridBagConstraints.SOUTH;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.weightx = 0;
    gbc.weighty = 0;
    gbc.gridx = 0;
    gbc.gridy = 2;
    gbc.gridwidth = 2;
    detailPane.add( createButtonPane(), gbc );

    filler = new JPanel();
    filler.setPreferredSize( new Dimension( 0, 0 ) );
    filler.setBackground( Color.green );
    gbc = new GridBagConstraints();
    gbc.anchor = GridBagConstraints.NORTH;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.weightx = 1;
    gbc.weighty = 5;
    gbc.gridx = 0;
    gbc.gridy = 3;
    gbc.gridwidth = 2;
    detailPane.add( filler, gbc );

    gbc = new GridBagConstraints();
    gbc.anchor = GridBagConstraints.SOUTHWEST;
    gbc.fill = GridBagConstraints.BOTH;
    gbc.weightx = 1;
    gbc.weighty = 5;
    gbc.gridx = 0;
    gbc.gridy = 4;
    gbc.gridwidth = 2;
    detailPane.add( scroller, gbc );

    setContentPane( detailPane );
  }

  /**
   * Adjusts the size of the dialog to fit the with of the contained message and stacktrace.
   */
  public void adjustSize() {
    final Dimension scSize = scroller.getPreferredSize();
    final Dimension cbase = filler.getPreferredSize();
    cbase.width = Math.max( scSize.width, cbase.width );
    cbase.height = 0;
    filler.setMinimumSize( cbase );
    pack();

  }

  /**
   * Defines, whether the scroll pane of the exception stack trace area is visible.
   *
   * @param b
   *          true, if the scroller should be visible, false otherwise.
   */
  protected void setScrollerVisible( final boolean b ) {
    scroller.setVisible( b );
  }

  /**
   * Checks whether the scroll pane of the exception stack trace area is visible.
   *
   * @return true, if the scroller is visible, false otherwise.
   */
  protected boolean isScrollerVisible() {
    return scroller.isVisible();
  }

  /**
   * Initializes the buttonpane.
   *
   * @return a panel containing the 'OK' and 'Details' buttons.
   */
  private JPanel createButtonPane() {
    final JPanel buttonPane = new JPanel();
    buttonPane.setLayout( new FlowLayout( FlowLayout.RIGHT, 5, 5 ) );
    buttonPane.setBorder( BorderFactory.createEmptyBorder( 2, 2, 2, 2 ) );

    buttonPane.add( new JButton( new OKAction() ) );
    buttonPane.add( new JButton( detailsAction ) );
    return buttonPane;
  }

  /**
   * Sets the message for this exception dialog. The message is displayed on the main page.
   *
   * @param mesg
   *          the message.
   */
  public void setMessage( final String mesg ) {
    messageLabel.setText( mesg );
  }

  /**
   * Returns the message for this exception dialog. The message is displayed on the main page.
   *
   * @return the message.
   */
  public String getMessage() {
    return messageLabel.getText();
  }

  /**
   * Sets the exception for this dialog. If no exception is set, the "Detail" button is disabled and the stacktrace text
   * cleared. Else the stacktraces text is read into the detail message area.
   *
   * @param e
   *          the exception.
   */
  public void setException( final Exception e ) {
    currentEx = e;
    if ( e == null ) {
      detailsAction.setEnabled( false );
      backtraceArea.setText( "" ); //$NON-NLS-1$
    } else {
      backtraceArea.setText( readFromException( e ) );
    }
  }

  /**
   * Reads the stacktrace text from the exception.
   *
   * @param e
   *          the exception.
   * @return the stack trace.
   * @noinspection IOResourceOpenedButNotSafelyClosed
   */
  private String readFromException( final Exception e ) {
    String text = messages.getString( "ExceptionDialog.USER_NO_BACKTRACE" ); //$NON-NLS-1$
    try {
      final MemoryStringWriter writer = new MemoryStringWriter();
      final PrintWriter pwriter = new PrintWriter( writer );
      e.printStackTrace( pwriter );
      text = writer.toString();
      pwriter.close();
    } catch ( Exception ex ) {
      ExceptionDialog.logger.info( messages.getString( "ExceptionDialog.INFO_EXCEPTION_SUPRESSED" ) ); //$NON-NLS-1$
    }
    return text;
  }

  /**
   * Returns the exception that was the reason for this dialog to show up.
   *
   * @return the exception.
   */
  public Exception getException() {
    return currentEx;
  }

  /**
   * Shows an default dialog with the given message and title and the exceptions stacktrace in the detail area.
   *
   * @param parent
   *          the parent component.
   * @param title
   *          the title.
   * @param message
   *          the message.
   * @param e
   *          the exception.
   */
  public static void showExceptionDialog( final Component parent, final String title, final String message,
      final Exception e ) {
    final Window window = LibSwingUtil.getWindowAncestor( parent );
    final ExceptionDialog defaultDialog;
    if ( window instanceof Frame ) {
      defaultDialog = new ExceptionDialog( (Frame) window );
    } else if ( window instanceof Dialog ) {
      defaultDialog = new ExceptionDialog( (Dialog) window );
    } else {
      defaultDialog = new ExceptionDialog();
    }
    if ( e != null ) {
      final Messages messages =
          new Messages( Locale.getDefault(), SwingCommonModule.BUNDLE_NAME, ObjectUtilities
              .getClassLoader( SwingCommonModule.class ) );
      logger.error( messages.getErrorString( "ExceptionDialog.ERROR_0001_USER_ERROR" ), e ); //$NON-NLS-1$
    }
    defaultDialog.setTitle( title );
    defaultDialog.setMessage( message );
    defaultDialog.setException( e );
    defaultDialog.adjustSize();
    defaultDialog.setModal( true );
    LibSwingUtil.centerDialogInParent( defaultDialog );
    defaultDialog.setVisible( true );
  }

}
