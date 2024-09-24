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
import org.junit.Before;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.metadata.MetaData;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@SuppressWarnings( { "HardCodedStringLiteral", "EmptyCatchBlock" } )
public class MetaDataValidationTestBase<M extends MetaData> {
  private final ArrayList<String> missingProperties;
  protected final Log logger = LogFactory.getLog( getClass() );

  public MetaDataValidationTestBase() {
    missingProperties = new ArrayList<String>();
  }

  public ArrayList<String> getMissingProperties() {
    return missingProperties;
  }

  @Before
  public void setUp() throws Exception {
    ClassicEngineBoot.getInstance().start();
  }

  public void clear() {
    missingProperties.clear();
  }

  public List<M> performTest( final M[] testData ) {
    final ArrayList<M> retval = new ArrayList<M>();

    for ( final M metaData : testData ) {
      if ( metaData == null ) {
        logger.warn( "Null encountered" );
        continue;
      }

      clear();

      performTestOnElement( metaData );

      System.err.flush();
      try {
        Thread.sleep( 25 );
      } catch ( final InterruptedException e ) {
      }

      ArrayList<String> missingProperties = getMissingProperties();
      for ( int x = 0; x < missingProperties.size(); x++ ) {
        final String property = missingProperties.get( x );
        System.out.println( property );
      }

      if ( missingProperties.isEmpty() == false ) {
        retval.add( metaData );
        missingProperties.clear();
      }
      System.out.flush();
      try {
        Thread.sleep( 25 );
      } catch ( final InterruptedException e ) {
      }
    }

    return retval;

  }

  protected void performTestOnElement( final M metaData ) {
  }

  protected void validate( final MetaData metaData ) {
    final String typeName = metaData.getName();

    final Locale locale = Locale.getDefault();
    final String displayName = metaData.getDisplayName( locale );
    if ( isValid( displayName ) == false ) {
      logger.warn( "MetaData '" + metaData.getClass().getSimpleName() + ":" + typeName + ": No valid display name" );
    }
    if ( metaData.isDeprecated() ) {
      final String deprecateMessage = metaData.getDeprecationMessage( locale );
      if ( isValid( deprecateMessage ) == false ) {
        logger.warn( "MetaData '" + metaData.getClass().getSimpleName() + ":" + typeName
            + ": No valid deprecate message" );
      }
    }
    final String grouping = metaData.getGrouping( locale );
    if ( isValid( grouping ) == false ) {
      logger.warn( "MetaData '" + metaData.getClass().getSimpleName() + ":" + typeName + ": No valid grouping message" );
    }

    final String desc = metaData.getDescription( locale );
    if ( isValid( desc ) == false ) {
      logger.warn( "MetaData '" + metaData.getClass().getSimpleName() + ":" + typeName
          + ": No valid description message" );
    }

    final String ordinal = metaData.getMetaAttribute( "ordinal", locale );
    if ( isValid( ordinal ) == false ) {
      logger.warn( "MetaData '" + metaData.getClass().getSimpleName() + ":" + typeName + ": No valid ordinal message" );
    }

    final String groupingOrdinal = metaData.getMetaAttribute( "grouping.ordinal", locale );
    if ( isValid( groupingOrdinal ) == false ) {
      logger.warn( "MetaData '" + metaData.getClass().getSimpleName() + ":" + typeName
          + ": No valid grouping-ordinal message" );
    }
  }

  protected boolean isValid( final String translation ) {
    if ( translation == null ) {
      return false;
    }
    if ( translation.length() > 2 && translation.charAt( 0 ) == '!'
        && translation.charAt( translation.length() - 1 ) == '!' ) {
      final String retval = translation.substring( 1, translation.length() - 1 );
      missingProperties.add( retval );
      return false;
    }
    return true;
  }

}
