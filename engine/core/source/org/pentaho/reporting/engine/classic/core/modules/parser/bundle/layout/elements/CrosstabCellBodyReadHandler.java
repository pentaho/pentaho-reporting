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

package org.pentaho.reporting.engine.classic.core.modules.parser.bundle.layout.elements;

import java.util.ArrayList;

import org.pentaho.reporting.engine.classic.core.CrosstabCellBody;
import org.pentaho.reporting.engine.classic.core.filter.types.bands.CrosstabCellBodyType;
import org.pentaho.reporting.libraries.xmlns.parser.ParseException;
import org.pentaho.reporting.libraries.xmlns.parser.XmlReadHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class CrosstabCellBodyReadHandler extends AbstractElementReadHandler
{
  private ArrayList<CrosstabCellReadHandler> crosstabCellReadHandlers;
  private DetailsHeaderBandReadHandler detailsHeaderReadHandler;

  public CrosstabCellBodyReadHandler()
      throws ParseException
  {
    super(CrosstabCellBodyType.INSTANCE);
    crosstabCellReadHandlers = new ArrayList<CrosstabCellReadHandler>();
  }

  public CrosstabCellBody getElement()
  {
    return (CrosstabCellBody) super.getElement();
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
      if ("details-header".equals(tagName))
      {
        detailsHeaderReadHandler = new DetailsHeaderBandReadHandler();
        return detailsHeaderReadHandler;
      }
      if ("crosstab-cell".equals(tagName))
      {
        final CrosstabCellReadHandler readHandler = new CrosstabCellReadHandler();
        crosstabCellReadHandlers.add(readHandler);
        return readHandler;
      }
    }

    return super.getHandlerForChild(uri, tagName, atts);
  }

  /**
   * Done parsing.
   *
   * @throws org.xml.sax.SAXException if there is a parsing error.
   */
  protected void doneParsing() throws SAXException
  {
    super.doneParsing();
    final CrosstabCellBody body = getElement();
    if (detailsHeaderReadHandler != null)
    {
      body.setHeader(detailsHeaderReadHandler.getElement());
    }
    for (int i = 0; i < crosstabCellReadHandlers.size(); i++)
    {
      final CrosstabCellReadHandler readHandler = crosstabCellReadHandlers.get(i);
      body.addElement(readHandler.getElement());
    }
  }
}
