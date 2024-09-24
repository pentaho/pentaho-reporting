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
