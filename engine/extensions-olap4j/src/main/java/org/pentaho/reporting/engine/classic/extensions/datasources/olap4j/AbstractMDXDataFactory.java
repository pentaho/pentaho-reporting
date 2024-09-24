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

package org.pentaho.reporting.engine.classic.extensions.datasources.olap4j;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.olap4j.CellSet;
import org.olap4j.CellSetAxis;
import org.olap4j.OlapConnection;
import org.olap4j.OlapException;
import org.olap4j.OlapParameterMetaData;
import org.olap4j.OlapStatement;
import org.olap4j.Position;
import org.olap4j.PreparedOlapStatement;
import org.olap4j.metadata.Cube;
import org.olap4j.metadata.Hierarchy;
import org.olap4j.metadata.Member;
import org.olap4j.type.MemberType;
import org.olap4j.type.NumericType;
import org.olap4j.type.SetType;
import org.olap4j.type.StringType;
import org.olap4j.type.Type;
import org.pentaho.reporting.engine.classic.core.AbstractDataFactory;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.DataFactory;
import org.pentaho.reporting.engine.classic.core.DataFactoryContext;
import org.pentaho.reporting.engine.classic.core.DataRow;
import org.pentaho.reporting.engine.classic.core.ReportDataFactoryException;
import org.pentaho.reporting.engine.classic.core.util.PropertyLookupParser;
import org.pentaho.reporting.engine.classic.extensions.datasources.olap4j.connections.OlapConnectionProvider;
import org.pentaho.reporting.libraries.base.config.Configuration;
import org.pentaho.reporting.libraries.base.util.CSVTokenizer;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
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
import java.util.Set;
import java.util.regex.PatternSyntaxException;

public abstract class AbstractMDXDataFactory extends AbstractDataFactory {
  private static final Log logger = LogFactory.getLog( AbstractMDXDataFactory.class );

  /**
   * The message compiler maps all named references into numeric references.
   */
  protected static class MDXCompiler extends PropertyLookupParser {
    private DataRow parameters;
    private Locale locale;
    private HashSet<String> collectedLists;

    /**
     * Default Constructor.
     */
    protected MDXCompiler( final DataRow parameters,
                           final Locale locale ) {
      this.collectedLists = new HashSet<String>();
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
        return null;
      }

      final String parameterName = tokenizer.nextToken();
      final Object o = parameters.get( parameterName );
      collectedLists.add( parameterName );

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
          return "null";
        }
        return quote( String.valueOf( o ) );
      }

      final FastMessageFormat messageFormat = new FastMessageFormat( formatString, locale );
      return messageFormat.format( new Object[] { o } );
    }

    public Set<String> getParameter() {
      //noinspection unchecked
      return Collections.unmodifiableSet( (Set<String>) collectedLists.clone() );
    }

  }

  private static final String[] EMPTY_QUERYNAMES = new String[ 0 ];

  private OlapConnectionProvider connectionProvider;
  private transient OlapConnection connection;

  private String jdbcUserField;
  private String jdbcPasswordField;
  private String roleField;
  private boolean membersOnAxisSorted;

  public AbstractMDXDataFactory( final OlapConnectionProvider connectionProvider ) {
    if ( connectionProvider == null ) {
      throw new NullPointerException();
    }
    this.connectionProvider = connectionProvider;
  }

  public void setConnectionProvider( final OlapConnectionProvider connectionProvider ) {
    if ( connectionProvider == null ) {
      throw new NullPointerException();
    }

    if ( connection != null ) {
      throw new IllegalStateException();
    }
    this.connectionProvider = connectionProvider;
  }

  public OlapConnectionProvider getConnectionProvider() {
    return connectionProvider;
  }

  public boolean isMembersOnAxisSorted() {
    return membersOnAxisSorted;
  }

  public void setMembersOnAxisSorted( final boolean membersOnAxisSorted ) {
    this.membersOnAxisSorted = membersOnAxisSorted;
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

  public String getRoleField() {
    return roleField;
  }

  public void setRoleField( final String roleField ) {
    this.roleField = roleField;
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

  public String[] getQueryNames() {
    return EMPTY_QUERYNAMES;
  }

  protected PreparedOlapStatement getStatement( final String query, final DataRow parameter )
    throws ReportDataFactoryException, OlapException {

    if ( connection == null ) {
      try {
        connection =
          connectionProvider.createConnection( computeJdbcUser( parameter ), computeJdbcPassword( parameter ) );
        connection.setLocale( getLocale() );

        final String role = computeRole( parameter );
        if ( role != null ) {
          connection.setRoleName( role );
        }
      } catch ( final SQLException e ) {
        throw new ReportDataFactoryException( "Failed to obtain a connection", e );
      }
    }

    final MDXCompiler compiler = new MDXCompiler( parameter, getLocale() );
    final String translatedQuery = compiler.translateAndLookup( query, parameter );
    return connection.prepareOlapStatement( translatedQuery );
  }


  private String computeJdbcUser( final DataRow parameters ) {
    if ( jdbcUserField != null ) {
      final Object field = parameters.get( jdbcUserField );
      if ( field != null ) {
        return String.valueOf( field );
      }
    }

    return null;
  }

  private String computeJdbcPassword( final DataRow parameters ) {
    if ( jdbcPasswordField != null ) {
      final Object field = parameters.get( jdbcPasswordField );
      if ( field != null ) {
        return String.valueOf( field );
      }
    }

    return null;
  }

  private String computeRole( final DataRow parameters ) throws ReportDataFactoryException {
    if ( roleField != null ) {
      final Object field = parameters.get( roleField );
      if ( field != null ) {
        if ( field instanceof Object[] ) {
          final Object[] roleArray = (Object[]) field;
          final StringBuilder buffer = new StringBuilder();
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
          final StringBuilder buffer = new StringBuilder();
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

    return null;
  }

  private String quoteRole( final String role ) {
    if ( role.indexOf( ',' ) == -1 ) {
      return role;
    }
    final StringBuilder b = new StringBuilder( role.length() + 5 );
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

  protected QueryResultWrapper performQuery( final String rawMdxQuery, final DataRow parameters )
    throws ReportDataFactoryException, SQLException {
    final PreparedOlapStatement statement = getStatement( rawMdxQuery, parameters );
    final int queryTimeoutValue = calculateQueryTimeOut( parameters );
    if ( queryTimeoutValue > 0 ) {
      statement.setQueryTimeout( queryTimeoutValue );
    }

    parametrizeQuery( parameters, statement );
    return new QueryResultWrapper( statement, statement.executeQuery() );
  }

  private void parametrizeQuery( final DataRow parameters,
                                 final PreparedOlapStatement statement )
    throws SQLException, ReportDataFactoryException {
    final OlapParameterMetaData olapParameterMetaData = statement.getParameterMetaData();
    final int paramCount = olapParameterMetaData.getParameterCount();
    for ( int i = 1; i <= paramCount; i++ ) {
      final String paramName = olapParameterMetaData.getParameterName( i );
      Object parameterValue = parameters.get( paramName );
      final Type parameterType = olapParameterMetaData.getParameterOlapType( i );
      parameterValue = computeParameterValue( statement, parameterType, parameterValue );
      statement.setObject( i, parameterValue );
    }
  }

  private Object computeParameterValue( final PreparedOlapStatement statement,
                                        final Type parameterType,
                                        Object parameterValue ) throws ReportDataFactoryException, SQLException {
    if ( parameterValue == null ) {
      return null;
    }

    if ( parameterType instanceof StringType ) {
      if ( !( parameterValue instanceof String ) ) {
        throw new ReportDataFactoryException( parameterValue + " is incorrect for type " + parameterType );
      }
    }
    if ( parameterType instanceof NumericType ) {
      if ( !( parameterValue instanceof Number ) ) {
        throw new ReportDataFactoryException( parameterValue + " is incorrect for type " + parameterType );
      }
    }

    if ( parameterType instanceof MemberType ) {
      if ( parameterValue instanceof String ) {
        final MemberType type = (MemberType) parameterType;
        final Hierarchy hierarchy = type.getHierarchy();
        final Cube cube = statement.getCube();
        parameterValue = findMember( hierarchy, cube, String.valueOf( parameterValue ) );
      } else if ( !( parameterValue instanceof Member ) ) {
        throw new ReportDataFactoryException( parameterValue + " is incorrect for type " + parameterType );
      }
    }
    if ( parameterType instanceof SetType ) {
      if ( parameterValue instanceof String ) {
        final SetType type = (SetType) parameterType;
        final Hierarchy hierarchy = type.getHierarchy();
        final Cube cube = statement.getCube();

        final String rawString = (String) parameterValue;
        final String[] memberStr = rawString.replaceFirst( "^ *\\{", "" ).replaceFirst( "} *$", "" ).split( "," );
        final List<Member> list = new ArrayList<Member>( memberStr.length );

        for ( int j = 0; j < memberStr.length; j++ ) {
          final String str = memberStr[ j ];
          final Member member = findMember( hierarchy, cube, String.valueOf( str ) );
          list.add( member );
        }

        parameterValue = list;
      } else if ( !( parameterValue instanceof Member ) ) {
        throw new ReportDataFactoryException( parameterValue + " is incorrect for type " + parameterType );
      }
    }

    return parameterValue;
  }

  public String[] getReferencedFields( final String queryName,
                                       final DataRow parameter ) throws ReportDataFactoryException {
    final boolean isNewConnection = connection == null;
    try {
      if ( connection == null ) {
        connection =
          connectionProvider.createConnection( computeJdbcUser( parameter ), computeJdbcPassword( parameter ) );
        connection.setLocale( getLocale() );

        final String role = computeRole( parameter );
        if ( role != null ) {
          connection.setRoleName( role );
        }
      }

      final MDXCompiler compiler = new MDXCompiler( parameter, getLocale() );
      final String value = computedQuery( queryName, parameter );
      final String translatedQuery = compiler.translateAndLookup( value, parameter );
      final LinkedHashSet<String> params = new LinkedHashSet<String>();
      params.addAll( compiler.getParameter() );
      if ( getRoleField() != null ) {
        params.add( getRoleField() );
      }
      if ( getJdbcPasswordField() != null ) {
        params.add( getJdbcPasswordField() );
      }
      if ( getJdbcUserField() != null ) {
        params.add( getJdbcUserField() );
      }
      final PreparedOlapStatement statement = connection.prepareOlapStatement( translatedQuery );

      final OlapParameterMetaData data = statement.getParameterMetaData();
      final int count = data.getParameterCount();
      for ( int i = 0; i < count; i++ ) {
        final String parameterName = data.getParameterName( i + 1 );
        params.add( parameterName );
      }
      params.add( DataFactory.QUERY_LIMIT );
      return params.toArray( new String[ params.size() ] );
    } catch ( final Throwable e ) {
      throw new ReportDataFactoryException( "Failed to obtain a connection", e );
    } finally {
      if ( isNewConnection ) {
        close();
      }
    }
  }


  private Member findMember( final Hierarchy hierarchy,
                             final Cube cube,
                             final String parameter ) throws ReportDataFactoryException, SQLException {
    Member memberById = null;
    Member memberByUniqueId = null;
    final Configuration configuration = getConfiguration();
    final boolean searchForNames = "true".equals( configuration.getConfigProperty
      ( "org.pentaho.reporting.engine.classic.extensions.datasources.olap4j.NeedDimensionPrefix" ) ) == false;
    final boolean missingMembersIsFatal = "true".equals( configuration.getConfigProperty
      ( "org.pentaho.reporting.engine.classic.extensions.datasources.olap4j.IgnoreInvalidMembersDuringQuery" ) )
      == false;

    try {
      final Member directValue = lookupDirectly( hierarchy, cube, parameter, searchForNames );
      if ( directValue != null ) {
        return directValue;
      }
    } catch ( final Exception e ) {
      // It is non fatal if that fails. Invalid input has this effect.
    }

    final OlapStatement statement = connection.createStatement();
    try {
      final CellSet result = statement.executeOlapQuery( "SELECT " + hierarchy.getUniqueName() +
        ".AllMembers ON 0, {} ON 1 FROM " + cube.getUniqueName() );
      try {
        final List<CellSetAxis> setAxises = result.getAxes();
        final List<Position> positionList = setAxises.get( 0 ).getPositions();
        for ( int i = 0; i < positionList.size(); i++ ) {
          final Position position = positionList.get( i );
          final List<Member> memberList = position.getMembers();
          for ( int j = 0; j < memberList.size(); j++ ) {
            final Member member = memberList.get( j );
            if ( parameter.equals( Olap4jUtil.getUniqueMemberName( member ) ) ) {
              if ( memberByUniqueId == null ) {
                memberByUniqueId = member;
              } else {
                logger.warn( "Encountered a member with a duplicate unique key: " + member.getUniqueName() );
              }
            }
            if ( searchForNames == false ) {
              continue;
            }
            if ( parameter.equals( member.getName() ) ) {
              if ( memberById == null ) {
                memberById = member;
              } else {
                logger.warn( "Encountered a member with a duplicate name: " + member.getUniqueName() );
              }
            }
          }
        }
      } finally {
        result.close();
      }
    } finally {
      try {
        statement.close();
      } catch ( final SQLException e ) {
        // ignore 
      }
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
                                 final boolean searchForNames ) throws SQLException {
    Member memberById = null;
    Member memberByUniqueId = null;
    final OlapStatement statement = connection.createStatement();
    try {
      final CellSet result = statement.executeOlapQuery( "SELECT STRTOMEMBER(" + quote( parameter ) +
        ") ON 0, {} ON 1 FROM " + cube.getUniqueName() );
      try {
        final List<CellSetAxis> setAxises = result.getAxes();
        final List<Position> positionList = setAxises.get( 0 ).getPositions();
        for ( int i = 0; i < positionList.size(); i++ ) {
          final Position position = positionList.get( i );
          final List<Member> memberList = position.getMembers();
          for ( int j = 0; j < memberList.size(); j++ ) {
            final Member member = memberList.get( j );
            // If the parameter starts with '[', we'll assume we have the full
            // member specification specification. Otherwise, keep the funky lookup
            // route. We do check whether we get a second member (heck, should not
            // happen, but I've seen pigs fly already).
            if ( parameter.startsWith( "[" ) ) {
              if ( memberByUniqueId == null ) {
                memberByUniqueId = member;
              } else {
                logger.warn( "Encountered a member with a duplicate unique key: " + member.getUniqueName() );
              }
            }
            if ( searchForNames == false ) {
              continue;
            }
            if ( parameter.equals( member.getName() ) ) {
              if ( memberById == null ) {
                memberById = member;
              } else {
                logger.warn( "Encountered a member with a duplicate name: " + member.getUniqueName() );
              }
            }
          }
        }
      } finally {
        result.close();
      }
    } finally {
      try {
        statement.close();
      } catch ( final SQLException e ) {
        // ignore
      }
    }
    if ( memberByUniqueId != null ) {
      final Hierarchy memberHierarchy = memberByUniqueId.getHierarchy();
      if ( hierarchy != memberHierarchy ) {
        if ( ObjectUtilities.equal( hierarchy, memberHierarchy ) == false ) {
          logger.warn( "Cannot match hierarchy of member found with the hierarchy specfied in the parameter: " +
            "Unabe to guarantee that the correct member has been queried, returning null." );
          return null;
        }
      }
      return memberByUniqueId;
    }
    if ( memberById != null ) {
      final Hierarchy memberHierarchy = memberById.getHierarchy();
      if ( hierarchy != memberHierarchy ) {
        if ( ObjectUtilities.equal( hierarchy, memberHierarchy ) == false ) {
          logger.warn( "Cannot match hierarchy of member found with the hierarchy specfied in the parameter: " +
            "Unabe to guarantee that the correct member has been queried, returning null." );
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

  /**
   * Closes the data factory and frees all resources held by this instance.
   */
  public void close() {
    if ( connection != null ) {
      try {
        connection.close();
      } catch ( final SQLException e ) {
        // ignore ..
      }
    }
    connection = null;
  }

  public AbstractMDXDataFactory clone() {
    final AbstractMDXDataFactory dataFactory = (AbstractMDXDataFactory) super.clone();
    dataFactory.connection = null;
    return dataFactory;
  }

  protected static String quote( final String original ) {
    // This solution needs improvements. Copy blocks instead of single
    // characters.
    final int length = original.length();
    final StringBuilder b = new StringBuilder( length * 12 / 10 );
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
    if ( "true".equals( configuration.getConfigProperty
      ( "org.pentaho.reporting.engine.classic.extensions.datasources.olap4j.role-filter.enable" ) ) == false ) {
      return role;
    }

    final Iterator staticDenyKeys = configuration.findPropertyKeys
      ( "org.pentaho.reporting.engine.classic.extensions.datasources.olap4j.role-filter.static.deny" );
    while ( staticDenyKeys.hasNext() ) {
      final String key = (String) staticDenyKeys.next();
      final String value = configuration.getConfigProperty( key );
      if ( ObjectUtilities.equal( value, role ) ) {
        return null;
      }
    }

    final Iterator regExpDenyKeys = configuration.findPropertyKeys
      ( "org.pentaho.reporting.engine.classic.extensions.datasources.olap4j.role-filter.reg-exp.deny" );
    while ( regExpDenyKeys.hasNext() ) {
      final String key = (String) regExpDenyKeys.next();
      final String value = configuration.getConfigProperty( key );
      try {
        if ( role.matches( value ) ) {
          return null;
        }
      } catch ( final PatternSyntaxException pe ) {
        throw new ReportDataFactoryException( "Unable to match reg-exp role filter:", pe );
      }
    }

    boolean hasAccept = false;
    final Iterator staticAcceptKeys = configuration.findPropertyKeys
      ( "org.pentaho.reporting.engine.classic.extensions.datasources.olap4j.role-filter.static.accept" );
    while ( staticAcceptKeys.hasNext() ) {
      hasAccept = true;
      final String key = (String) staticAcceptKeys.next();
      final String value = configuration.getConfigProperty( key );
      if ( ObjectUtilities.equal( value, role ) ) {
        return role;
      }
    }

    final Iterator regExpAcceptKeys = configuration.findPropertyKeys
      ( "org.pentaho.reporting.engine.classic.extensions.datasources.olap4j.role-filter.reg-exp.accept" );
    while ( regExpAcceptKeys.hasNext() ) {
      hasAccept = true;
      final String key = (String) regExpAcceptKeys.next();
      final String value = configuration.getConfigProperty( key );
      try {
        if ( role.matches( value ) ) {
          return role;
        }
      } catch ( final PatternSyntaxException pe ) {
        throw new ReportDataFactoryException( "Unable to match reg-exp role filter:", pe );
      }
    }
    if ( hasAccept == false ) {
      return role;
    }
    return null;
  }

  protected String computedQuery( final String queryName, final DataRow parameters ) throws ReportDataFactoryException {
    return queryName;
  }

  protected String translateQuery( final String queryName ) {
    return queryName;
  }

  public ArrayList<Object> getQueryHash( final String queryRaw, final DataRow parameter )
    throws ReportDataFactoryException {
    final Object connection = getConnectionProvider().getConnectionHash();
    final ArrayList<Object> list = new ArrayList<Object>();
    list.add( getClass().getName() );
    list.add( translateQuery( queryRaw ) );
    list.add( connection );
    return list;
  }

  public void initialize( final DataFactoryContext dataFactoryContext ) throws ReportDataFactoryException {
    super.initialize( dataFactoryContext );
    membersOnAxisSorted = "true".equals
      ( dataFactoryContext.getConfiguration().getConfigProperty( Olap4JDataFactoryModule.MEMBER_ON_AXIS_SORTED_KEY ) );
  }
}
