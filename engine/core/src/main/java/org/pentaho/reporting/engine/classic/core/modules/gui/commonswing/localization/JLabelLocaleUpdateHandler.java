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

import java.awt.IllegalComponentStateException;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javax.swing.JLabel;

import org.pentaho.reporting.engine.classic.core.modules.gui.commonswing.SwingCommonModule;
import org.pentaho.reporting.libraries.base.util.Messages;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;

/**
 * Creation-Date: 30.11.2006, 13:04:07
 *
 * @author Thomas Morgner
 */
public class JLabelLocaleUpdateHandler implements PropertyChangeListener {
  private String resourceBundleName;
  private String resourceKey;
  private JLabel target;
  private Messages messages;

  public JLabelLocaleUpdateHandler( final JLabel target, final String resourceBundleName, final String resourceKey ) {
    this.target = target;
    this.resourceBundleName = resourceBundleName;
    this.resourceKey = resourceKey;
    this.messages =
        new Messages( target.getLocale(), SwingCommonModule.BUNDLE_NAME, ObjectUtilities
            .getClassLoader( SwingCommonModule.class ) );
  }

  /**
   * This method gets called when a bound property is changed.
   *
   * @param evt
   *          A PropertyChangeEvent object describing the event source and the property that has changed.
   */

  public void propertyChange( final PropertyChangeEvent evt ) {
    try {
      final Locale locale = target.getLocale();
      final ResourceBundle bundle = ResourceBundle.getBundle( resourceBundleName, locale );
      final String string = bundle.getString( resourceKey );
      target.setText( string );
    } catch ( IllegalComponentStateException ice ) {
      target.setText( messages.getString( "USER_NO_PARENT_ERROR", resourceKey ) ); //$NON-NLS-1$
    } catch ( MissingResourceException mre ) {
      target.setText( messages.getString( "USER_NO_PARENT_ERROR", resourceKey ) ); //$NON-NLS-1$
      target.setText( messages.getString( "USER_MISSING_RESOURCE_ERROR", resourceKey ) ); //$NON-NLS-1$
    }

  }
}
