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


package org.pentaho.reporting.libraries.fonts.registry;

import java.io.Serializable;

/**
 * A font identifier is a general handle to map Font-Metrics for a given font. The same font identifier may be used by
 * several fonts, if the fonts share the same metrics (this is commonly true for TrueType fonts).
 *
 * @author Thomas Morgner
 */
public interface FontIdentifier extends Serializable {
  public boolean equals( Object o );

  public int hashCode();

  /**
   * Defines, whether the font identifier represents a scalable font type. Such fonts usually create one font metric
   * object for each physical font, and apply the font size afterwards.
   *
   * @return true, if the font is scalable, false otherwise
   */
  public boolean isScalable();

  /**
   * Returns the general type of this font identifier. This is for debugging, not for the real world.
   *
   * @return
   */
  public FontType getFontType();
}
