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

package org.pentaho.reporting.designer.core.editor.expressions;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.libraries.base.util.ResourceBundleSupport;

import javax.swing.*;
import java.util.Locale;
import java.util.MissingResourceException;

public class EditorExpressionsMessages {
  private static final Log logger = LogFactory.getLog( EditorExpressionsMessages.class );
  private static ResourceBundleSupport bundle =
    new ResourceBundleSupport( Locale.getDefault(),
      "org.pentaho.reporting.designer.core.editor.expressions.messages",// NON-NLS
      ObjectUtilities.getClassLoader( EditorExpressionsMessages.class ) );

  private EditorExpressionsMessages() {
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

  /**
   * Gets a string for the given key from this resource bundle or one of its parents. If the key is a link, the link is
   * resolved and the referenced string is returned instead. If the given key cannot be resolved, no exception will be
   * thrown and a generic placeholder is used instead.
   *
   * @param key the key for the desired string
   * @return the string for the given key
   * @throws NullPointerException               if <code>key</code> is <code>null</code>
   * @throws java.util.MissingResourceException if no object for the given key can be found
   */
  public static String getString( final String key ) {
    try {
      return bundle.getString( key );
    } catch ( MissingResourceException e ) {
      logger.warn( "Missing localization: " + key, e );//NON-NLS
      return '!' + key + '!';
    }
  }

  /**
   * Formats the message stored in the resource bundle (using a MessageFormat).
   *
   * @param key    the resourcebundle key
   * @param param1 the parameter for the message
   * @return the formated string
   */
  public static String getString( final String key, final Object param1 ) {
    try {
      return bundle.formatMessage( key, param1 );
    } catch ( MissingResourceException e ) {
      logger.warn( "Missing localization: " + key, e );//NON-NLS
      return '!' + key + '!';
    }
  }

  /**
   * Formats the message stored in the resource bundle (using a MessageFormat).
   *
   * @param key    the resourcebundle key
   * @param param1 the parameter for the message
   * @param param2 the parameter for the message
   * @return the formated string
   */
  public static String getString( final String key, final Object param1, final Object param2 ) {
    try {
      return bundle.formatMessage( key, param1, param2 );
    } catch ( MissingResourceException e ) {
      logger.warn( "Missing localization: " + key, e );//NON-NLS
      return '!' + key + '!';
    }
  }

  /**
   * Formats the message stored in the resource bundle (using a MessageFormat).
   *
   * @param key    the resourcebundle key
   * @param param1 the parameter for the message
   * @param param2 the parameter for the message
   * @param param3 the parameter for the message
   * @return the formated string
   */
  public static String getString( final String key, final Object param1, final Object param2, final Object param3 ) {
    try {
      return bundle.formatMessage( key, new Object[] { param1, param2, param3 } );
    } catch ( MissingResourceException e ) {
      logger.warn( "Missing localization: " + key, e );//NON-NLS
      return '!' + key + '!';
    }
  }

  /**
   * Formats the message stored in the resource bundle (using a MessageFormat).
   *
   * @param key    the resourcebundle key
   * @param param1 the parameter for the message
   * @param param2 the parameter for the message
   * @param param3 the parameter for the message
   * @param param4 the parameter for the message
   * @return the formated string
   */
  public static String getString( final String key,
                                  final Object param1,
                                  final Object param2,
                                  final Object param3,
                                  final Object param4 ) {
    try {
      return bundle.formatMessage( key, new Object[] { param1, param2, param3, param4 } );
    } catch ( MissingResourceException e ) {
      logger.warn( "Missing localization: " + key, e );//NON-NLS
      return '!' + key + '!';
    }
  }


  /**
   * Formats the message stored in the resource bundle (using a MessageFormat).
   *
   * @param key    the resourcebundle key
   * @param param1 the parameter for the message
   * @param param2 the parameter for the message
   * @param param3 the parameter for the message
   * @param param4 the parameter for the message
   * @param param5 the parameter for the message
   * @return the formated string
   */
  public static String getString( final String key,
                                  final Object param1,
                                  final Object param2,
                                  final Object param3,
                                  final Object param4,
                                  final Object param5 ) {
    try {
      return bundle.formatMessage( key, new Object[] { param1, param2, param3, param4, param5 } );
    } catch ( MissingResourceException e ) {
      logger.warn( "Missing localization: " + key, e );//NON-NLS
      return '!' + key + '!';
    }
  }

}
