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

import java.util.ArrayList;

import org.pentaho.reporting.designer.core.editor.drilldown.model.ParameterSelection;
import org.pentaho.reporting.libraries.xmlns.parser.AbstractXmlReadHandler;
import org.pentaho.reporting.libraries.xmlns.parser.XmlReadHandler;
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
public class ParameterValuesReadHandler extends AbstractXmlReadHandler
{
  private ParameterSelection[] selections;
  private ArrayList selectionHandlers;
  private String parameterType;

  public ParameterValuesReadHandler(final String parameterType)
  {
    this.parameterType = parameterType;
    selectionHandlers = new ArrayList();
  }

  /**
   * Returns the handler for a child element.
   *
   * @param uri     the URI of the namespace of the current element.
   * @param tagName the tag name.
   * @param atts    the attributes.
   * @return the handler or null, if the tagname is invalid.
   * @throws org.xml.sax.SAXException if there is a parsing error.
   */
  protected XmlReadHandler getHandlerForChild(final String uri,
                                              final String tagName,
                                              final Attributes atts) throws SAXException
  {
    if (isSameNamespace(uri))
    {
      if ("value".equals(tagName))
      {
        final ParameterValueReadHandler readHandler = new ParameterValueReadHandler(parameterType);
        selectionHandlers.add(readHandler);
        return readHandler;
      }
    }
    return null;    
  }

  /**
   * Done parsing.
   *
   * @throws org.xml.sax.SAXException if there is a parsing error.
   */
  protected void doneParsing() throws SAXException
  {
    selections = new ParameterSelection[selectionHandlers.size()];
    for (int i = 0; i < selectionHandlers.size(); i++)
    {
      final ParameterValueReadHandler handler = (ParameterValueReadHandler) selectionHandlers.get(i);
      selections[i] = handler.getSelection();
    }
    super.doneParsing();
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
    return selections;
  }

  public ParameterSelection[] getSelections()
  {
    return selections;
  }
}
