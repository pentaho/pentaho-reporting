/*!
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
 * Copyright (c) 2002-2017 Hitachi Vantara..  All rights reserved.
 */

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
