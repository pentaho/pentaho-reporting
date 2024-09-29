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


package org.pentaho.reporting.libraries.designtime.swing;

import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * A base class for implementations that have to track the current keyboard focus. This class takes care of all change
 * events regarding the focus-manager and channels all focus-change events through a single update method.
 *
 * @author Thomas Morgner
 */
public abstract class FocusTracker {
  /**
   * An internal change handler that checks whether the current focus-component has been replaced.
   */
  private class FocusManagerChangeHandler implements PropertyChangeListener {
    private FocusManagerChangeHandler() {
    }

    public void propertyChange( final PropertyChangeEvent evt ) {
      if ( evt.getNewValue() instanceof Component ) {
        final Component component = (Component) evt.getNewValue();
        focusChanged( component );
      } else {
        focusChanged( null );
      }
    }
  }

  private static final String PERMANENT_FOCUS_OWNER = "permanentFocusOwner";

  /**
   * Creates a new focus-tracker and registers it on the permanent-focus-owner property.
   */
  protected FocusTracker() {
    final KeyboardFocusManager currentManager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
    currentManager.addPropertyChangeListener( PERMANENT_FOCUS_OWNER, new FocusManagerChangeHandler() );
  }

  /**
   * A notifier method that is called whenever the permanentFocusOwner property changed.
   *
   * @param c the component that has the focus, or null if no component in this application has the focus.
   */
  protected abstract void focusChanged( final Component c );
}
