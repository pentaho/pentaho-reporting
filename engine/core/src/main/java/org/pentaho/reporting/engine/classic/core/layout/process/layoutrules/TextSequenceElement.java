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


package org.pentaho.reporting.engine.classic.core.layout.process.layoutrules;

import org.pentaho.reporting.engine.classic.core.layout.model.RenderNode;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderableText;

/**
 * Text in a line. A text item is special as it may shrink or stretch to better fit the line. If hyphenation is
 * implemented, it may even split or change into a different text (this happens a lot in the german language).
 *
 * @author Thomas Morgner
 */
public class TextSequenceElement implements InlineSequenceElement {
  public static final InlineSequenceElement INSTANCE = new TextSequenceElement();

  private TextSequenceElement() {
  }

  /**
   * The width of the element. This is the minimum width of the element.
   *
   * @return
   */
  public long getMinimumWidth( final RenderNode node ) {
    final RenderableText text = (RenderableText) node;
    return text.getMinimumWidth();
  }

  /**
   * The extra-space width for an element. Some elements can expand to fill some more space (justified text is a good
   * example, adding some space between the letters of each word to reduce the inner-word spacing).
   *
   * @return
   */
  public long getMaximumWidth( final RenderNode node ) {
    final RenderableText text = (RenderableText) node;
    return text.getPreferredWidth();
  }

  public boolean isPreserveWhitespace( final RenderNode node ) {
    return false;
  }

  public int getClassification() {
    return CONTENT;
  }

  public Classification getType() {
    return Classification.CONTENT;
  }
}
