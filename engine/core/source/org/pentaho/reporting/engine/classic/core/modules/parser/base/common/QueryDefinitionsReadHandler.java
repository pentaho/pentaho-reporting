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

package org.pentaho.reporting.engine.classic.core.modules.parser.base.common;

import java.util.ArrayList;

import org.pentaho.reporting.libraries.xmlns.parser.AbstractXmlReadHandler;
import org.pentaho.reporting.libraries.xmlns.parser.XmlReadHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class QueryDefinitionsReadHandler extends AbstractXmlReadHandler
{
  private ArrayList<QueryDefinitionReadHandler> scriptedQueries;

  public QueryDefinitionsReadHandler()
  {
    scriptedQueries = new ArrayList<QueryDefinitionReadHandler>();
  }

  protected XmlReadHandler getHandlerForChild(final String uri,
                                              final String tagName,
                                              final Attributes atts) throws SAXException
  {
    if (isSameNamespace(uri) == false)
    {
      return null;
    }
    if ("query".equals(tagName))
    {
      final QueryDefinitionReadHandler queryReadHandler = new QueryDefinitionReadHandler();
      scriptedQueries.add(queryReadHandler);
      return queryReadHandler;
    }
    return null;
  }

  public Object getObject() throws SAXException
  {
    return getScriptedQueries();
  }

  public ArrayList<QueryDefinitionReadHandler> getScriptedQueries()
  {
    return scriptedQueries;
  }
}
