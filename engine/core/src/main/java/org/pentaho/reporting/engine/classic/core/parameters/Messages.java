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


package org.pentaho.reporting.engine.classic.core.parameters;

import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.libraries.base.util.ResourceBundleSupport;

import java.util.Locale;

public class Messages extends ResourceBundleSupport {

  public static Messages getInstance( Locale locale ) {
    return new Messages( locale );
  }

  /**
   * Creates a new instance based on locale.
   */
  private Messages( Locale locale ) {
    super( locale, "org.pentaho.reporting.engine.classic.core.parameters.messages", ObjectUtilities
            .getClassLoader( Messages.class ) );
  }
}
