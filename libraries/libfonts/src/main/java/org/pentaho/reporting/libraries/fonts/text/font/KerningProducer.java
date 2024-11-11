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


package org.pentaho.reporting.libraries.fonts.text.font;

import org.pentaho.reporting.libraries.fonts.text.ClassificationProducer;

/**
 * Creation-Date: 11.06.2006, 16:06:54
 *
 * @author Thomas Morgner
 */
public interface KerningProducer extends ClassificationProducer {
  /**
   * Returns the kerning to the previous character (unit is micro-point).
   *
   * @param codePoint
   * @return
   */
  public long getKerning( final int codePoint );
}
