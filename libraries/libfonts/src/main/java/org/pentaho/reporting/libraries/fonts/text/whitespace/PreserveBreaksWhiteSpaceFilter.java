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

import org.pentaho.reporting.libraries.fonts.text.ClassificationProducer;

/**
 * Creation-Date: 11.06.2006, 20:18:00
 *
 * @author Thomas Morgner
 */
public class PreserveBreaksWhiteSpaceFilter implements WhiteSpaceFilter {
  private boolean collapse;

  public PreserveBreaksWhiteSpaceFilter() {
  }

  /**
   * Reset the filter to the same state as if the filter had been constructed but not used yet.
   */
  public void reset() {
    collapse = false;
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
    if ( isLinebreak( codepoint ) ) {
      collapse = true;
      return codepoint;
    }

    if ( isWhitespace( codepoint ) ) {
      if ( collapse == true ) {
        return WhiteSpaceFilter.STRIP_WHITESPACE;
      } else {
        collapse = true;
        return ' ';
      }
    }

    if ( codepoint == ClassificationProducer.START_OF_TEXT ) {
      collapse = true;
      return WhiteSpaceFilter.STRIP_WHITESPACE;
    } else if ( codepoint == ClassificationProducer.END_OF_TEXT ) {
      return WhiteSpaceFilter.STRIP_WHITESPACE;
    }

    collapse = false;
    return codepoint;
  }

  private boolean isWhitespace( final int codepoint ) {
    final char ch = (char) ( codepoint & 0xFFFF );
    return Character.isWhitespace( ch );
  }

  protected boolean isLinebreak( final int codepoint ) {
    if ( codepoint == 0xa || codepoint == 0xd ) {
      return true;
    } else {
      return false;
    }
  }


  public Object clone() throws CloneNotSupportedException {
    return super.clone();
  }

}
