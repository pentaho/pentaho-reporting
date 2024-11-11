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
