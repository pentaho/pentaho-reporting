/*
 * This program is free software; you can redistribute it and/or modify it under the
 *  terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
 *  Foundation.
 *
 *  You should have received a copy of the GNU Lesser General Public License along with this
 *  program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 *  or from the Free Software Foundation, Inc.,
 *  51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 *  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *  See the GNU Lesser General Public License for more details.
 *
 *  Copyright (c) 2006 - 2017 Hitachi Vantara..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.layout.process.text;

import org.pentaho.reporting.engine.classic.core.layout.model.ParagraphRenderBox;

import java.awt.font.FontRenderContext;
import java.awt.font.LineBreakMeasurer;
import java.awt.font.TextLayout;
import java.text.AttributedCharacterIterator;
import java.text.BreakIterator;

public class WordBreakingLineIterator extends LineBreakIterator {
  private static final float INFINITY = 10000000f;
  private final BreakIterator wordInstance;

  public WordBreakingLineIterator( final ParagraphRenderBox box, final FontRenderContext fontRenderContext,
      final AttributedCharacterIterator ci, final String text ) {
    super( box, fontRenderContext, ci );

    wordInstance = BreakIterator.getWordInstance();
    wordInstance.setText( text );
  }

  protected TextLayout postProcess( final int start, final TextLayout textLayout,
      final LineBreakMeasurer lineBreakMeasurer ) {
    int end = lineBreakMeasurer.getPosition();
    final TextLayout layout = performWordBreak( start, textLayout, lineBreakMeasurer, end );
    return super.postProcess( start, layout, lineBreakMeasurer );
  }

  private TextLayout performWordBreak( final int start, final TextLayout textLayout,
      final LineBreakMeasurer lineBreakMeasurer, final int end ) {
    final TextLayout layout;
    if ( wordInstance.isBoundary( end ) != false ) {
      return textLayout;
    }

    int preceding = wordInstance.preceding( end );
    if ( preceding == start ) {
      // single word does not fit on the line, so print full word
      lineBreakMeasurer.setPosition( start );
      return lineBreakMeasurer.nextLayout( INFINITY, wordInstance.following( end ), false );
    } else {
      lineBreakMeasurer.setPosition( start );
      return lineBreakMeasurer.nextLayout( INFINITY, preceding, false );
    }
  }
}
