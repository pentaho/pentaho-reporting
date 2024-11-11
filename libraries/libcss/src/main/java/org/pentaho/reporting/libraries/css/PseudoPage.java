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


package org.pentaho.reporting.libraries.css;

/**
 * Creation-Date: 29.05.2006, 17:23:07
 *
 * @author Thomas Morgner
 */
public final class PseudoPage {
  public static final PseudoPage LEFT = new PseudoPage( "Left" );
  public static final PseudoPage RIGHT = new PseudoPage( "Right" );
  public static final PseudoPage FIRST = new PseudoPage( "First" );

  private final String myName; // for debug only

  private PseudoPage( String name ) {
    myName = name;
  }

  public String toString() {
    return myName;
  }
}
