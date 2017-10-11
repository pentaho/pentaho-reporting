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

package org.pentaho.reporting.engine.classic.core.modules.misc.datafactory;

import org.mozilla.javascript.NativeArray;
import org.mozilla.javascript.NativeJavaObject;

/**
 * In case we do not run on a JDK 1.6 from Oracle, the "sun" name-space may not be available. We shield ourselves via
 * dynamic class loading.
 */
public class RhinoValueConverter implements ScriptValueConverter {
  public RhinoValueConverter() {
  }

  public Object convert( final Object o ) {
    if ( o instanceof NativeJavaObject ) {
      final NativeJavaObject object = (NativeJavaObject) o;
      return object.unwrap();
    }
    if ( o instanceof NativeArray ) {
      final NativeArray array = (NativeArray) o;
      final Object[] result = new Object[(int) array.getLength()];
      for ( final Object val : array.getIds() ) {
        final int index = (Integer) val;
        result[index] = array.get( index, null );
      }
      return result;
    }
    return null;
  }
}
