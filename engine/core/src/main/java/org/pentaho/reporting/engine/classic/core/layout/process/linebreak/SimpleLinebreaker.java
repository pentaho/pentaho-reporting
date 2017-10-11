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

import java.util.Arrays;

/**
 * This implementation is used in the simple mode. The pool-box is used as is - none of the nodes get derived, but we
 * keep track of the calls in case we encounter a manual break later. In that case, the SimpleLinebreaker converts
 * itself into a FullLinebreaker.
 *
 * @author Thomas Morgner
 */
public final class SimpleLinebreaker implements ParagraphLinebreaker {
  private enum BreakMethod {
    StartBlock, FinishBlock, StartInline, FinishInline, Node
  }

  private BreakMethod[] methods;
  private Object[] parameters;
  private int counter;

  private ParagraphRenderBox paragraphRenderBox;
  private Object suspendItem;
  private boolean breakRequested;

  public SimpleLinebreaker( final ParagraphRenderBox paragraphRenderBox ) {
    this.paragraphRenderBox = paragraphRenderBox;
    final int poolSize = Math.max( 20, paragraphRenderBox.getPoolSize() );
    this.methods = new BreakMethod[poolSize];
    this.parameters = new Object[poolSize];
  }

  public void dispose() {
    counter = 0;
    paragraphRenderBox = null;
    suspendItem = null;
    breakRequested = false;
  }

  public void recycle( final ParagraphRenderBox box ) {
    this.counter = 0;
    this.paragraphRenderBox = box;
    this.suspendItem = null;
    this.breakRequested = false;
    final int poolSize = this.paragraphRenderBox.getPoolSize();
    if ( poolSize > this.methods.length ) {
      this.methods = new BreakMethod[poolSize];
      this.parameters = new Object[poolSize];
    }
  }

  private void add( final BreakMethod method, final Object parameter ) {
    if ( methods.length == counter ) {
      // Grow the arrays ..
      final int nextSize = Math.max( 30, ( methods.length * 2 ) );
      final BreakMethod[] newMethods = new BreakMethod[nextSize];
      System.arraycopy( methods, 0, newMethods, 0, methods.length );

      final Object[] newParameters = new Object[nextSize];
      System.arraycopy( parameters, 0, newParameters, 0, parameters.length );

      this.methods = newMethods;
      this.parameters = newParameters;
    }

    methods[counter] = method;
    parameters[counter] = parameter;
    counter += 1;
  }

  public boolean isWritable() {
    return true;
  }

  public FullLinebreaker startComplexLayout() {
    final FullLinebreaker fullBreaker = new FullLinebreaker( paragraphRenderBox );
    for ( int i = 0; i < counter; i++ ) {
      final BreakMethod method = methods[i];
      final Object parameter = parameters[i];
      switch ( method ) {
        case StartBlock: {
          fullBreaker.startBlockBox( (RenderBox) parameter );
          break;
        }
        case FinishBlock: {
          fullBreaker.finishBlockBox( (RenderBox) parameter );
          break;
        }
        case StartInline: {
          fullBreaker.startInlineBox( (InlineRenderBox) parameter );
          break;
        }
        case FinishInline: {
          fullBreaker.finishInlineBox( (InlineRenderBox) parameter );
          break;
        }
        case Node: {
          fullBreaker.addNode( (RenderNode) parameter );
          break;
        }
        default: {
          throw new IllegalStateException();
        }
      }
    }

    paragraphRenderBox.setPoolSize( counter );
    // replay ..
    return fullBreaker;
  }

  public void startBlockBox( final RenderBox child ) {
    if ( suspendItem != null ) {
      suspendItem = child.getInstanceId();
    }

    add( BreakMethod.StartBlock, child );
  }

  public void finishBlockBox( final RenderBox box ) {
    if ( suspendItem == box.getInstanceId() ) {
      suspendItem = null;
    }

    add( BreakMethod.FinishBlock, box );
  }

  public ParagraphLinebreaker startParagraphBox( final ParagraphRenderBox box ) {
    throw new UnsupportedOperationException();
  }

  public void finishParagraphBox( final ParagraphRenderBox box ) {
    throw new UnsupportedOperationException();
  }

  public boolean isSuspended() {
    return suspendItem != null;
  }

  public void finish() {
    paragraphRenderBox.setPoolSize( counter );
    paragraphRenderBox.setLineBoxAge( paragraphRenderBox.getPool().getChangeTracker() );
    counter = 0;
    Arrays.fill( parameters, null );
  }

  public void startInlineBox( final InlineRenderBox box ) {
    add( BreakMethod.StartInline, box );
  }

  public void finishInlineBox( final InlineRenderBox box ) {
    add( BreakMethod.FinishInline, box );
  }

  public boolean isBreakRequested() {
    return breakRequested;
  }

  public void addNode( final RenderNode node ) {
    add( BreakMethod.Node, node );
  }

  public void setBreakRequested( final boolean breakRequested ) {
    this.breakRequested = breakRequested;
  }
}
