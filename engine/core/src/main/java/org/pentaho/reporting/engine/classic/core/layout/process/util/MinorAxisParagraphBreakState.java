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

package org.pentaho.reporting.engine.classic.core.layout.process.util;

import org.pentaho.reporting.engine.classic.core.layout.model.ParagraphRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderNode;
import org.pentaho.reporting.engine.classic.core.layout.process.layoutrules.DefaultSequenceList;
import org.pentaho.reporting.engine.classic.core.layout.process.layoutrules.InlineNodeSequenceElement;
import org.pentaho.reporting.engine.classic.core.layout.process.layoutrules.InlineSequenceElement;
import org.pentaho.reporting.engine.classic.core.layout.process.layoutrules.ReplacedContentSequenceElement;
import org.pentaho.reporting.engine.classic.core.layout.process.layoutrules.SequenceList;
import org.pentaho.reporting.engine.classic.core.layout.process.layoutrules.TextSequenceElement;
import org.pentaho.reporting.engine.classic.core.util.InstanceID;

public final class MinorAxisParagraphBreakState {
  private InstanceID suspendItem;
  private SequenceList elementSequence;
  private ParagraphRenderBox paragraph;
  private boolean containsContent;

  public MinorAxisParagraphBreakState() {
    this.elementSequence = new DefaultSequenceList();
  }

  public void init( final ParagraphRenderBox paragraph ) {
    if ( paragraph == null ) {
      throw new NullPointerException();
    }
    this.paragraph = paragraph;
    this.elementSequence.clear();
    this.suspendItem = null;
    this.containsContent = false;
  }

  public void deinit() {
    this.paragraph = null;
    this.elementSequence.clear();
    this.suspendItem = null;
    this.containsContent = false;
  }

  public boolean isInsideParagraph() {
    return paragraph != null;
  }

  public ParagraphRenderBox getParagraph() {
    return paragraph;
  }

  /*
   * public InstanceID getSuspendItem() { return suspendItem; }
   * 
   * public void setSuspendItem(final InstanceID suspendItem) { this.suspendItem = suspendItem; }
   */
  public void add( final InlineSequenceElement element, final RenderNode node ) {
    elementSequence.add( element, node );
    if ( element instanceof TextSequenceElement || element instanceof InlineNodeSequenceElement
        || element instanceof ReplacedContentSequenceElement ) {
      containsContent = true;
    }
  }

  public boolean isContainsContent() {
    return containsContent;
  }

  public boolean isSuspended() {
    return suspendItem != null;
  }

  public SequenceList getSequence() {
    return elementSequence;
  }

  public void clear() {
    elementSequence.clear();
    suspendItem = null;
    containsContent = false;
  }
}
