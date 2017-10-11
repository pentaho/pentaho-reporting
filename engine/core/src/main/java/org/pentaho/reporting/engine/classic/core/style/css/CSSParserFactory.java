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
