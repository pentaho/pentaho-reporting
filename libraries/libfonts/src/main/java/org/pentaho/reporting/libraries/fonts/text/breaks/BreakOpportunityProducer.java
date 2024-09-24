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

package org.pentaho.reporting.libraries.fonts.text.breaks;

import org.pentaho.reporting.libraries.fonts.text.ClassificationProducer;

/**
 * Checks for break-opportunities. The break opportunity is always a break after the last codepoint, under the
 * condition, that this codepoint does not belong to a grapheme cluster.
 * <p/>
 * This means, if we test the sequence 'ab', we cannot be sure that the letter 'a' is breakable, unless we've seen 'b'
 * and have verified that 'b' is no extension or formatting character.
 * <p/>
 * To use this producer properly, make sure that no extension characters get fed into it.
 *
 * @author Thomas Morgner
 */
public interface BreakOpportunityProducer extends ClassificationProducer {
  /**
   * Never do any breaking.
   */
  public static final int BREAK_NEVER = 0;
  /**
   * Breaks allowed, it is an generic position.
   */
  public static final int BREAK_CHAR = 1;
  /**
   * Break allowed, this is after a syllable is complete.
   */
  public static final int BREAK_SYLLABLE = 2;
  /**
   * Break allowed, this is after a word is complete or a whitespace has been encountered.
   */
  public static final int BREAK_WORD = 3;
  /**
   * Break allowed, this is after a line is complete or a forced linebreak has been encountered.
   */
  public static final int BREAK_LINE = 4;

  public int createBreakOpportunity( int codepoint );

  public Object clone() throws CloneNotSupportedException;

  public void reset();
}
