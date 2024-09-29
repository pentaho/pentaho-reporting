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


package org.pentaho.reporting.designer.core.util.dnd;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.designer.core.util.exceptions.UncaughtExceptionsModel;
import org.pentaho.reporting.libraries.designtime.swing.GenericTransferable;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

/**
 * Very simple, this class just encapsulates a single clipboard, which may or may not be the system clipboard.
 *
 * @author Thomas Morgner
 */
public class ClipboardManager implements ClipboardOwner {

  public static synchronized ClipboardManager getManager() {
    if ( manager == null ) {
      manager = new ClipboardManager();
    }
    return manager;
  }

  private EventListenerList listeners;
  private static ClipboardManager manager;
  private Clipboard clipboard;
  private static final Log logger = LogFactory.getLog( ClipboardManager.class );

  private ClipboardManager() {
    this.listeners = new EventListenerList();
    try {
      this.clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
    } catch ( final SecurityException se ) {
      logger.error( "Failed to create clipboard", se ); // NON-NLS
      this.clipboard = new Clipboard( "local-clipboard" ); // NON-NLS
    }
  }

  public Object[] getContents() throws UnsupportedFlavorException, IOException {
    try {
      if ( clipboard.isDataFlavorAvailable( GenericTransferable.ELEMENT_FLAVOR ) ) {
        return (Object[]) clipboard.getData( GenericTransferable.ELEMENT_FLAVOR );
      }
    } catch ( Exception e ) {
      UncaughtExceptionsModel.getInstance().addException( e );
    }
    return null;
  }

  public void setRawContent( final Transferable t ) {
    try {
      clipboard.setContents( t, this );
      fireContentsChanged();
    } catch ( Exception ie ) {
      UncaughtExceptionsModel.getInstance().addException( ie );
    }
  }

  public Transferable getRawContent() {
    try {
      return clipboard.getContents( this );
    } catch ( Exception ie ) {
      UncaughtExceptionsModel.getInstance().addException( ie );
      return null;
    }
  }

  public void setContents( final Object[] contents ) {
    try {
      clipboard.setContents( new GenericTransferable( contents ), this );
      fireContentsChanged();
    } catch ( Exception ie ) {
      UncaughtExceptionsModel.getInstance().addException( ie );
    }
  }

  public void lostOwnership( final Clipboard clipboard, final Transferable contents ) {
    fireContentsChanged();
    logger.debug( "Lost ownership:" + contents ); // NON-NLS
  }

  private void fireContentsChanged() {
    final ChangeListener[] changeListeners = listeners.getListeners( ChangeListener.class );
    final ChangeEvent event = new ChangeEvent( this );

    for ( int i = 0; i < changeListeners.length; i++ ) {
      final ChangeListener changeListener = changeListeners[ i ];
      changeListener.stateChanged( event );
    }
  }

  public boolean isDataAvailable() {
    try {
      return clipboard.isDataFlavorAvailable( GenericTransferable.ELEMENT_FLAVOR );
    } catch ( Exception ie ) {
      UncaughtExceptionsModel.getInstance().addException( ie );
    }
    return false;
  }

  public void addChangeListener( final ChangeListener c ) {
    listeners.add( ChangeListener.class, c );
  }

  public void removeChangeListener( final ChangeListener c ) {
    listeners.remove( ChangeListener.class, c );
  }
}
