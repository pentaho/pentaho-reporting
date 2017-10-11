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

package org.pentaho.reporting.engine.classic.core.modules.gui.commonswing.localization;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Locale;

import javax.swing.JComponent;

/**
 * Copies the locale from one central point (usually the dialog) to all listeners.
 *
 * @author Thomas Morgner
 */
public class LocaleUpdateHandler implements PropertyChangeListener {
  private JComponent localizedComponent;

  public LocaleUpdateHandler( final JComponent localizedComponent ) {
    this.localizedComponent = localizedComponent;
  }

  /**
   * This method gets called when a bound property is changed.
   *
   * @param evt
   *          A PropertyChangeEvent object describing the event source and the property that has changed.
   */

  public void propertyChange( final PropertyChangeEvent evt ) {
    final Object newValue = evt.getNewValue();
    if ( newValue instanceof Locale == false ) {
      return;
    }
    localizedComponent.setLocale( (Locale) newValue );
  }
}
