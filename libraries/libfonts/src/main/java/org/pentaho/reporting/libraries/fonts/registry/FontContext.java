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

/**
 * The font context decribes how a certain font will be used. The context influences the font metrics and therefore a
 * certain metrics object is only valid for a given font context.
 *
 * @author Thomas Morgner
 */
public interface FontContext {
  public String getEncoding();

  public boolean isEmbedded();

  /**
   * This is controlled by the output target and the stylesheet. If the output target does not support aliasing, it
   * makes no sense to enable it and all such requests are ignored.
   *
   * @return
   */
  public boolean isAntiAliased();

  /**
   * This is defined by the output target. This is not controlled by the stylesheet.
   *
   * @return
   */
  public boolean isFractionalMetrics();

  /**
   * The requested font size. A font may have a fractional font size (ie. 8.5 point). The font size may be influenced by
   * the output target.
   *
   * @return the font size.
   */
  public double getFontSize();

}
