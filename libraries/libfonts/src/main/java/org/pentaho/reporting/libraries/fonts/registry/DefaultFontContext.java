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
 * Creation-Date: 01.02.2006, 22:10:01
 *
 * @author Thomas Morgner
 */
public class DefaultFontContext implements FontContext {
  private double fontSize;
  private boolean antiAliased;
  private boolean fractionalMetrics;
  private boolean embedded;
  private String encoding;

  public DefaultFontContext( final double fontSize,
                             final boolean antiAliased,
                             final boolean fractionalMetrics,
                             final boolean embedded,
                             final String encoding ) {
    this.embedded = embedded;
    this.encoding = encoding;
    this.fontSize = fontSize;
    this.antiAliased = antiAliased;
    this.fractionalMetrics = fractionalMetrics;
  }

  /**
   * This is controlled by the output target and the stylesheet. If the output target does not support aliasing, it
   * makes no sense to enable it and all such requests are ignored.
   *
   * @return
   */
  public boolean isAntiAliased() {
    return antiAliased;
  }

  /**
   * This is defined by the output target. This is not controlled by the stylesheet.
   *
   * @return
   */
  public boolean isFractionalMetrics() {
    return fractionalMetrics;
  }

  /**
   * The requested font size. A font may have a fractional font size (ie. 8.5 point). The font size may be influenced by
   * the output target.
   *
   * @return the font size.
   */
  public double getFontSize() {
    return fontSize;
  }

  public boolean isEmbedded() {
    return embedded;
  }

  public String getEncoding() {
    return encoding;
  }
}
