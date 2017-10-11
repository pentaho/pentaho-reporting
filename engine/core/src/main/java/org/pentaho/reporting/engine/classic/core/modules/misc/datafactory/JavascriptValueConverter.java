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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.lang.reflect.Method;

/**
 * In case we do not run on a JDK 1.6 from Oracle, the "sun" name-space may not be available. We shield ourselves via
 * dynamic class loading. The javax.scripting API is a nightmare to work with when it comes to converting values in and
 * out of the context of the script.
 * <p/>
 * We have to rely on these hacks to make it work - luckily everyone is either using Sun's JDK or OpenJDK these days (I
 * hope).e
 */
public class JavascriptValueConverter implements ScriptValueConverter {
  private static final Log logger = LogFactory.getLog( JavascriptValueConverter.class );

  public JavascriptValueConverter() {
  }

  public Object convert( final Object o ) {
    if ( o == null ) {
      return null;
    }
    final String classString = o.getClass().getName();
    if ( "sun.org.mozilla.javascript.internal.Wrapper".equals( classString )
        || "sun.org.mozilla.javascript.Wrapper".equals( classString ) ) {
      try {
        final Method unwrap = o.getClass().getMethod( "unwrap" );
        return unwrap.invoke( o );
      } catch ( Throwable e ) {
        logger.debug( "Unable to call 'unwrap' on suspected javascript object." );
        return null;
      }
    }
    if ( "sun.org.mozilla.javascript.internal.NativeArray".equals( classString ) ) {
      final String scriptableClassName = "sun.org.mozilla.javascript.internal.Scriptable";
      return unwrapArray( o, scriptableClassName );
    } else if ( "sun.org.mozilla.javascript.NativeArray".equals( classString ) ) {
      final String scriptableClassName = "sun.org.mozilla.javascript.Scriptable";
      return unwrapArray( o, scriptableClassName );
    }
    return null;
  }

  private Object unwrapArray( final Object o, final String scriptableClassName ) {
    try {
      final Method getLength = o.getClass().getMethod( "getLength" );
      final Long length = (Long) getLength.invoke( o );
      final Object[] result = new Object[length.intValue()];
      final Method getIds = o.getClass().getMethod( "getIds" );
      final Object[] ids = (Object[]) getIds.invoke( o );
      final Method get = o.getClass().getMethod( "get", Integer.TYPE, Class.forName( scriptableClassName ) );

      for ( final Object val : ids ) {
        final int index = (Integer) val;
        result[index] = get.invoke( o, index, null );
      }
      return result;
    } catch ( Throwable e ) {
      e.printStackTrace();
      logger.debug( "Unable to call 'unwrap' on suspected javascript array." );
      return null;
    }
  }
}
