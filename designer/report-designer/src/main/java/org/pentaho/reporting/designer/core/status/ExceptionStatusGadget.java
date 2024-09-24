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
import org.pentaho.reporting.designer.core.util.IconLoader;
import org.pentaho.reporting.designer.core.util.exceptions.UncaughtExceptionModelListener;
import org.pentaho.reporting.designer.core.util.exceptions.UncaughtExceptionsModel;
import org.pentaho.reporting.libraries.designtime.swing.LibSwingUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Todo: Document me!
 *
 * @author Thomas Morgner
 */
public class ExceptionStatusGadget extends JLabel implements UncaughtExceptionModelListener {
  private boolean firstTime;
  private ExceptionDialog exceptionDialog;
  private ImageIcon errorIcon;
  private ImageIcon noErrorIcon;

  public ExceptionStatusGadget() {
    firstTime = true;
    errorIcon = IconLoader.getInstance().getErrorIcon();
    noErrorIcon = IconLoader.getInstance().getNoErrorIcon();

    setHorizontalAlignment( JLabel.CENTER );
    setIcon( noErrorIcon );
    setCursor( Cursor.getPredefinedCursor( Cursor.HAND_CURSOR ) );

    addMouseListener( new MouseSelectionHandler() );

    final Window parent = LibSwingUtil.getWindowAncestor( this );
    if ( parent instanceof Frame ) {
      exceptionDialog = new ExceptionDialog( (Frame) parent );
    } else if ( parent instanceof Dialog ) {
      exceptionDialog = new ExceptionDialog( (Dialog) parent );
    } else {
      exceptionDialog = new ExceptionDialog();
    }
    UncaughtExceptionsModel.getInstance().addUncaughtExceptionModelListener( this );
  }

  private static class ClearErrorMessageAction implements ActionListener {
    private final JLabel label;

    private ClearErrorMessageAction( final JLabel label ) {
      this.label = label;
    }

    public void actionPerformed( final ActionEvent e ) {
      final Container container = label.getParent();
      if ( container != null ) {
        container.remove( label );
        container.repaint();
      }
    }
  }

  protected void handleExceptionClick() {
    setIcon( IconLoader.getInstance().getNoErrorIcon() );
    UncaughtExceptionsModel.getInstance().exceptionsViewed();

    exceptionDialog.showDialog();
  }

  private class MouseSelectionHandler extends MouseAdapter {
    @Override
    public void mouseClicked( final MouseEvent ex ) {
      handleExceptionClick();
    }
  }

  public void exceptionCaught( final Throwable throwable ) {
    if ( firstTime ) {
      firstTime = false;

      Component rootPane = getParent();
      while ( rootPane != null && !( rootPane instanceof JRootPane ) ) {
        rootPane = rootPane.getParent();
      }
      if ( rootPane != null ) {
        final JRootPane jRootPane = (JRootPane) rootPane;

        final JLabel errorPopup = createPopup();
        jRootPane.getLayeredPane().add( errorPopup, JLayeredPane.POPUP_LAYER );

        final Rectangle rectangle = SwingUtilities.convertRectangle
          ( getParent(), getBounds(), jRootPane.getLayeredPane() );
        final Dimension size = errorPopup.getPreferredSize();
        errorPopup.setBounds( (int) ( rectangle.getX() - size.width ),
          (int) ( rectangle.getY() - size.height ), size.width, size.height );
        jRootPane.getLayeredPane().revalidate();
        jRootPane.getLayeredPane().repaint();
      }
    }
    if ( getIcon() == noErrorIcon ) {
      setIcon( errorIcon );
    }
    repaint();
  }

  private JLabel createPopup() {
    final JLabel label = new JLabel( Messages.getString( "StatusBar.InternalError.Message" ) ); // NON-NLS
    label.setForeground( new Color( 120, 0, 0 ) );
    label.setBorder( BorderFactory.createCompoundBorder( BorderFactory.createLineBorder( Color.RED ),
      BorderFactory.createEmptyBorder( 8, 8, 8, 8 ) ) );
    label.setBackground( new Color( 255, 0, 0, 125 ) );
    label.setOpaque( true );

    final Timer t = new Timer( 5000, new ClearErrorMessageAction( label ) );
    t.setRepeats( false );
    t.start();
    return label;
  }

  public void exceptionsCleared() {
    firstTime = true;
    setIcon( IconLoader.getInstance().getNoErrorIcon() );
  }

  public void exceptionsViewed() {
    setIcon( IconLoader.getInstance().getNoErrorIcon() );
  }
}
