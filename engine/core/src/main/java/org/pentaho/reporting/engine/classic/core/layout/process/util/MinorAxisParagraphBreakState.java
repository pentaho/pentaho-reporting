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
 * Copyright (c) 2001 - 2013 Object Refinery Ltd, Hitachi Vantara and Contributors..  All rights reserved.
 */

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
