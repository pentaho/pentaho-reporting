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

import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;

public final class CompatibilityMapperUtil {
  /**
   * When set to "true", expressions that have been removed from the engine are skipped while parsing instead of
   * failing the whole report. Meant for design tools that need to open and migrate old reports.
   */
  public static final String IGNORE_REMOVED_EXPRESSIONS_KEY =
      "org.pentaho.reporting.engine.classic.core.modules.parser.base.IgnoreRemovedExpressions";

  private static CompatiblityMapping mapping = new DefaultCompatibilityMapping();

  private CompatibilityMapperUtil() {
  }

  public static String mapClassName( final String className ) {
    return mapping.mapClassName( className );
  }

  /**
   * Checks whether the expression has been removed from the engine and the parser has been configured to skip such
   * expressions. If this returns false for a removed expression, the parser fails as for any unknown class.
   */
  public static boolean isIgnoredExpression( final String mappedClassName ) {
    if ( !mapping.isRemovedExpression( mappedClassName ) ) {
      return false;
    }
    return "true".equals( ClassicEngineBoot.getInstance().getGlobalConfig()
        .getConfigProperty( IGNORE_REMOVED_EXPRESSIONS_KEY, "false" ) );
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
