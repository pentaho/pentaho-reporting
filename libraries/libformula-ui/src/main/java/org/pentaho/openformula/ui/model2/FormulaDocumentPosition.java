/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


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
