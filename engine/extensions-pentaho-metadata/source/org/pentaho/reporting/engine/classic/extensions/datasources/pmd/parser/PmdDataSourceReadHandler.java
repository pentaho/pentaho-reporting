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

package org.pentaho.reporting.engine.classic.extensions.datasources.pmd.parser;

import java.util.ArrayList;

import org.pentaho.reporting.engine.classic.core.DataFactory;
import org.pentaho.reporting.engine.classic.core.modules.parser.base.DataFactoryReadHandler;
import org.pentaho.reporting.engine.classic.core.modules.parser.base.common.QueryDefinitionReadHandler;
import org.pentaho.reporting.engine.classic.core.modules.parser.base.common.QueryDefinitionsReadHandler;
import org.pentaho.reporting.engine.classic.extensions.datasources.pmd.PmdDataFactory;
import org.pentaho.reporting.libraries.xmlns.parser.AbstractXmlReadHandler;
import org.pentaho.reporting.libraries.xmlns.parser.PropertyReadHandler;
import org.pentaho.reporting.libraries.xmlns.parser.XmlReadHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class PmdDataSourceReadHandler extends AbstractXmlReadHandler implements DataFactoryReadHandler
{
  private IPmdConfigReadHandler configReadHandler;
  private ArrayList<PropertyReadHandler> queries;
  private QueryDefinitionsReadHandler queryDefinitionsReadHandler;
  private PmdDataFactory dataFactory;
  private PropertyReadHandler globalScriptReadHandler;

  public PmdDataSourceReadHandler()
  {
    queries = new ArrayList<PropertyReadHandler>();
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
    final PmdConfigReadHandlerFactory configfactory = PmdConfigReadHandlerFactory.getInstance();
    final XmlReadHandler confighandler = configfactory.getHandler(uri, tagName);
    if (confighandler instanceof IPmdConfigReadHandler)
    {
      configReadHandler = (IPmdConfigReadHandler) confighandler;
      return confighandler;
    }

    if (isSameNamespace(uri) == false)
    {
      return null;
    }

    if ("global-script".equals(tagName))
    {
      globalScriptReadHandler = new PropertyReadHandler("language", true);
      return globalScriptReadHandler;
    }

    if ("query".equals(tagName))
    {
      final PropertyReadHandler queryReadHandler = new PropertyReadHandler();
      queries.add(queryReadHandler);
      return queryReadHandler;
    }

    if ("query-definitions".equals(tagName))
    {
      queryDefinitionsReadHandler = new QueryDefinitionsReadHandler();
      return queryDefinitionsReadHandler;
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
    final PmdDataFactory pmddf = new PmdDataFactory();
    pmddf.setConnectionProvider(configReadHandler.getConnectionProvider());
    pmddf.setDomainId(configReadHandler.getDomain());
    pmddf.setXmiFile(configReadHandler.getXmiFile());
    if (globalScriptReadHandler != null)
    {
      pmddf.setGlobalScript(globalScriptReadHandler.getResult());
      pmddf.setGlobalScriptLanguage(globalScriptReadHandler.getName());
    }

    for (int i = 0; i < queries.size(); i++)
    {
      final PropertyReadHandler handler = queries.get(i);
      pmddf.setQuery(handler.getName(), handler.getResult(), null, null);
    }

    if (queryDefinitionsReadHandler != null)
    {
      final ArrayList<QueryDefinitionReadHandler> scriptedQueries = queryDefinitionsReadHandler.getScriptedQueries();
      for (final QueryDefinitionReadHandler scriptedQuery : scriptedQueries)
      {
        pmddf.setQuery(scriptedQuery.getName(), scriptedQuery.getQuery(),
            scriptedQuery.getScriptLanguage(), scriptedQuery.getScript());
      }
    }
    dataFactory = pmddf;
  }

  /**
   * Returns the object for this element or null, if this element does not create an object.
   *
   * @return the object.
   * @throws SAXException if an parser error occured.
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
