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

package org.pentaho.reporting.engine.classic.extensions.datasources.olap4j.parser;

import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import org.pentaho.reporting.engine.classic.core.modules.parser.base.common.PasswordPropertiesReadHandler;
import org.pentaho.reporting.engine.classic.extensions.datasources.olap4j.connections.DriverConnectionProvider;
import org.pentaho.reporting.engine.classic.extensions.datasources.olap4j.connections.OlapConnectionProvider;
import org.pentaho.reporting.libraries.xmlns.parser.AbstractXmlReadHandler;
import org.pentaho.reporting.libraries.xmlns.parser.PropertiesReadHandler;
import org.pentaho.reporting.libraries.xmlns.parser.StringReadHandler;
import org.pentaho.reporting.libraries.xmlns.parser.XmlReadHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * Creation-Date: 07.04.2006, 18:09:25
 *
 * @author Thomas Morgner
 */
public class DriverConnectionReadHandler extends AbstractXmlReadHandler
    implements OlapConnectionReadHandler
{
  private StringReadHandler driverReadHandler;
  private StringReadHandler urlReadHandler;
  private PropertiesReadHandler propertiesReadHandler;
  private DriverConnectionProvider driverConnectionProvider;

  public DriverConnectionReadHandler()
  {
  }

  /**
   * Returns the handler for a child element.
   *
   * @param tagName the tag name.
   * @param atts    the attributes.
   * @return the handler or null, if the tagname is invalid.
   * @throws SAXException if there is a parsing error.
   */
  protected XmlReadHandler getHandlerForChild(final String uri,
                                              final String tagName,
                                              final Attributes atts)
      throws SAXException
  {
    if (isSameNamespace(uri) == false)
    {
      return null;
    }
    if ("driver".equals(tagName))
    {
      driverReadHandler = new StringReadHandler();
      return driverReadHandler;
    }
    if ("url".equals(tagName))
    {
      urlReadHandler = new StringReadHandler();
      return urlReadHandler;
    }
    if ("properties".equals(tagName))
    {
      propertiesReadHandler = new PasswordPropertiesReadHandler();
      return propertiesReadHandler;
    }
    return null;
  }

  /**
   * Done parsing.
   *
   * @throws SAXException if there is a parsing error.
   */
  protected void doneParsing() throws SAXException
  {
    final DriverConnectionProvider provider = new DriverConnectionProvider();
    if (driverReadHandler != null)
    {
      provider.setDriver(driverReadHandler.getResult());
    }
    if (urlReadHandler != null)
    {
      provider.setUrl(urlReadHandler.getResult());
    }
    if (propertiesReadHandler != null)
    {
      final Properties p = (Properties) propertiesReadHandler.getObject();
      final Iterator it = p.entrySet().iterator();
      while (it.hasNext())
      {
        final Map.Entry entry = (Map.Entry) it.next();
        provider.setProperty((String) entry.getKey(), (String) entry.getValue());
      }
    }
    driverConnectionProvider = provider;
  }

  /**
   * Returns the object for this element or null, if this element does not
   * create an object.
   *
   * @return the object.
   * @throws SAXException if there is a parsing error.
   */
  public Object getObject() throws SAXException
  {
    return driverConnectionProvider;
  }

  public OlapConnectionProvider getProvider()
  {
    return driverConnectionProvider;
  }
}
