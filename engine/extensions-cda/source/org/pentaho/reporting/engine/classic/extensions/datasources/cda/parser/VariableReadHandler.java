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

package org.pentaho.reporting.engine.classic.extensions.datasources.cda.parser;

import org.pentaho.reporting.engine.classic.core.ParameterMapping;
import org.pentaho.reporting.libraries.xmlns.parser.AbstractXmlReadHandler;
import org.pentaho.reporting.libraries.xmlns.parser.ParseException;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class VariableReadHandler extends AbstractXmlReadHandler
{
  private String dataRowName;
  private String variableName;

  public VariableReadHandler()
  {
  }

  /**
   * Starts parsing.
   *
   * @param attrs the attributes.
   * @throws SAXException if there is a parsing error.
   */
  @Override
  protected void startParsing(final Attributes attrs) throws SAXException
  {
    dataRowName = attrs.getValue(getUri(), "datarow-name");
    if (dataRowName == null)
    {
      throw new ParseException("Required attribute 'datarow-name' is not defined");
    }

    variableName = attrs.getValue(getUri(), "variable-name");
    if (variableName == null)
    {
      variableName = dataRowName;
    }
  }

  /**
   * Returns the object for this element or null, if this element does
   * not create an object.
   *
   * @return the object.
   * @throws SAXException if an parser error occured.
   */
  @Override
  public Object getObject() throws SAXException
  {
    return new ParameterMapping(dataRowName, variableName);
  }

  public String getVariableName()
  {
    return variableName;
  }

  public String getDataRowName()
  {
    return dataRowName;
  }
}
