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

package org.pentaho.reporting.libraries.fonts.text.whitespace;

/**
 * Creation-Date: 11.06.2006, 20:18:00
 *
 * @author Thomas Morgner
 */
public class PreserveWhiteSpaceFilter implements WhiteSpaceFilter {
  public PreserveWhiteSpaceFilter() {
  }

  /**
   * Filters the whitespaces. This method returns '-1', if the whitespace should be removed from the stream; otherwise
   * it presents a replacement character. If the codepoint is no whitespace at all, the codepoint is returned
   * unchanged.
   *
   * @param codepoint
   * @return
   */
  public int filter( final int codepoint ) {
    return codepoint;
  }

  /**
   * Reset the filter to the same state as if the filter had been constructed but not used yet.
   */
  public void reset() {

  }


  public Object clone() throws CloneNotSupportedException {
    return super.clone();
  }

}
