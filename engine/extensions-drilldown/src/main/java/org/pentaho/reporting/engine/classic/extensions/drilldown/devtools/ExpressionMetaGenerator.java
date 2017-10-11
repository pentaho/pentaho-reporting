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
