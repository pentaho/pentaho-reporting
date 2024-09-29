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


package org.pentaho.reporting.engine.classic.core.util.beans;

import org.pentaho.reporting.engine.classic.core.util.PropertyLookupParser;
import org.pentaho.reporting.libraries.base.util.CSVTokenizer;

/**
 * A lookup parser that uses the standard ${..} syntax to lookup bean references. It can chain lookups along standard
 * bean properties.
 */
public abstract class BeanPropertyLookupParser extends PropertyLookupParser {
  protected BeanPropertyLookupParser() {
  }

  /**
   * @param name
   * @return
   */
  protected abstract Object performInitialLookup( String name );

  protected String lookupVariable( final String entity ) {
    // first, split the entity into separate strings (separator is '.').

    final CSVTokenizer tokenizer = new CSVTokenizer( entity, "." );
    if ( tokenizer.hasMoreTokens() ) {
      final String name = tokenizer.nextToken();
      final Object base = performInitialLookup( name );
      try {
        if ( tokenizer.hasMoreTokens() ) {
          return BeanPropertyLookupParser.continueLookupVariable( tokenizer, base );
        } else {
          return ConverterRegistry.toAttributeValue( base );
        }
      } catch ( BeanException e ) {
        return entity;
      }
    }
    return entity;
  }

  private static String continueLookupVariable( final CSVTokenizer tokenizer, final Object parent )
    throws BeanException {
    if ( tokenizer.hasMoreTokens() ) {
      final String name = tokenizer.nextToken();
      final Object base = ConverterRegistry.toPropertyValue( name, parent.getClass() );
      if ( tokenizer.hasMoreTokens() ) {
        return continueLookupVariable( tokenizer, base );
      } else {
        return ConverterRegistry.toAttributeValue( base );
      }
    }
    return null;
  }

}
