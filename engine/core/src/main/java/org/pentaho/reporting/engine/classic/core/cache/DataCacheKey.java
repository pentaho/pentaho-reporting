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

package org.pentaho.reporting.engine.classic.core.cache;

import org.pentaho.reporting.libraries.base.util.ObjectUtilities;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class DataCacheKey {
  public static final String QUERY_CACHE = "QueryCache";

  private Map<String, Object> parameter;
  private Map<String, Object> attributes;

  public DataCacheKey() {
    parameter = new HashMap<String, Object>();
    attributes = new HashMap<String, Object>();
  }

  public void addParameter( final String key, final Object value ) {
    if ( key == null ) {
      throw new NullPointerException();
    }
    parameter.put( key, value );
  }

  public void addAttribute( final String key, final Object value ) {
    if ( key == null ) {
      throw new NullPointerException();
    }
    attributes.put( key, value );
  }

  public boolean equals( final Object o ) {
    if ( this == o ) {
      return true;
    }
    if ( o == null || getClass() != o.getClass() ) {
      return false;
    }

    final DataCacheKey that = (DataCacheKey) o;

    if ( !equalsMap( attributes, that.attributes ) ) {
      return false;
    }
    if ( !equalsMap( parameter, that.parameter ) ) {
      return false;
    }

    return true;
  }

  public int hashCode() {
    int result = hashCodeMap( parameter );
    result = 31 * result + hashCodeMap( attributes );
    return result;
  }

  public void makeReadOnly() {
    // Make the following maps immutable. This method should be called after
    // the parameters & attributes have been seeded.
    parameter = toImmutable( parameter );
    attributes = toImmutable( attributes );
  }

  private static Map<String, Object> toImmutable( Map<String, Object> map ) {
    switch ( map.size() ) {
      case 0:
        return Collections.emptyMap();
      case 1:
        Map.Entry<String, Object> entry = map.entrySet().iterator().next();
        return Collections.singletonMap( entry.getKey(), entry.getValue() );
      default:
        return Collections.unmodifiableMap( map );
    }
  }

  private boolean equalsMap( final Map<String, Object> values, final Map<String, Object> otherValues ) {
    if ( otherValues.size() != values.size() ) {
      return false;
    }

    for ( final String key : values.keySet() ) {
      final Object value = values.get( key );
      final Object otherValue = otherValues.get( key );

      if ( value == null && otherValue == null ) {
        continue;
      }

      if ( value instanceof Object[] && otherValue instanceof Object[] ) {
        if ( ObjectUtilities.equalArray( (Object[]) value, (Object[]) otherValue ) == false ) {
          return false;
        }
      } else if ( ObjectUtilities.equal( value, otherValue ) == false ) {
        return false;
      }
    }
    return true;
  }

  private int hashCodeMap( final Map<String, Object> values ) {
    int hashCode = values.size();

    for ( final String key : values.keySet() ) {
      final Object value = values.get( key );

      hashCode = 31 * hashCode + ( key != null ? key.hashCode() : 0 );
      if ( value == null ) {
        hashCode = 31 * hashCode;
      } else if ( value instanceof Object[] ) {
        hashCode = 31 * hashCode + ObjectUtilities.hashCode( (Object[]) value );
      } else {
        hashCode = 31 * hashCode + value.hashCode();
      }
    }
    return hashCode;
  }
}
