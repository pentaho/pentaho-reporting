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

package org.pentaho.reporting.engine.classic.core.modules.parser.simple.readhandlers;

import org.pentaho.reporting.engine.classic.core.elementfactory.ResourceLabelElementFactory;
import org.pentaho.reporting.engine.classic.core.elementfactory.TextElementFactory;
import org.pentaho.reporting.engine.classic.core.modules.parser.base.PropertyAttributes;
import org.pentaho.reporting.engine.classic.core.modules.parser.base.common.PropertyStringReadHandler;
import org.pentaho.reporting.libraries.xmlns.parser.RootXmlReadHandler;
import org.xml.sax.SAXException;

public class ResourceLabelReadHandler extends AbstractTextElementReadHandler
{
  private PropertyStringReadHandler stringReadHandler;
  private ResourceLabelElementFactory elementFactory;

  public ResourceLabelReadHandler()
  {
    elementFactory = new ResourceLabelElementFactory();
    stringReadHandler = new PropertyStringReadHandler();
  }

  protected TextElementFactory getTextElementFactory()
  {
    return elementFactory;
  }

  public void init(final RootXmlReadHandler rootXmlReadHandler, final String namespace, final String tag) throws SAXException
  {
    super.init(rootXmlReadHandler, namespace, tag);
    stringReadHandler.init(rootXmlReadHandler, namespace, tag);
  }

  public void characters(final char[] ch, final int start, final int length) throws SAXException
  {
    stringReadHandler.characters(ch, start, length);
  }

  /**
   * Starts parsing.
   *
   * @param atts the attributes.
   * @throws org.xml.sax.SAXException if there is a parsing error.
   */
  protected void startParsing(final PropertyAttributes atts)
      throws SAXException
  {
    super.startParsing(atts);
    elementFactory.setResourceKey(atts.getValue(getUri(), "resource-key"));
    elementFactory.setResourceBase(atts.getValue(getUri(), "resource-base"));
    elementFactory.setNullString(atts.getValue(getUri(), "nullstring"));
    stringReadHandler.startParsing(atts);
  }


  /**
   * Done parsing.
   *
   * @throws org.xml.sax.SAXException if there is a parsing error.
   */
  protected void doneParsing()
      throws SAXException
  {
    stringReadHandler.doneParsing();
    final String key = stringReadHandler.getResult();
    if (key.trim().length() > 0)
    {
      elementFactory.setResourceKey(key);
    }
    super.doneParsing();
  }
}
