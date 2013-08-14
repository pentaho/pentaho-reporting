package org.pentaho.reporting.engine.classic.core.imagemap.parser;

import java.io.StringReader;
import java.util.Collections;

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

public class ImageMapParser
{
  private static class ImageMapRootHandler extends RootXmlReadHandler
  {
    /**
     * Creates a new root-handler using the given versioning information and
     * resource-manager.
     *
     * @param manager the resource manager that loaded this xml-file.
     * @param source  the source-key that identifies from where the file was loaded.
     * @param context the key that should be used to resolve relative paths.
     * @param version the versioning information for the root-file.
     */
    private ImageMapRootHandler(final ResourceManager manager,
                                final ResourceKey source,
                                final ResourceKey context,
                                final long version)
    {
      super(manager, source, context, version);
      setRootHandler(new ImageMapReadHandler());
      pushDefaultNamespace(LibXmlInfo.XHTML_NAMESPACE);
    }


  }

  private static class ImageMapXmlResourceFactory extends AbstractXmlResourceFactory
  {
    /**
     * Returns the configuration that should be used to initialize this factory.
     *
     * @return the configuration for initializing the factory.
     */
    protected Configuration getConfiguration()
    {
      return ClassicEngineBoot.getInstance().getGlobalConfig();
    }

    /**
     * Returns the expected result type.
     *
     * @return the result type.
     */
    public Class getFactoryType()
    {
      return ImageMap.class;
    }

    public void initializeDefaults()
    {
      // we can skip that part, as we make no use of the XmlFactoryModules.
    }

    protected RootXmlReadHandler createRootHandler(final ResourceManager manager,
                                                   final XmlFactoryModule[] rootHandlers,
                                                   final ResourceKey contextKey,
                                                   final long version,
                                                   final ResourceKey targetKey)
    {
      return new ImageMapRootHandler(manager, contextKey, targetKey, version);
    }
  }

  public ImageMap parseFromString (final String imageMapXml)
      throws ResourceKeyCreationException, ResourceCreationException, ResourceLoadingException
  {
    final ResourceManager resourceManager = new ResourceManager();
    final ImageMapXmlResourceFactory resourceFactory = new ImageMapXmlResourceFactory();
    final InputSource source = new InputSource();
    source.setCharacterStream(new StringReader(imageMapXml));
    return (ImageMap) resourceFactory.parseDirectly
        (resourceManager, source, null, Collections.EMPTY_MAP);
  }
}
