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
* Copyright (c) 2006 - 2013 Pentaho Corporation and Contributors.  All rights reserved.
*/

package org.pentaho.reporting.libraries.fonts.text.classifier;

/**
 * Creation-Date: 26.06.2006, 16:36:50
 *
 * @author Thomas Morgner
 */
public class WhitespaceClassificationProducer implements GlyphClassificationProducer
{
  public WhitespaceClassificationProducer()
  {
  }

  public int getClassification(final int codepoint)
  {
    if (Character.isWhitespace((char) codepoint))
    {
      return GlyphClassificationProducer.SPACE_CHAR;
    }
    return GlyphClassificationProducer.LETTER;
  }

  public Object clone() throws CloneNotSupportedException
  {
    return super.clone();
  }

  public void reset()
  {

  }
}
