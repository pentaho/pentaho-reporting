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

package org.pentaho.reporting.libraries.css.keys.page;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;

/**
 * This will be replaced by a 'media-names' implementation according to ftp://ftp.pwg
 * .org/pub/pwg/candidates/cs-pwgmsn10-20020226-5101.1.pdf
 *
 * @author Thomas Morgner
 */
public class PageSizeFactory {
  private static PageSizeFactory factory;

  public static synchronized PageSizeFactory getInstance() {
    if ( factory == null ) {
      factory = new PageSizeFactory();
      factory.registerKnownMedias();
    }
    return factory;
  }

  private HashMap knownPageSizes;

  private PageSizeFactory() {
    knownPageSizes = new HashMap();
  }

  public PageSize getPageSizeByName( String name ) {
    return (PageSize) knownPageSizes.get( name.toLowerCase() );
  }

  public String[] getPageSizeNames() {
    return (String[]) knownPageSizes.keySet().toArray( new String[ knownPageSizes.size() ] );
  }

  private void registerKnownMedias() {
    Field[] fields = PageSize.class.getFields();
    for ( int i = 0; i < fields.length; i++ ) {
      try {
        Field f = fields[ i ];
        if ( Modifier.isPublic( f.getModifiers() ) == false ||
          Modifier.isStatic( f.getModifiers() ) == false ) {
          continue;
        }
        final Object o = f.get( this );
        if ( o instanceof PageSize == false ) {
          // Log.debug ("Is no valid pageformat definition");
          continue;
        }
        final PageSize pageSize = (PageSize) o;
        knownPageSizes.put( f.getName().toLowerCase(), pageSize );
      } catch ( IllegalAccessException aie ) {
        // Log.debug ("There is no pageformat " + name + " accessible.");
      }
    }
  }

}
