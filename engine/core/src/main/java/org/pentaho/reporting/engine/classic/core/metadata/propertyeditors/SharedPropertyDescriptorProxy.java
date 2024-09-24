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

package org.pentaho.reporting.engine.classic.core.metadata.propertyeditors;

import org.pentaho.reporting.engine.classic.core.metadata.SharedBeanInfo;

import java.beans.PropertyDescriptor;
import java.io.Serializable;

public class SharedPropertyDescriptorProxy implements Serializable {
  private transient SharedBeanInfo beanInfo;
  private transient PropertyDescriptor propertyDescriptor;
  private Class<?> baseClass;
  private String propertyName;

  public SharedPropertyDescriptorProxy( final SharedBeanInfo beanInfo, final String propertyName ) {
    this.beanInfo = beanInfo;
    this.baseClass = beanInfo.getBeanClass();
    this.propertyName = propertyName;
  }

  public SharedPropertyDescriptorProxy( final Class<?> baseClass, final String propertyName ) {
    this.baseClass = baseClass;
    this.propertyName = propertyName;
  }

  public synchronized PropertyDescriptor get() {
    if ( propertyDescriptor == null ) {
      propertyDescriptor = getBeanInfo().getPropertyDescriptor( propertyName );
    }
    return propertyDescriptor;
  }

  private SharedBeanInfo getBeanInfo() {
    if ( beanInfo == null ) {
      beanInfo = new SharedBeanInfo( baseClass );
    }
    return beanInfo;
  }
}
