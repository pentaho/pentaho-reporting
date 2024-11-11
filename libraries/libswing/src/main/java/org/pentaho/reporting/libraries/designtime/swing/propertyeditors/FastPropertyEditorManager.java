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
