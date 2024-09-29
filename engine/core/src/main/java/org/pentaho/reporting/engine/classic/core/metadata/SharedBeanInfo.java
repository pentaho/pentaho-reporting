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


package org.pentaho.reporting.engine.classic.core.metadata;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.HashMap;

public class SharedBeanInfo {
  private Class beanClass;
  private BeanInfo beanInfo;
  private HashMap<String, PropertyDescriptor> propertyDescriptors;

  public SharedBeanInfo( final Class beanClass ) {
    this.beanClass = beanClass;
  }

  public Class getBeanClass() {
    return beanClass;
  }

  public BeanInfo getBeanInfo() {
    if ( beanInfo == null ) {
      this.propertyDescriptors = new HashMap<String, PropertyDescriptor>();
      try {
        beanInfo = Introspector.getBeanInfo( beanClass );
        final PropertyDescriptor[] descriptors = beanInfo.getPropertyDescriptors();
        for ( int i = 0; i < descriptors.length; i++ ) {
          final PropertyDescriptor descriptor = descriptors[i];
          propertyDescriptors.put( descriptor.getName(), descriptor );
        }
      } catch ( IntrospectionException e ) {
        throw new IllegalStateException( "Cannot introspect specified " + beanClass );
      }
    }
    return beanInfo;
  }

  public PropertyDescriptor getPropertyDescriptor( final String name ) {
    if ( beanInfo == null ) {
      // initialize propery map ..
      getBeanInfo();
    }
    return propertyDescriptors.get( name );
  }
}
