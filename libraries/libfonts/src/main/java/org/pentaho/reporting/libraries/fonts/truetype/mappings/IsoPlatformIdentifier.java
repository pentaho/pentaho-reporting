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
 * Creation-Date: 07.11.2005, 15:27:14
 *
 * @author Thomas Morgner
 */
public class IsoPlatformIdentifier extends PlatformIdentifier {
  public IsoPlatformIdentifier() {
    super( 2 );
  }

  public String getEncoding( final int encodingId, final int language ) {
    if ( encodingId == 0 ) {
      return "US_ASCII";
    } else if ( encodingId == 1 ) {
      return "UTF-16";
    }
    return "ISO-8859-1";
  }
}
