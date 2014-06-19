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

package org.pentaho.reporting.libraries.css.parser.stylehandler.content;

import java.util.ArrayList;

import org.pentaho.reporting.libraries.css.model.StyleKey;
import org.pentaho.reporting.libraries.css.parser.CSSValueFactory;
import org.pentaho.reporting.libraries.css.parser.CSSValueReadHandler;
import org.pentaho.reporting.libraries.css.values.CSSConstant;
import org.pentaho.reporting.libraries.css.values.CSSNumericType;
import org.pentaho.reporting.libraries.css.values.CSSNumericValue;
import org.pentaho.reporting.libraries.css.values.CSSValue;
import org.pentaho.reporting.libraries.css.values.CSSValueList;
import org.pentaho.reporting.libraries.css.values.CSSValuePair;
import org.w3c.css.sac.LexicalUnit;

/**
 * Handles both the counter-increment and the counter-reset
 *
 * @author Thomas Morgner
 */
public class CounterModificationReadHandler implements CSSValueReadHandler
{
  public static final CSSNumericValue ZERO =
      CSSNumericValue.createValue(CSSNumericType.NUMBER, 0);

  public CounterModificationReadHandler()
  {
  }

  public CSSValue createValue(StyleKey name, LexicalUnit value)
  {
    if (value.getLexicalUnitType() != LexicalUnit.SAC_IDENT)
    {
      return null;
    }
    final String mayBeNone = value.getStringValue();
    if ("none".equalsIgnoreCase(mayBeNone))
    {
      return new CSSConstant("none");
    }

    final ArrayList counterSpecs = new ArrayList();
    while (value != null)
    {
      if (value.getLexicalUnitType() != LexicalUnit.SAC_IDENT)
      {
        return null;
      }
      final String identifier = value.getStringValue();
      value = value.getNextLexicalUnit();
      CSSValue counterValue = ZERO;
      if (value != null)
      {
        if (value.getLexicalUnitType() == LexicalUnit.SAC_INTEGER)
        {
          counterValue = CSSNumericValue.createValue
              (CSSNumericType.NUMBER, value.getIntegerValue());
          value = value.getNextLexicalUnit();
        }
        else if (value.getLexicalUnitType() == LexicalUnit.SAC_ATTR)
        {
          counterValue = CSSValueFactory.parseAttrFunction(value);
          value = value.getNextLexicalUnit();
        }
        else if (CSSValueFactory.isFunctionValue(value))
        {
          counterValue = CSSValueFactory.parseFunction(value);
          value = value.getNextLexicalUnit();
        }
      }
      counterSpecs.add(new CSSValuePair
          (new CSSConstant(identifier), counterValue));
    }

    return new CSSValueList(counterSpecs);
  }
}
