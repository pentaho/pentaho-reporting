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

package org.pentaho.reporting.engine.classic.core.testsupport.base;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.ReportPreProcessor;
import org.pentaho.reporting.engine.classic.core.metadata.ReportPreProcessorMetaData;
import org.pentaho.reporting.engine.classic.core.metadata.ReportPreProcessorPropertyMetaData;
import org.pentaho.reporting.engine.classic.core.metadata.ReportPreProcessorRegistry;

import java.util.ArrayList;
import java.util.Locale;

public class PreProcessorTestHelper {
  private static final Log logger = LogFactory.getLog( PreProcessorTestHelper.class );

  private PreProcessorTestHelper() {
  }

  public static boolean validateElementMetaData( Class<? extends ReportPreProcessor> elementType ) {
    ReportPreProcessorMetaData metaData =
        ReportPreProcessorRegistry.getInstance().getReportPreProcessorMetaData( elementType.getName() );

    if ( metaData == null ) {
      logger.warn( "No Metadata defined" );
      return false;
    }

    if ( validateCanInstantiate( metaData ) ) {
      return false;
    }

    final String typeName = metaData.getName();
    logger.debug( "Processing " + typeName );

    ArrayList<String> missingProperties = new ArrayList<String>();
    validateCoreMetaData( metaData, missingProperties );
    validatePropertyMetaData( metaData, missingProperties );

    flushSystemErr();

    for ( String property : missingProperties ) {
      System.out.println( property );
    }

    return missingProperties.isEmpty();
  }

  private static void flushSystemErr() {
    System.err.flush();
    try {
      Thread.sleep( 25 );
    } catch ( InterruptedException e ) {
      // wait for system.error to print. it makes the logging cleaner
    }
  }

  private static boolean validateCanInstantiate( final ReportPreProcessorMetaData metaData ) {
    try {
      final Object type = metaData.create();
      if ( type == null ) {
        return true;
      }
      return false;
    } catch ( InstantiationException e ) {
      e.printStackTrace();
      return true;
    }
  }

  private static void validateCoreMetaData( final ReportPreProcessorMetaData metaData,
      final ArrayList<String> missingProperties ) {
    final Locale locale = Locale.getDefault();
    final String typeName = metaData.getName();

    final String displayName = metaData.getDisplayName( locale );
    if ( isValid( displayName, metaData.getName(), missingProperties ) == false ) {
      logger.warn( "ReportPreProcessorMetaData '" + typeName + ": No valid display name" );
    }
    if ( metaData.isDeprecated() ) {
      final String deprecateMessage = metaData.getDeprecationMessage( locale );
      if ( isValid( deprecateMessage, "Property", missingProperties ) == false ) {
        logger.warn( "ReportPreProcessorMetaData '" + typeName + ": No valid deprecate message" );
      }
    }
    final String grouping = metaData.getGrouping( locale );
    if ( isValid( grouping, "User-Defined", missingProperties ) == false ) {
      logger.warn( "ReportPreProcessorMetaData '" + typeName + ": No valid grouping message" );
    }
  }

  private static void validatePropertyMetaData( final ReportPreProcessorMetaData metaData,
      final ArrayList<String> missingProperties ) {
    final Locale locale = Locale.getDefault();
    final String typeName = metaData.getName();

    final ReportPreProcessorPropertyMetaData[] styleMetaDatas = metaData.getPropertyDescriptions();
    for ( int j = 0; j < styleMetaDatas.length; j++ ) {
      final ReportPreProcessorPropertyMetaData propertyMetaData = styleMetaDatas[j];
      final String propertyDisplayName = propertyMetaData.getDisplayName( locale );
      if ( isValid( propertyDisplayName, propertyMetaData.getName(), missingProperties ) == false ) {
        logger.warn( "ReportPreProcessorPropertyMetaData '" + typeName + ": Property " + propertyMetaData.getName()
            + ": No DisplayName" );
      }

      final String propertyGrouping = propertyMetaData.getGrouping( locale );
      if ( isValid( propertyGrouping, "Required", missingProperties ) == false ) {
        logger.warn( "ReportPreProcessorPropertyMetaData '" + typeName + ": Property " + propertyMetaData.getName()
            + ": Grouping is not valid" );
      }
      if ( propertyMetaData.isDeprecated() ) {
        final String deprecateMessage = propertyMetaData.getDeprecationMessage( locale );
        if ( isValid( deprecateMessage, "Deprecated", missingProperties ) == false ) {
          logger.warn( "ReportPreProcessorPropertyMetaData '" + typeName + ": Property " + propertyMetaData.getName()
              + ": No valid deprecate message" );
        }
      }
    }
  }

  private static boolean isValid( String translation, String displayName, ArrayList<String> missingProperties ) {
    if ( translation == null ) {
      return false;
    }
    if ( translation.length() > 2 && translation.charAt( 0 ) == '!'
        && translation.charAt( translation.length() - 1 ) == '!' ) {
      final String retval = translation.substring( 1, translation.length() - 1 );
      missingProperties.add( retval + "=" + displayName );
      return false;
    }
    return true;
  }

}
