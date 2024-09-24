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

import java.io.Serializable;

/**
 * Creation-Date: 07.11.2005, 19:06:25
 *
 * @author Thomas Morgner
 */
public interface FontFamily extends Serializable {
  /**
   * Returns the name of the font family (in english).
   *
   * @return
   */
  public String getFamilyName();

  public String[] getAllNames();

  /**
   * This selects the most suitable font in that family. Italics fonts are preferred over oblique fonts.
   *
   * @param bold
   * @param italics
   * @return
   */
  public FontRecord getFontRecord( final boolean bold, final boolean italics );
}
