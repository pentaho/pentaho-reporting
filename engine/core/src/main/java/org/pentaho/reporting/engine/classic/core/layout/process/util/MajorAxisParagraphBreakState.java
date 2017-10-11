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
import org.pentaho.reporting.engine.classic.core.layout.model.RenderBox;
import org.pentaho.reporting.engine.classic.core.layout.process.valign.BoxAlignContext;
import org.pentaho.reporting.libraries.base.util.FastStack;

/**
 * Creation-Date: 12.07.2007, 14:16:07
 *
 * @author Thomas Morgner
 */
public final class MajorAxisParagraphBreakState {
  private BoxAlignContext currentLine;

  private Object suspendItem;
  private FastStack<BoxAlignContext> contexts;
  private ParagraphRenderBox paragraph;

  /**
   */
  public MajorAxisParagraphBreakState() {
    this.contexts = new FastStack<BoxAlignContext>( 50 );
  }

  public void init( final ParagraphRenderBox paragraph ) {
    if ( paragraph == null ) {
      throw new NullPointerException();
    }
    this.paragraph = paragraph;
    this.contexts.clear();
    this.suspendItem = null;
  }

  public void deinit() {
    this.paragraph = null;
    this.suspendItem = null;
    this.contexts.clear();
  }

  public boolean isActive() {
    return paragraph != null;
  }

  public ParagraphRenderBox getParagraph() {
    return paragraph;
  }

  public Object getSuspendItem() {
    return suspendItem;
  }

  public void setSuspendItem( final Object suspendItem ) {
    this.suspendItem = suspendItem;
  }

  public boolean isSuspended() {
    return suspendItem != null;
  }

  public BoxAlignContext getCurrentLine() {
    return currentLine;
  }

  public void openContext( final RenderBox box ) {
    final BoxAlignContext context = new BoxAlignContext( box );
    if ( currentLine != null ) {
      currentLine.addChild( context );
    }
    contexts.push( context );
    currentLine = context;
  }

  public BoxAlignContext closeContext() {
    final BoxAlignContext context = contexts.pop();
    context.validate();
    if ( contexts.isEmpty() ) {
      currentLine = null;
    } else {
      currentLine = contexts.peek();
    }
    return context;
  }
}
