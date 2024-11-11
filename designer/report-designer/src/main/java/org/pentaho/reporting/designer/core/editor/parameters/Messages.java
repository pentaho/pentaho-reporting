/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


package org.pentaho.reporting.designer.core.editor.parameters;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.libraries.base.util.ResourceBundleSupport;

import java.util.Locale;
import java.util.MissingResourceException;

public class Messages {
  private static final Log logger = LogFactory.getLog( Messages.class );
  private static ResourceBundleSupport bundle =
    new ResourceBundleSupport( Locale.getDefault(),
      "org.pentaho.reporting.designer.core.editor.parameters.messages.messages", // NON-NLS
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
      logger.warn( "Missing localization: " + key, e ); // NON-NLS
      return '!' + key + '!';
    }
  }
}
