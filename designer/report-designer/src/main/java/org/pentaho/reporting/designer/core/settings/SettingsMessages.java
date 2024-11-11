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


package org.pentaho.reporting.designer.core.settings;

import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.libraries.base.util.ResourceBundleSupport;

import java.util.Locale;

/**
 * Todo: Document me!
 * <p/>
 * Date: 07.12.2009 Time: 15:14:55
 *
 * @author Thomas Morgner.
 */
public class SettingsMessages extends ResourceBundleSupport {
  private static SettingsMessages instance;

  /**
   * Creates a new instance.
   */
  private SettingsMessages() {
    super( Locale.getDefault(), "org.pentaho.reporting.designer.core.settings.messages.messages",//NON-NLS
      ObjectUtilities.getClassLoader( SettingsMessages.class ) );
  }

  public static synchronized SettingsMessages getInstance() {
    if ( instance == null ) {
      instance = new SettingsMessages();
    }
    return instance;
  }
}

