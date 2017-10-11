/*
* This program is free software; you can redistribute it and/or modify it under the
* terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
* Foundation.
*
* You should have received a copy of the GNU Lesser General Public License along with this
* program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
* or from the Free Software Foundation, Inc.,
* 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
*
* This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
* without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
* See the GNU Lesser General Public License for more details.
*
* Copyright (c) 2006 - 2017 Hitachi Vantara and Contributors.  All rights reserved.
*/

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
