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


package org.pentaho.reporting.engine.classic.extensions.datasources.xpath;

import org.pentaho.reporting.engine.classic.core.metadata.ElementMetaDataParser;
import org.pentaho.reporting.engine.classic.core.modules.parser.base.DataFactoryReadHandlerFactory;
import org.pentaho.reporting.engine.classic.core.modules.parser.base.DataFactoryXmlResourceFactory;
import org.pentaho.reporting.engine.classic.extensions.datasources.xpath.parser.XPathDataSourceReadHandler;
import org.pentaho.reporting.engine.classic.extensions.datasources.xpath.parser.XPathDataSourceXmlFactoryModule;
import org.pentaho.reporting.libraries.base.boot.AbstractModule;
import org.pentaho.reporting.libraries.base.boot.ModuleInitializeException;
import org.pentaho.reporting.libraries.base.boot.SubSystem;

public class XPathDataFactoryModule extends AbstractModule {
  public static final String NAMESPACE = "http://reporting.pentaho.org/namespaces/datasources/xpath";
  public static final String TAG_DEF_PREFIX =
    "org.pentaho.reporting.engine.classic.extensions.datasources.xpath.tag-def.";

  public XPathDataFactoryModule() throws ModuleInitializeException {
    loadModuleInfo();
  }

  public void initialize( final SubSystem subSystem )
    throws ModuleInitializeException {
    DataFactoryXmlResourceFactory.register( XPathDataSourceXmlFactoryModule.class );

    DataFactoryReadHandlerFactory.getInstance()
      .setElementHandler( NAMESPACE, "xpath-datasource", XPathDataSourceReadHandler.class );

    ElementMetaDataParser.initializeOptionalDataFactoryMetaData
      ( "org/pentaho/reporting/engine/classic/extensions/datasources/xpath/meta-datafactory.xml" );

  }
}
