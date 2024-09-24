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
 * Creation-Date: 16.12.2005, 19:51:49
 *
 * @author Thomas Morgner
 */
public class FontType {
  public static final FontType PFM = new FontType( "PFM" );
  public static final FontType AFM = new FontType( "AFM" );
  public static final FontType OTHER = new FontType( "OTHER" );
  public static final FontType OPENTYPE = new FontType( "OPENTYPE" );
  public static final FontType AWT = new FontType( "AWT" );
  public static final FontType MONOSPACE = new FontType( "MONOSPACE" );

  private final String myName; // for debug only

  /**
   * We intentionally allow others to derive other font types.
   *
   * @param name the name.
   */
  protected FontType( final String name ) {
    myName = name;
  }

  public String toString() {
    return myName;
  }
}
