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
* Copyright (c) 2002-2013 Pentaho Corporation..  All rights reserved.
*/

package org.pentaho.openformula.ui.model2;

public class FormulaTextElement extends FormulaElement
{
  private String text;

  public FormulaTextElement(final FormulaDocument document,
                            final FormulaRootElement parentElement,
                            final String text)
  {
    super(document, parentElement);
    this.text = text;
  }

  public String getText()
  {
    return text;
  }

  /**
   * Fetches the name of the element.  If the element is used to represent some type of structure, this would be the
   * type name.
   *
   * @return the element name
   */
  public String getName()
  {
    return "content"; // NON-NLS
  }
}
