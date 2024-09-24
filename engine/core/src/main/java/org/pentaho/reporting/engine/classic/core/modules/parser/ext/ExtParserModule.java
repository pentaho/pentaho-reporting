/*
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
 * Copyright (c) 2001 - 2013 Object Refinery Ltd, Hitachi Vantara and Contributors..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.modules.parser.ext;

import org.pentaho.reporting.engine.classic.core.modules.parser.base.DataFactoryReadHandlerFactory;
import org.pentaho.reporting.engine.classic.core.modules.parser.base.MasterReportXmlResourceFactory;
import org.pentaho.reporting.engine.classic.core.modules.parser.base.SubReportReadHandlerFactory;
import org.pentaho.reporting.engine.classic.core.modules.parser.base.SubReportXmlResourceFactory;
import org.pentaho.reporting.engine.classic.core.modules.parser.base.common.DataFactoryRefReadHandler;
import org.pentaho.reporting.engine.classic.core.modules.parser.ext.readhandlers.ExtSubReportReadHandler;
import org.pentaho.reporting.libraries.base.boot.AbstractModule;
import org.pentaho.reporting.libraries.base.boot.ModuleInitializeException;
import org.pentaho.reporting.libraries.base.boot.SubSystem;

/**
 * The module definition for the extended parser module.
 *
 * @author Thomas Morgner
 */
public class ExtParserModule extends AbstractModule {
  public static final String NAMESPACE = "http://jfreereport.sourceforge.net/namespaces/reports/legacy/ext";

  /**
   * DefaultConstructor. Loads the module specification.
   *
   * @throws ModuleInitializeException
   *           if an error occured.
   */
  public ExtParserModule() throws ModuleInitializeException {
    loadModuleInfo();
  }

  /**
   * Initalizes the module. This performs the external initialization and checks that an JAXP1.1 parser is available.
   *
   * @param subSystem
   *          the subsystem for this module.
   * @throws ModuleInitializeException
   *           if an error occured.
   */
  public void initialize( final SubSystem subSystem ) throws ModuleInitializeException {
    if ( AbstractModule.isClassLoadable( "org.xml.sax.ext.LexicalHandler", ExtParserModule.class ) == false ) {
      throw new ModuleInitializeException( "Unable to load JAXP-1.1 classes. "
          + "Check your classpath and XML parser configuration." );
    }

    SubReportReadHandlerFactory.getInstance()
        .setElementHandler( NAMESPACE, "sub-report", ExtSubReportReadHandler.class );
    DataFactoryReadHandlerFactory.getInstance().setElementHandler( NAMESPACE, "data-factory",
        DataFactoryRefReadHandler.class );

    SubReportXmlResourceFactory.register( ExtSubReportXmlFactoryModule.class );
    MasterReportXmlResourceFactory.register( ExtReportXmlFactoryModule.class );

    performExternalInitialize( ExtParserModuleInit.class.getName(), ExtParserModule.class );
  }

}
