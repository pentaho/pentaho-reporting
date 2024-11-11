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


package org.pentaho.reporting.engine.classic.core.modules.parser.data.sequence;

import org.pentaho.reporting.engine.classic.core.modules.parser.base.DataFactoryReadHandlerFactory;
import org.pentaho.reporting.engine.classic.core.modules.parser.base.DataFactoryXmlResourceFactory;
import org.pentaho.reporting.libraries.base.boot.AbstractModule;
import org.pentaho.reporting.libraries.base.boot.ModuleInitializeException;
import org.pentaho.reporting.libraries.base.boot.SubSystem;

public class SequenceDataFactoryModule extends AbstractModule {
  public static final String NAMESPACE = "http://reporting.pentaho.org/namespaces/datasources/sequence/1.0";
  public static final String TAG_DEF_PREFIX =
      "org.pentaho.reporting.engine.classic.core.modules.parser.data.sequence.tag-def.";

  public SequenceDataFactoryModule() throws ModuleInitializeException {
    loadModuleInfo();
  }

  public void initialize( final SubSystem subSystem ) throws ModuleInitializeException {
    DataFactoryXmlResourceFactory.register( SequenceResourceXmlFactoryModule.class );

    DataFactoryReadHandlerFactory.getInstance().setElementHandler( NAMESPACE, "sequence-datasource",
        SequenceDataSourceReadHandler.class );
  }
}
