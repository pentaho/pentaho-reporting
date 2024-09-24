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
