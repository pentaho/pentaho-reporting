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

package org.pentaho.reporting.libraries.fonts;

import org.pentaho.reporting.libraries.base.boot.AbstractBoot;
import org.pentaho.reporting.libraries.base.config.Configuration;
import org.pentaho.reporting.libraries.base.versioning.ProjectInformation;
import org.pentaho.reporting.libraries.fonts.cache.FontCache;
import org.pentaho.reporting.libraries.fonts.cache.LeastFrequentlyUsedCache;


/**
 * Creation-Date: 06.11.2005, 18:25:11
 *
 * @author Thomas Morgner
 */
public class LibFontBoot extends AbstractBoot {
  private static LibFontBoot instance;

  public static synchronized LibFontBoot getInstance() {
    if ( instance == null ) {
      instance = new LibFontBoot();
    }
    return instance;
  }

  private LibFontBoot() {
  }

  protected Configuration loadConfiguration() {
    return createDefaultHierarchicalConfiguration
      ( "/org/pentaho/reporting/libraries/fonts/libfont.properties",
        "/libfont.properties", true, LibFontBoot.class );

  }

  protected void performBoot() {
    //    Log.debug ("LibFonts ..");
  }

  protected ProjectInformation getProjectInfo() {
    return LibFontInfo.getInstance();
  }

  public FontCache createDefaultCache() {
    // we should make this configurable in some way ..
    return new LeastFrequentlyUsedCache( 30 );
  }
}
