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

package org.pentaho.reporting.engine.classic.extensions.datasources.xquery;

import java.util.HashMap;
import java.util.List;
import javax.swing.table.TableModel;
import javax.xml.namespace.QName;
import javax.xml.xquery.XQConnection;
import javax.xml.xquery.XQConstants;
import javax.xml.xquery.XQException;
import javax.xml.xquery.XQItem;
import javax.xml.xquery.XQPreparedExpression;
import javax.xml.xquery.XQResultSequence;
import javax.xml.xquery.XQStaticContext;

import org.pentaho.reporting.engine.classic.core.AbstractDataFactory;
import org.pentaho.reporting.engine.classic.core.DataRow;
import org.pentaho.reporting.engine.classic.core.ReportDataFactoryException;
import org.pentaho.reporting.libraries.base.util.DebugLog;

/**
 * @author Cedric Pronzato
 */
public class XQReportDataFactory extends AbstractDataFactory
{
  private XQConnectionProvider provider;
  private transient XQConnection xqConnection;
  private HashMap<String,String> queryMappings;


  public XQReportDataFactory(final XQConnectionProvider provider)
  {
    queryMappings = new HashMap<String,String>();
    this.provider = provider;
  }

  public TableModel queryData(final String queryName, final DataRow parameters) throws ReportDataFactoryException
  {
    final String xquery = queryMappings.get(queryName);
    if (xquery == null)
    {
      throw new ReportDataFactoryException("No such query: " + queryName);
    }

    final XQConnection xqConnection = getConnection();

    try
    {

      // resolve variables
      final XQParameterLookupParser parser = new XQParameterLookupParser();
      final String translatedQuery = parser.translateAndLookup(xquery, parameters);
      // compile query
      final XQPreparedExpression expression = xqConnection.prepareExpression(translatedQuery);
      // bind variables, we cannot bind all variables of the datarow, only the ones defined in the script
      final List<String> fields = parser.getFields();
      for (final String col: fields)
      {
        final QName var = new QName(col);
        expression.bindObject(var, parameters.get(col), null);  // null = do not override the default object mapping
      }
      // execute query
      final XQResultSequence sequence = expression.executeQuery();
      while (sequence.next())
      {
        final XQItem item = sequence.getItem();
        System.out.println(item.getItemType().getTypeName());
        System.out.println(item.getAtomicValue());
      }
      // free result resources
      // sequence.close();
    }
    catch (XQException e)
    {
      throw new ReportDataFactoryException("Failed to query xml-database", e);
    }

    return null;
  }

  public XQReportDataFactory derive()
  {
    final XQReportDataFactory o = (XQReportDataFactory) clone();
    o.xqConnection = null;
    return o;
  }

  public XQConnection getConnection() throws ReportDataFactoryException
  {
    try
    {
      xqConnection = provider.getConnection();
      final XQStaticContext sctx = xqConnection.getStaticContext();
      // allow rebinding of external variables on a compiled query
      sctx.setBindingMode(XQConstants.BINDING_MODE_DEFERRED);
      // reset the context
      xqConnection.setStaticContext(sctx);
      return xqConnection;
    }
    catch (XQException e)
    {
      throw new ReportDataFactoryException("Error while accessing to XQuery connection", e);
    }
  }

  public void close()
  {
    try
    {
      if (xqConnection != null)
      {
        xqConnection.close();
      }
    }
    catch (XQException e)
    {
      DebugLog.log("Failed to close XQuery datafactory", e);
    }
  }

  public void setQuery(final String name, final String queryString)
  {
    if (queryString == null)
    {
      queryMappings.remove(name);
    }
    else
    {
      queryMappings.put(name, queryString);
    }
  }

  public boolean isQueryExecutable(final String query, final DataRow parameters)
  {
    return queryMappings.containsKey(query);
  }

  public String[] getQueryNames()
  {
    return queryMappings.keySet().toArray(new String[queryMappings.size()]);
  }

  public void cancelRunningQuery()
  {
  }

  public XQReportDataFactory clone()
  {
    final XQReportDataFactory rd = (XQReportDataFactory) super.clone();
    rd.queryMappings = (HashMap<String,String>) queryMappings.clone();
    return rd;
  }
}
