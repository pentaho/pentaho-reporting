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


package org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.DataFactory;
import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.metadata.ElementMetaData;
import org.pentaho.reporting.engine.classic.core.metadata.ElementType;
import org.pentaho.reporting.libraries.base.config.Configuration;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;

/**
 * Todo: Document Me
 *
 * @author Thomas Morgner
 */
public class BundleWriterUtilities {
  private static final Log logger = LogFactory.getLog( BundleWriterUtilities.class );

  private static final String DATA_PREFIX =
      "org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer.data-factory-handler.";
  private static final String ELEMENT_PREFIX =
      "org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer.element-handler.";

  private BundleWriterUtilities() {
  }

  public static BundleDataFactoryWriterHandler lookupWriteHandler( final DataFactory dataFactory ) {
    if ( dataFactory == null ) {
      throw new NullPointerException();
    }

    final String configKey = DATA_PREFIX + dataFactory.getClass().getName();
    final Configuration globalConfig = ClassicEngineBoot.getInstance().getGlobalConfig();
    final String value = globalConfig.getConfigProperty( configKey );
    if ( value != null ) {

      final BundleDataFactoryWriterHandler dataFactoryWriterHandler =
          (BundleDataFactoryWriterHandler) ObjectUtilities.loadAndInstantiate( value, dataFactory.getClass(),
              BundleDataFactoryWriterHandler.class );
      if ( dataFactoryWriterHandler == null ) {
        logger.warn( "Specified data-source write handler implementation could not be loaded: " + value );
      }
      return dataFactoryWriterHandler;
    }
    logger.warn( "No data-source write handler known for: " + value );
    return null;
  }

  public static BundleDataFactoryWriterHandler lookupWriteHandler( final Element element ) {
    if ( element == null ) {
      throw new NullPointerException();
    }

    final ElementType type = element.getElementType();
    if ( type == null ) {
      // A legacy element. Cannot handle that this way.
      return null;
    }

    final ElementMetaData metaData = type.getMetaData();
    final String configKey = ELEMENT_PREFIX + metaData.getName();
    final Configuration globalConfig = ClassicEngineBoot.getInstance().getGlobalConfig();
    final String value = globalConfig.getConfigProperty( configKey );
    if ( value != null ) {
      return (BundleDataFactoryWriterHandler) ObjectUtilities.loadAndInstantiate( value, metaData.getClass(),
          BundleDataFactoryWriterHandler.class );
    }
    return null;
  }
}
