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

package org.pentaho.reporting.libraries.xmlns.common;

import junit.framework.TestCase;
import org.pentaho.reporting.libraries.xmlns.LibXmlBoot;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;

public class AttributeMapTest extends TestCase {
  public AttributeMapTest() {
    super();
  }

  public AttributeMapTest( final String name ) {
    super( name );
  }

  protected void setUp() throws Exception {
    LibXmlBoot.getInstance().start();
  }

  public void testNamespaceOrder() {
    final AttributeMap<String> e = new AttributeMap<String>();
    e.setAttribute( "Namespace1", "Attr1", "value1" );
    e.setAttribute( "Namespace1", "Attr2", "value1" );
    e.setAttribute( "Namespace1", "Attr3", "value1" );
    e.setAttribute( "Namespace2", "Attr1", "value1" );
    e.setAttribute( "Namespace2", "Attr2", "value1" );
    e.setAttribute( "Namespace2", "Attr3", "value1" );
    e.setAttribute( "Namespace3", "Attr1", "value1" );

    final AttributeMap<String> e2 = new AttributeMap<String>( e );
    final String[] attributeNamespaces = e2.getNameSpaces();
    assertEquals( Arrays.asList( attributeNamespaces ), Arrays.asList( e.getNameSpaces() ) );
    for ( int i = 0; i < attributeNamespaces.length; i++ ) {
      final String namespace = attributeNamespaces[ i ];
      final String[] names = e2.getNames( namespace );
      assertEquals( Arrays.asList( names ), Arrays.asList( e.getNames( namespace ) ) );
    }
  }

  public void testNamespaceOrderClone() {
    final AttributeMap<String> e = new AttributeMap<String>();
    e.setAttribute( "Namespace1", "Attr1", "value1" );
    e.setAttribute( "Namespace1", "Attr2", "value1" );
    e.setAttribute( "Namespace1", "Attr3", "value1" );
    e.setAttribute( "Namespace2", "Attr1", "value1" );
    e.setAttribute( "Namespace2", "Attr2", "value1" );
    e.setAttribute( "Namespace2", "Attr3", "value1" );
    e.setAttribute( "Namespace3", "Attr1", "value1" );

    final AttributeMap<String> e2 = (AttributeMap<String>) ( e.clone() );
    final String[] attributeNamespaces = e2.getNameSpaces();
    assertEquals( Arrays.asList( attributeNamespaces ), Arrays.asList( e.getNameSpaces() ) );
    for ( int i = 0; i < attributeNamespaces.length; i++ ) {
      final String namespace = attributeNamespaces[ i ];
      final String[] names = e2.getNames( namespace );
      assertEquals( Arrays.asList( names ), Arrays.asList( e.getNames( namespace ) ) );
    }
  }


  public void testNamespaceOrderSerialize() throws ClassNotFoundException, IOException {
    final AttributeMap<String> e = new AttributeMap<String>();
    e.setAttribute( "Namespace1", "Attr1", "value1" );
    e.setAttribute( "Namespace1", "Attr2", "value1" );
    e.setAttribute( "Namespace1", "Attr3", "value1" );
    e.setAttribute( "Namespace2", "Attr1", "value1" );
    e.setAttribute( "Namespace2", "Attr2", "value1" );
    e.setAttribute( "Namespace2", "Attr3", "value1" );
    e.setAttribute( "Namespace3", "Attr1", "value1" );

    final AttributeMap<String> e2 = serializeAndDeserialize( e );
    final String[] attributeNamespaces = e2.getNameSpaces();
    assertEquals( Arrays.asList( attributeNamespaces ), Arrays.asList( e.getNameSpaces() ) );
    for ( int i = 0; i < attributeNamespaces.length; i++ ) {
      final String namespace = attributeNamespaces[ i ];
      final String[] names = e2.getNames( namespace );
      assertEquals( Arrays.asList( names ), Arrays.asList( e.getNames( namespace ) ) );
    }
  }

  private <T> AttributeMap<T> serializeAndDeserialize( final AttributeMap<T> e )
    throws IOException, ClassNotFoundException {
    final ByteArrayOutputStream bo = new ByteArrayOutputStream();
    final ObjectOutputStream out = new ObjectOutputStream( bo );
    out.writeObject( e );

    final ObjectInputStream oin = new ObjectInputStream( new ByteArrayInputStream( bo.toByteArray() ) );
    return (AttributeMap<T>) oin.readObject();
  }

  public void testSetAttribute() {
    final AttributeMap<String> e = new AttributeMap<String>();
    String s1 = e.setAttribute( "Namespace1", "Attr1", "value1" );
    assertEquals( null, s1 );
    assertEquals( e.keySet().size(), 1 );
    s1 = e.setAttribute( "Namespace1", "Attr1", null );
    assertEquals( "value1", s1 );
    assertEquals( e.keySet().size(), 0 );
    s1 = e.setAttribute( "Namespace1", "Attr1", null );
    assertEquals( null, s1 );
    assertEquals( e.keySet().size(), 0 );
  }

}
