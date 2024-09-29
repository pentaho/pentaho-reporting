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


package org.pentaho.reporting.designer.extensions.toc;

import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.libraries.base.util.ResourceBundleSupport;

import java.util.Locale;

public class Messages extends ResourceBundleSupport {
  private static Messages instance;

  public static synchronized Messages getInstance() {
    if ( instance == null ) {
      instance = new Messages( Locale.getDefault() );
    }
    return instance;
  }

  /**
   * Creates a new instance.
   */
  public Messages( final Locale locale ) {
    super( locale, "org.pentaho.reporting.designer.extensions.toc.messages",  // NON-NLS
      ObjectUtilities.getClassLoader( Messages.class ) );
  }
}
