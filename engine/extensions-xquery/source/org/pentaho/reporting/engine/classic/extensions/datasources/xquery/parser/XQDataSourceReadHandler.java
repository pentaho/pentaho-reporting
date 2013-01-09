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

package org.pentaho.reporting.engine.classic.extensions.datasources.xquery.parser;

import org.pentaho.reporting.engine.classic.core.DataFactory;
import org.pentaho.reporting.engine.classic.core.modules.parser.base.DataFactoryReadHandler;
import org.pentaho.reporting.engine.classic.extensions.datasources.xquery.XQConnectionProvider;
import org.pentaho.reporting.engine.classic.extensions.datasources.xquery.XQReportDataFactory;
import org.pentaho.reporting.libraries.xmlns.parser.AbstractXmlReadHandler;
import org.pentaho.reporting.libraries.xmlns.parser.PropertyReadHandler;
import org.pentaho.reporting.libraries.xmlns.parser.XmlReadHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import java.util.ArrayList;

/**
 * @author Cedric Pronzato
 */
public class XQDataSourceReadHandler extends AbstractXmlReadHandler
    implements DataFactoryReadHandler
{
  private XQConnectionReadHandler connectionReadHandler;
  private ArrayList queries;
  private XQReportDataFactory dataFactory;

  public XQDataSourceReadHandler()
  {
    queries = new ArrayList();
  }

  /**
   * Returns the handler for a child element.
   *
   * @param uri     the URI of the namespace of the current element.
   * @param tagName the tag name.
   * @param atts    the attributes.
   *
   * @return the handler or null, if the tagname is invalid.
   *
   * @throws org.xml.sax.SAXException if there is a parsing error.
   */
  protected XmlReadHandler getHandlerForChild(final String uri, final String tagName, final Attributes atts) throws SAXException
  {
    if (isSameNamespace(uri) == false)
    {
      return null;
    }

    if ("connection".equals(tagName))
    {
      connectionReadHandler = new DriverXQConnectionReadHandler();
      return connectionReadHandler;
    }
    if ("jndi".equals(tagName))
    {
      connectionReadHandler = new JndiXQConnectionReadHandler();
      return connectionReadHandler;
    }
    if ("query".equals(tagName))
    {
      final PropertyReadHandler queryReadHandler = new PropertyReadHandler();
      queries.add(queryReadHandler);
      return queryReadHandler;
    }

    return null;
  }

  @Override
  protected void doneParsing() throws SAXException
  {
    super.doneParsing();
    final XQConnectionProvider provider = (XQConnectionProvider) connectionReadHandler.getObject();

    dataFactory = new XQReportDataFactory(provider);
    for (int i = 0; i < queries.size(); i++)
    {
      final PropertyReadHandler q = (PropertyReadHandler) queries.get(i);
      dataFactory.setQuery(q.getName(), q.getResult());
    }
  }

  /**
   * Returns the object for this element or null, if this element does not create an object.
   *
   * @return the object.
   *
   * @throws org.xml.sax.SAXException if an parser error occured.
   */
  public Object getObject() throws SAXException
  {
    return dataFactory;
  }

  public DataFactory getDataFactory()
  {
    return dataFactory;
  }
}
