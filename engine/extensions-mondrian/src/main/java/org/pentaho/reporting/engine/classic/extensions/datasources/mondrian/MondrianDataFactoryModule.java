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

package org.pentaho.reporting.engine.classic.extensions.datasources.mondrian;

import org.pentaho.reporting.engine.classic.core.designtime.compat.CompatibilityConverterRegistry;
import org.pentaho.reporting.engine.classic.core.metadata.ElementMetaDataParser;
import org.pentaho.reporting.engine.classic.core.modules.parser.base.DataFactoryReadHandlerFactory;
import org.pentaho.reporting.engine.classic.core.modules.parser.base.DataFactoryXmlResourceFactory;
import org.pentaho.reporting.engine.classic.extensions.datasources.mondrian.compat
  .MondrianDataSource_50_CompatibilityConverter;
import org.pentaho.reporting.engine.classic.extensions.datasources.mondrian.parser.BandedMDXDataSourceReadHandler;
import org.pentaho.reporting.engine.classic.extensions.datasources.mondrian.parser.BandedMDXDataSourceXmlFactoryModule;
import org.pentaho.reporting.engine.classic.extensions.datasources.mondrian.parser.CubeFileProviderReadHandlerFactory;
import org.pentaho.reporting.engine.classic.extensions.datasources.mondrian.parser.DataSourceProviderReadHandlerFactory;
import org.pentaho.reporting.engine.classic.extensions.datasources.mondrian.parser.DefaultCubeFileProviderReadHandler;
import org.pentaho.reporting.engine.classic.extensions.datasources.mondrian.parser.DenormalizedMDXDataSourceReadHandler;
import org.pentaho.reporting.engine.classic.extensions.datasources.mondrian.parser
  .DenormalizedMDXDataSourceXmlFactoryModule;
import org.pentaho.reporting.engine.classic.extensions.datasources.mondrian.parser.DriverDataSourceProviderReadHandler;
import org.pentaho.reporting.engine.classic.extensions.datasources.mondrian.parser.JndiDataSourceProviderReadHandler;
import org.pentaho.reporting.engine.classic.extensions.datasources.mondrian.parser.LegacyBandedMDXDataSourceReadHandler;
import org.pentaho.reporting.engine.classic.extensions.datasources.mondrian.parser
  .LegacyBandedMDXDataSourceXmlFactoryModule;
import org.pentaho.reporting.engine.classic.extensions.datasources.mondrian.parser.SimpleBandedMDXDataSourceReadHandler;
import org.pentaho.reporting.engine.classic.extensions.datasources.mondrian.parser
  .SimpleBandedMDXDataSourceXmlFactoryModule;
import org.pentaho.reporting.engine.classic.extensions.datasources.mondrian.parser
  .SimpleDenormalizedMDXDataSourceReadHandler;
import org.pentaho.reporting.engine.classic.extensions.datasources.mondrian.parser
  .SimpleDenormalizedMDXDataSourceXmlFactoryModule;
import org.pentaho.reporting.engine.classic.extensions.datasources.mondrian.parser
  .SimpleLegacyBandedMDXDataSourceReadHandler;
import org.pentaho.reporting.engine.classic.extensions.datasources.mondrian.parser
  .SimpleLegacyBandedMDXDataSourceXmlFactoryModule;
import org.pentaho.reporting.libraries.base.boot.AbstractModule;
import org.pentaho.reporting.libraries.base.boot.ModuleInitializeException;
import org.pentaho.reporting.libraries.base.boot.SubSystem;

public class MondrianDataFactoryModule extends AbstractModule {
  public static final String NAMESPACE = "http://jfreereport.sourceforge.net/namespaces/datasources/mondrian";
  public static final String META_DOMAIN =
    "http://reporting.pentaho.org/namespaces/engine/meta-attributes/mondrian";
  public static final String TAG_DEF_PREFIX =
    "org.pentaho.reporting.engine.classic.extensions.datasources.mondrian.tag-def.";
  public static final String DATASOURCE_WRITER_PREFIX =
    "org.pentaho.reporting.engine.classic.extensions.datasources.mondrian.writer.datasource-provider.";
  public static final String DATASOURCE_BUNDLEWRITER_PREFIX =
    "org.pentaho.reporting.engine.classic.extensions.datasources.mondrian.bundle-writer.datasource-provider.";
  public static final String CUBEFILE_WRITER_PREFIX =
    "org.pentaho.reporting.engine.classic.extensions.datasources.mondrian.writer.cubefile-provider.";
  public static final String CUBEFILE_BUNDLEWRITER_PREFIX =
    "org.pentaho.reporting.engine.classic.extensions.datasources.mondrian.bundle-writer.cubefile-provider.";

  public static final String MEMBER_ON_AXIS_SORTED_KEY =
    "org.pentaho.reporting.engine.classic.extensions.datasources.mondrian.MembersOnAxisSorted";

  public MondrianDataFactoryModule() throws ModuleInitializeException {
    loadModuleInfo();
  }

  /**
   * Initializes the module. Use this method to perform all initial setup operations. This method is called only once in
   * a modules lifetime. If the initializing cannot be completed, throw a ModuleInitializeException to indicate the
   * error,. The module will not be available to the system.
   *
   * @param subSystem the subSystem.
   * @throws ModuleInitializeException if an error ocurred while initializing the module.
   */
  public void initialize( final SubSystem subSystem ) throws ModuleInitializeException {
    DataFactoryXmlResourceFactory.register( BandedMDXDataSourceXmlFactoryModule.class );
    DataFactoryXmlResourceFactory.register( LegacyBandedMDXDataSourceXmlFactoryModule.class );
    DataFactoryXmlResourceFactory.register( DenormalizedMDXDataSourceXmlFactoryModule.class );
    DataFactoryXmlResourceFactory.register( SimpleBandedMDXDataSourceXmlFactoryModule.class );
    DataFactoryXmlResourceFactory.register( SimpleLegacyBandedMDXDataSourceXmlFactoryModule.class );
    DataFactoryXmlResourceFactory.register( SimpleDenormalizedMDXDataSourceXmlFactoryModule.class );

    DataFactoryReadHandlerFactory.getInstance()
      .setElementHandler( NAMESPACE, "banded-mdx-datasource", BandedMDXDataSourceReadHandler.class );
    DataFactoryReadHandlerFactory.getInstance()
      .setElementHandler( NAMESPACE, "denormalized-mdx-datasource", DenormalizedMDXDataSourceReadHandler.class );
    DataFactoryReadHandlerFactory.getInstance()
      .setElementHandler( NAMESPACE, "legacy-banded-mdx-datasource", LegacyBandedMDXDataSourceReadHandler.class );
    DataFactoryReadHandlerFactory.getInstance()
      .setElementHandler( NAMESPACE, "simple-banded-mdx-datasource", SimpleBandedMDXDataSourceReadHandler.class );
    DataFactoryReadHandlerFactory.getInstance().setElementHandler( NAMESPACE, "simple-denormalized-mdx-datasource",
      SimpleDenormalizedMDXDataSourceReadHandler.class );
    DataFactoryReadHandlerFactory.getInstance().setElementHandler( NAMESPACE, "simple-legacy-banded-mdx-datasource",
      SimpleLegacyBandedMDXDataSourceReadHandler.class );

    CubeFileProviderReadHandlerFactory.getInstance()
      .setElementHandler( NAMESPACE, "cube-file", DefaultCubeFileProviderReadHandler.class );

    DataSourceProviderReadHandlerFactory.getInstance()
      .setElementHandler( NAMESPACE, "driver", DriverDataSourceProviderReadHandler.class );
    DataSourceProviderReadHandlerFactory.getInstance()
      .setElementHandler( NAMESPACE, "jndi", JndiDataSourceProviderReadHandler.class );

    ElementMetaDataParser.initializeOptionalDataFactoryMetaData
      ( "org/pentaho/reporting/engine/classic/extensions/datasources/mondrian/meta-datafactory.xml" );

    CompatibilityConverterRegistry.getInstance().register( MondrianDataSource_50_CompatibilityConverter.class );
  }

}
