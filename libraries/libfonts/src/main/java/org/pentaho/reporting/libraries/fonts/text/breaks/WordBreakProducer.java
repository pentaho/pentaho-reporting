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

/**
 * This is the standard behaviour for HTML.It breaks texts at word boundaries.
 *
 * @author Thomas Morgner
 */
public class WordBreakProducer extends LineBreakProducer {
  public WordBreakProducer() {
  }

  public int createBreakOpportunity( final int codepoint ) {
    final int breakOpportunity = super.createBreakOpportunity( codepoint );
    if ( breakOpportunity != BreakOpportunityProducer.BREAK_NEVER ) {
      return breakOpportunity;
    }

    // cheating here for now. Needs an implementation.
    if ( Character.isWhitespace( (char) codepoint ) ||
      codepoint == 8203 ) // zero-width-no-joiner.
    {
      return BreakOpportunityProducer.BREAK_WORD;
    }

    return BreakOpportunityProducer.BREAK_CHAR;
  }
}
