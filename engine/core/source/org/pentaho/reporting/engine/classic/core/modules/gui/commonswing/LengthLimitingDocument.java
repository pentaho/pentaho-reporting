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
* Copyright (c) 2001 - 2013 Object Refinery Ltd, Pentaho Corporation and Contributors..  All rights reserved.
*/

package org.pentaho.reporting.engine.classic.core.modules.gui.commonswing;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

/**
 * This Document restricts the size of the contained plain text to the given number of characters.
 *
 * @author Thomas Morgner
 */
public class LengthLimitingDocument extends PlainDocument
{

  /**
   * The maximum length.
   */
  private int maxlen;

  /**
   * Creates a new LengthLimitingDocument, with no limitation.
   */
  public LengthLimitingDocument()
  {
    this(-1);
  }

  /**
   * Creates a new LengthLimitingDocument with the given limitation. No more than maxlen characters can be added to the
   * document. If maxlen is negative, then no length check is performed.
   *
   * @param maxlen the maximum number of elements in this document
   */
  public LengthLimitingDocument(final int maxlen)
  {
    super();
    this.maxlen = maxlen;
  }

  /**
   * Sets the maximum number of characters for this document. Existing characters are not removed.
   *
   * @param maxlen the maximum number of characters in this document.
   */
  public void setMaxLength(final int maxlen)
  {
    this.maxlen = maxlen;
  }

  /**
   * Returns the defined maximum number characters for this document.
   *
   * @return the maximum number of characters
   */
  public int getMaxLength()
  {
    return this.maxlen;
  }

  /**
   * Inserts the string into the document. If the length of the document would violate the maximum characters
   * restriction, then the string is cut down so that
   *
   * @param offs the offset, where the string should be inserted into the document
   * @param str  the string that should be inserted
   * @param a    the attribute set assigned for the document
   * @throws javax.swing.text.BadLocationException
   *          if the offset is not correct
   */
  public void insertString(final int offs, final String str, final AttributeSet a)
      throws BadLocationException
  {
    if (str == null)
    {
      return;
    }

    if (this.maxlen < 0)
    {
      super.insertString(offs, str, a);
    }

    final char[] numeric = str.toCharArray();
    final StringBuffer b = new StringBuffer();
    b.append(numeric, 0, Math.min(this.maxlen, numeric.length));
    super.insertString(offs, b.toString(), a);
  }

}
