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


package org.pentaho.reporting.engine.classic.core.modules.parser.base;

import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.DataFactory;
import org.pentaho.reporting.libraries.base.config.Configuration;
import org.pentaho.reporting.libraries.resourceloader.Resource;
import org.pentaho.reporting.libraries.resourceloader.ResourceKey;
import org.pentaho.reporting.libraries.xmlns.parser.AbstractXmlResourceFactory;
import org.pentaho.reporting.libraries.xmlns.parser.MultiplexRootElementHandler;
import org.pentaho.reporting.libraries.xmlns.parser.RootXmlReadHandler;
import org.pentaho.reporting.libraries.xmlns.parser.XmlFactoryModule;
import org.pentaho.reporting.libraries.xmlns.parser.XmlFactoryModuleRegistry;
import org.xml.sax.XMLReader;

public class DataFactoryXmlResourceFactory extends AbstractXmlResourceFactory {
  private static final XmlFactoryModuleRegistry registry = new XmlFactoryModuleRegistry();

  public static void register( final Class<? extends XmlFactoryModule> readHandler ) {
    registry.register( readHandler );
  }

  public DataFactoryXmlResourceFactory() {
  }

  public void initializeDefaults() {
    super.initializeDefaults();
    final XmlFactoryModule[] registeredHandlers = registry.getRegisteredHandlers();
    for ( int i = 0; i < registeredHandlers.length; i++ ) {
      registerModule( registeredHandlers[i] );
    }
  }

  /**
   * Configures the xml reader. Use this to set features or properties before the documents get parsed.
   *
   * @param handler
   *          the parser implementation that will handle the SAX-Callbacks.
   * @param reader
   *          the xml reader that should be configured.
   */
  protected void configureReader( final XMLReader reader, final MultiplexRootElementHandler handler ) {
    super.configureReader( reader, handler );
    handler.setHelperObject( "property-expansion", Boolean.FALSE );
  }

  protected Configuration getConfiguration() {
    return ClassicEngineBoot.getInstance().getGlobalConfig();
  }

  public Class getFactoryType() {
    return DataFactory.class;
  }

  protected Resource createResource( final ResourceKey targetKey, final RootXmlReadHandler handler,
      final Object createdProduct, final Class createdType ) {
    return new ReportResource( targetKey, handler.getDependencyCollector(), createdProduct, createdType, true );
  }

}
