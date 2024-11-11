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
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.libraries.base.config.Configuration;
import org.pentaho.reporting.libraries.resourceloader.Resource;
import org.pentaho.reporting.libraries.resourceloader.ResourceCreationException;
import org.pentaho.reporting.libraries.resourceloader.ResourceData;
import org.pentaho.reporting.libraries.resourceloader.ResourceKey;
import org.pentaho.reporting.libraries.resourceloader.ResourceLoadingException;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;
import org.pentaho.reporting.libraries.xmlns.parser.AbstractXmlResourceFactory;
import org.pentaho.reporting.libraries.xmlns.parser.RootXmlReadHandler;
import org.pentaho.reporting.libraries.xmlns.parser.XmlFactoryModule;
import org.pentaho.reporting.libraries.xmlns.parser.XmlFactoryModuleRegistry;

public class MasterReportXmlResourceFactory extends AbstractXmlResourceFactory {
  private static final XmlFactoryModuleRegistry registry = new XmlFactoryModuleRegistry();

  public static void register( final Class<? extends XmlFactoryModule> readHandler ) {
    registry.register( readHandler );
  }

  public MasterReportXmlResourceFactory() {
  }

  public void initializeDefaults() {
    super.initializeDefaults();
    final XmlFactoryModule[] registeredHandlers = registry.getRegisteredHandlers();
    for ( int i = 0; i < registeredHandlers.length; i++ ) {
      registerModule( registeredHandlers[i] );
    }
  }

  protected Configuration getConfiguration() {
    return ClassicEngineBoot.getInstance().getGlobalConfig();
  }

  public Class getFactoryType() {
    return MasterReport.class;
  }

  protected Object finishResult( final Object res, final ResourceManager manager, final ResourceData data,
      final ResourceKey context ) throws ResourceCreationException, ResourceLoadingException {
    final MasterReport report = (MasterReport) res;
    if ( report == null ) {
      throw new ResourceCreationException( "Report has not been parsed." );
    }

    if ( context != null ) {
      report.setContentBase( context );
    } else {
      report.setContentBase( data.getKey() );
    }
    report.setDefinitionSource( data.getKey() );
    report.setResourceManager( manager );
    report.updateLegacyConfiguration();
    return report;

  }

  protected Resource createResource( final ResourceKey targetKey, final RootXmlReadHandler handler,
      final Object createdProduct, final Class createdType ) {
    if ( ReportParserUtil.INCLUDE_PARSING_VALUE
        .equals( handler.getHelperObject( ReportParserUtil.INCLUDE_PARSING_KEY ) ) ) {
      return new ReportResource( targetKey, handler.getDependencyCollector(), createdProduct, createdType, false );
    }
    return new ReportResource( targetKey, handler.getDependencyCollector(), createdProduct, createdType, true );

  }

}
