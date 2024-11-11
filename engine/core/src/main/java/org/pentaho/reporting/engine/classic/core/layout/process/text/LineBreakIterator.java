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


package org.pentaho.reporting.engine.classic.core.layout.process.text;

import org.pentaho.reporting.engine.classic.core.ElementAlignment;
import org.pentaho.reporting.engine.classic.core.layout.model.ParagraphRenderBox;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleKeys;
import org.pentaho.reporting.engine.classic.core.util.geom.StrictGeomUtility;

import java.awt.font.FontRenderContext;
import java.awt.font.LineBreakMeasurer;
import java.awt.font.TextLayout;
import java.text.AttributedCharacterIterator;
import java.util.Iterator;

public class LineBreakIterator implements Iterator<LineBreakIteratorState> {
  private final boolean justifiedLayout;
  private final LineBreakMeasurer lineBreakMeasurer;
  private final AttributedCharacterIterator ci;
  private final float wrappingWidth;

  public LineBreakIterator( ParagraphRenderBox box, FontRenderContext fontRenderContext, AttributedCharacterIterator ci ) {
    this.wrappingWidth = (float) StrictGeomUtility.toExternalValue( box.getCachedWidth() );
    this.justifiedLayout =
        ElementAlignment.JUSTIFY.equals( box.getStyleSheet().getStyleProperty( ElementStyleKeys.ALIGNMENT ) );
    this.ci = ci;
    this.lineBreakMeasurer = new LineBreakMeasurer( ci, fontRenderContext );
    this.lineBreakMeasurer.setPosition( ci.getBeginIndex() );
  }

  public boolean hasNext() {
    return lineBreakMeasurer.getPosition() < ci.getEndIndex();
  }

  public LineBreakIteratorState next() {
    // For each line produced by the LinebreakMeasurer
    int start = lineBreakMeasurer.getPosition();
    // float is the worst option to have accurate layouts. So we have to 'adjust' for rounding errors
    // and hope that no one notices ..
    TextLayout textLayout = lineBreakMeasurer.nextLayout( wrappingWidth + 0.5f, ci.getEndIndex(), false );
    textLayout = postProcess( start, textLayout, lineBreakMeasurer );
    int end = lineBreakMeasurer.getPosition();

    // check if the text must be justified

    return new LineBreakIteratorState( textLayout, start, end );
  }

  protected TextLayout postProcess( final int start, final TextLayout textLayout,
      final LineBreakMeasurer lineBreakMeasurer ) {
    if ( justifiedLayout ) {
      return textLayout.getJustifiedLayout( wrappingWidth );
    } else {
      return textLayout;
    }
  }

  public void remove() {
    throw new UnsupportedOperationException();
  }
}
