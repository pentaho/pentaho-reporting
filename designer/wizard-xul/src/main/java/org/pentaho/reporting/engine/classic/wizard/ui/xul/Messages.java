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


package org.pentaho.reporting.engine.classic.wizard.ui.xul;

import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.libraries.base.util.ResourceBundleSupport;

import java.util.HashMap;
import java.util.Locale;

public class Messages extends ResourceBundleSupport {
  private static final String BUNDLENAME = "org.pentaho.reporting.engine.classic.wizard.ui.xul.messages"; //$NON-NLS-1$
  private static HashMap<Locale, Messages> locales;

  public static Messages getInstance() {
    return getInstance( Locale.getDefault() );
  }

  public static synchronized Messages getInstance( final Locale locale ) {
    if ( locales == null ) {
      locales = new HashMap<Locale, Messages>();
      final Messages retval = new Messages( locale, BUNDLENAME );
      locales.put( locale, retval );
      return retval;
    }

    final Messages o = locales.get( locale );
    if ( o != null ) {
      return o;
    }

    final Messages retval = new Messages( locale, BUNDLENAME );
    locales.put( locale, retval );
    return retval;
  }

  private Messages( final Locale locale, final String s ) {
    super( locale, s, ObjectUtilities.getClassLoader( Messages.class ) );
  }
}
