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
import org.pentaho.reporting.engine.classic.core.layout.model.RenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderNode;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderableText;
import org.pentaho.reporting.engine.classic.core.layout.model.SpacerRenderNode;
import org.pentaho.reporting.engine.classic.core.metadata.AttributeMetaData;
import org.pentaho.reporting.engine.classic.core.metadata.ElementMetaData;
import org.pentaho.reporting.engine.classic.core.metadata.ElementType;
import org.pentaho.reporting.engine.classic.core.metadata.StyleMetaData;

import java.util.ArrayList;
import java.util.Locale;

@SuppressWarnings( "HardCodedStringLiteral" )
public class ElementTestHelper {
  private static final Log logger = LogFactory.getLog( ElementTestHelper.class );

  private ElementTestHelper() {
  }

  public static boolean validateElementMetaData( ElementType elementType ) {
    ElementMetaData metaData = elementType.getMetaData();
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
    validateStyleMetaData( metaData, missingProperties );
    validateAttributeMetaData( metaData, missingProperties );

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

  private static boolean validateCanInstantiate( final ElementMetaData metaData ) {
    try {
      // noinspection UnusedDeclaration
      final Object type = metaData.create();
    } catch ( InstantiationException e ) {
      logger.warn( "Failed to instantiate ElementType" );
      return true;
    }
    return false;
  }

  private static void validateCoreMetaData( final ElementMetaData metaData, final ArrayList<String> missingProperties ) {
    final Locale locale = Locale.getDefault();
    final String typeName = metaData.getName();

    final String displayName = metaData.getDisplayName( locale );
    if ( isValid( displayName, metaData.getName(), missingProperties ) == false ) {
      logger.warn( "ElementType '" + typeName + ": No valid display name" );
    }
    if ( metaData.isDeprecated() ) {
      final String deprecateMessage = metaData.getDeprecationMessage( locale );
      if ( isValid( deprecateMessage, "Deprecated", missingProperties ) == false ) {
        logger.warn( "ElementType '" + typeName + ": No valid deprecate message" );
      }
    }
    final String grouping = metaData.getGrouping( locale );
    if ( isValid( grouping, "common", missingProperties ) == false ) {
      logger.warn( "ElementType '" + typeName + ": No valid grouping message" );
    }
  }

  private static void validateAttributeMetaData( final ElementMetaData metaData,
      final ArrayList<String> missingProperties ) {
    final Locale locale = Locale.getDefault();
    final String typeName = metaData.getName();

    final AttributeMetaData[] attributeMetaDatas = metaData.getAttributeDescriptions();
    for ( int j = 0; j < attributeMetaDatas.length; j++ ) {
      final AttributeMetaData propertyMetaData = attributeMetaDatas[j];
      final String propertyDisplayName = propertyMetaData.getDisplayName( locale );
      if ( isValid( propertyDisplayName, propertyMetaData.getName(), missingProperties ) == false ) {
        logger.warn( "ElementType '" + typeName + ": Attr " + propertyMetaData.getName() + ": No DisplayName" );
      }

      final String propertyGrouping = propertyMetaData.getGrouping( locale );
      if ( isValid( propertyGrouping, "common", missingProperties ) == false ) {
        logger.warn( "ElementType '" + typeName + ": Attr " + propertyMetaData.getName() + ": Grouping is not valid" );
      }
      if ( propertyMetaData.isDeprecated() ) {
        final String deprecateMessage = propertyMetaData.getDeprecationMessage( locale );
        if ( isValid( deprecateMessage, "Deprecated", missingProperties ) == false ) {
          logger.warn( "ElementType '" + typeName + ": Attr " + propertyMetaData.getName()
              + ": No valid deprecate message" );
        }
      }
    }
  }

  private static void validateStyleMetaData( final ElementMetaData metaData, final ArrayList<String> missingProperties ) {
    final Locale locale = Locale.getDefault();
    final String typeName = metaData.getName();

    final StyleMetaData[] styleMetaDatas = metaData.getStyleDescriptions();
    for ( int j = 0; j < styleMetaDatas.length; j++ ) {
      final StyleMetaData propertyMetaData = styleMetaDatas[j];
      final String propertyDisplayName = propertyMetaData.getDisplayName( locale );
      if ( isValid( propertyDisplayName, propertyMetaData.getName(), missingProperties ) == false ) {
        logger.warn( "ElementType '" + typeName + ": Style " + propertyMetaData.getName() + ": No DisplayName" );
      }

      final String propertyGrouping = propertyMetaData.getGrouping( locale );
      if ( isValid( propertyGrouping, "common", missingProperties ) == false ) {
        logger.warn( "ElementType '" + typeName + ": Style " + propertyMetaData.getName() + ": Grouping is not valid" );
      }
      if ( propertyMetaData.isDeprecated() ) {
        final String deprecateMessage = propertyMetaData.getDeprecationMessage( locale );
        if ( isValid( deprecateMessage, "Deprecated", missingProperties ) == false ) {
          logger.warn( "ElementType '" + typeName + ": Style " + propertyMetaData.getName()
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

  public static String computePrintedText( RenderBox renderBox ) {
    StringBuilder b = new StringBuilder();
    RenderNode lineChild = renderBox.getFirstChild();

    while ( lineChild != null ) {
      if ( lineChild instanceof RenderableText ) {
        RenderableText text = (RenderableText) lineChild;
        b.append( text.getRawText() );
      } else if ( lineChild instanceof SpacerRenderNode ) {
        SpacerRenderNode spacer = (SpacerRenderNode) lineChild;
        for ( int i = 0; i < spacer.getSpaceCount(); i += 1 ) {
          b.append( ' ' );
        }
      } else if ( lineChild instanceof RenderBox ) {
        b.append( computePrintedText( (RenderBox) lineChild ) );
      }

      lineChild = lineChild.getNext();
    }
    return b.toString();
  }

}
