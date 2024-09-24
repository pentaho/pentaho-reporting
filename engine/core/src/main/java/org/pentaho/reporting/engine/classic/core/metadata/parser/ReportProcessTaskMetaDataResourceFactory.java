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

package org.pentaho.reporting.engine.classic.core.metadata.parser;

import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.modules.parser.base.ReportResource;
import org.pentaho.reporting.libraries.base.config.Configuration;
import org.pentaho.reporting.libraries.resourceloader.Resource;
import org.pentaho.reporting.libraries.resourceloader.ResourceKey;
import org.pentaho.reporting.libraries.xmlns.parser.AbstractXmlResourceFactory;
import org.pentaho.reporting.libraries.xmlns.parser.RootXmlReadHandler;
import org.pentaho.reporting.libraries.xmlns.parser.XmlFactoryModule;
import org.pentaho.reporting.libraries.xmlns.parser.XmlFactoryModuleRegistry;

public class ReportProcessTaskMetaDataResourceFactory extends AbstractXmlResourceFactory {
  private static final XmlFactoryModuleRegistry registry = new XmlFactoryModuleRegistry();

  public static void register( final Class<? extends XmlFactoryModule> readHandler ) {
    registry.register( readHandler );
  }

  public ReportProcessTaskMetaDataResourceFactory() {
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
    return ReportProcessTaskMetaDataCollection.class;
  }

  protected Resource createResource( final ResourceKey targetKey, final RootXmlReadHandler handler,
      final Object createdProduct, final Class createdType ) {
    return new ReportResource( targetKey, handler.getDependencyCollector(), createdProduct, createdType, true );
  }
}
