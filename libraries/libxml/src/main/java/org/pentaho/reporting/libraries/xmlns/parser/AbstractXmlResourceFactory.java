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

package org.pentaho.reporting.libraries.xmlns.parser;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.libraries.base.config.Configuration;
import org.pentaho.reporting.libraries.base.config.DefaultConfiguration;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.libraries.resourceloader.CompoundResource;
import org.pentaho.reporting.libraries.resourceloader.FactoryParameterKey;
import org.pentaho.reporting.libraries.resourceloader.Resource;
import org.pentaho.reporting.libraries.resourceloader.ResourceCreationException;
import org.pentaho.reporting.libraries.resourceloader.ResourceData;
import org.pentaho.reporting.libraries.resourceloader.ResourceFactory;
import org.pentaho.reporting.libraries.resourceloader.ResourceKey;
import org.pentaho.reporting.libraries.resourceloader.ResourceKeyCreationException;
import org.pentaho.reporting.libraries.resourceloader.ResourceLoadingException;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;
import org.pentaho.reporting.libraries.resourceloader.loader.raw.RawResourceData;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

/**
 * A base-class for resource-factories that load their resources from XML files. This class provides a multiplexing
 * option. For this, the parser looks at the root-element of the document to be parsed and selects the most suitable
 * XmlFactoryModule implementation registered.
 *
 * @author Thomas Morgner
 * @noinspection HardCodedStringLiteral
 */
public abstract class AbstractXmlResourceFactory implements ResourceFactory {
  private static final Log logger = LogFactory.getLog( AbstractXmlResourceFactory.class );

  /**
   * A key for the content base.
   */
  public static final String CONTENTBASE_KEY = "content-base";
  private static final byte[] EMPTY_DATA = new byte[ 0 ];

  private ArrayList<XmlFactoryModule> modules;
  private ArrayList<XmlFactoryModule> modulesFromConfiguration;
  private SAXParserFactory factory;

  /**
   * Default-Constructor.
   */
  protected AbstractXmlResourceFactory() {
    modules = new ArrayList<XmlFactoryModule>();
    modulesFromConfiguration = new ArrayList<XmlFactoryModule>();
  }


  /**
   * Returns a SAX parser.
   *
   * @return a SAXParser.
   * @throws ParserConfigurationException if there is a problem configuring the parser.
   * @throws SAXException                 if there is a problem with the parser initialisation
   */
  protected SAXParser getParser()
    throws ParserConfigurationException, SAXException {
    if ( this.factory == null ) {
      this.factory = SAXParserFactory.newInstance();
    }
    return this.factory.newSAXParser();
  }


  /**
   * Configures the xml reader. Use this to set features or properties before the documents get parsed.
   *
   * @param handler the parser implementation that will handle the SAX-Callbacks.
   * @param reader  the xml reader that should be configured.
   */
  protected void configureReader( final XMLReader reader,
                                  final RootXmlReadHandler handler ) {
    try {
      reader.setProperty( "http://xml.org/sax/properties/lexical-handler", handler.getCommentHandler() );
    } catch ( final SAXException se ) {
      // ignore ..
      logger.debug( "Comments are not supported by this SAX implementation." );
    }

    try {
      reader.setFeature( "http://xml.org/sax/features/xmlns-uris", true );
    } catch ( final SAXException e ) {
      // ignore
      handler.setXmlnsUrisNotAvailable( true );
    }
    try {
      // disable validation, as our parsers should handle that already. And we do not want to read
      // external DTDs that may not exist at all.
      reader.setFeature( "http://xml.org/sax/features/validation", false );
      reader.setFeature( "http://xml.org/sax/features/external-parameter-entities", false );
      reader.setFeature( "http://xml.org/sax/features/external-general-entities", false );
    } catch ( final SAXException e ) {
      // ignore
      if ( logger.isDebugEnabled() ) {
        logger.debug( "Disabling external validation failed. Parsing may or may not fail with a parse error later." );
      }
    }

    try {
      reader.setFeature( "http://xml.org/sax/features/namespaces", true );
      reader.setFeature( "http://xml.org/sax/features/namespace-prefixes", true );
    } catch ( final SAXException e ) {
      if ( logger.isDebugEnabled() ) {
        logger.warn( "No Namespace features will be available. (Yes, this is serious)", e );
      } else if ( logger.isWarnEnabled() ) {
        logger.warn( "No Namespace features will be available. (Yes, this is serious)" );
      }
    }
  }

  /**
   * Creates a resource by interpreting the data given in the resource-data object. If additional datastreams need to be
   * parsed, the provided resource manager should be used. This method parses the given resource-data as XML stream.
   *
   * @param manager the resource manager used for all resource loading.
   * @param data    the resource-data from where the binary data is read.
   * @param context the resource context used to resolve relative resource paths.
   * @return the parsed result, never null.
   * @throws ResourceCreationException if the resource could not be parsed due to syntaxctial or logical errors in the
   *                                   data.
   * @throws ResourceLoadingException  if the resource could not be accessed from the physical storage.
   */
  public Resource create( final ResourceManager manager,
                          final ResourceData data,
                          final ResourceKey context )
    throws ResourceCreationException, ResourceLoadingException {
    try {
      final SAXParser parser = getParser();

      final XMLReader reader = parser.getXMLReader();
      final XmlFactoryModule[] rootHandlers = getModules();
      if ( rootHandlers.length == 0 ) {
        throw new ResourceCreationException(
          "There are no root-handlers registered for the factory for type " + getFactoryType() );
      }

      final ResourceDataInputSource input = new ResourceDataInputSource( data, manager );

      final ResourceKey contextKey;
      final long version;
      final ResourceKey targetKey = data.getKey();
      if ( context == null ) {
        contextKey = targetKey;
        version = data.getVersion( manager );
      } else {
        contextKey = context;
        version = -1;
      }

      final RootXmlReadHandler handler = createRootHandler( manager, targetKey, rootHandlers, contextKey, version );

      final DefaultConfiguration parserConfiguration = handler.getParserConfiguration();
      final URL value = manager.toURL( contextKey );
      if ( value != null ) {
        parserConfiguration.setConfigProperty( CONTENTBASE_KEY, value.toExternalForm() );
      }

      configureReader( reader, handler );
      reader.setContentHandler( handler );
      reader.setDTDHandler( handler );
      reader.setEntityResolver( handler.getEntityResolver() );
      reader.setErrorHandler( getErrorHandler() );

      final Map parameters = targetKey.getFactoryParameters();
      final Iterator it = parameters.keySet().iterator();
      while ( it.hasNext() ) {
        final Object o = it.next();
        if ( o instanceof FactoryParameterKey ) {
          final FactoryParameterKey fpk = (FactoryParameterKey) o;
          handler.setHelperObject( fpk.getName(), parameters.get( fpk ) );
        }
      }

      reader.parse( input );

      final Object createdProduct = finishResult
        ( handler.getResult(), manager, data, contextKey );
      handler.getDependencyCollector().add( targetKey, data.getVersion( manager ) );
      return createResource( targetKey, handler, createdProduct, getFactoryType() );
    } catch ( ParserConfigurationException e ) {
      throw new ResourceCreationException( "Unable to initialize the XML-Parser", e );
    } catch ( SAXException e ) {
      throw new ResourceCreationException( "Unable to parse the document: " + data.getKey(), e );
    } catch ( IOException e ) {
      throw new ResourceLoadingException( "Unable to read the stream from document: " + data.getKey(), e );
    }
  }

  protected RootXmlReadHandler createRootHandler( final ResourceManager manager,
                                                  final ResourceKey targetKey,
                                                  final XmlFactoryModule[] rootHandlers,
                                                  final ResourceKey contextKey,
                                                  final long version ) {
    return new MultiplexRootElementHandler( manager, targetKey, contextKey, version, rootHandlers );
  }

  /**
   * A method to allow to invoke the parsing without accessing the LibLoader layer. The data to be parsed is held in the
   * given InputSource object.
   *
   * @param manager    the resource manager used for all resource loading.
   * @param input      the raw-data given as SAX-InputSource.
   * @param context    the resource context used to resolve relative resource paths.
   * @param parameters the parse parameters.
   * @return the parsed result, never null.
   * @throws ResourceCreationException    if the resource could not be parsed due to syntaxctial or logical errors in
   *                                      the data.
   * @throws ResourceLoadingException     if the resource could not be accessed from the physical storage.
   * @throws ResourceKeyCreationException if creating the context key failed.
   */
  public Object parseDirectly( final ResourceManager manager,
                               final InputSource input,
                               final ResourceKey context,
                               final Map parameters )
    throws ResourceKeyCreationException, ResourceCreationException, ResourceLoadingException {
    try {
      final SAXParser parser = getParser();

      final XMLReader reader = parser.getXMLReader();

      final ResourceKey targetKey = manager.createKey( EMPTY_DATA );
      final ResourceKey contextKey;
      if ( context == null ) {
        contextKey = targetKey;
      } else {
        contextKey = context;
      }

      final XmlFactoryModule[] rootHandlers = getModules();
      final RootXmlReadHandler handler = createRootHandler( manager, targetKey, rootHandlers, contextKey, -1 );

      final DefaultConfiguration parserConfiguration = handler.getParserConfiguration();
      final URL value = manager.toURL( contextKey );
      if ( value != null ) {
        parserConfiguration.setConfigProperty( CONTENTBASE_KEY, value.toExternalForm() );
      }

      configureReader( reader, handler );
      reader.setContentHandler( handler );
      reader.setDTDHandler( handler );
      reader.setEntityResolver( handler.getEntityResolver() );
      reader.setErrorHandler( getErrorHandler() );

      final Iterator it = parameters.keySet().iterator();
      while ( it.hasNext() ) {
        final Object o = it.next();
        if ( o instanceof FactoryParameterKey ) {
          final FactoryParameterKey fpk = (FactoryParameterKey) o;
          handler.setHelperObject( fpk.getName(), parameters.get( fpk ) );
        }
      }

      reader.parse( input );

      return finishResult( handler.getResult(), manager, new RawResourceData( targetKey ), contextKey );
    } catch ( ParserConfigurationException e ) {
      throw new ResourceCreationException
        ( "Unable to initialize the XML-Parser", e );
    } catch ( SAXException e ) {
      throw new ResourceCreationException( "Unable to parse the document", e );
    } catch ( IOException e ) {
      throw new ResourceLoadingException( "Unable to read the stream", e );
    }

  }

  /**
   * Returns the registered XmlFactoryModules as array. We assume that the modules are evaluated in the given order. The
   * modules from the configuration are listed first (highest priority, as they may be supplied by user-overrides), then
   * the modules that have been registered manually, where the oldest modules are returned as lowest priority elements.
   *
   * @return the modules as array.
   */
  protected final XmlFactoryModule[] getModules() {
    final ArrayList<XmlFactoryModule> realModules = new ArrayList<XmlFactoryModule>();
    realModules.addAll( modulesFromConfiguration );
    for ( int i = modules.size() - 1; i >= 0; i -= 1 ) {
      final XmlFactoryModule xmlFactoryModule = modules.get( i );
      realModules.add( xmlFactoryModule );
    }
    return realModules.toArray( new XmlFactoryModule[ realModules.size() ] );
  }

  /**
   * Creates a Resource object for the given product. By default this returns a compound-resource that holds all the key
   * that identify the resources used during the content production.
   *
   * @param targetKey      the target key.
   * @param handler        the root handler used for the parsing.
   * @param createdProduct the created product.
   * @param createdType    the type information for the object that has been parsed.
   * @return the product wrapped into a resource object.
   */
  protected Resource createResource( final ResourceKey targetKey,
                                     final RootXmlReadHandler handler,
                                     final Object createdProduct,
                                     final Class createdType ) {
    return new CompoundResource( targetKey, handler.getDependencyCollector(), createdProduct, createdType );
  }

  /**
   * Finishes up the result. This can be used for general clean up and post-parse initializaion of the result. The
   * default implementation does nothing and just returns the object itself.
   *
   * @param res     the parsed resource.
   * @param manager the resource manager that was used to load the resource.
   * @param data    the data object from where the resource is loaded.
   * @param context the context that resolves relative resource paths.
   * @return the parsed resource.
   * @throws ResourceCreationException if the post initialization fails.
   * @throws ResourceLoadingException  if loading external resources failed with an IO error.
   */
  protected Object finishResult( final Object res,
                                 final ResourceManager manager,
                                 final ResourceData data,
                                 final ResourceKey context )
    throws ResourceCreationException, ResourceLoadingException {
    return res;
  }

  /**
   * Returns the configuration that should be used to initialize this factory.
   *
   * @return the configuration for initializing the factory.
   */
  protected abstract Configuration getConfiguration();

  /**
   * Loads all XmlFactoryModule-implementations from the given configuration.
   *
   * @see #getConfiguration()
   */
  public void initializeDefaults() {
    final String type = getFactoryType().getName();
    final String prefix = ResourceFactory.CONFIG_PREFIX + type;
    final Configuration config = getConfiguration();
    final Iterator itType = config.findPropertyKeys( prefix );
    while ( itType.hasNext() ) {
      final String key = (String) itType.next();
      final String modClass = config.getConfigProperty( key );
      final XmlFactoryModule maybeFactory = ObjectUtilities.loadAndInstantiate
        ( modClass, AbstractXmlResourceFactory.class, XmlFactoryModule.class );
      if ( maybeFactory == null ) {
        continue;
      }
      modulesFromConfiguration.add( maybeFactory );
    }
  }

  /**
   * Registers a factory module for being used during the parsing. If the factory module does not return a result that
   * matches the factory's type, the parsing will always fail.
   *
   * @param factoryModule the factory module.
   * @throws NullPointerException if the module given is null.
   */
  public void registerModule( final XmlFactoryModule factoryModule ) {
    if ( factoryModule == null ) {
      throw new NullPointerException();
    }
    modules.add( factoryModule );
  }

  /**
   * Returns the XML-Error handler that should be registered with the XML parser. By default, this returns a logger.
   *
   * @return the error handler.
   */
  protected ErrorHandler getErrorHandler() {
    return new LoggingErrorHandler();
  }
}
