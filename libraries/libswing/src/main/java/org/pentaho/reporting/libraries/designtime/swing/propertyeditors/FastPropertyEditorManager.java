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

package org.pentaho.reporting.libraries.designtime.swing.propertyeditors;

import java.beans.PropertyEditor;
import java.beans.PropertyEditorManager;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;

public class FastPropertyEditorManager {
  private static final Object NULL;
  private static final FastPropertyEditorManager instance;
  private final HashMap<Class, Object> propertyEditors;

  static {
    NULL = new Object();
    instance = new FastPropertyEditorManager();
  }

  private FastPropertyEditorManager() {
    this.propertyEditors = new HashMap<Class, Object>();
    this.propertyEditors.put( Number.class, NULL );
    this.propertyEditors.put( Integer.class, NULL );
    this.propertyEditors.put( Float.class, NULL );
    this.propertyEditors.put( Double.class, NULL );
    this.propertyEditors.put( Short.class, NULL );
    this.propertyEditors.put( Byte.class, NULL );
    this.propertyEditors.put( Long.class, NULL );
    this.propertyEditors.put( BigInteger.class, NULL );
    this.propertyEditors.put( BigDecimal.class, NULL );
    this.propertyEditors.put( String.class, NULL );
  }

  public static PropertyEditor findEditor( final Class c ) {
    return instance.internalFindEditor( c );
  }

  private PropertyEditor internalFindEditor( final Class c ) {
    synchronized( propertyEditors ) {
      final Object o = propertyEditors.get( c );
      if ( o == NULL ) {
        return null;
      }
      if ( o != null ) {
        final Class editorClass = (Class) o;
        try {
          return (PropertyEditor) editorClass.newInstance();
        } catch ( final Exception e ) {
          return null;
        }
      }
    }

    // this is a long-running task
    final PropertyEditor retval = PropertyEditorManager.findEditor( c );
    synchronized( propertyEditors ) {
      if ( retval == null ) {
        propertyEditors.put( c, NULL );
      } else {
        propertyEditors.put( c, retval.getClass() );
      }
    }
    return retval;
  }
}
