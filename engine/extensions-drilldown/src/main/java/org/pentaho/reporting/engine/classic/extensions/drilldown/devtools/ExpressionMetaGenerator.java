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

package org.pentaho.reporting.engine.classic.extensions.drilldown.devtools;

import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.devtools.ExpressionQueryTool;
import org.pentaho.reporting.engine.classic.core.function.OutputFunction;
import org.pentaho.reporting.libraries.xmlns.common.AttributeList;
import org.pentaho.reporting.libraries.xmlns.writer.DefaultTagDescription;
import org.pentaho.reporting.libraries.xmlns.writer.XmlWriter;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.io.PrintWriter;

public class ExpressionMetaGenerator {
  private static final String META_NAMESPACE = "http://jfreereport.sourceforge.net/namespaces/engine/classic/metadata";

  public static void main( String[] args ) throws IOException, IntrospectionException {
    ClassicEngineBoot.getInstance().start();
    ExpressionQueryTool eqt = new ExpressionQueryTool();
    eqt.processDirectory( null );
    final Class[] classes = eqt.getExpressions();

    final DefaultTagDescription dtd = new DefaultTagDescription();
    dtd.setNamespaceHasCData( META_NAMESPACE, false );

    final XmlWriter writer = new XmlWriter( new PrintWriter( System.out ), dtd );

    final AttributeList attrList = new AttributeList();
    attrList.addNamespaceDeclaration( "", META_NAMESPACE );
    writer.writeTag( META_NAMESPACE, "meta-data", attrList, XmlWriter.OPEN );

    for ( int i = 0; i < classes.length; i++ ) {
      final Class aClass = classes[ i ];

      if ( OutputFunction.class.isAssignableFrom( aClass ) ) {
        // Output functions will not be recognized.
        continue;
      }
      if ( aClass.getName().indexOf( '$' ) >= 0 ) {
        // Inner-Classes will not be recognized.
        continue;
      }

      final AttributeList expressionAttrList = new AttributeList();
      expressionAttrList.setAttribute( META_NAMESPACE, "class", aClass.getName() );
      expressionAttrList
        .setAttribute( META_NAMESPACE, "bundle-name", "org.pentaho.reporting.engine.classic.core.metadata.messages" );
      expressionAttrList.setAttribute( META_NAMESPACE, "result", "java.lang.Object" );
      expressionAttrList.setAttribute( META_NAMESPACE, "expert", "false" );
      expressionAttrList.setAttribute( META_NAMESPACE, "hidden", "false" );
      expressionAttrList.setAttribute( META_NAMESPACE, "preferred", "false" );
      writer.writeTag( META_NAMESPACE, "expression", expressionAttrList, XmlWriter.OPEN );

      final BeanInfo beanInfo = Introspector.getBeanInfo( aClass );
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

        if ( descriptor.getReadMethod() == null || descriptor.getWriteMethod() == null ) {
          continue;
        }

        final AttributeList propAttrList = new AttributeList();
        propAttrList.setAttribute( META_NAMESPACE, "name", descriptor.getName() );
        if ( "name".equals( key ) ) {
          propAttrList.setAttribute( META_NAMESPACE, "mandatory", "true" );
          propAttrList.setAttribute( META_NAMESPACE, "preferred", "true" );
          propAttrList.setAttribute( META_NAMESPACE, "value-role", "Name" );
          propAttrList.setAttribute( META_NAMESPACE, "expert", "false" );
        } else {
          propAttrList.setAttribute( META_NAMESPACE, "mandatory", "false" );
          propAttrList.setAttribute( META_NAMESPACE, "preferred", "false" );
          propAttrList.setAttribute( META_NAMESPACE, "value-role", "Value" );
          if ( "dependencyLevel".equals( key ) ) {
            propAttrList.setAttribute( META_NAMESPACE, "expert", "true" );
          } else {
            propAttrList.setAttribute( META_NAMESPACE, "expert", "false" );
          }
        }
        propAttrList.setAttribute( META_NAMESPACE, "hidden", "false" );
        writer.writeTag( META_NAMESPACE, "property", propAttrList, XmlWriter.CLOSE );

      }

      writer.writeCloseTag();
    }

    writer.writeCloseTag();
    writer.flush();
  }
}
