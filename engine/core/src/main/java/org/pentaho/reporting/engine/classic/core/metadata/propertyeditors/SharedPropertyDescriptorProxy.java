/*
 * This program is free software; you can redistribute it and/or modify it under the
 *  terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
 *  Foundation.
 *
 *  You should have received a copy of the GNU Lesser General Public License along with this
 *  program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 *  or from the Free Software Foundation, Inc.,
 *  51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 *  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *  See the GNU Lesser General Public License for more details.
 *
 *  Copyright (c) 2006 - 2017 Hitachi Vantara..  All rights reserved.
 */

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
