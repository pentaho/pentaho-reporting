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

import org.pentaho.reporting.libraries.base.config.Configuration;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.libraries.css.LibCssBoot;
import org.pentaho.reporting.libraries.css.selectors.CSSSelectorFactory;
import org.pentaho.reporting.libraries.css.selectors.conditions.CSSConditionFactory;
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

  public Parser createCSSParser()
    throws CSSParserInstantiationException {
    final Configuration config = LibCssBoot.getInstance().getGlobalConfig();
    final String parserClass = config.getConfigProperty( "org.pentaho.reporting.libraries.css.Parser" );
    if ( parserClass != null ) {
      Parser p = (Parser) ObjectUtilities.loadAndInstantiate
        ( parserClass, CSSParserFactory.class, Parser.class );
      if ( p != null ) {
        p.setConditionFactory( new FixNamespaceConditionFactory( new CSSConditionFactory() ) );
        p.setSelectorFactory( new FixNamespaceSelectorFactory( new CSSSelectorFactory() ) );
        return p;
      }
    }
    try {
      Parser p = new ParserFactory().makeParser();
      if ( p == null ) {
        return null;
      }
      p.setConditionFactory( new FixNamespaceConditionFactory( new CSSConditionFactory() ) );
      p.setSelectorFactory( new FixNamespaceSelectorFactory( new CSSSelectorFactory() ) );
      return p;
    } catch ( Exception e ) {
      throw new CSSParserInstantiationException( e.getMessage(), e );
    }
  }
}
