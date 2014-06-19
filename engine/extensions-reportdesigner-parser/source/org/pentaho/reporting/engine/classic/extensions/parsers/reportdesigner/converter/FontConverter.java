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

package org.pentaho.reporting.engine.classic.extensions.parsers.reportdesigner.converter;

import java.awt.Font;

import org.pentaho.reporting.libraries.xmlns.parser.ParseException;
import org.xml.sax.Locator;

public class FontConverter implements ObjectConverter
{
  public FontConverter()
  {
  }

  public Object convertFromString(final String s, final Locator locator) throws ParseException
  {
    if (s == null)
    {
      throw new IllegalArgumentException("s must not be null");
    }

    int i = s.indexOf(',');
    if (i == -1)
    {
      throw new ParseException("Malformed format");
    }
    int i2 = s.indexOf(',', i + 1);
    if (i2 == -1)
    {
      throw new ParseException("Malformed format");
    }
    return new Font(s.substring(0, i).trim(),
        Integer.parseInt(s.substring(i2 + 1).trim()), Integer.parseInt(s.substring(i + 1, i2).trim()));
  }
}
