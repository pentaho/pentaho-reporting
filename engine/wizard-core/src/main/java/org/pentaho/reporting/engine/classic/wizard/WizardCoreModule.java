/*!
* This program is free software; you can redistribute it and/or modify it under the
* terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
* Foundation.
*
* You should have received a copy of the GNU Lesser General Public License along with this
* program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
* or from the Free Software Foundation, Inc.,
* 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
*
* This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
* without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
* See the GNU Lesser General Public License for more details.
*
* Copyright (c) 2002-2017 Hitachi Vantara..  All rights reserved.
*/

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
