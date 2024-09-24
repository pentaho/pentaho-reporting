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

package org.pentaho.reporting.engine.classic.core.imagemap.parser;

import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.imagemap.ImageMap;
import org.pentaho.reporting.libraries.base.config.Configuration;
import org.pentaho.reporting.libraries.resourceloader.ResourceCreationException;
import org.pentaho.reporting.libraries.resourceloader.ResourceKey;
import org.pentaho.reporting.libraries.resourceloader.ResourceKeyCreationException;
import org.pentaho.reporting.libraries.resourceloader.ResourceLoadingException;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;
import org.pentaho.reporting.libraries.xmlns.LibXmlInfo;
import org.pentaho.reporting.libraries.xmlns.parser.AbstractXmlResourceFactory;
import org.pentaho.reporting.libraries.xmlns.parser.RootXmlReadHandler;
import org.pentaho.reporting.libraries.xmlns.parser.XmlFactoryModule;
import org.xml.sax.InputSource;

import java.io.StringReader;
import java.util.Collections;

public class ImageMapParser {
  private static class ImageMapRootHandler extends RootXmlReadHandler {
    /**
     * Creates a new root-handler using the given versioning information and resource-manager.
     *
     * @param manager
     *          the resource manager that loaded this xml-file.
     * @param source
     *          the source-key that identifies from where the file was loaded.
     * @param context
     *          the key that should be used to resolve relative paths.
     * @param version
     *          the versioning information for the root-file.
     */
    private ImageMapRootHandler( final ResourceManager manager, final ResourceKey source, final ResourceKey context,
        final long version ) {
      super( manager, source, context, version );
      setRootHandler( new ImageMapReadHandler() );
      pushDefaultNamespace( LibXmlInfo.XHTML_NAMESPACE );
    }

  }

  private static class ImageMapXmlResourceFactory extends AbstractXmlResourceFactory {
    /**
     * Returns the configuration that should be used to initialize this factory.
     *
     * @return the configuration for initializing the factory.
     */
    protected Configuration getConfiguration() {
      return ClassicEngineBoot.getInstance().getGlobalConfig();
    }

    /**
     * Returns the expected result type.
     *
     * @return the result type.
     */
    public Class getFactoryType() {
      return ImageMap.class;
    }

    public void initializeDefaults() {
      // we can skip that part, as we make no use of the XmlFactoryModules.
    }

    protected RootXmlReadHandler createRootHandler( final ResourceManager manager,
        final XmlFactoryModule[] rootHandlers, final ResourceKey contextKey, final long version,
        final ResourceKey targetKey ) {
      return new ImageMapRootHandler( manager, contextKey, targetKey, version );
    }
  }

  public ImageMap parseFromString( final String imageMapXml ) throws ResourceKeyCreationException,
    ResourceCreationException, ResourceLoadingException {
    final ResourceManager resourceManager = new ResourceManager();
    final ImageMapXmlResourceFactory resourceFactory = new ImageMapXmlResourceFactory();
    final InputSource source = new InputSource();
    source.setCharacterStream( new StringReader( imageMapXml ) );
    return (ImageMap) resourceFactory.parseDirectly( resourceManager, source, null, Collections.EMPTY_MAP );
  }
}
