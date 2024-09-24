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
