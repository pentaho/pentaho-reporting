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
