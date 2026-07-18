/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 - 2026 by Pentaho Canada Inc. : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2030-06-15
 ******************************************************************************/



package org.pentaho.reporting.designer.extensions.pentaho.repository;

import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.libraries.base.util.ResourceBundleSupport;

import java.util.Locale;

public class Messages extends ResourceBundleSupport {
  private static Messages instance;

  /**
   * Creates a new instance.
   */
  private Messages() {
    super( Locale.getDefault(), "org.pentaho.reporting.designer.extensions.pentaho.repository.messages",
      ObjectUtilities.getClassLoader( Messages.class ) );
  }

  public static synchronized Messages getInstance() {
    if ( instance == null ) {
      instance = new Messages();
    }
    return instance;
  }
}
