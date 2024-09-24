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

package org.pentaho.reporting.engine.classic.core.layout.model;

import org.pentaho.reporting.libraries.base.util.FastStack;

import java.util.Iterator;

public class RenderBoxNonAutoIterator implements Iterator<RenderNode> {
  private RenderNode result;
  private boolean beforeStart;
  private FastStack<RenderBox> boxes;

  public RenderBoxNonAutoIterator( final RenderBox sectionRenderBox ) {
    this.result = sectionRenderBox.getFirstChild();
    this.beforeStart = true;
  }

  public boolean hasNext() {
    if ( beforeStart ) {
      beforeStart = false;

      findNonAuto();

      return result != null;
    }

    if ( result == null ) {
      return false;
    } else {
      result = result.getNext();
      findNonAuto();
    }
    return result != null;
  }

  private void findNonAuto() {
    while ( result != null ) {
      if ( result.getNodeType() != LayoutNodeTypes.TYPE_BOX_AUTOLAYOUT ) {
        break;
      }

      final RenderBox box = (RenderBox) result;
      final RenderNode firstChild = box.getFirstChild();
      if ( firstChild != null ) {
        if ( boxes == null ) {
          boxes = new FastStack<RenderBox>();
        }

        boxes.push( box );
        result = firstChild;
      } else {
        result = result.getNext();

        if ( result == null && boxes != null ) {
          while ( boxes.isEmpty() == false ) {
            final RenderBox parent = boxes.pop();
            result = parent.getNext();
            if ( result != null ) {
              break;
            }
          }

          // at this point, result is only null if the stack is empty and we reached the end of the search.
        }
      }
    }
  }

  public RenderNode next() {
    if ( beforeStart ) {
      return null;
    }
    return result;
  }

  public void remove() {
    throw new UnsupportedOperationException();
  }
}
