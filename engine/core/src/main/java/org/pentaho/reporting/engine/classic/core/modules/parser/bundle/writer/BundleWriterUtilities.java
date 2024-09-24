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

package org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.DataFactory;
import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.metadata.ElementMetaData;
import org.pentaho.reporting.engine.classic.core.metadata.ElementType;
import org.pentaho.reporting.libraries.base.config.Configuration;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;

/**
 * Todo: Document Me
 *
 * @author Thomas Morgner
 */
public class BundleWriterUtilities {
  private static final Log logger = LogFactory.getLog( BundleWriterUtilities.class );

  private static final String DATA_PREFIX =
      "org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer.data-factory-handler.";
  private static final String ELEMENT_PREFIX =
      "org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer.element-handler.";

  private BundleWriterUtilities() {
  }

  public static BundleDataFactoryWriterHandler lookupWriteHandler( final DataFactory dataFactory ) {
    if ( dataFactory == null ) {
      throw new NullPointerException();
    }

    final String configKey = DATA_PREFIX + dataFactory.getClass().getName();
    final Configuration globalConfig = ClassicEngineBoot.getInstance().getGlobalConfig();
    final String value = globalConfig.getConfigProperty( configKey );
    if ( value != null ) {

      final BundleDataFactoryWriterHandler dataFactoryWriterHandler =
          (BundleDataFactoryWriterHandler) ObjectUtilities.loadAndInstantiate( value, dataFactory.getClass(),
              BundleDataFactoryWriterHandler.class );
      if ( dataFactoryWriterHandler == null ) {
        logger.warn( "Specified data-source write handler implementation could not be loaded: " + value );
      }
      return dataFactoryWriterHandler;
    }
    logger.warn( "No data-source write handler known for: " + value );
    return null;
  }

  public static BundleDataFactoryWriterHandler lookupWriteHandler( final Element element ) {
    if ( element == null ) {
      throw new NullPointerException();
    }

    final ElementType type = element.getElementType();
    if ( type == null ) {
      // A legacy element. Cannot handle that this way.
      return null;
    }

    final ElementMetaData metaData = type.getMetaData();
    final String configKey = ELEMENT_PREFIX + metaData.getName();
    final Configuration globalConfig = ClassicEngineBoot.getInstance().getGlobalConfig();
    final String value = globalConfig.getConfigProperty( configKey );
    if ( value != null ) {
      return (BundleDataFactoryWriterHandler) ObjectUtilities.loadAndInstantiate( value, metaData.getClass(),
          BundleDataFactoryWriterHandler.class );
    }
    return null;
  }
}
