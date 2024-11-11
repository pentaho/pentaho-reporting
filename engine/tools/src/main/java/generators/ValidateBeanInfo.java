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
