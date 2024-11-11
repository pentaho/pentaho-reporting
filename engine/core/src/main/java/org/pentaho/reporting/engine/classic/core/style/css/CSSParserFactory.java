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


package org.pentaho.reporting.engine.classic.core.style.css;

import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.style.css.namespaces.NamespaceCollection;
import org.pentaho.reporting.engine.classic.core.style.css.selector.CSSSelectorFactory;
import org.pentaho.reporting.engine.classic.core.style.css.selector.conditions.CSSConditionFactory;
import org.pentaho.reporting.libraries.base.config.Configuration;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.w3c.css.sac.Parser;
import org.w3c.css.sac.helpers.ParserFactory;

/**
 * Creates a new CSS parser by first looking for a specified parser in the libLayout configuration and if that fails, by
 * using the W3C parser factory.
 *
 * @author Thomas Morgner
 */
public class CSSParserFactory {
  private static CSSParserFactory parserFactory;

  public static synchronized CSSParserFactory getInstance() {
    if ( parserFactory == null ) {
      parserFactory = new CSSParserFactory();
    }
    return parserFactory;
  }

  private CSSParserFactory() {
  }

  public Parser createCSSParser( final NamespaceCollection namespaceCollection ) throws InstantiationException {
    final Configuration config = ClassicEngineBoot.getInstance().getGlobalConfig();
    final String parserClass = config.getConfigProperty( "org.w3c.css.sac.Parser" );
    if ( parserClass != null ) {
      final Parser p = ObjectUtilities.loadAndInstantiate( parserClass, CSSParserFactory.class, Parser.class );
      if ( p != null ) {
        p.setConditionFactory( new FixNamespaceConditionFactory( new CSSConditionFactory(), namespaceCollection ) );
        p.setSelectorFactory( new FixNamespaceSelectorFactory( new CSSSelectorFactory(), namespaceCollection ) );
        return p;
      }
    }
    try {
      final Parser p = new ParserFactory().makeParser();
      if ( p == null ) {
        return null;
      }
      p.setConditionFactory( new FixNamespaceConditionFactory( new CSSConditionFactory(), namespaceCollection ) );
      p.setSelectorFactory( new FixNamespaceSelectorFactory( new CSSSelectorFactory(), namespaceCollection ) );
      return p;
    } catch ( Exception e ) {
      e.printStackTrace();
      throw new InstantiationException( e.getMessage() );
    }
  }
}
