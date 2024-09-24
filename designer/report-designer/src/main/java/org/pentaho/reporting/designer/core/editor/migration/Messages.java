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

package org.pentaho.reporting.designer.core.editor.migration;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.libraries.base.util.ResourceBundleSupport;

import javax.swing.*;
import java.util.Locale;
import java.util.MissingResourceException;

public class Messages {
  private static final Log logger = LogFactory.getLog( Messages.class );
  private static ResourceBundleSupport bundle =
    new ResourceBundleSupport( Locale.getDefault(), "org.pentaho.reporting.designer.core.editor.migration.messages",
      //NON-NLS
      ObjectUtilities.getClassLoader( Messages.class ) );

  private Messages() {
  }

  /**
   * Formats the message stored in the resource bundle (using a MessageFormat).
   *
   * @param key    the resourcebundle key
   * @param param1 the parameter for the message
   * @return the formated string
   */
  public static String getString( final String key, final Object... param1 ) {
    try {
      return bundle.formatMessage( key, param1 );
    } catch ( MissingResourceException e ) {
      logger.warn( "Missing localization: " + key, e );//NON-NLS
      return '!' + key + '!';
    }
  }

  public static Icon getIcon( final String key, final boolean large ) {
    return bundle.getIcon( key, large );
  }

  public static Icon getIcon( final String key ) {
    return bundle.getIcon( key );
  }

  public static Integer getMnemonic( final String key ) {
    return bundle.getMnemonic( key );
  }

  public static Integer getOptionalMnemonic( final String key ) {
    return bundle.getOptionalMnemonic( key );
  }

  public static KeyStroke getKeyStroke( final String key ) {
    return bundle.getKeyStroke( key );
  }

  public static KeyStroke getOptionalKeyStroke( final String key ) {
    return bundle.getOptionalKeyStroke( key );
  }

  public static KeyStroke getKeyStroke( final String key, final int mask ) {
    return bundle.getKeyStroke( key, mask );
  }

  public static KeyStroke getOptionalKeyStroke( final String key, final int mask ) {
    return bundle.getOptionalKeyStroke( key, mask );
  }

}
