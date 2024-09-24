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
* Copyright (c) 2002-2017 Hitachi Vantara..  All rights reserved.
*/

package org.pentaho.reporting.engine.classic.extensions.datasources.mondrian;

import mondrian.mdx.MemberExpr;
import mondrian.olap.CacheControl;
import mondrian.olap.Connection;
import mondrian.olap.Cube;
import mondrian.olap.Exp;
import mondrian.olap.Hierarchy;
import mondrian.olap.Literal;
import mondrian.olap.Member;
import mondrian.olap.MondrianException;
import mondrian.olap.MondrianProperties;
import mondrian.olap.OlapElement;
import mondrian.olap.Parameter;
import mondrian.olap.Position;
import mondrian.olap.Query;
import mondrian.olap.Result;
import mondrian.olap.Util;
import mondrian.olap.type.MemberType;
import mondrian.olap.type.NumericType;
import mondrian.olap.type.SetType;
import mondrian.olap.type.StringType;
import mondrian.olap.type.Type;
import mondrian.server.Statement;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.AbstractDataFactory;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.DataFactory;
import org.pentaho.reporting.engine.classic.core.DataFactoryContext;
import org.pentaho.reporting.engine.classic.core.DataRow;
import org.pentaho.reporting.engine.classic.core.ReportDataFactoryException;
import org.pentaho.reporting.engine.classic.core.util.PropertyLookupParser;
import org.pentaho.reporting.libraries.base.config.Configuration;
import org.pentaho.reporting.libraries.base.util.CSVTokenizer;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.libraries.base.util.StringUtils;
import org.pentaho.reporting.libraries.formatting.FastMessageFormat;

import java.lang.reflect.Array;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.Set;
import java.util.regex.PatternSyntaxException;

/**
 * This data-factory operates in Legacy-Mode providing a preprocessed view on the mondrian result. It behaves exactly as
 * known from the Pentaho-Platform and the Hitachi Vantara-Report-Designer. This mode of operation breaks the structure of the
 * resulting table as soon as new rows are returned by the server.
 *
 * @author Thomas Morgner
 */
public abstract class AbstractMDXDataFactory extends AbstractDataFactory {
  /**
   * The message compiler maps all named references into numeric references.
   */
  protected static class MDXCompiler extends PropertyLookupParser {
    private HashSet<String> collectedParameter;
    private DataRow parameters;
    private Locale locale;

    /**
     * Default Constructor.
     */
    protected MDXCompiler( final DataRow parameters,
                           final Locale locale ) {
      if ( locale == null ) {
        throw new NullPointerException( "Locale must not be null" );
      }
      if ( parameters == null ) {
        throw new NullPointerException( "Parameter datarow must not be null" );
      }

      this.collectedParameter = new HashSet<String>();
      this.parameters = parameters;
      this.locale = locale;
      setMarkerChar( '$' );
      setOpeningBraceChar( '{' );
      setClosingBraceChar( '}' );
    }

    /**
     * Looks up the property with the given name. This replaces the name with the current index position.
     *
     * @param name the name of the property to look up.
     * @return the translated value.
     */
    protected String lookupVariable( final String name ) {
      final CSVTokenizer tokenizer = new CSVTokenizer( name, false );
      if ( tokenizer.hasMoreTokens() == false ) {
        // invalid reference ..
        return null;
      }

      final String parameterName = tokenizer.nextToken();
      collectedParameter.add( parameterName );
      final Object o = parameters.get( parameterName );
      String subType = null;
      final StringBuilder b = new StringBuilder( name.length() + 4 );
      b.append( '{' );
      b.append( "0" );
      while ( tokenizer.hasMoreTokens() ) {
        b.append( ',' );
        final String token = tokenizer.nextToken();
        b.append( token );
        if ( subType == null ) {
          subType = token;
        }
      }
      b.append( '}' );
      final String formatString = b.toString();

      if ( "string".equals( subType ) ) {
        if ( o == null ) {
          return "null"; // NON-NLS
        }
        return quote( String.valueOf( o ) );
      }

      final FastMessageFormat messageFormat = new FastMessageFormat( formatString, locale );
      return messageFormat.format( new Object[] { o } );
    }

    public Set<String> getCollectedParameter() {
      return Collections.unmodifiableSet( (Set<String>) collectedParameter.clone() );
    }
  }

  private static final String ACCEPT_ROLES_CONFIG_KEY =
    "org.pentaho.reporting.engine.classic.extensions.datasources.mondrian.role-filter.static.accept";
  private static final String ACCEPT_REGEXP_CONFIG_KEY =
    "org.pentaho.reporting.engine.classic.extensions.datasources.mondrian.role-filter.reg-exp.accept";
  private static final String DENY_ROLE_CONFIG_KEY =
    "org.pentaho.reporting.engine.classic.extensions.datasources.mondrian.role-filter.static.deny";
  private static final String DENY_REGEXP_CONFIG_KEY =
    "org.pentaho.reporting.engine.classic.extensions.datasources.mondrian.role-filter.reg-exp.deny";
  private static final String ROLE_FILTER_ENABLE_CONFIG_KEY =
    "org.pentaho.reporting.engine.classic.extensions.datasources.mondrian.role-filter.enable";

  private String jdbcUser;
  private String jdbcUserField;
  private String jdbcPassword;
  private String jdbcPasswordField;
  private String dynamicSchemaProcessor;
  private Boolean useSchemaPool;
  private Boolean useContentChecksum;
  private Properties baseConnectionProperties;

  private String role;
  private String roleField;
  private CubeFileProvider cubeFileProvider;
  private DataSourceProvider dataSourceProvider;
  private MondrianConnectionProvider mondrianConnectionProvider;
  private String designTimeName;
  private transient Connection connection;
  private static final String[] EMPTY_QUERYNAMES = new String[ 0 ];
  private static final Log logger = LogFactory.getLog( AbstractMDXDataFactory.class );
  private boolean membersOnAxisSorted;

  public AbstractMDXDataFactory() {
    this.mondrianConnectionProvider =
      ClassicEngineBoot.getInstance().getObjectFactory().get( MondrianConnectionProvider.class );
    this.baseConnectionProperties = new Properties();
  }

  public MondrianConnectionProvider getMondrianConnectionProvider() {
    return mondrianConnectionProvider;
  }

  public void setMondrianConnectionProvider( final MondrianConnectionProvider mondrianConnectionProvider ) {
    if ( mondrianConnectionProvider == null ) {
      throw new NullPointerException();
    }
    this.mondrianConnectionProvider = mondrianConnectionProvider;
  }

  public String getDynamicSchemaProcessor() {
    return dynamicSchemaProcessor;
  }

  public void setDynamicSchemaProcessor( final String dynamicSchemaProcessor ) {
    this.dynamicSchemaProcessor = dynamicSchemaProcessor;
  }

  public boolean isMembersOnAxisSorted() {
    return membersOnAxisSorted;
  }

  public void setMembersOnAxisSorted( final boolean membersOnAxisSorted ) {
    this.membersOnAxisSorted = membersOnAxisSorted;
  }

  public Boolean isUseSchemaPool() {
    return useSchemaPool;
  }

  public void setUseSchemaPool( final Boolean useSchemaPool ) {
    this.useSchemaPool = useSchemaPool;
  }

  public Boolean isUseContentChecksum() {
    return useContentChecksum;
  }

  public void setUseContentChecksum( final Boolean useContentChecksum ) {
    this.useContentChecksum = useContentChecksum;
  }

  public String getRole() {
    return role;
  }

  public void setRole( final String role ) {
    this.role = role;
  }

  public String getRoleField() {
    return roleField;
  }

  public void setRoleField( final String roleField ) {
    this.roleField = roleField;
  }

  public CubeFileProvider getCubeFileProvider() {
    return cubeFileProvider;
  }

  public void setCubeFileProvider( final CubeFileProvider cubeFileProvider ) {
    this.cubeFileProvider = cubeFileProvider;
  }

  public DataSourceProvider getDataSourceProvider() {
    return dataSourceProvider;
  }

  public void setDataSourceProvider( final DataSourceProvider dataSourceProvider ) {
    this.dataSourceProvider = dataSourceProvider;
  }

  public String getJdbcUser() {
    return jdbcUser;
  }

  public void setJdbcUser( final String jdbcUser ) {
    this.jdbcUser = jdbcUser;
  }

  public String getJdbcPassword() {
    return jdbcPassword;
  }

  public void setJdbcPassword( final String jdbcPassword ) {
    this.jdbcPassword = jdbcPassword;
  }

  public String getJdbcUserField() {
    return jdbcUserField;
  }

  public void setJdbcUserField( final String jdbcUserField ) {
    this.jdbcUserField = jdbcUserField;
  }

  public String getJdbcPasswordField() {
    return jdbcPasswordField;
  }

  public void setJdbcPasswordField( final String jdbcPasswordField ) {
    this.jdbcPasswordField = jdbcPasswordField;
  }

  public Properties getBaseConnectionProperties() {
    return (Properties) baseConnectionProperties.clone();
  }

  /**
   * Sets base connection properties. These will be overriden by any programatically set properties.
   *
   * @param connectionProperties
   */
  public void setBaseConnectionProperties( final Properties connectionProperties ) {
    if ( connectionProperties != null ) {
      this.baseConnectionProperties.clear();
      this.baseConnectionProperties.putAll( connectionProperties );
    }
  }

  /**
   * Checks whether the query would be executable by this datafactory. This performs a rough check, not a full query.
   *
   * @param query
   * @param parameters
   * @return
   */
  public boolean isQueryExecutable( final String query, final DataRow parameters ) {
    return true;
  }

  /**
   * Closes the data factory and frees all resources held by this instance.
   */
  public void close() {
    if ( connection != null ) {
      connection.close();
    }
    connection = null;
  }

  /**
   * Access the cache control on a per-datasource level. Setting "onlyCurrentSchema" to true will selectively purge the
   * mondrian cache for the specifc schema only.
   *
   * @param parameters
   * @param onlyCurrentSchema
   * @throws ReportDataFactoryException
   */
  public void clearCache( final DataRow parameters,
                          final boolean onlyCurrentSchema ) throws ReportDataFactoryException {
    try {
      final Connection connection =
        mondrianConnectionProvider
          .createConnection( computeProperties( parameters ), dataSourceProvider.getDataSource() );
      try {
        final CacheControl cacheControl = connection.getCacheControl( null );
        if ( onlyCurrentSchema ) {
          cacheControl.flushSchema( connection.getSchema() );
        } else {
          cacheControl.flushSchemaCache();
        }
      } finally {
        connection.close();
      }
    } catch ( SQLException e ) {
      logger.error( e );
      throw new ReportDataFactoryException(
        "Failed to create DataSource (SQL Exception - error code: " + e.getErrorCode() + "):" + e.toString(), e );
    } catch ( MondrianException e ) {
      logger.error( e );
      throw new ReportDataFactoryException( "Failed to create DataSource (Mondrian Exception):" + e.toString(), e );
    }
  }

  /**
   * Queries a datasource. The string 'query' defines the name of the query. The Parameterset given here may contain
   * more data than actually needed for the query.
   * <p/>
   * The parameter-dataset may change between two calls, do not assume anything, and do not hold references to the
   * parameter-dataset or the position of the columns in the dataset.
   *
   * @param rawMdxQuery the mdx Query string.
   * @param parameters  the parameters for the query
   * @return the result of the query as table model.
   * @throws org.pentaho.reporting.engine.classic.core.ReportDataFactoryException if an error occured while performing
   *                                                                              the query.
   */
  public Result performQuery( final String rawMdxQuery, final DataRow parameters ) throws ReportDataFactoryException {
    try {
      if ( connection == null ) {
        connection = mondrianConnectionProvider
          .createConnection( computeProperties( parameters ), dataSourceProvider.getDataSource() );
      }
    } catch ( SQLException e ) {
      throw new ReportDataFactoryException( "Failed to create datasource:" + e.getLocalizedMessage(), e );
    } catch ( MondrianException e ) {
      throw new ReportDataFactoryException( "Failed to create datasource:" + e.getLocalizedMessage(), e );
    }

    try {
      if ( connection == null ) {
        throw new ReportDataFactoryException( "Factory is closed." );
      }

      final MDXCompiler compiler = new MDXCompiler( parameters, getLocale() );
      final String mdxQuery = compiler.translateAndLookup( rawMdxQuery, parameters );
      // Alternatively, JNDI is possible. Maybe even more ..
      final Query query = connection.parseQuery( mdxQuery );
      final Statement statement = query.getStatement();
      final int queryTimeoutValue = calculateQueryTimeOut( parameters );
      if ( queryTimeoutValue > 0 ) {
        statement.setQueryTimeoutMillis( queryTimeoutValue * 1000 );
      }

      parametrizeQuery( parameters, query );

      //noinspection deprecation
      final Result resultSet = connection.execute( query );
      if ( resultSet == null ) {
        throw new ReportDataFactoryException( "query returned no resultset" );
      }
      return resultSet;
    } catch ( MondrianException e ) {
      throw new ReportDataFactoryException( "Failed to create datasource:" + e.getLocalizedMessage(), e );
    }
  }

  private void parametrizeQuery( final DataRow parameters, final Query query ) throws ReportDataFactoryException {
    final Parameter[] parameterDefs = query.getParameters();
    for ( int i = 0; i < parameterDefs.length; i++ ) {
      final Parameter def = parameterDefs[ i ];
      final Type parameterType = def.getType();
      final Object parameterValue = preprocessMemberParameter( def, parameters, parameterType );
      final Object processedParamValue = computeParameterValue( query, parameterValue, parameterType );

      // Mondrian allows null values to be passed in, so we'll go ahead and
      // convert null values to their defaults for now until MONDRIAN-745 is
      // resolved.

      final Exp exp = def.getDefaultExp();
      if ( processedParamValue == null && exp != null && exp instanceof Literal ) {
        Literal exp1 = (Literal) exp;
        def.setValue( exp1.getValue() );
      } else {
        def.setValue( processedParamValue );
      }
    }
  }

  private Object preprocessMemberParameter( final Parameter def,
                                            final DataRow parameters,
                                            final Type parameterType ) {
    Object parameterValue = parameters.get( def.getName() );
    // Mondrian doesn't handle null MemberType/SetType parameters well (http://jira.pentaho.com/browse/MONDRIAN-745)
    // If parameterValue is null, give it the default value
    if ( parameterValue != null ) {
      return parameterValue;
    }
    try {
      if ( parameterType instanceof MemberType || parameterType instanceof SetType ) {
        return def.getDefaultExp().toString();
      }
    } catch ( final Exception e ) {
      // Ignore - this is a safety procedure anyway
    }
    return null;
  }

  private Object computeParameterValue( final Query query,
                                        final Object parameterValue,
                                        final Type parameterType ) throws ReportDataFactoryException {
    final Object processedParamValue;
    if ( parameterValue != null ) {

      if ( parameterType instanceof StringType ) {
        if ( !( parameterValue instanceof String ) ) {
          throw new ReportDataFactoryException( parameterValue + " is incorrect for type " + parameterType );
        }
        processedParamValue = parameterValue;
      } else if ( parameterType instanceof NumericType ) {
        if ( !( parameterValue instanceof Number ) ) {
          throw new ReportDataFactoryException( parameterValue + " is incorrect for type " + parameterType );
        }
        processedParamValue = parameterValue;
      } else if ( parameterType instanceof MemberType ) {
        final MemberType memberType = (MemberType) parameterType;
        final Hierarchy hierarchy = memberType.getHierarchy();
        if ( parameterValue instanceof String ) {
          final Member member = findMember( query, hierarchy, query.getCube(), String.valueOf( parameterValue ) );
          if ( member != null ) {
            processedParamValue = new MemberExpr( member );
          } else {
            processedParamValue = null;
          }
        } else {
          if ( !( parameterValue instanceof OlapElement ) ) {
            throw new ReportDataFactoryException( parameterValue + " is incorrect for type " + parameterType );
          } else {
            processedParamValue = parameterValue;
          }
        }
      } else if ( parameterType instanceof SetType ) {
        final SetType setType = (SetType) parameterType;
        final Hierarchy hierarchy = setType.getHierarchy();
        if ( parameterValue instanceof String ) {
          final String rawString = (String) parameterValue;
          final String[] memberStr = rawString.replaceFirst( "^ *\\{", "" ).replaceFirst( "} *$", "" ).split( "," );
          final List<Member> list = new ArrayList<Member>( memberStr.length );

          for ( int j = 0; j < memberStr.length; j++ ) {
            final String str = memberStr[ j ];
            final Member member = findMember( query, hierarchy, query.getCube(), String.valueOf( str ) );
            if ( member != null ) {
              list.add( member );
            }
          }

          processedParamValue = list;
        } else {
          if ( !( parameterValue instanceof OlapElement ) ) {
            throw new ReportDataFactoryException( parameterValue + " is incorrect for type " + parameterType );
          } else {
            processedParamValue = parameterValue;
          }
        }
      } else {
        processedParamValue = parameterValue;
      }
    } else {
      processedParamValue = null;
    }
    return processedParamValue;
  }

  private Member findMember( final Query query,
                             final Hierarchy hierarchy,
                             final Cube cube,
                             final String parameter ) throws ReportDataFactoryException {
    try {
      final Member directValue = yuckyInternalMondrianLookup( query, hierarchy, parameter );
      if ( directValue != null ) {
        return directValue;
      }
    } catch ( Exception e ) {
      // It is non fatal if that fails. Invalid input has this effect.
    }

    Member memberById = null;
    Member memberByUniqueId = null;

    final boolean searchForNames = MondrianProperties.instance().NeedDimensionPrefix.get() == false;
    final boolean missingMembersIsFatal = MondrianProperties.instance().IgnoreInvalidMembersDuringQuery.get();

    try {
      final Member directValue = lookupDirectly( hierarchy, cube, parameter, searchForNames );
      if ( directValue != null ) {
        return directValue;
      }
    } catch ( Exception e ) {
      // It is non fatal if that fails. Invalid input has this effect.
    }

    final Query memberQuery = connection.parseQuery( "SELECT " + hierarchy.getQualifiedName() // NON-NLS
      + ".AllMembers ON 0, {} ON 1 FROM " + cube.getQualifiedName() ); // NON-NLS
    final Result result = connection.execute( memberQuery );
    try {
      final List<Position> positionList = result.getAxes()[ 0 ].getPositions();
      for ( int i = 0; i < positionList.size(); i++ ) {
        final Position position = positionList.get( i );
        for ( int j = 0; j < position.size(); j++ ) {
          final Member member = position.get( j );
          if ( parameter.equals( MondrianUtil.getUniqueMemberName( member ) ) ) {
            if ( memberByUniqueId == null ) {
              memberByUniqueId = member;
            } else {
              logger
                .warn( "Encountered a member with a duplicate unique key: " + member.getQualifiedName() ); // NON-NLS
            }
          }
          if ( searchForNames == false ) {
            continue;
          }
          if ( parameter.equals( member.getName() ) ) {
            if ( memberById == null ) {
              memberById = member;
            } else {
              logger.warn( "Encountered a member with a duplicate name: " + member.getQualifiedName() ); // NON-NLS
            }
          }
        }
      }
    } finally {
      result.close();
    }
    if ( memberByUniqueId != null ) {
      return memberByUniqueId;
    }
    if ( memberById != null ) {
      return memberById;
    }

    if ( missingMembersIsFatal ) {
      throw new ReportDataFactoryException( "No member matches parameter value '" + parameter + "'." );
    }
    return null;
  }

  private Member lookupDirectly( final Hierarchy hierarchy,
                                 final Cube cube,
                                 final String parameter,
                                 final boolean searchForNames ) {
    Member memberById = null;
    Member memberByUniqueId = null;
    final Query queryDirect =
      connection.parseQuery( "SELECT STRTOMEMBER(" + quote( parameter ) + ") ON 0, {} ON 1 FROM " // NON-NLS
        + cube.getQualifiedName() );
    final Result resultDirect = connection.execute( queryDirect );
    try {
      final List<Position> positionList = resultDirect.getAxes()[ 0 ].getPositions();
      for ( int i = 0; i < positionList.size(); i++ ) {
        final Position position = positionList.get( i );
        for ( int j = 0; j < position.size(); j++ ) {
          final Member member = position.get( j );

          // If the parameter starts with '[', we'll assume we have the full
          // member specification specification. Otherwise, keep the funky lookup
          // route. We do check whether we get a second member (heck, should not
          // happen, but I've seen pigs fly already).

          if ( parameter.startsWith( "[" ) ) {
            if ( memberByUniqueId == null ) {
              memberByUniqueId = member;
            } else {
              logger.warn( "Encountered a member with a duplicate key: " + member.getQualifiedName() ); // NON-NLS
            }
          }
          if ( searchForNames == false ) {
            continue;
          }
          if ( parameter.equals( member.getName() ) ) {
            if ( memberById == null ) {
              memberById = member;
            } else {
              logger.warn( "Encountered a member with a duplicate name: " + member.getQualifiedName() ); // NON-NLS
            }
          }
        }
      }
    } finally {
      resultDirect.close();
    }
    if ( memberByUniqueId != null ) {
      final Hierarchy memberHierarchy = memberByUniqueId.getHierarchy();
      if ( hierarchy != memberHierarchy ) {
        if ( ObjectUtilities.equal( hierarchy, memberHierarchy ) == false ) {
          logger
            .warn( "Cannot match hierarchy of member found with the hierarchy specfied in the parameter: " // NON-NLS
              + "Unabe to guarantee that the correct member has been queried, returning null." ); // NON-NLS
          return null;
        }
      }
      return memberByUniqueId;
    }
    if ( memberById != null ) {
      final Hierarchy memberHierarchy = memberById.getHierarchy();
      if ( hierarchy != memberHierarchy ) {
        if ( ObjectUtilities.equal( hierarchy, memberHierarchy ) == false ) {
          logger
            .warn( "Cannot match hierarchy of member found with the hierarchy specfied in the parameter: " // NON-NLS
              + "Unabe to guarantee that the correct member has been queried, returning null" ); // NON-NLS
          return null;
        }
      }
      return memberById;
    }
    return null;
  }

  protected Member yuckyInternalMondrianLookup( final Query query, final Hierarchy hierarchy, final String parameter ) {
    final Member memberById = (Member) Util.lookup( query, Util.parseIdentifier( parameter ) );
    if ( memberById != null ) {
      final Hierarchy memberHierarchy = memberById.getHierarchy();
      if ( hierarchy != memberHierarchy ) {
        if ( ObjectUtilities.equal( hierarchy, memberHierarchy ) == false ) {
          logger
            .warn( "Cannot match hierarchy of member found with the hierarchy specfied in the parameter: " // NON-NLS
              + "Unabe to guarantee that the correct member has been queried, returning null" ); // NON-NLS
          return null;
        }
      }
      return memberById;
    }
    return null;
  }

  protected int extractQueryLimit( final DataRow parameters ) {
    final Object queryLimit = parameters.get( DataFactory.QUERY_LIMIT );
    final int queryLimitValue;
    if ( queryLimit instanceof Number ) {
      final Number i = (Number) queryLimit;
      queryLimitValue = Math.max( 0, i.intValue() );
    } else {
      // means no limit at all
      queryLimitValue = 0;
    }
    return queryLimitValue;
  }

  private String computeRole( final DataRow parameters ) throws ReportDataFactoryException {
    if ( roleField != null ) {
      final Object field = parameters.get( roleField );
      if ( field != null ) {
        if ( field instanceof Object[] ) {
          final Object[] roleArray = (Object[]) field;
          final StringBuffer buffer = new StringBuffer();
          final int length = roleArray.length;
          for ( int i = 0; i < length; i++ ) {
            final Object o = roleArray[ i ];
            if ( o == null ) {
              continue;
            }

            final String role = filter( String.valueOf( o ) );
            if ( role == null ) {
              continue;
            }
            buffer.append( quoteRole( role ) );
          }
          return buffer.toString();
        } else if ( field.getClass().isArray() ) {
          final StringBuffer buffer = new StringBuffer();
          final int length = Array.getLength( field );
          for ( int i = 0; i < length; i++ ) {
            final Object o = Array.get( field, i );
            if ( o == null ) {
              continue;
            }

            final String role = filter( String.valueOf( o ) );
            if ( role == null ) {
              continue;
            }
            buffer.append( quoteRole( role ) );
          }
          return buffer.toString();
        }
        final String role = filter( String.valueOf( field ) );
        if ( role != null ) {
          return role;
        }
      }
    }

    return filter( role );
  }

  private String quoteRole( final String role ) {
    if ( role.indexOf( ',' ) == -1 ) {
      return role;
    }
    final StringBuffer b = new StringBuffer( role.length() + 5 );
    final char[] chars = role.toCharArray();
    for ( int i = 0; i < chars.length; i++ ) {
      final char c = chars[ i ];
      if ( c == ',' ) {
        b.append( c );
      }
      b.append( c );
    }
    return b.toString();
  }

  private String computeJdbcUser( final DataRow parameters ) {
    if ( jdbcUserField != null ) {
      final Object field = parameters.get( jdbcUserField );
      if ( field != null ) {
        return String.valueOf( field );
      }
    }

    return jdbcUser;
  }

  private String computeJdbcPassword( final DataRow parameters ) {
    if ( jdbcPasswordField != null ) {
      final Object field = parameters.get( jdbcPasswordField );
      if ( field != null ) {
        return String.valueOf( field );
      }
    }

    return jdbcPassword;
  }

  private Properties computeProperties( final DataRow parameters ) throws ReportDataFactoryException {
    if ( cubeFileProvider == null ) {
      throw new ReportDataFactoryException( "No CubeFileProvider" );
    }

    final Properties properties = getBaseConnectionProperties();
    final String catalog = cubeFileProvider.getCubeFile( getResourceManager(), getContextKey() );
    if ( catalog == null ) {
      throw new ReportDataFactoryException( "No valid catalog given." );
    }
    properties.setProperty( "Catalog", catalog ); // NON-NLS
    final String role = computeRole( parameters );
    if ( role != null ) {
      properties.setProperty( "Role", role ); // NON-NLS
    }
    final String jdbcUser = computeJdbcUser( parameters );
    if ( StringUtils.isEmpty( jdbcUser ) == false ) {
      properties.setProperty( "JdbcUser", jdbcUser ); // NON-NLS
    }
    final String jdbcPassword = computeJdbcPassword( parameters );
    if ( StringUtils.isEmpty( jdbcPassword ) == false ) {
      properties.setProperty( "JdbcPassword", jdbcPassword ); // NON-NLS
    }
    final Locale locale = getLocale();
    if ( locale != null ) {
      properties.setProperty( "Locale", locale.toString() ); // NON-NLS
    }

    if ( isUseContentChecksum() != null ) {
      properties.setProperty( "UseContentChecksum", String.valueOf( isUseContentChecksum() ) ); // NON-NLS
    }
    if ( isUseSchemaPool() != null ) {
      properties.setProperty( "UseSchemaPool", String.valueOf( isUseSchemaPool() ) ); // NON-NLS
    }
    if ( getDynamicSchemaProcessor() != null ) {
      properties.setProperty( "DynamicSchemaProcessor", getDynamicSchemaProcessor() ); // NON-NLS
    }
    return properties;
  }

  public AbstractMDXDataFactory clone() {
    final AbstractMDXDataFactory dataFactory = (AbstractMDXDataFactory) super.clone();
    dataFactory.connection = null;
    if ( this.baseConnectionProperties != null ) {
      dataFactory.baseConnectionProperties = (Properties) this.baseConnectionProperties.clone();
    }
    return dataFactory;
  }

  public String getDesignTimeName() {
    return designTimeName;
  }

  public void setDesignTimeName( final String designTimeName ) {
    this.designTimeName = designTimeName;
  }

  /**
   * Returns all known query-names. A data-factory may accept more than the query-names returned here.
   *
   * @return the known query names.
   */
  public String[] getQueryNames() {
    return EMPTY_QUERYNAMES;
  }

  /**
   * Attempts to cancel the query process that is generating the data for this data factory. If it is not possible to
   * cancel the query, this call should be ignored.
   */
  public void cancelRunningQuery() {
  }

  protected static String quote( final String original ) {
    // This solution needs improvements. Copy blocks instead of single
    // characters.
    final int length = original.length();
    final StringBuffer b = new StringBuffer( length * 12 / 10 );
    b.append( '"' );

    for ( int i = 0; i < length; i++ ) {
      final char c = original.charAt( i );
      if ( c == '"' ) {
        b.append( '"' );
        b.append( '"' );
      } else {
        b.append( c );
      }
    }
    b.append( '"' );
    return b.toString();
  }

  private String filter( final String role ) throws ReportDataFactoryException {
    final Configuration configuration = ClassicEngineBoot.getInstance().getGlobalConfig();
    if ( "true".equals( configuration.getConfigProperty( ROLE_FILTER_ENABLE_CONFIG_KEY ) ) == false ) {
      return role;
    }

    final Iterator staticDenyKeys = configuration.findPropertyKeys( DENY_ROLE_CONFIG_KEY );
    while ( staticDenyKeys.hasNext() ) {
      final String key = (String) staticDenyKeys.next();
      final String value = configuration.getConfigProperty( key );
      if ( ObjectUtilities.equal( value, role ) ) {
        return null;
      }
    }

    final Iterator regExpDenyKeys = configuration.findPropertyKeys( DENY_REGEXP_CONFIG_KEY );
    while ( regExpDenyKeys.hasNext() ) {
      final String key = (String) regExpDenyKeys.next();
      final String value = configuration.getConfigProperty( key );
      try {
        if ( role.matches( value ) ) {
          return null;
        }
      } catch ( PatternSyntaxException pe ) {
        throw new ReportDataFactoryException( "Unable to match reg-exp role filter:", pe );
      }
    }

    boolean hasAccept = false;
    final Iterator staticAcceptKeys = configuration.findPropertyKeys( ACCEPT_ROLES_CONFIG_KEY );
    while ( staticAcceptKeys.hasNext() ) {
      hasAccept = true;
      final String key = (String) staticAcceptKeys.next();
      final String value = configuration.getConfigProperty( key );
      if ( ObjectUtilities.equal( value, role ) ) {
        return role;
      }
    }

    final Iterator regExpAcceptKeys = configuration.findPropertyKeys( ACCEPT_REGEXP_CONFIG_KEY );
    while ( regExpAcceptKeys.hasNext() ) {
      hasAccept = true;
      final String key = (String) regExpAcceptKeys.next();
      final String value = configuration.getConfigProperty( key );
      try {
        if ( role.matches( value ) ) {
          return role;
        }
      } catch ( PatternSyntaxException pe ) {
        throw new ReportDataFactoryException( "Unable to match reg-exp role filter:", pe );
      }
    }
    if ( hasAccept == false ) {
      return role;
    }
    return null;
  }

  protected String translateQuery( final String query ) {
    return query;
  }

  protected String computedQuery( final String queryName, final DataRow parameters ) throws ReportDataFactoryException {
    return queryName;
  }

  public ArrayList<Object> getQueryHash( final String queryRaw, final DataRow parameter )
    throws ReportDataFactoryException {
    final ArrayList<Object> list = new ArrayList<Object>();
    list.add( getClass().getName() );
    list.add( translateQuery( queryRaw ) );
    if ( getCubeFileProvider() != null ) {
      list.add( getCubeFileProvider().getConnectionHash() );
    }
    if ( getDataSourceProvider() != null ) {
      list.add( getDataSourceProvider().getConnectionHash() );
    }
    list.add( getMondrianConnectionProvider().getConnectionHash( computeProperties( parameter ) ) );
    list.add( computeProperties( parameter ) );
    return list;
  }

  public String[] getReferencedFields( final String queryName,
                                       final DataRow parameters ) throws ReportDataFactoryException {
    final boolean isNewConnection = connection == null;
    try {
      if ( connection == null ) {
        connection = mondrianConnectionProvider.createConnection
          ( computeProperties( parameters ), dataSourceProvider.getDataSource() );
      }
    } catch ( SQLException e ) {
      logger.error( e );
      throw new ReportDataFactoryException(
        "Failed to create DataSource (SQL Exception - error code: " + e.getErrorCode() + "):" + e.toString(), e );
    } catch ( MondrianException e ) {
      logger.error( e );
      throw new ReportDataFactoryException( "Failed to create DataSource (Mondrian Exception):" + e.toString(), e );
    }

    try {
      if ( connection == null ) {
        throw new ReportDataFactoryException( "Factory is closed." );
      }
      final LinkedHashSet<String> parameter = new LinkedHashSet<String>();

      final MDXCompiler compiler = new MDXCompiler( parameters, getLocale() );
      final String computedQuery = computedQuery( queryName, parameters );
      final String mdxQuery = compiler.translateAndLookup( computedQuery, parameters );
      parameter.addAll( compiler.getCollectedParameter() );
      // Alternatively, JNDI is possible. Maybe even more ..
      final Query query = connection.parseQuery( mdxQuery );
      final Parameter[] queryParameters = query.getParameters();
      for ( int i = 0; i < queryParameters.length; i++ ) {
        final Parameter queryParameter = queryParameters[ i ];
        parameter.add( queryParameter.getName() );
      }
      if ( jdbcUserField != null ) {
        parameter.add( jdbcUserField );
      }
      if ( roleField != null ) {
        parameter.add( roleField );
      }
      parameter.add( DataFactory.QUERY_LIMIT );
      return parameter.toArray( new String[ parameter.size() ] );
    } catch ( MondrianException e ) {
      throw new ReportDataFactoryException( "Failed to create datasource:" + e.getLocalizedMessage(), e );
    } finally {
      if ( isNewConnection ) {
        close();
      }
    }
  }

  public void initialize( final DataFactoryContext dataFactoryContext ) throws ReportDataFactoryException {
    super.initialize( dataFactoryContext );
    membersOnAxisSorted = "true".equals
      ( dataFactoryContext.getConfiguration()
        .getConfigProperty( MondrianDataFactoryModule.MEMBER_ON_AXIS_SORTED_KEY ) );
  }
}
