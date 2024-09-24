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
