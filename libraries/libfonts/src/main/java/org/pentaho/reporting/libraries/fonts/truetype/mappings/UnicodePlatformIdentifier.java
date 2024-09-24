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

package org.pentaho.reporting.libraries.fonts.truetype.mappings;

/**
 * Creation-Date: 06.11.2005, 21:44:21
 *
 * @author Thomas Morgner
 */
public class UnicodePlatformIdentifier extends PlatformIdentifier {
  public UnicodePlatformIdentifier() {
    super( 0 );
  }

  /**
   * According to the Apple OpenType specifications, all Unicode characters must be encoded using UTF-16. Depending on
   * the encodingId, some blocks may be interpreted differently. LibFont ignores that and uses the Java-Default UTF-16
   * mapping.
   * <p/>
   * <a href="http://developer.apple.com/fonts/TTRefMan/RM06/Chap6name.html#ID">Source</a>
   *
   * @param encodingId
   * @param language
   * @return the encoding, always "UTF-16"
   */
  public String getEncoding( final int encodingId, final int language ) {
    return "UTF-16";
  }
}
