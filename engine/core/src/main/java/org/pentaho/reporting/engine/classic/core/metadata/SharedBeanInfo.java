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
