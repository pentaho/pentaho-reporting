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

/**
 * Creation-Date: 16.12.2005, 20:11:11
 *
 * @author Thomas Morgner
 */
public interface FontRegistry {
  public void initialize();

  public FontCache getSecondLevelCache();

  /**
   * Tries to find a font family with the given name, looking through all alternative font names if neccessary.
   *
   * @param name
   * @return the font family or null, if there is no such family.
   */
  public FontFamily getFontFamily( String name );

  public String[] getRegisteredFamilies();

  public String[] getAllRegisteredFamilies();

  /**
   * Creates a new font metrics factory. That factory is specific to a certain font registry and is not required to
   * handle font records from foreign font registries.
   * <p/>
   * A font metrics factory should never be used on its own. It should be embedded into and used by a FontStorage
   * implementation.
   *
   * @return
   */
  public FontMetricsFactory createMetricsFactory();
}
