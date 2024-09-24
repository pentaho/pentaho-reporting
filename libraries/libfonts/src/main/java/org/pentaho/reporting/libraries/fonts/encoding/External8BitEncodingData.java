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

package org.pentaho.reporting.libraries.fonts.encoding;

/**
 * Creation-Date: 29.04.2006, 14:22:21
 *
 * @author Thomas Morgner
 */
public class External8BitEncodingData extends EncodingData {
  private static final long serialVersionUID = 9017639110342367007L;

  private int[] indexDelta;
  private int[] valueDelta;

  public External8BitEncodingData( final int[] indexDelta,
                                   final int[] valueDelta ) {
    if ( indexDelta == null ) {
      throw new NullPointerException();
    }
    if ( valueDelta == null ) {
      throw new NullPointerException();
    }

    this.indexDelta = indexDelta;
    this.valueDelta = valueDelta;
  }

  public int[] getIndexDelta() {
    return indexDelta;
  }

  public int[] getValueDelta() {
    return valueDelta;
  }

}
