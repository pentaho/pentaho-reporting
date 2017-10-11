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
