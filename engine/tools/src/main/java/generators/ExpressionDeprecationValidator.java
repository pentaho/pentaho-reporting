/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


package generators;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.metadata.ExpressionMetaData;
import org.pentaho.reporting.engine.classic.core.metadata.ExpressionRegistry;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.Locale;

public class ExpressionDeprecationValidator {
  private static final Log logger = LogFactory.getLog( ExpressionDeprecationValidator.class );

  public static void main( final String[] args ) throws IOException {
    ClassicEngineBoot.getInstance().start();

    final ExpressionRegistry expressionRegistry = ExpressionRegistry.getInstance();
    final ExpressionMetaData[] allExpressions = expressionRegistry.getAllExpressionMetaDatas();
    for ( int i = 0; i < allExpressions.length; i++ ) {
      final ExpressionMetaData expression = allExpressions[ i ];
      if ( expression == null ) {
        logger.warn( "Null Expression encountered" );
        continue;
      }
      if ( isDeprecated( expression ) ) {
        if ( expression.isDeprecated() == false ) {
          logger.warn( "Expression code is deprecated, but metadata is not:" + expression.getExpressionType() );
        }

        if ( "Deprecated Function".equals( expression.getGrouping( Locale.US ) ) == false ) {
          logger.warn( "Expression metadata is not in deprecated group:" + expression.getExpressionType() );
        }
      } else {
        if ( expression.isDeprecated() == true ) {
          logger.warn( "Expression metadata is deprecated, but code is not:" + expression.getExpressionType() );
        }
      }

    }

  }

  private static boolean isDeprecated( final ExpressionMetaData expression ) {

    final Class type = expression.getExpressionType();
    final Annotation annotation = type.getAnnotation( Deprecated.class );
    if (annotation != null) {
      return true;
    }
    return false;
  }

}
