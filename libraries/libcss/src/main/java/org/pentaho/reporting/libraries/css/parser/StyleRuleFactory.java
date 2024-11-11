/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


package org.pentaho.reporting.libraries.css.parser;

import org.pentaho.reporting.libraries.css.dom.DocumentContext;
import org.pentaho.reporting.libraries.css.model.CSSDeclarationRule;
import org.pentaho.reporting.libraries.css.model.CSSStyleRule;
import org.pentaho.reporting.libraries.css.model.StyleKeyRegistry;
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

/**
 * Parses a single style rule.
 *
 * @author Thomas Morgner
 */
public class StyleRuleFactory implements ResourceFactory {
  public StyleRuleFactory() {
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

      final DocumentContext documentContext;

      final StyleSheetHandler handler = new StyleSheetHandler();
      handler.init( StyleKeyRegistry.getRegistry(), manager, key, version, null );
      parser.setDocumentHandler( handler );

      final InputSource inputSource = new InputSource();
      inputSource.setByteStream( data.getResourceAsStream( manager ) );

      handler.initParseContext( inputSource );
      handler.setStyleRule( new CSSStyleRule( null, null ) );
      parser.parseStyleDeclaration( inputSource );

      final DependencyCollector dependencies = handler.getDependencies();
      if ( context != null ) {
        dependencies.add( data.getKey(), data.getVersion( manager ) );
      }

      CSSParserContext.getContext().destroy();

      final CSSDeclarationRule styleRule = handler.getStyleRule();
      if ( styleRule == null ) {
        throw new ResourceCreationException( "Damn, the style rule is null" );
      }
      return new CompoundResource( data.getKey(), dependencies, styleRule, getFactoryType() );
    } catch ( CSSParserInstantiationException e ) {
      throw new ResourceCreationException( "Failed to parse the stylesheet." );
    } catch ( IOException e ) {
      throw new ResourceLoadingException( "Failed to load the stylesheet." );
    }
  }

  public Class getFactoryType() {
    return CSSDeclarationRule.class;
  }

  public void initializeDefaults() {
    // nothing needed ...
  }
}
