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
 * Copyright (c) 2008 - 2009 Pentaho Corporation, .  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.extensions.datasources.pmd;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.swing.table.TableModel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.di.core.database.DatabaseInterface;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.metadata.messages.LocaleHelper;
import org.pentaho.metadata.model.LogicalTable;
import org.pentaho.metadata.model.SqlPhysicalModel;
import org.pentaho.metadata.query.impl.sql.MappedQuery;
import org.pentaho.metadata.query.impl.sql.SqlGenerator;
import org.pentaho.metadata.query.model.Parameter;
import org.pentaho.metadata.query.model.Query;
import org.pentaho.metadata.query.model.Selection;
import org.pentaho.metadata.query.model.util.QueryXmlHelper;
import org.pentaho.metadata.repository.IMetadataDomainRepository;
import org.pentaho.metadata.util.DatabaseMetaUtil;
import org.pentaho.metadata.util.ThinModelConverter;
import org.pentaho.reporting.engine.classic.core.AbstractDataFactory;
import org.pentaho.reporting.engine.classic.core.DataFactory;
import org.pentaho.reporting.engine.classic.core.DataRow;
import org.pentaho.reporting.engine.classic.core.MetaTableModel;
import org.pentaho.reporting.engine.classic.core.ReportDataFactoryException;
import org.pentaho.reporting.engine.classic.core.ResourceBundleFactory;
import org.pentaho.reporting.engine.classic.core.modules.misc.datafactory.sql.SimpleSQLReportDataFactory;
import org.pentaho.reporting.engine.classic.core.util.ReportParameterValues;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;

/**
 * Performs MQL queries. A MQL-query usually contains all information needed to connect to the database. However the platform also allows to override the
 * connection-information and to provide an own connection instead.
 * <p/>
 * We mirror that case by allowing to provide a connection provider. If no connection provider is given, we use whatever connection information is stored in the
 * MQL data itself.
 *
 * @author Thomas Morgner
 */
public class SimplePmdDataFactory extends AbstractDataFactory
{
  private class PmdSQLDataFactory extends SimpleSQLReportDataFactory
  {
    private PmdSQLDataFactory(final Connection connection)
    {
      super(connection);
    }

    public TableModel parametrizeAndQuery(final DataRow parameters,
                                          final String translatedQuery,
                                          final String[] params) throws SQLException
    {
      return super.parametrizeAndQuery(parameters, translatedQuery, params);
    }

    protected boolean isExpandArrays()
    {
      return true;
    }
  }

  private static final Log logger = LogFactory.getLog(SimplePmdDataFactory.class);
  private static final String[] EMPTY_QUERYNAMES = new String[0];

  private String domainId;
  private String xmiFile;
  private IPmdConnectionProvider connectionProvider;

  private String userField;
  private String passwordField;

  private transient IMetadataDomainRepository domainRepository;
  private transient Connection connection;
  private transient PmdSQLDataFactory sqlReportDataFactory;

  public SimplePmdDataFactory()
  {
  }

  public IPmdConnectionProvider getConnectionProvider()
  {
    return connectionProvider;
  }

  public void setConnectionProvider(final IPmdConnectionProvider connectionProvider)
  {
    this.connectionProvider = connectionProvider;
  }

  public String getDomainId()
  {
    return domainId;
  }

  public void setDomainId(final String domainId)
  {
    this.domainId = domainId;
  }

  public String getXmiFile()
  {
    return xmiFile;
  }

  public void setXmiFile(final String xmiFile)
  {
    this.xmiFile = xmiFile;
  }

  public String getUserField()
  {
    return userField;
  }

  public void setUserField(final String userField)
  {
    this.userField = userField;
  }

  public String getPasswordField()
  {
    return passwordField;
  }

  public void setPasswordField(final String passwordField)
  {
    this.passwordField = passwordField;
  }

  /**
   * Checks whether the query would be executable by this datafactory. This performs a rough check, not a full query.
   *
   * @param query      the query.
   * @param parameters the parameters.
   * @return true, if the query would be executable, false if the query is not recognized.
   */
  public boolean isQueryExecutable(final String query, final DataRow parameters)
  {
    return true;
  }

  public String[] getQueryNames()
  {
    return EMPTY_QUERYNAMES;
  }

  protected IMetadataDomainRepository getDomainRepository() throws ReportDataFactoryException
  {
    if (domainRepository == null)
    {
      domainRepository = connectionProvider.getMetadataDomainRepository(domainId, getResourceManager(), getContextKey(), xmiFile);
      if (domainRepository == null)
      {
        throw new ReportDataFactoryException("No repository found.");
      }
    }
    return domainRepository;
  }

  protected Query parseQuery(final String query) throws ReportDataFactoryException
  {
    final String xmlHelperClass = getConfiguration().getConfigProperty
        ("org.pentaho.reporting.engine.classic.extensions.datasources.pmd.XmlHelperClass");

    final QueryXmlHelper helper =
        ObjectUtilities.loadAndInstantiate(xmlHelperClass, SimplePmdDataFactory.class, QueryXmlHelper.class);
    if (helper == null)
    {
      throw new ReportDataFactoryException("Failed to create XmlHelper: " + xmlHelperClass);//$NON-NLS-1$
    }


    try
    {
      // never returns null
      return helper.fromXML(getDomainRepository(), query);
    }
    catch (ReportDataFactoryException e)
    {
      throw e;
    }
    catch (Exception e)
    {
      logger.error("error", e); //$NON-NLS-1$
      throw new ReportDataFactoryException("Failed to parse query", e);//$NON-NLS-1$
    }
  }

  private DatabaseMeta getDatabaseMeta(final Query queryObject) throws ReportDataFactoryException
  {
    // need to get the correct DatabaseMeta
    final List<LogicalTable> tables = queryObject.getLogicalModel().getLogicalTables();
    if (tables.isEmpty())
    {
      throw new ReportDataFactoryException("No Tables in this query");
    }
    final SqlPhysicalModel sqlModel = (SqlPhysicalModel) tables.get(0).getPhysicalTable().getPhysicalModel();
    return ThinModelConverter.convertToLegacy(sqlModel.getId(), sqlModel.getDatasource());
  }

  private MappedQuery generateSQL(final Query queryObject,
                                  final DatabaseMeta databaseMeta,
                                  final DataRow parameters) throws ReportDataFactoryException
  {
    try
    {
      final String sqlGeneratorClass = getConfiguration().getConfigProperty
          ("org.pentaho.reporting.engine.classic.extensions.datasources.pmd.SqlGeneratorClass");
      final SqlGenerator sqlGenerator = (SqlGenerator) ObjectUtilities.loadAndInstantiate
          (sqlGeneratorClass, SimplePmdDataFactory.class, SqlGenerator.class);
      if (sqlGenerator == null)
      {
        logger.error("Default SqlGenerator class " + sqlGeneratorClass + " not found."); //$NON-NLS-1$
        throw new ReportDataFactoryException("Failed to generate SQL. No valid SqlGenerator class found.");//$NON-NLS-1$
      }
      // convert DataRow into Map<String,Object>
      final Map<String, Object> parameterMap = new HashMap<String, Object>();
      final String[] columnNames = parameters.getColumnNames();
      for (int i = 0; i < columnNames.length; i++)
      {
        final String key = columnNames[i];
        final Object value = parameters.get(key);
        parameterMap.put(key, value);
      }

      final IMetadataDomainRepository domainRepository = getDomainRepository();

      Locale locale;
      ResourceBundleFactory resourceBundleFactory = getResourceBundleFactory();
      if (resourceBundleFactory != null)
      {
        locale = resourceBundleFactory.getLocale();
      }
      else
      {
        locale = LocaleHelper.getLocale();
      }
      if (locale == null)
      {
        locale = Locale.getDefault();
      }

      return sqlGenerator.generateSql(queryObject, locale.toString(), domainRepository, databaseMeta, parameterMap, true);
    }
    catch (ReportDataFactoryException e)
    {
      throw e;
    }
    catch (Exception e)
    {
      throw new ReportDataFactoryException(e.getMessage(), e);//$NON-NLS-1$
    }
  }

  private TableModel buildTableModel(final DatabaseMeta databaseMeta,
                                     final Query queryObject,
                                     final MappedQuery mappedQuery,
                                     final DataRow parameters)
      throws ReportDataFactoryException
  {
    boolean isConnectionValid;
    try
    {
      isConnectionValid = connection != null && connection.isClosed() == false;
    }
    catch (SQLException ex)
    {
      isConnectionValid = false;
    }

    if (sqlReportDataFactory == null || isConnectionValid == false)
    {
      if (sqlReportDataFactory != null)
      {
        sqlReportDataFactory.close();
      }
      else if (connection != null)
      {
        try
        {
          connection.close();
        }
        catch (SQLException e)
        {
          // ignore ;
        }
      }

      final String user = computeUsername(parameters);
      final String password = computePassword(parameters);
      connection = getConnectionProvider().createConnection(databaseMeta, user, password);
      sqlReportDataFactory = new PmdSQLDataFactory(connection);
      sqlReportDataFactory.initialize(getDataFactoryContext());
    }

    final ReportParameterValues computedParameterSet = new ReportParameterValues();
    computedParameterSet.put(DataFactory.QUERY_LIMIT, parameters.get(DataFactory.QUERY_LIMIT));
    computedParameterSet.put(DataFactory.QUERY_TIMEOUT, parameters.get(DataFactory.QUERY_TIMEOUT));


    final String sqlQuery = mappedQuery.getQuery();

    // first prepare the query to have a sensible access method for the parameters.
    final HashMap<String, Parameter> queryParamMap = convertParametersToMap(queryObject);

    // convert ? into ${PARAM} style parameters for the SQL report datafactory
    // populate prepared sql params
    final String[] parameterNames;
    final List<String> paramColNames = mappedQuery.getParamList();
    if (paramColNames != null)
    {
      for (final String colName : paramColNames)
      {
        final Object parameterValue = parameters.get(colName);
        if (parameterValue != null)
        {
          computedParameterSet.put(colName, parameterValue);
        }
        else
        {
          final Parameter parameter = queryParamMap.get(colName);
          computedParameterSet.put(colName, parameter.getDefaultValue());
        }
      }
      parameterNames = paramColNames.toArray(new String[paramColNames.size()]);
    }
    else
    {
      parameterNames = new String[0];
    }

    // Add in model parameters if not overridden in report - PRD-3862
    
    // Check to see if timeout is already in the report
    try {
	    Object existingQueryTimeoutObj = computedParameterSet.get(DataFactory.QUERY_TIMEOUT);
	    if ( (existingQueryTimeoutObj == null) || 
	    		( (existingQueryTimeoutObj instanceof Number) && ( ((Number)existingQueryTimeoutObj).intValue() == 0)) ) { // If null, or if default of 0
	      // Timeout isn't in the parameters - check the model and see if it's defined.
	      Object timeoutProperty = queryObject.getLogicalModel().getProperty("timeout"); //$NON-NLS-1$
	      if (timeoutProperty != null && timeoutProperty instanceof Number) {
	    	  // timeout is provided in the model - add it to the computed parameter set
	          int timeoutVal = ((Number)timeoutProperty).intValue();
	          computedParameterSet.put(DataFactory.QUERY_TIMEOUT, timeoutVal);
	      }
	    }
    } catch (Exception ex) {
    	// This shouldn't stop the operation from happening, but we need to log the error.
    	logger.error("ERROR_0001 - Could not read query timeout from model.", ex);
    }
    // Check to see if limit is already in the report
    try {
	    Object existingQueryLimitObj = computedParameterSet.get(DataFactory.QUERY_LIMIT);
	    if ( (existingQueryLimitObj == null) || 
	    		( (existingQueryLimitObj instanceof Number) && ( ((Number)existingQueryLimitObj).intValue() == -1)) ) { // If null, or if default of -1
	      // Limit isn't in the parameters - check the model and see if it's defined.
	      Object maxRowsProperty = queryObject.getLogicalModel().getProperty("max_rows"); //$NON-NLS-1$
	      if (maxRowsProperty != null && maxRowsProperty instanceof Number) {
	    	  // max_rows is provided in the model - add it to the computed parameter set
	          int maxRowsVal = ((Number)maxRowsProperty).intValue();
	          computedParameterSet.put(DataFactory.QUERY_LIMIT, maxRowsVal);
	      }
	    }
    } catch (Exception ex) {
    	// This shouldn't stop the operation from happening, but we need to log the error.
    	logger.error("ERROR_0002 - Could not read max_rows from model.", ex);
    }
    // End PRD-3862 fix

    try
    {
      final TableModel tableModel =
          sqlReportDataFactory.parametrizeAndQuery(computedParameterSet, sqlQuery, parameterNames);

      // now lets wrap up the model into a meta-data aware model ..
      final List<Selection> selections = queryObject.getSelections();
      if (selections.size() != tableModel.getColumnCount())
      {
        throw new ReportDataFactoryException("Whatever the query returned, it does not look familiar");
      }
      // cast is safe, as the SQL-Datasource is guaranteed to return a
      // MetaTableModel
      return new PmdMetaTableModel((MetaTableModel) tableModel, queryObject.getSelections());
    }
    catch (SQLException e)
    {
      throw new ReportDataFactoryException("The generated SQL-query did not execute successfully.", e);
    }

  }

  private HashMap<String, Parameter> convertParametersToMap(final Query queryObject)
  {
    final List<Parameter> queryParamValues = queryObject.getParameters();
    final HashMap<String, Parameter> queryParamMap = new HashMap<String, Parameter>();
    for (int i = 0; i < queryParamValues.size(); i++)
    {
      final Parameter parameter = queryParamValues.get(i);
      queryParamMap.put(parameter.getName(), parameter);
    }
    return queryParamMap;
  }

  /**
   * Closes the data factory and frees all resources held by this instance.
   */
  public void close()
  {
    if (sqlReportDataFactory != null)
    {
      sqlReportDataFactory.close();
      sqlReportDataFactory = null;
    }
    else if (connection != null)
    {
      try
      {
        // only try to close if it's not closed
        // PDB-539
        if (connection.isClosed() == false)
        {
          connection.close();
        }
      }
      catch (SQLException e)
      {
        logger.warn("Unable to close connection", e);
      }
      connection = null;
    }

    domainRepository = null;
  }

  /**
   * Queries a datasource. The string 'query' defines the name of the query. The Parameterset given here may contain more data than actually needed for the
   * query.
   * <p/>
   * The parameter-dataset may change between two calls, do not assume anything, and do not hold references to the parameter-dataset or the position of the
   * columns in the dataset.
   *
   * @param queryName  the query name
   * @param parameters the parameters for the query
   * @return the result of the query as table model.
   * @throws org.pentaho.reporting.engine.classic.core.ReportDataFactoryException
   *          if an error occured while performing the query.
   */
  public TableModel queryData(final String queryName, final DataRow parameters) throws ReportDataFactoryException
  {
    // domain must exist and be loaded in the domain repository already
    // parse the metadata query
    final Query queryObject = parseQuery(queryName);
    if (queryObject.getLogicalModel().getPhysicalModel() instanceof SqlPhysicalModel) {
      try
      {
        final DatabaseMeta databaseMeta = getDatabaseMeta(queryObject);
        final DatabaseMeta activeDatabaseMeta = getActiveDatabaseMeta(databaseMeta, parameters);
        final MappedQuery mappedQuery = generateSQL(queryObject, activeDatabaseMeta, parameters);
        // get active database meta
  
        return buildTableModel(databaseMeta, queryObject, mappedQuery, parameters);
      }
      catch (ReportDataFactoryException e)
      {
        throw e;
      }
      catch (Exception e)
      {
        logger.error("error", e); //$NON-NLS-1$
        throw new ReportDataFactoryException("Failed to perform query", e);//$NON-NLS-1$
      }
    } else {
      // broker the execution of this query to the connection provider
      return connectionProvider.executeQuery(queryObject, parameters);
    }
  }

  private DatabaseMeta getActiveDatabaseMeta(final DatabaseMeta databaseMeta,
                                             final DataRow dataRow)
  {
    // retrieve a temporary connection to determine if a dialect change is necessary
    // for generating the MQL Query.
    final String user = computeUsername(dataRow);
    final String password = computePassword(dataRow);

    final Connection connection;
    try
    {
      connection = getConnectionProvider().createConnection(databaseMeta, user, password);
    }
    catch (ReportDataFactoryException rdfe)
    {
      return databaseMeta;
    }

    try
    {

      // if the connection type is not of the current dialect, regenerate the query
      final DatabaseInterface di = getDatabaseInterface(connection);

      if ((di != null) && !databaseMeta.getPluginId().equals(di.getPluginId()))
      {
        // we need to reinitialize our mqlQuery object and reset the query.
        // note that using this di object wipes out connection info
        final DatabaseMeta meta = (DatabaseMeta) databaseMeta.clone();
        final DatabaseInterface di2 = (DatabaseInterface) di.clone();
        di2.setAccessType(databaseMeta.getAccessType());
        di2.setDatabaseName(databaseMeta.getDatabaseName());
        meta.setDatabaseInterface(di2);
        return meta;
      }
      else
      {
        return databaseMeta;
      }
    }
    finally
    {
      if (connection != null)
      {
        try
        {
          connection.close();
        }
        catch (SQLException ignored)
        {
          // this is just cleanup
          logger.debug("debug", ignored); //$NON-NLS-1$
        }
      }
    }

  }

  private String computeUsername(final DataRow dataRow)
  {
    final String user;
    if (userField == null)
    {
      user = null;
    }
    else
    {
      final Object userRaw = dataRow.get(userField);
      if (userRaw instanceof String)
      {
        user = String.valueOf(userRaw);
      }
      else
      {
        user = null;
      }
    }
    return user;
  }

  private String computePassword(final DataRow dataRow)
  {
    final String password;
    if (passwordField == null)
    {
      password = null;
    }
    else
    {
      final Object passwordField = dataRow.get(this.passwordField);
      if (passwordField instanceof String)
      {
        password = String.valueOf(passwordField);
      }
      else
      {
        password = null;
      }
    }
    return password;
  }

  private DatabaseInterface getDatabaseInterface(final Connection conn)
  {
    try
    {
      final String prod = conn.getMetaData().getDatabaseProductName();
      final DatabaseInterface di = DatabaseMetaUtil.getDatabaseInterface(prod);
      if (prod != null && di == null)
      {
        logger.warn("dialect not detected"); //$NON-NLS-1$
      }
      return di;
    }
    catch (SQLException e)
    {
      logger.warn("dialect exception", e); //$NON-NLS-1$
    }
    return null;
  }

  /**
   * Returns a copy of the data factory that is not affected by its anchestor and holds no connection to the anchestor anymore. A data-factory will be derived
   * at the beginning of the report processing.
   *
   * @return a copy of the data factory.
   */
  public DataFactory derive()
  {
    final SimplePmdDataFactory dataFactory = (SimplePmdDataFactory) clone();
    dataFactory.connection = null;
    dataFactory.sqlReportDataFactory = null;
    return dataFactory;
  }

  public void cancelRunningQuery()
  {
    if (sqlReportDataFactory != null)
    {
      sqlReportDataFactory.cancelRunningQuery();
    }
  }

  protected String translateQuery(final String query)
  {
    return query;
  }

  protected String computedQuery(final String queryName, final DataRow parameters) throws ReportDataFactoryException
  {
    return queryName;
  }

  public String[] getReferencedFields(final String query, final DataRow parameter) throws ReportDataFactoryException
  {
    final String queryRaw = computedQuery(query, parameter);
    if (query == null)
    {
      return null;
    }
    
    final Query queryObject = parseQuery(queryRaw);
    final List<Parameter> queryParamValues = queryObject.getParameters();
    final LinkedHashSet<String> retval = new LinkedHashSet<String>();
    if (userField != null)
    {
      retval.add(userField);
    }
    if (passwordField != null)
    {
      retval.add(passwordField);
    }
    if (queryParamValues != null)
    {
      for (final Parameter p : queryParamValues)
      {
        retval.add(p.getName());
      }
    }
    retval.add(DataFactory.QUERY_LIMIT);
    return retval.toArray(new String[retval.size()]);
  }

  public ArrayList<Object> getQueryHash(final String queryName, final DataRow parameters)
  {
    final ArrayList<Object> retval = new ArrayList<Object>();
    retval.add(getClass().getName());
    retval.add(translateQuery(queryName));
    retval.add(domainId);
    retval.add(xmiFile);
    retval.add(getContextKey());
    retval.add(connectionProvider.getClass());
    return retval;
  }
}
