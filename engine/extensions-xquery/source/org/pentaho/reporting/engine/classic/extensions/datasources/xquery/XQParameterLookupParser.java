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
 * Copyright (c) 2001 - 2009 Object Refinery Ltd, Pentaho Corporation and Contributors..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.extensions.datasources.xquery;

import org.pentaho.reporting.engine.classic.core.DataRow;
import org.pentaho.reporting.engine.classic.core.util.PropertyLookupParser;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Cedric Pronzato
 */
public class XQParameterLookupParser extends PropertyLookupParser
{
  private ArrayList<String> fields;

  public XQParameterLookupParser()
  {
    setEscapeMode(PropertyLookupParser.ESCAPE_MODE_NONE);
    
    this.fields = new ArrayList<String>();
    setMarkerChar('$');
    setOpeningBraceChar('{');
    setClosingBraceChar('}');
  }

  protected String lookupVariable(final String name)
  {
    fields.add(name);
    return "$" + name; //$NON-NLS-1$
  }

  public List<String> getFields()
  {
    return (List<String>) fields.clone();
  }

  /**
   * Translates the given string and resolves the embedded property references.
   *
   * @param value the raw value,
   *
   * @return the fully translated string.
   */
  public String translateAndLookup(final String value, final DataRow parameters)
  {

    if (value != null)
    {
      return super.translateAndLookup(value, parameters);

      //todo: lookup variable type => extend PropertyLookupParser behaviour

      /*final StringBuffer buffer = new StringBuffer(value.length());
      // first prune script lines which are not declaration for external variables.
      // we expect that scripts use line breaks for each statements
      final String[] lines = StringUtils.split(value, StringUtils.getLineSeparator());

      for (int i = 0; i < lines.length; i++)
      {
        // a typical xquery external variable declaration: declare variable $x as xs:integer external;
        final String line = lines[i];
        // we don't really care about the case
        if (StringUtils.startsWithIgnoreCase(line, "declare variable")
            && StringUtils.endsWithIgnoreCase(line, "external;"))
        {
          final String lookup = super.translateAndLookup(line, parameters);
          buffer.append(lookup);
        }
        else
        {
          // else just copy content
          buffer.append(line);
        }
        if (i != lines.length-1)
        {
          buffer.append(StringUtils.getLineSeparator());
        }
      }
      return buffer.toString(); */
    }

    return null;
  }
}