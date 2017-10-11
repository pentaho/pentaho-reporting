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

package org.pentaho.openformula.ui.model2;

import javax.swing.text.BadLocationException;
import javax.swing.text.Position;

public class FormulaDocumentPosition implements Position {
  private FormulaElement node;
  private boolean fromStart;
  private int offset;

  public FormulaDocumentPosition( final FormulaElement node,
                                  final int offset,
                                  final boolean fromStart ) throws BadLocationException {
    if ( node == null ) {
      throw new NullPointerException();
    }
    if ( offset < 0 ) {
      throw new IllegalArgumentException();
    }
    if ( offset < 0 || offset > node.getEndOffset() ) {
      throw new BadLocationException( "Offset not valid", offset );
    }

    this.node = node;
    this.fromStart = fromStart;
    this.offset = offset;
  }

  /**
   * Fetches the current offset within the document.
   *
   * @return the offset >= 0
   */
  public int getOffset() {
    if ( fromStart ) {
      return node.getStartOffset() + offset;
    } else {
      return node.getEndOffset() - offset;
    }

  }
}
