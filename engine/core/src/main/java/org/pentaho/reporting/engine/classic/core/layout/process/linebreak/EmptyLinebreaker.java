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

package org.pentaho.reporting.engine.classic.core.layout.process.linebreak;

import org.pentaho.reporting.engine.classic.core.layout.model.InlineRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.ParagraphRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderNode;

/**
 * This implementation does nothing and is used as dummy if the paragraph was unchanged.
 *
 * @author Thomas Morgner
 */
public final class EmptyLinebreaker implements ParagraphLinebreaker {
  public EmptyLinebreaker() {
  }

  public boolean isWritable() {
    return false;
  }

  public FullLinebreaker startComplexLayout() {
    throw new UnsupportedOperationException();
  }

  public void startBlockBox( final RenderBox child ) {
    throw new UnsupportedOperationException();
  }

  public void finishBlockBox( final RenderBox box ) {
    throw new UnsupportedOperationException();
  }

  public ParagraphLinebreaker startParagraphBox( final ParagraphRenderBox box ) {
    throw new UnsupportedOperationException();
  }

  public void finishParagraphBox( final ParagraphRenderBox box ) {
    throw new UnsupportedOperationException();
  }

  public boolean isSuspended() {
    return false;
  }

  public void finish() {
  }

  public void startInlineBox( final InlineRenderBox box ) {
    throw new UnsupportedOperationException();
  }

  public void finishInlineBox( final InlineRenderBox box ) {
    throw new UnsupportedOperationException();
  }

  public boolean isBreakRequested() {
    throw new UnsupportedOperationException();
  }

  public void addNode( final RenderNode node ) {
    throw new UnsupportedOperationException();
  }

  public void setBreakRequested( final boolean breakRequested ) {
    throw new UnsupportedOperationException();
  }
}
