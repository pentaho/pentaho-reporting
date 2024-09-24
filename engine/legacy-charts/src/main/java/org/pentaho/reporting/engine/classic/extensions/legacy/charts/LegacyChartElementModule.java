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

package org.pentaho.reporting.engine.classic.extensions.legacy.charts;

import org.pentaho.reporting.engine.classic.core.metadata.ElementMetaDataParser;
import org.pentaho.reporting.engine.classic.core.metadata.ElementTypeRegistry;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.BundleElementRegistry;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer.BundleWriterHandlerRegistry;
import org.pentaho.reporting.libraries.base.boot.AbstractModule;
import org.pentaho.reporting.libraries.base.boot.ModuleInitializeException;
import org.pentaho.reporting.libraries.base.boot.SubSystem;

public class LegacyChartElementModule extends AbstractModule {
  public static final String NAMESPACE = "http://reporting.pentaho.org/namespaces/engine/classic/legacy/charting/1.0";

  public static final String PRIMARY_DATA_COLLECTOR_FUNCTION_ATTRIBUTE = "primary-dataset-expression";
  public static final String SECONDARY_DATA_COLLECTOR_FUNCTION_ATTRIBUTE = "secondary-dataset-expression";

  public LegacyChartElementModule() throws ModuleInitializeException {
    loadModuleInfo();
  }

  public void initialize( final SubSystem subSystem ) throws ModuleInitializeException {
    ElementTypeRegistry.getInstance().registerNamespacePrefix( NAMESPACE, "legacy-charts" );
    ElementMetaDataParser.initializeOptionalElementMetaData
      ( "org/pentaho/reporting/engine/classic/extensions/legacy/charts/meta-elements.xml" );
    ElementMetaDataParser.initializeOptionalReportPreProcessorMetaData
      ( "org/pentaho/reporting/engine/classic/extensions/legacy/charts/meta-report-preprocessors.xml" );

    BundleElementRegistry.getInstance().registerGenericElement( LegacyChartType.INSTANCE );
    BundleWriterHandlerRegistry.getInstance().setNamespaceHasCData( NAMESPACE, false );

  }
}
