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

package generators;

import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.util.beans.BeanUtility;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;

public class ValidateBeanInfo {
  public static void main( String[] args ) throws Exception {
    ClassicEngineBoot.getInstance().start();
    //    ExpressionQueryTool eqt = new ExpressionQueryTool();
    //    eqt.processDirectory(null);
    //    final Class[] classes = eqt.getExpressions();
    final Class[] classes = new Class[] {};

    for ( int i = 0; i < classes.length; i++ ) {
      final Class aClass = classes[ i ];

      final BeanInfo beanInfo = Introspector.getBeanInfo( aClass );
      final BeanUtility bu = new BeanUtility( aClass.newInstance() );
      final PropertyDescriptor[] descriptors = beanInfo.getPropertyDescriptors();
      for ( int j = 0; j < descriptors.length; j++ ) {
        final PropertyDescriptor descriptor = descriptors[ j ];
        final String key = descriptor.getName();

        if ( "runtime".equals( key ) ) {
          continue;
        }
        if ( "active".equals( key ) ) {
          continue;
        }
        if ( "preserve".equals( key ) ) {
          continue;
        }

        if ( descriptor.getReadMethod() == null ) {
          //          System.out.println("Skipping " + key + " from " + aClass + " No read method");
          continue;
        }
        if ( descriptor.getWriteMethod() == null ) {
          //          System.out.println("Skipping " + key + " from " + aClass + " No write method");
          continue;
        }

        if ( bu.getPropertyType( key ) == null ) {
          System.out.println( "NOT Accepting " + key + " from " + aClass );
        }
        System.out.println( "Accepting " + key + " from " + aClass );


      }

    }
  }
}
