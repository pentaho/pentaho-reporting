/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


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
