/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2028-08-13
 ******************************************************************************/


package org.pentaho.reporting.engine.classic.core.modules.parser.bundle.data;

import org.pentaho.reporting.engine.classic.core.DataFactory;
import org.pentaho.reporting.engine.classic.core.ReportDataFactoryException;
import org.pentaho.reporting.engine.classic.core.function.Expression;
import org.pentaho.reporting.engine.classic.core.parameters.ReportParameterDefinition;

/**
 * A result object for the XML parser. It contains all parameter definitions as well as all the data-source definitions
 * and once the parsing finishs, it is merged with the current report definition.
 *
 * @author Thomas Morgner
 */
public class DataDefinition {
  private static final Expression[] EMPTY_EXPRESSIONS = new Expression[0];

  private ReportParameterDefinition parameterDefinition;
  private DataFactory primaryDataFactory;
  private Expression[] expressions;
  private String query;
  private int queryLimit;
  private int queryTimeout;

  public DataDefinition( final ReportParameterDefinition parameterDefinition, final DataFactory primaryDataFactory,
      final String query, final int queryLimit, final int queryTimeout, final Expression[] expressions ) {
    this.parameterDefinition = parameterDefinition;
    this.primaryDataFactory = primaryDataFactory;
    this.query = query;
    this.queryLimit = queryLimit;
    this.queryTimeout = queryTimeout;
    if ( expressions != null ) {
      this.expressions = (Expression[]) expressions.clone();
    }
  }

  public String getQuery() {
    return query;
  }

  public int getQueryLimit() {
    return queryLimit;
  }

  public int getQueryTimeout() {
    return queryTimeout;
  }

  public Expression[] getExpressions() {
    if ( expressions == null ) {
      return EMPTY_EXPRESSIONS;
    }

    final Expression[] targetExpressions = new Expression[expressions.length];
    for ( int i = 0; i < expressions.length; i++ ) {
      final Expression expression = expressions[i];
      targetExpressions[i] = expression.getInstance();
    }
    return targetExpressions;
  }

  public ReportParameterDefinition getParameterDefinition() {
    if ( parameterDefinition != null ) {
      return (ReportParameterDefinition) parameterDefinition.clone();
    }
    return null;
  }

  public DataFactory getDataFactory() throws ReportDataFactoryException {
    if ( primaryDataFactory != null ) {
      return primaryDataFactory.derive();
    }
    return null;
  }
}
