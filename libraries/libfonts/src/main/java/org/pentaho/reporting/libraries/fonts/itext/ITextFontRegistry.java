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

package org.pentaho.reporting.libraries.fonts.itext;

import org.pentaho.reporting.libraries.fonts.LibFontBoot;
import org.pentaho.reporting.libraries.fonts.afm.AfmFontRegistry;
import org.pentaho.reporting.libraries.fonts.cache.FontCache;
import org.pentaho.reporting.libraries.fonts.merge.CompoundFontRegistry;
import org.pentaho.reporting.libraries.fonts.pfm.PfmFontRegistry;
import org.pentaho.reporting.libraries.fonts.registry.FontMetricsFactory;
import org.pentaho.reporting.libraries.fonts.truetype.TrueTypeFontRegistry;

/**
 * This class provides access to the iText font system. The IText registry does not actually use iText to register the
 * fonts (as iText does not provide all information we need for that task).
 *
 * @author Thomas Morgner
 */
public class ITextFontRegistry extends CompoundFontRegistry {
  private static FontCache secondLevelCache;

  protected static synchronized FontCache internalGetSecondLevelCache() {
    if ( secondLevelCache == null ) {
      secondLevelCache = LibFontBoot.getInstance().createDefaultCache();
    }
    return secondLevelCache;
  }

  public ITextFontRegistry() {
    addRegistry( new ITextBuiltInFontRegistry() );
    addRegistry( new TrueTypeFontRegistry() );
    addRegistry( new AfmFontRegistry() );
    addRegistry( new PfmFontRegistry() );
  }

  public FontCache getSecondLevelCache() {
    return internalGetSecondLevelCache();
  }

  public FontMetricsFactory createMetricsFactory() {
    return new ITextFontMetricsFactory( this );
  }
}
