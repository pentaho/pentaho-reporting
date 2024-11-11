/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


package org.pentaho.reporting.libraries.fonts.text.breaks;

import org.pentaho.reporting.libraries.fonts.text.ClassificationProducer;

/**
 * This produces linebreaks when a CR/LF is encountered. This corresponds to the expected behaviour of HTML-pre
 * elements.
 *
 * @author Thomas Morgner
 */
public class LineBreakProducer implements BreakOpportunityProducer {
  public LineBreakProducer() {
  }

  /**
   * Signals the start of text. Resets the state to the initial values.
   */
  public void startText() {
  }

  public int createBreakOpportunity( final int codepoint ) {
    if ( codepoint == ClassificationProducer.START_OF_TEXT ) {
      return BreakOpportunityProducer.BREAK_NEVER;
    }

    if ( codepoint == '\n' || codepoint == '\r' ) {
      return BreakOpportunityProducer.BREAK_LINE;
    }

    return BreakOpportunityProducer.BREAK_NEVER;
  }

  public Object clone() throws CloneNotSupportedException {
    return super.clone();
  }

  public void reset() {
  }
}
