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

package org.pentaho.reporting.libraries.css.namespace;

import org.pentaho.reporting.libraries.base.config.Configuration;
import org.pentaho.reporting.libraries.resourceloader.ResourceException;
import org.pentaho.reporting.libraries.resourceloader.ResourceKey;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Known and supported namespaces.
 *
 * @author Thomas Morgner
 */
public final class Namespaces {
  public static final String LIBLAYOUT_NAMESPACE =
    "http://jfreereport.sourceforge.net/namespaces/layout";

  /**
   * The XML-Namespace is used for the 'id' attribute.
   */
  public static final String XML_NAMESPACE =
    "http://www.w3.org/XML/1998/namespace";

  /**
   * The XML-Namespace is used for the 'id' attribute.
   */
  // The old HTML namespace is not supported, use XHTML instead.
  //  public static final String HTML_NAMESPACE =
  //          "http://www.w3.org/TR/REC-html40";
  public static final String XHTML_NAMESPACE =
    "http://www.w3.org/1999/xhtml";


  private Namespaces() {
  }

  public static NamespaceDefinition[] createFromConfig
    ( final Configuration config,
      final String prefix,
      final ResourceManager resourceManager ) {
    final ArrayList retvals = new ArrayList();
    final Iterator keys = config.findPropertyKeys( prefix );
    while ( keys.hasNext() ) {
      final String key = (String) keys.next();
      if ( key.endsWith( ".Uri" ) == false ) {
        continue;
      }
      final String nsPrefix = key.substring( 0, key.length() - 3 );
      final String uri = config.getConfigProperty( key );
      if ( uri == null ) {
        continue;
      }
      final String trimmedUri = uri.trim();
      if ( trimmedUri.length() == 0 ) {
        continue;
      }
      final String classAttr = config.getConfigProperty( nsPrefix + "ClassAttr" );
      final String styleAttr = config.getConfigProperty( nsPrefix + "StyleAttr" );
      final String prefixAttr = config.getConfigProperty( nsPrefix + "Prefix" );
      final String defaultStyle = config.getConfigProperty(
        nsPrefix + "Default-Style" );

      ResourceKey styleResourceKey = null;
      if ( resourceManager != null ) {
        try {
          if ( defaultStyle != null ) {
            styleResourceKey = resourceManager.createKey( defaultStyle );
          }
        } catch ( ResourceException e ) {
          // ignored ..
          //          Log.info("Unable to create resourcekey for style " + trimmedUri);
        }
      }
      retvals.add( new DefaultNamespaceDefinition
        ( trimmedUri, styleResourceKey, classAttr, styleAttr, prefixAttr ) );
    }

    return (NamespaceDefinition[])
      retvals.toArray( new NamespaceDefinition[ retvals.size() ] );
  }
}
