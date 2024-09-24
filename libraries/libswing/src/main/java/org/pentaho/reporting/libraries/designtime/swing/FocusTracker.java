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
