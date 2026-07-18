/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 - 2026 by Pentaho Canada Inc. : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2030-06-15
 ******************************************************************************/



package org.pentaho.reporting.engine.classic.core.modules.parser.base.compat;

public interface CompatiblityMapping {
  public String mapClassName( String className );

  public String mapConfigurationKey( String key );

  public String mapConfigurationValue( String key, String mappedKey, String value );

  public String mapExpressionProperty( final String expressionName, final String mappedExpression,
      final String propertyName );

}
