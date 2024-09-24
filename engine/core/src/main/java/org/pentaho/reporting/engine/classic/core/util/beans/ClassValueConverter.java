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

import org.pentaho.reporting.engine.classic.core.modules.parser.base.compat.CompatibilityMapperUtil;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;

/**
 * A class that handles the conversion of {@link Integer} attributes to and from their {@link String} representation.
 *
 * @author Thomas Morgner
 */
public class ClassValueConverter implements ValueConverter {
  public ClassValueConverter() {
  }

  public String toAttributeValue( final Object o ) throws BeanException {
    if ( o == null ) {
      throw new NullPointerException();
    }
    if ( o instanceof Class ) {
      final Class c = (Class) o;
      return c.getName();
    }
    throw new BeanException( "Failed to convert object of type " + o.getClass() + ": Not a Class." );
  }

  public Object toPropertyValue( final String s ) throws BeanException {
    if ( s == null ) {
      throw new NullPointerException();
    }
    try {
      final ClassLoader loader = ObjectUtilities.getClassLoader( ClassValueConverter.class );
      return Class.forName( CompatibilityMapperUtil.mapClassName( s ), false, loader );
    } catch ( Throwable e ) {
      throw new BeanException( "Specified class does not exist or cannot be loaded.", e );
    }
  }
}
