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

/**
 * Creation-Date: 13.05.2007, 13:43:54
 *
 * @author Thomas Morgner
 */
public interface FontSource extends FontRecord {

  /**
   * Returns the file name used to load the font. This method exists only for iText.
   *
   * @return this is needed for iText.
   */
  public String getFontSource();

  public boolean isEmbeddable();
}
