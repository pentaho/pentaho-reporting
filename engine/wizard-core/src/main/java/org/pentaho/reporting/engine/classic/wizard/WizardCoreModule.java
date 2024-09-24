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

package org.pentaho.reporting.engine.classic.wizard;

import org.pentaho.reporting.engine.classic.core.metadata.ElementMetaDataParser;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.LegacyBundleResourceRegistry;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer.BundleWriterHandlerRegistry;
import org.pentaho.reporting.engine.classic.wizard.parser.WizardSpecifcationXmlFactoryModule;
import org.pentaho.reporting.engine.classic.wizard.parser.WizardspecificationResourceFactory;
import org.pentaho.reporting.engine.classic.wizard.writer.WizardSpecificationWriteHandler;
import org.pentaho.reporting.libraries.base.boot.AbstractModule;
import org.pentaho.reporting.libraries.base.boot.ModuleInitializeException;
import org.pentaho.reporting.libraries.base.boot.SubSystem;

public class WizardCoreModule extends AbstractModule {
  public static final String NAMESPACE =
    "http://reporting.pentaho.org/namespaces/engine/classic/bundle/wizard-specification/1.0";
  public static final String TAG_DEF_PREFIX = "org.pentaho.reporting.engine.classic.wizard.tag-def.";

  public WizardCoreModule() throws ModuleInitializeException {
    loadModuleInfo();
  }

  public void initialize( final SubSystem subSystem ) throws ModuleInitializeException {
    BundleWriterHandlerRegistry.getInstance().registerMasterReportHandler( WizardSpecificationWriteHandler.class );
    BundleWriterHandlerRegistry.getInstance().registerSubReportHandler( WizardSpecificationWriteHandler.class );

    WizardspecificationResourceFactory.register( WizardSpecifcationXmlFactoryModule.class );
    LegacyBundleResourceRegistry.getInstance().register( "wizard-specification.xml" );

    ElementMetaDataParser.initializeOptionalExpressionsMetaData
      ( "org/pentaho/reporting/engine/classic/wizard/wizard-meta-expressions.xml" );

    ElementMetaDataParser.initializeOptionalReportPreProcessorMetaData
      ( "org/pentaho/reporting/engine/classic/wizard/wizard-report-preprocessors.xml" );

  }
}
