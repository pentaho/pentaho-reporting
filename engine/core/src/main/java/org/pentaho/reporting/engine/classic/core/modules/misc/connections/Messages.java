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


package org.pentaho.reporting.engine.classic.core.modules.misc.connections;

import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.libraries.base.util.ResourceBundleSupport;

import java.util.Locale;

public class Messages extends ResourceBundleSupport {
  private static Messages instance;

  public static Messages getInstance() {
    // its ok that this one is not synchronized. I dont care whether we have multiple instances of this
    // beast sitting around, as this is a singleton for convinience reasons.
    if ( instance == null ) {
      instance = new Messages();
    }
    return instance;
  }

  /**
   * Creates a new instance.
   */
  private Messages() {
    super( Locale.getDefault(), "org.pentaho.reporting.engine.classic.core.modules.misc.connections.messages",
        ObjectUtilities.getClassLoader( Messages.class ) );
  }
}
