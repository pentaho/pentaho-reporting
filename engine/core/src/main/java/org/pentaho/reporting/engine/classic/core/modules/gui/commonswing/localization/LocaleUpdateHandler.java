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
