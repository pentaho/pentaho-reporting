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


package org.pentaho.reporting.engine.classic.core.metadata;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.libraries.base.boot.ModuleInitializeException;
import org.pentaho.reporting.libraries.base.config.Configuration;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;

import java.net.URL;
import java.util.Iterator;

public class ElementMetaDataParser {
  public static final String GLOBAL_INCLUDES_PREFIX =
      "org.pentaho.reporting.engine.classic.core.metadata.global-includes.";

  private static final Log logger = LogFactory.getLog( ElementMetaDataParser.class );

  private ElementMetaDataParser() {
  }

  public static void registerNamespaces() {
    final String namespaceRegistryPrefix = "org.pentaho.reporting.engine.classic.core.metadata.namespaces.";
    final Configuration configuration = ClassicEngineBoot.getInstance().getGlobalConfig();
    final Iterator<String> keys = configuration.findPropertyKeys( namespaceRegistryPrefix );
    while ( keys.hasNext() ) {
      final String key = keys.next();
      final String prefix = key.substring( namespaceRegistryPrefix.length() );
      final String namespaceUri = configuration.getConfigProperty( key );
      if ( prefix.length() == 0 || namespaceUri == null || namespaceUri.length() == 0 ) {
        continue;
      }
      ElementTypeRegistry.getInstance().registerNamespacePrefix( namespaceUri, prefix );
    }
  }

  public static void initializeOptionalReportPreProcessorMetaData( final String source )
    throws ModuleInitializeException {
    final URL reportPreProcessorMetaSource = ObjectUtilities.getResource( source, ElementMetaDataParser.class );
    if ( reportPreProcessorMetaSource == null ) {
      throw new ModuleInitializeException(
          "Error: Could not find the core report-preprocessor meta-data description file: " + source );
    }
    try {
      ReportPreProcessorRegistry.getInstance().registerFromXml( reportPreProcessorMetaSource );
    } catch ( Exception e ) {
      logger.debug( "Failed:", e );
      throw new ModuleInitializeException( "Error: Could not parse the report-preprocessor meta-data description file",
          e );
    }
  }

  public static void initializeOptionalReportProcessTaskMetaData( final String source )
    throws ModuleInitializeException {
    final URL reportPreProcessorMetaSource = ObjectUtilities.getResource( source, ElementMetaDataParser.class );
    if ( reportPreProcessorMetaSource == null ) {
      throw new ModuleInitializeException(
          "Error: Could not find the core report-process-task meta-data description file: " + source );
    }
    try {
      ReportProcessTaskRegistry.getInstance().registerFromXml( reportPreProcessorMetaSource );
    } catch ( Exception e ) {
      logger.debug( "Failed:", e );
      throw new ModuleInitializeException( "Error: Could not parse the report-process-task meta-data description file",
          e );
    }
  }

  public static void initializeOptionalExpressionsMetaData( final String source ) throws ModuleInitializeException {
    final URL expressionMetaSource = ObjectUtilities.getResource( source, ElementMetaDataParser.class );
    if ( expressionMetaSource == null ) {
      throw new ModuleInitializeException( "Error: Could not find the expression meta-data description file: " + source );
    }
    try {
      ExpressionRegistry.getInstance().registerFromXml( expressionMetaSource );
    } catch ( Exception e ) {
      logger.debug( "Failed:", e );
      throw new ModuleInitializeException( "Error: Could not parse the expression meta-data description file", e );
    }
  }

  public static void initializeOptionalElementMetaData( final String source ) throws ModuleInitializeException {
    final URL metaDataSource = ObjectUtilities.getResource( source, ElementMetaDataParser.class );
    if ( metaDataSource == null ) {
      throw new ModuleInitializeException( "Error: Could not find the optional element meta-data description file: "
          + source );
    }
    try {
      ElementTypeRegistry.getInstance().registerFromXml( metaDataSource );
    } catch ( Exception e ) {
      logger.debug( "Failed:", e );
      throw new ModuleInitializeException( "Error: Could not parse the element meta-data description file", e );
    }

  }

  public static void initializeOptionalDataFactoryMetaData( final String source ) throws ModuleInitializeException {
    final URL expressionMetaSource = ObjectUtilities.getResource( source, ElementMetaDataParser.class );
    if ( expressionMetaSource == null ) {
      throw new ModuleInitializeException( "Error: Could not find the datafactory meta-data description file" );
    }
    try {
      DataFactoryRegistry.getInstance().registerFromXml( expressionMetaSource );
    } catch ( Exception e ) {
      logger.debug( "Failed:", e );
      throw new ModuleInitializeException( "Error: Could not parse the datafactory meta-data description file", e );
    }
  }
}
