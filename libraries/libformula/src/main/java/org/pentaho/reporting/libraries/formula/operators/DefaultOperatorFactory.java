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
* Copyright (c) 2006 - 2017 Hitachi Vantara and Contributors.  All rights reserved.
*/

package org.pentaho.reporting.libraries.formula.operators;

import org.pentaho.reporting.libraries.base.config.Configuration;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;

import java.util.HashMap;
import java.util.Iterator;

/**
 * Creation-Date: 02.11.2006, 12:29:27
 *
 * @author Thomas Morgner
 */
public class DefaultOperatorFactory implements OperatorFactory {
  private static final String INFIX_PREFIX = "org.pentaho.reporting.libraries.formula.operators.infix.";
  private static final String PREFIX_PREFIX = "org.pentaho.reporting.libraries.formula.operators.prefix.";
  private static final String POSTFIX_PREFIX = "org.pentaho.reporting.libraries.formula.operators.postfix.";

  private HashMap<String, InfixOperator> infixOperators;
  private HashMap<String, PrefixOperator> prefixOperators;
  private HashMap<String, PostfixOperator> postfixOperators;

  public DefaultOperatorFactory() {
    infixOperators = new HashMap<String, InfixOperator>();
    prefixOperators = new HashMap<String, PrefixOperator>();
    postfixOperators = new HashMap<String, PostfixOperator>();
  }

  public void initalize( final Configuration configuration ) {
    loadInfixOperators( configuration );
    loadPrefixOperators( configuration );
    loadPostfixOperators( configuration );
  }

  private void loadInfixOperators( final Configuration configuration ) {
    final Iterator infixKeys = configuration.findPropertyKeys( INFIX_PREFIX );
    while ( infixKeys.hasNext() ) {
      final String configKey = (String) infixKeys.next();
      if ( configKey.endsWith( ".class" ) == false ) {
        continue;
      }
      final String operatorClass = configuration.getConfigProperty( configKey );
      if ( operatorClass == null ) {
        continue;
      }
      if ( operatorClass.length() == 0 ) {
        continue;
      }
      final String tokenKey = configKey.substring
        ( 0, configKey.length() - ".class".length() ) + ".token";
      final String token = configuration.getConfigProperty( tokenKey );
      if ( token == null ) {
        continue;
      }
      final String tokenTrimmed = token.trim();

      // this assumption was breaking >=, <=, and <>
      // if (tokenTrimmed.length() != 1)
      // {
      //   continue;
      // }

      final InfixOperator operator = ObjectUtilities.loadAndInstantiate
        ( operatorClass, DefaultOperatorFactory.class, InfixOperator.class );
      if ( operator != null ) {
        infixOperators.put( tokenTrimmed, operator );
      }
    }
  }

  private void loadPrefixOperators( final Configuration configuration ) {
    final Iterator infixKeys = configuration.findPropertyKeys( PREFIX_PREFIX );
    while ( infixKeys.hasNext() ) {
      final String configKey = (String) infixKeys.next();
      if ( configKey.endsWith( ".class" ) == false ) {
        continue;
      }
      final String operatorClass = configuration.getConfigProperty( configKey );
      if ( operatorClass == null ) {
        continue;
      }
      if ( operatorClass.length() == 0 ) {
        continue;
      }
      final String tokenKey = configKey.substring
        ( 0, configKey.length() - ".class".length() ) + ".token";
      final String token = configuration.getConfigProperty( tokenKey );
      if ( token == null ) {
        continue;
      }
      final String tokenTrimmed = token.trim();

      // this is an invalid assumption
      // if (tokenTrimmed.length() != 1)
      // {
      //  continue;
      // }

      final PrefixOperator operator = ObjectUtilities.loadAndInstantiate
        ( operatorClass, DefaultOperatorFactory.class, PrefixOperator.class );
      if ( operator != null ) {
        prefixOperators.put( tokenTrimmed, operator );
      }
    }
  }

  private void loadPostfixOperators( final Configuration configuration ) {
    final Iterator infixKeys = configuration.findPropertyKeys( POSTFIX_PREFIX );
    while ( infixKeys.hasNext() ) {
      final String configKey = (String) infixKeys.next();
      if ( configKey.endsWith( ".class" ) == false ) {
        continue;
      }
      final String operatorClass = configuration.getConfigProperty( configKey );
      if ( operatorClass == null ) {
        continue;
      }
      if ( operatorClass.length() == 0 ) {
        continue;
      }
      final String tokenKey = configKey.substring
        ( 0, configKey.length() - ".class".length() ) + ".token";
      final String token = configuration.getConfigProperty( tokenKey );
      if ( token == null ) {
        continue;
      }
      final String tokenTrimmed = token.trim();
      // this is an invalid assumption
      // if (tokenTrimmed.length() != 1)
      // {
      //   continue;
      // }

      final PostfixOperator operator = ObjectUtilities.loadAndInstantiate
        ( operatorClass, DefaultOperatorFactory.class, PostfixOperator.class );
      if ( operator != null ) {
        postfixOperators.put( tokenTrimmed, operator );
      }
    }
  }

  public InfixOperator createInfixOperator( final String operator ) {
    return infixOperators.get( operator );
  }

  public PostfixOperator createPostfixOperator( final String operator ) {
    return postfixOperators.get( operator );
  }

  public PrefixOperator createPrefixOperator( final String operator ) {
    return prefixOperators.get( operator );
  }
}
