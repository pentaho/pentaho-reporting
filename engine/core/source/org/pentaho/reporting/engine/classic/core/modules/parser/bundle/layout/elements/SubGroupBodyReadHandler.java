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

package org.pentaho.reporting.engine.classic.core.modules.parser.bundle.layout.elements;

import org.pentaho.reporting.engine.classic.core.SubGroupBody;
import org.pentaho.reporting.engine.classic.core.filter.types.bands.SubGroupBodyType;
import org.pentaho.reporting.libraries.xmlns.parser.ParseException;
import org.pentaho.reporting.libraries.xmlns.parser.XmlReadHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class SubGroupBodyReadHandler extends AbstractElementReadHandler
{
  private RelationalGroupReadHandler groupReadHandler;
  private CrosstabGroupReadHandler crosstabGroupReadHandler;
  
  public SubGroupBodyReadHandler()
      throws ParseException
  {
    super(SubGroupBodyType.INSTANCE);
  }

  public SubGroupBody getElement()
  {
    return (SubGroupBody) super.getElement();
  }

  /**
   * Returns the handler for a child element.
   *
   * @param uri     the URI of the namespace of the current element.
   * @param tagName the tag name.
   * @param atts    the attributes.
   * @return the handler or null, if the tagname is invalid.
   * @throws SAXException if there is a parsing error.
   */
  protected XmlReadHandler getHandlerForChild(final String uri,
                                              final String tagName,
                                              final Attributes atts) throws SAXException
  {
    if (isSameNamespace(uri))
    {
      if ("group".equals(tagName))
      {
        // Handle the subgroup
        groupReadHandler = new RelationalGroupReadHandler();
        return groupReadHandler;
      }
      if ("crosstab".equals(tagName))
      {
        // Handle the subgroup
        crosstabGroupReadHandler = new CrosstabGroupReadHandler();
        return crosstabGroupReadHandler;
      }
    }

    return super.getHandlerForChild(uri, tagName, atts);
  }

  /**
   * Done parsing.
   *
   * @throws SAXException if there is a parsing error.
   */
  protected void doneParsing() throws SAXException
  {
    super.doneParsing();

    final SubGroupBody body = getElement();
    if (groupReadHandler != null)
    {
      body.setGroup(groupReadHandler.getElement());
    }
    else if (crosstabGroupReadHandler != null)
    {
      body.setGroup(crosstabGroupReadHandler.getElement());
    }
    else
    {
      throw new ParseException("Mandatory element 'group' or 'crosstab' was not found.");
    }

  }
}
