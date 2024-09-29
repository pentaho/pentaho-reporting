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


package org.pentaho.reporting.engine.classic.core.modules.parser.base.compat;

public final class CompatibilityMapperUtil {
  private static CompatiblityMapping mapping = new DefaultCompatibilityMapping();

  private CompatibilityMapperUtil() {
  }

  public static String mapClassName( final String className ) {
    return mapping.mapClassName( className );
  }

  public static String mapConfigurationKey( final String key ) {
    return mapping.mapConfigurationKey( key );
  }

  public static String mapConfigurationValue( final String key, final String mappedKey, final String value ) {
    return mapping.mapConfigurationValue( key, mappedKey, value );
  }

  public static String mapExpressionProperty( final String expressionName, final String mappedExpression,
      final String propertyName ) {
    return mapping.mapExpressionProperty( expressionName, mappedExpression, propertyName );
  }
}
