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

package org.pentaho.reporting.engine.classic.extensions.modules.sbarcodes;

import net.sourceforge.barbecue.env.EnvironmentFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.metadata.ElementMetaDataParser;
import org.pentaho.reporting.engine.classic.core.metadata.ElementTypeRegistry;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.BundleElementRegistry;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer.BundleWriterHandlerRegistry;
import org.pentaho.reporting.engine.classic.extensions.modules.sbarcodes.xml.SimpleBarcodesElementReadHandler;
import org.pentaho.reporting.libraries.base.boot.AbstractModule;
import org.pentaho.reporting.libraries.base.boot.ModuleInitializeException;
import org.pentaho.reporting.libraries.base.boot.SubSystem;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;

/**
 * This module adds support for sbarcodes. It is called 'simple' because no much properties can be accessed (mainly type
 * name, bar width & height, checksum, show text, some font & color properties)
 *
 * @author Cedric Pronzato
 */
public class SimpleBarcodesModule extends AbstractModule {
  public static final String NAMESPACE = SimpleBarcodesAttributeNames.NAMESPACE;

  private static final Log logger = LogFactory.getLog( SimpleBarcodesModule.class );

  public SimpleBarcodesModule() throws ModuleInitializeException {
    loadModuleInfo();
  }

  /**
   * Initializes the module. Use this method to perform all initial setup operations. This method is called only once in
   * a modules lifetime. If the initializing cannot be completed, throw a <code>ModuleInitializeException</code> to
   * indicate the error,. The module will not be available to the system.
   *
   * @param subSystem
   *          the subSystem.
   * @throws org.pentaho.reporting.libraries.base.boot.ModuleInitializeException
   *           if an error occurred while initializing the module.
   */
  public void initialize( final SubSystem subSystem ) throws ModuleInitializeException {
    try {
      final ClassLoader loader = ObjectUtilities.getClassLoader( getClass() );
      Class.forName( "net.sourceforge.barbecue.Barcode", false, loader );
      EnvironmentFactory.setHeadlessMode();
    } catch ( Throwable t ) {
      throw new ModuleInitializeException( "Unable to load Barbecue library class.", t );
    }

    ElementTypeRegistry.getInstance().registerNamespacePrefix( NAMESPACE, "sbarcodes" );

    ElementMetaDataParser
        .initializeOptionalElementMetaData( "org/pentaho/reporting/engine/classic/extensions/modules/sbarcodes/meta-elements.xml" );
    ElementMetaDataParser
        .initializeOptionalExpressionsMetaData( "org/pentaho/reporting/engine/classic/extensions/modules/sbarcodes/meta-expressions.xml" );

    BundleElementRegistry.getInstance().registerGenericElement( SimpleBarcodesType.INSTANCE );
    // legacy handler for a buggy iteration ..
    BundleElementRegistry.getInstance().register( NAMESPACE, "simple-barcode", SimpleBarcodesElementReadHandler.class );

    BundleWriterHandlerRegistry.getInstance().setNamespaceHasCData( NAMESPACE, false );

  }
}
