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
