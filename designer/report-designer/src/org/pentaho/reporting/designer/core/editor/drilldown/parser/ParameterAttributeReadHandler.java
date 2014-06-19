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

package org.pentaho.reporting.designer.core.editor.drilldown.parser;

import org.pentaho.reporting.engine.classic.core.parameters.ParameterAttributeNames;
import org.pentaho.reporting.libraries.base.util.StringUtils;
import org.pentaho.reporting.libraries.xmlns.parser.AbstractXmlReadHandler;
import org.pentaho.reporting.libraries.xmlns.parser.ParseException;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * Todo: Document me!
 * <p/>
 * Date: 13.08.2010
 * Time: 17:34:04
 *
 * @author Thomas Morgner.
 */
public class ParameterAttributeReadHandler extends AbstractXmlReadHandler
{
  private String namespace;
  private String name;
  private String value;

  public ParameterAttributeReadHandler()
  {
  }

  public String getNamespace()
  {
    return namespace;
  }

  public String getName()
  {
    return name;
  }

  public String getValue()
  {
    return value;
  }

  /**
   * Starts parsing.
   *
   * @param attrs the attributes.
   * @throws org.xml.sax.SAXException if there is a parsing error.
   */
  protected void startParsing(final Attributes attrs) throws SAXException
  {
    name = attrs.getValue(getUri(), "name");
    if (StringUtils.isEmpty(name))
    {
      throw new ParseException("Required attribute 'name' is missing", getLocator());
    }
    namespace = attrs.getValue(getUri(), "namespace");
    if (StringUtils.isEmpty(namespace))
    {
      namespace = ParameterAttributeNames.Core.NAMESPACE;
    }
    value = attrs.getValue(getUri(), "value");
    if (StringUtils.isEmpty(value))
    {
      throw new ParseException("Required attribute 'value' is missing", getLocator());
    }
  }

  /**
   * Returns the object for this element or null, if this element does
   * not create an object.
   *
   * @return the object.
   * @throws org.xml.sax.SAXException if an parser error occured.
   */
  public Object getObject() throws SAXException
  {
    return null;
  }
}
