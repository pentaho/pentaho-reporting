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


package org.pentaho.reporting.libraries.designtime.swing.colorchooser;

import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.libraries.base.util.ResourceBundleSupport;

import java.util.Locale;

public class ColorChooserMessages extends ResourceBundleSupport {
  private static ColorChooserMessages instance;

  public static synchronized ColorChooserMessages getInstance() {
    if ( instance == null ) {
      instance = new ColorChooserMessages( Locale.getDefault() );
    }
    return instance;
  }

  /**
   * Creates a new instance.
   */
  public ColorChooserMessages( final Locale locale ) {
    super( locale, "org.pentaho.reporting.libraries.designtime.swing.colorchooser.messages",  // NON-NLS
      ObjectUtilities.getClassLoader( ColorChooserMessages.class ) );
  }
}
