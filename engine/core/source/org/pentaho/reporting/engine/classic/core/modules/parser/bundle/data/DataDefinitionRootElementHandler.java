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

package org.pentaho.reporting.engine.classic.core.modules.parser.bundle.data;

import java.util.ArrayList;

import org.pentaho.reporting.engine.classic.core.DataFactory;
import org.pentaho.reporting.engine.classic.core.function.Expression;
import org.pentaho.reporting.engine.classic.core.modules.parser.base.common.ExpressionReadHandler;
import org.pentaho.reporting.engine.classic.core.parameters.ReportParameterDefinition;
import org.pentaho.reporting.libraries.xmlns.parser.AbstractXmlReadHandler;
import org.pentaho.reporting.libraries.xmlns.parser.XmlReadHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class DataDefinitionRootElementHandler extends AbstractXmlReadHandler
{
  private MasterParameterDefinitionReadHandler parameterDefinitionHandler;

  private DataDefinition dataDefinition;
  private DataSourceElementHandler dataSourceElementHandler;
  private ArrayList<ExpressionReadHandler> expressionHandlers;

  public DataDefinitionRootElementHandler()
  {
    expressionHandlers = new ArrayList<ExpressionReadHandler>();
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
    if (isSameNamespace(uri) == false)
    {
      return null;
    }

    if ("parameter-definition".equals(tagName))
    {
      parameterDefinitionHandler = new MasterParameterDefinitionReadHandler();
      return parameterDefinitionHandler;
    }

    if ("expression".equals(tagName))
    {
      final ExpressionReadHandler readHandler = new ExpressionReadHandler();
      expressionHandlers.add(readHandler);
      return readHandler;
    }

    if ("data-source".equals(tagName))
    {
      dataSourceElementHandler = new DataSourceElementHandler();
      return dataSourceElementHandler;
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
    final String primaryQuery;
    final int primaryQueryLimit;
    final int primaryQueryTimeout;
    final DataFactory primaryDataFactory;
    if (dataSourceElementHandler == null)
    {
      primaryDataFactory = null;
      primaryQuery = null;
      primaryQueryLimit = 0;
      primaryQueryTimeout = 0;
    }
    else
    {
      primaryDataFactory = dataSourceElementHandler.getDataFactory();
      primaryQuery = dataSourceElementHandler.getQuery();
      primaryQueryLimit = dataSourceElementHandler.getQueryLimit();
      primaryQueryTimeout = dataSourceElementHandler.getQueryTimeout();
    }

    final ReportParameterDefinition reportParameterDefinition;
    if (parameterDefinitionHandler != null)
    {
      reportParameterDefinition = (ReportParameterDefinition) parameterDefinitionHandler.getObject();
    }
    else
    {
      reportParameterDefinition = null;
    }

    final ArrayList<Expression> expressionsList = new ArrayList<Expression>();
    for (int i = 0; i < expressionHandlers.size(); i++)
    {
      final ExpressionReadHandler readHandler = expressionHandlers.get(i);
      if (readHandler.getObject() != null)
      {
        expressionsList.add((Expression) readHandler.getObject());
      }
    }
    final Expression[] expressions = expressionsList.toArray(new Expression[expressionHandlers.size()]);


    dataDefinition = new DataDefinition
        (reportParameterDefinition, primaryDataFactory, primaryQuery, primaryQueryLimit,
            primaryQueryTimeout, expressions);

  }

  /**
   * Returns the object for this element or null, if this element does not create an object.
   *
   * @return the object.
   * @throws SAXException if an parser error occured.
   */
  public Object getObject() throws SAXException
  {
    return dataDefinition;
  }
}
