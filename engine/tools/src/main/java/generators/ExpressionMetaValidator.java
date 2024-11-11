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


package generators;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.metadata.DefaultExpressionMetaData;
import org.pentaho.reporting.engine.classic.core.metadata.DefaultExpressionPropertyMetaData;
import org.pentaho.reporting.engine.classic.core.metadata.ExpressionMetaData;
import org.pentaho.reporting.engine.classic.core.metadata.ExpressionPropertyMetaData;
import org.pentaho.reporting.engine.classic.core.metadata.ExpressionRegistry;
import org.pentaho.reporting.libraries.base.util.HashNMap;
import org.pentaho.reporting.libraries.base.util.StringUtils;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

public class ExpressionMetaValidator {

  private static final Log logger = LogFactory.getLog( ExpressionMetaValidator.class );
  private static final ArrayList missingProperties = new ArrayList();
  private static final ArrayList missingPropertyDefs = new ArrayList();

  public static void main( String[] args ) {
    ClassicEngineBoot.getInstance().start();
    int invalidExpressionsCounter = 0;
    int deprecatedExpressionsCounter = 0;
    final HashNMap expressionsByGroup = new HashNMap();

    ExpressionRegistry expressionRegistry = ExpressionRegistry.getInstance();
    final ExpressionMetaData[] allExpressions = expressionRegistry.getAllExpressionMetaDatas();
    for ( int i = 0; i < allExpressions.length; i++ ) {
      final ExpressionMetaData expression = allExpressions[ i ];
      if ( expression == null ) {
        logger.warn( "Null Expression encountered" );
        continue;
      }

      missingProperties.clear();

      final Class type = expression.getExpressionType();
      if ( type == null ) {
        logger.warn( "Expression class is null" );
      }

      logger.debug( "Processing " + type );

      final Class resultType = expression.getResultType();
      if ( resultType == null ) {
        logger.warn( "Expression '" + expression.getExpressionType() + " is null" );
      }

      try {
        final BeanInfo beanInfo = expression.getBeanDescriptor();
        if ( beanInfo == null ) {
          logger.warn( "Expression '" + expression.getExpressionType() + ": Cannot get BeanDescriptor: Null" );
        }
      } catch ( IntrospectionException e ) {
        logger.warn( "Expression '" + expression.getExpressionType() + ": Cannot get BeanDescriptor", e );
      }

      final Locale locale = Locale.getDefault();
      final String displayName = expression.getDisplayName( locale );
      if ( isValid( displayName, expression.getName() ) == false ) {
        logger.warn( "Expression '" + expression.getExpressionType() + ": No valid display name" );
      }
      if ( expression.isDeprecated() ) {
        deprecatedExpressionsCounter += 1;
        final String deprecateMessage = expression.getDeprecationMessage( locale );
        if ( isValid( deprecateMessage, "Use a Formula instead" ) == false ) {
          logger.warn( "Expression '" + expression.getExpressionType() + ": No valid deprecate message" );
        }
      }
      final String grouping = expression.getGrouping( locale );
      if ( isValid( grouping, "Group" ) == false ) {
        logger.warn( "Expression '" + expression.getExpressionType() + ": No valid grouping message" );
      }

      expressionsByGroup.add( grouping, expression );

      final ExpressionPropertyMetaData[] properties = expression.getPropertyDescriptions();
      for ( int j = 0; j < properties.length; j++ ) {
        final ExpressionPropertyMetaData propertyMetaData = properties[ j ];
        final String name = propertyMetaData.getName();
        if ( StringUtils.isEmpty( name ) ) {
          logger.warn( "Expression '" + expression.getExpressionType() + ": Property without a name" );
        }
        final String propertyDisplayName = propertyMetaData.getDisplayName( locale );
        if ( isValid( propertyDisplayName, name ) == false ) {
          logger.warn( "Expression '" + expression.getExpressionType() + ": Property " + propertyMetaData.getName()
            + ": No DisplayName" );
        }

        final String propertyGrouping = propertyMetaData.getGrouping( locale );
        if ( isValid( propertyGrouping, "Group" ) == false ) {
          logger.warn( "Expression '" + expression.getExpressionType() + ": Property " + propertyMetaData.getName()
            + ": Grouping is not valid" );
        }
        final int groupingOrdinal = propertyMetaData.getGroupingOrdinal( locale );
        if ( groupingOrdinal == Integer.MAX_VALUE ) {
          if ( propertyMetaData instanceof DefaultExpressionMetaData ) {
            final DefaultExpressionPropertyMetaData demd = (DefaultExpressionPropertyMetaData) propertyMetaData;
            missingProperties.add( demd.getKeyPrefix() + "grouping.ordinal=1000" );
          }
          logger.warn( "Expression '" + expression.getExpressionType() + ": Property " + propertyMetaData.getName()
            + ": Grouping ordinal is not valid" );
        }
        final int ordinal = propertyMetaData.getItemOrdinal( locale );
        if ( groupingOrdinal == Integer.MAX_VALUE ) {
          if ( propertyMetaData instanceof DefaultExpressionMetaData ) {
            final DefaultExpressionPropertyMetaData demd = (DefaultExpressionPropertyMetaData) propertyMetaData;
            missingProperties.add( demd.getKeyPrefix() + "ordinal=1000" );
          }
          logger.warn( "Expression '" + expression.getExpressionType() + ": Property " + propertyMetaData.getName()
            + ": Ordinal is not valid" );
        }
        final String propertyDescription = propertyMetaData.getDescription( locale );
        if ( isValid( propertyDescription, "" ) == false ) {
          logger.warn( "Expression '" + expression.getExpressionType() + ": Property " + propertyMetaData.getName()
            + ": Description is not valid" );
        }
        final String propertyDeprecated = propertyMetaData.getDeprecationMessage( locale );
        if ( isValid( propertyDeprecated, "" ) == false ) {
          logger.warn( "Expression '" + expression.getExpressionType() + ": Property " + propertyMetaData.getName()
            + ": Deprecation is not valid" );
        }

        final String role = propertyMetaData.getPropertyRole();
        if ( isValid( role, "Value" ) == false ) {
          logger.warn( "Expression '" + expression.getExpressionType() + ": Property " + propertyMetaData.getName()
            + ": Role is not valid" );
        }
        final Class propertyType = propertyMetaData.getPropertyType();
        if ( propertyType == null ) {
          logger.warn( "Expression '" + expression.getExpressionType() + ": Property " + propertyMetaData.getName()
            + ": Property Type is not valid" );
        }

        // should not crash!
        final PropertyDescriptor propertyDescriptor = propertyMetaData.getBeanDescriptor();

        if ( propertyMetaData.isDeprecated() ) {
          final String deprecateMessage = propertyMetaData.getDeprecationMessage( locale );
          if ( isValid( deprecateMessage, "Deprecated" ) == false ) {
            logger.warn( "Expression '" + expression.getExpressionType() + ": Property " + propertyMetaData.getName()
              + ": No valid deprecate message" );
          }
        }

      }

      try {
        final BeanInfo beanInfo = Introspector.getBeanInfo( expression.getExpressionType() );
        final PropertyDescriptor[] descriptors = beanInfo.getPropertyDescriptors();
        for ( int propIdx = 0; propIdx < descriptors.length; propIdx++ ) {
          final PropertyDescriptor descriptor = descriptors[ propIdx ];
          final String key = descriptor.getName();

          if ( "runtime".equals( key ) ) {
            continue;
          }
          if ( "active".equals( key ) ) {
            continue;
          }
          if ( "preserve".equals( key ) ) {
            continue;
          }

          if ( descriptor.getReadMethod() == null || descriptor.getWriteMethod() == null ) {
            continue;
          }

          if ( expression.getPropertyDescription( key ) == null ) {
            logger.warn( "Expression '" + expression.getExpressionType() + ": No property definition for " + key );
            missingPropertyDefs.add( "    <property name=\"" + key
              + "\" mandatory=\"false\" preferred=\"false\" value-role=\"Value\" expert=\"false\" hidden=\"false\"/>" );
          }
        }

      } catch ( Throwable e ) {
        logger.warn( "Expression '" + expression.getExpressionType() + ": Cannot get BeanDescriptor", e );
      }

      System.err.flush();
      try {
        Thread.sleep( 25 );
      } catch ( InterruptedException e ) {
      }

      for ( int x = 0; x < missingProperties.size(); x++ ) {
        final String property = (String) missingProperties.get( x );
        System.out.println( property );
      }

      for ( int j = 0; j < missingPropertyDefs.size(); j++ ) {
        final String def = (String) missingPropertyDefs.get( j );
        System.out.println( def );
      }

      if ( missingProperties.isEmpty() == false || missingPropertyDefs.isEmpty() == false ) {
        invalidExpressionsCounter += 1;
        missingProperties.clear();
        missingPropertyDefs.clear();
      }
      System.out.flush();
      try {
        Thread.sleep( 25 );
      } catch ( InterruptedException e ) {
      }
    }

    logger.info( "Validated " + allExpressions.length + " expressions. Invalid: " +
      invalidExpressionsCounter + " Deprecated: " + deprecatedExpressionsCounter );

    final Object[] keys = expressionsByGroup.keySet().toArray();
    Arrays.sort( keys );
    for ( int i = 0; i < keys.length; i++ ) {
      final Object key = keys[ i ];
      logger.info( "Group: '" + key + "' Size: " + expressionsByGroup.getValueCount( key ) );
      final Object[] objects = expressionsByGroup.toArray( key );
      for ( int j = 0; j < objects.length; j++ ) {
        ExpressionMetaData metaData = (ExpressionMetaData) objects[ j ];
        logger.info( "   " + metaData.getExpressionType() );

      }
    }
  }

  private static boolean isValid( String translation, String displayName ) {
    if ( translation == null ) {
      return false;
    }
    if ( translation.length() > 2 &&
      translation.charAt( 0 ) == '!' &&
      translation.charAt( translation.length() - 1 ) == '!' ) {
      final String retval = translation.substring( 1, translation.length() - 1 );
      missingProperties.add( retval + "=" + displayName );
      return false;
    }
    return true;
  }

}
