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


package org.pentaho.reporting.libraries.fonts.cache;

import org.pentaho.reporting.libraries.fonts.registry.FontKey;
import org.pentaho.reporting.libraries.fonts.registry.FontMetrics;

/**
 * A empty font cache that does not cache.
 *
 * @author Thomas Morgner
 */
public class NullFontCache implements FontCache {
  public NullFontCache() {
  }

  public FontMetrics getFontMetrics( final FontKey fontKey ) {
    return null;
  }

  public void putFontMetrics( final FontKey key, final FontMetrics fontMetrics ) {
  }

  public void commit() {

  }
}
