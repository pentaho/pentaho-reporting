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


package org.pentaho.reporting.libraries.fonts.registry;

import org.pentaho.reporting.libraries.fonts.cache.FontCache;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;

/**
 * A dummy implementation for test purposes
 *
 * @author Andrey Khayrutdinov
 */
public class DummyFontFileRegistry extends AbstractFontFileRegistry {
  protected FileFilter getFileFilter() {
    return null;
  }

  protected boolean addFont( final File font, final String encoding ) throws IOException {
    return false;
  }

  public FontCache getSecondLevelCache() {
    return null;
  }

  public FontMetricsFactory createMetricsFactory() {
    return null;
  }
}
