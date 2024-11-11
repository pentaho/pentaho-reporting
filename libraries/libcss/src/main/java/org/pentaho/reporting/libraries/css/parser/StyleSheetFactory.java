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


package org.pentaho.reporting.libraries.css.parser;

import org.pentaho.reporting.libraries.css.model.StyleKeyRegistry;
import org.pentaho.reporting.libraries.css.model.StyleSheet;
import org.pentaho.reporting.libraries.resourceloader.CompoundResource;
import org.pentaho.reporting.libraries.resourceloader.DependencyCollector;
import org.pentaho.reporting.libraries.resourceloader.Resource;
import org.pentaho.reporting.libraries.resourceloader.ResourceCreationException;
import org.pentaho.reporting.libraries.resourceloader.ResourceData;
import org.pentaho.reporting.libraries.resourceloader.ResourceFactory;
import org.pentaho.reporting.libraries.resourceloader.ResourceKey;
import org.pentaho.reporting.libraries.resourceloader.ResourceLoadingException;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;
import org.w3c.css.sac.InputSource;
import org.w3c.css.sac.Parser;

import java.io.IOException;
import java.io.InputStream;

/**
 * Creation-Date: 12.04.2006, 15:25:39
 *
 * @author Thomas Morgner
 */
public class StyleSheetFactory implements ResourceFactory {
  public StyleSheetFactory() {
  }

  public Resource create( final ResourceManager manager,
                          final ResourceData data,
                          final ResourceKey context )
    throws ResourceCreationException, ResourceLoadingException {
    try {
      final Parser parser = CSSParserFactory.getInstance().createCSSParser();

      final ResourceKey key;
      final long version;
      if ( context == null ) {
        key = data.getKey();
        version = data.getVersion( manager );
      } else {
        key = context;
        version = -1;
      }


      final StyleSheetHandler handler = new StyleSheetHandler();
      handler.init( StyleKeyRegistry.getRegistry(), manager, key, version, null );
      parser.setDocumentHandler( handler );
      parser.setErrorHandler( handler );

      final InputStream stream = data.getResourceAsStream( manager );
      final InputSource inputSource = new InputSource();
      inputSource.setByteStream( stream );
      parser.parseStyleSheet( inputSource );

      final DependencyCollector dependencies = handler.getDependencies();
      if ( context != null ) {
        dependencies.add( data.getKey(), data.getVersion( manager ) );
      }

      CSSParserContext.getContext().destroy();

      return new CompoundResource
        ( data.getKey(), dependencies, handler.getStyleSheet(), getFactoryType() );
    } catch ( CSSParserInstantiationException e ) {
      throw new ResourceCreationException( "Failed to parse the stylesheet.", e );
    } catch ( IOException e ) {
      throw new ResourceLoadingException( "Failed to load the stylesheet.", e );
    }
  }

  public Class getFactoryType() {
    return StyleSheet.class;
  }

  public void initializeDefaults() {
    // nothing needed ...
  }
}
