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


package org.pentaho.reporting.libraries.fonts.text;

/**
 * Creation-Date: 04.04.2007, 14:03:19
 *
 * @author Thomas Morgner
 */
public interface FontSpecification {
  public String getFontFamily();

  public boolean isAntiAliasing();

  public boolean isSmallCaps();

  public boolean isItalic();

  public boolean isOblique();

  public int getFontWeight();

  public double getFontSize();

  public String getEncoding();

  public boolean isEmbedFontData();
}
