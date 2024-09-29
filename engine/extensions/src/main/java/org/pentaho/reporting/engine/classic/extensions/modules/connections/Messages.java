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


package org.pentaho.reporting.engine.classic.extensions.modules.connections;

import java.util.Locale;

import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.libraries.base.util.ResourceBundleSupport;

public class Messages extends ResourceBundleSupport {
  private static Messages instance;

  public static Messages getInstance() {
    // its ok that this one is not synchronized. I don't care whether we have multiple instances of this
    // beast sitting around, as this is a singleton for convenience reasons.
    if ( instance == null ) {
      instance = new Messages();
    }
    return instance;
  }

  /**
   * Creates a new instance.
   */
  private Messages() {
    super( Locale.getDefault(), "org.pentaho.reporting.engine.classic.extensions.modules.connections.messages",
        ObjectUtilities.getClassLoader( Messages.class ) );
  }
}
