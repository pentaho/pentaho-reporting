/*
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
 * Copyright (c) 2000 - 2017 Hitachi Vantara, Simba Management Limited and Contributors...  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;

import junit.framework.TestCase;

import org.junit.Assert;
import org.pentaho.reporting.engine.classic.core.filter.types.LabelType;
import org.pentaho.reporting.engine.classic.core.filter.types.NumberFieldType;
import org.pentaho.reporting.engine.classic.core.function.Expression;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleKeys;

public class ElementTest extends TestCase {
  public ElementTest( final String s ) {
    super( s );
  }

  protected void setUp() throws Exception {
    ClassicEngineBoot.getInstance().start();
  }

  public void testElementCreate() {
    final Element e = new Element();
    assertNotNull( e.getDataSource() );
    assertNotNull( e.getStyle() );
    assertNotNull( e.getName() );
    assertTrue( e.isVisible() );
    assertNull( e.getParent() );
  }

  public void testElementAttributeCopyOnWrite() {
    final Element e = new Element();
    final Element clone = e.clone();
    e.setAttribute( "namespace", "name", "value" );
    assertNull( clone.getAttribute( "namespace", "name" ) );
  }

  public void testElementAttributeCopyOnWrite2() {
    final Element e = new Element();
    final Element clone = e.clone();
    clone.setAttribute( "namespace", "name", "value" );
    assertNull( e.getAttribute( "namespace", "name" ) );
  }

  public void testElementClone() throws CloneNotSupportedException {
    final Band band = new Band();
    final Element e = new Element();
    band.addElement( e );
    assertNotNull( e.getParent() );
    assertNotNull( e.getDataSource() );
    assertNotNull( e.getStyle() );
    assertNotNull( e.getName() );
    assertTrue( e.isVisible() );

    final Element clone = (Element) e.clone();
    assertNull( clone.getParent() );
    assertNotNull( clone.getDataSource() );
    assertNotNull( clone.getStyle() );
    assertNotNull( clone.getName() );
    assertTrue( clone.isVisible() );

    final Band clonedBand = (Band) band.clone();
    assertNull( clonedBand.getParent() );
    assertNotNull( clonedBand.getDataSource() );
    assertNotNull( clonedBand.getStyle() );
    assertNotNull( clonedBand.getName() );
    assertTrue( clonedBand.isVisible() );

    final Element clientElement = clonedBand.getElement( 0 );
    assertNotNull( clientElement.getParent() );
    assertNotNull( clientElement.getDataSource() );
    assertNotNull( clientElement.getStyle() );
    assertNotNull( clientElement.getName() );
    assertTrue( clientElement.isVisible() );
    assertEquals( clonedBand, clientElement.getParent() );
  }

  public void testElementMethods() {
    final Element e = new Element();
    assertTrue( e.isVisible() );
    e.setVisible( false );
    assertTrue( e.isVisible() == false );
    e.setVisible( true );
    assertTrue( e.isVisible() );

    try {
      e.setDataSource( null );
      fail();
    } catch ( NullPointerException npe ) {
      // expected, ignored
    }
    e.toString();
  }

  public void testSerialize() throws Exception {
    final Element e = new Element();
    final Element e2 = serializeAndDeserialize( e );
    assertNotNull( e2 ); // cannot assert equals, as this is not implemented ...
  }

  private Element serializeAndDeserialize( final Element e ) throws IOException, ClassNotFoundException {
    final ByteArrayOutputStream bo = new ByteArrayOutputStream();
    final ObjectOutputStream out = new ObjectOutputStream( bo );
    out.writeObject( e );

    final ObjectInputStream oin = new ObjectInputStream( new ByteArrayInputStream( bo.toByteArray() ) );
    return (Element) oin.readObject();
  }

  public void testSerializeAttributeOrder() throws Exception {
    final Element e = new Element();
    e.setAttribute( "Namespace1", "Attr1", "Value1" );
    e.setAttribute( "Namespace1", "Attr2", "Value1" );
    e.setAttribute( "Namespace1", "Attr3", "Value1" );
    e.setAttribute( "Namespace2", "Attr1", "Value1" );
    e.setAttribute( "Namespace2", "Attr2", "Value1" );
    e.setAttribute( "Namespace2", "Attr3", "Value1" );
    final Element e2 = serializeAndDeserialize( e );
    final String[] attributeNamespaces = e2.getAttributeNamespaces();
    assertEquals( Arrays.asList( attributeNamespaces ), Arrays.asList( e.getAttributeNamespaces() ) );
    for ( int i = 0; i < attributeNamespaces.length; i++ ) {
      final String namespace = attributeNamespaces[i];
      final String[] names = e2.getAttributeNames( namespace );
      assertEquals( Arrays.asList( names ), Arrays.asList( e.getAttributeNames( namespace ) ) );

    }
  }

  public void testCloneAttributeOrder() throws Exception {
    final Element e = new Element();
    e.setAttribute( "Namespace1", "Attr1", "Value1" );
    e.setAttribute( "Namespace1", "Attr2", "Value1" );
    e.setAttribute( "Namespace1", "Attr3", "Value1" );
    e.setAttribute( "Namespace2", "Attr1", "Value1" );
    e.setAttribute( "Namespace2", "Attr2", "Value1" );
    e.setAttribute( "Namespace2", "Attr3", "Value1" );
    final Element e2 = (Element) e.clone();
    final String[] attributeNamespaces = e2.getAttributeNamespaces();
    assertEquals( Arrays.asList( attributeNamespaces ), Arrays.asList( e.getAttributeNamespaces() ) );
    for ( int i = 0; i < attributeNamespaces.length; i++ ) {
      final String namespace = attributeNamespaces[i];
      final String[] names = e2.getAttributeNames( namespace );
      assertEquals( Arrays.asList( names ), Arrays.asList( e.getAttributeNames( namespace ) ) );

    }
  }

  public void testROAttributeOrder() throws Exception {
    final Element e = new Element();
    e.setAttribute( "Namespace1", "Attr1", "Value1" );
    e.setAttribute( "Namespace1", "Attr2", "Value1" );
    e.setAttribute( "Namespace1", "Attr3", "Value1" );
    e.setAttribute( "Namespace2", "Attr1", "Value1" );
    e.setAttribute( "Namespace2", "Attr2", "Value1" );
    e.setAttribute( "Namespace2", "Attr3", "Value1" );
    final ReportAttributeMap e2 = e.getAttributes().createUnmodifiableMap();
    final String[] attributeNamespaces = e2.getNameSpaces();
    assertEquals( Arrays.asList( attributeNamespaces ), Arrays.asList( e.getAttributeNamespaces() ) );
    for ( int i = 0; i < attributeNamespaces.length; i++ ) {
      final String namespace = attributeNamespaces[i];
      final String[] names = e2.getNames( namespace );
      assertEquals( Arrays.asList( names ), Arrays.asList( e.getAttributeNames( namespace ) ) );

    }
  }

  public void testCopyAttributeOrder() throws Exception {
    final Element e = new Element();
    e.setAttribute( "Namespace1", "Attr1", "Value1" );
    e.setAttribute( "Namespace1", "Attr2", "Value1" );
    e.setAttribute( "Namespace1", "Attr3", "Value1" );
    e.setAttribute( "Namespace2", "Attr1", "Value1" );
    e.setAttribute( "Namespace2", "Attr2", "Value1" );
    e.setAttribute( "Namespace2", "Attr3", "Value1" );
    final ReportAttributeMap e2 = new ReportAttributeMap( e.getAttributes() );
    final String[] attributeNamespaces = e2.getNameSpaces();
    assertEquals( Arrays.asList( attributeNamespaces ), Arrays.asList( e.getAttributeNamespaces() ) );
    for ( int i = 0; i < attributeNamespaces.length; i++ ) {
      final String namespace = attributeNamespaces[i];
      final String[] names = e2.getNames( namespace );
      assertEquals( Arrays.asList( names ), Arrays.asList( e.getAttributeNames( namespace ) ) );

    }
  }

  public void testCopyInto() {
    Element e1 = new Element();
    e1.setElementType( LabelType.INSTANCE );

    Element e2 = new Element();
    e2.setElementType( NumberFieldType.INSTANCE );

    e2.copyInto( e1 );

    Assert.assertEquals( LabelType.INSTANCE, e1.getElementType() );
    Assert.assertEquals( NumberFieldType.INSTANCE, e2.getElementType() );
  }

  public void testGetAttributeExpressions() {
    Expression expression = mock( Expression.class );
    Element elem = new Element();
    elem.setAttributeExpression( "namespace", "test_name", expression );
    assertNotNull( elem.getAttributeExpressions() );
    assertEquals( expression, elem.getAttributeExpressions().getAttribute( "namespace", "test_name" ) );
  }

  public void testGetFirstAttribute() {
    Element elem = new Element();
    elem.setAttribute( "namespace_0", "test_name", "test_value_0" );
    elem.setAttribute( "namespace_1", "test_name", "test_value_1" );
    assertNotNull( elem.getFirstAttribute( "test_name" ) );
    assertEquals( "test_value_0", elem.getFirstAttribute( "test_name" ) );
  }

  public void testIsVisible() {
    Element elem = new Element();
    elem.getStyle().setBooleanStyleProperty( ElementStyleKeys.VISIBLE, true );
    assertEquals( true, elem.isVisible() );
  }

  public void testGetId() {
    Element elem = new Element();
    elem.setAttribute( AttributeNames.Xml.NAMESPACE, AttributeNames.Xml.ID, "id_0" );
    assertEquals( "id_0", elem.getId() );
  }

  public void testIsDynamicContent() {
    Element elem = new Element();
    elem.getStyle().setBooleanStyleProperty( ElementStyleKeys.DYNAMIC_HEIGHT, true );
    assertEquals( true, elem.isDynamicContent() );
  }

  public void testSetDynamicContent() {
    Element elem = new Element();
    elem.setDynamicContent( true );
    assertEquals( true, elem.getStyle().getBooleanStyleProperty( ElementStyleKeys.DYNAMIC_HEIGHT ) );
  }

  public void testGetReportDefinition() {
    Element elem = new Element();
    elem.setParent( null );
    assertNull( elem.getReportDefinition() );

    ReportDefinition defn = mock( ReportDefinition.class );
    Section parent = mock( Section.class );
    doReturn( defn ).when( parent ).getReportDefinition();
    elem.setParent( parent );
    assertNotNull( elem.getReportDefinition() );
    assertEquals( defn, elem.getReportDefinition() );
  }

  public void testGetMasterReport() {
    Element elem = new Element();
    elem.setParent( null );
    assertNull( elem.getMasterReport() );

    ReportDefinition defn = mock( ReportDefinition.class );
    Section parent = mock( Section.class );
    doReturn( defn ).when( parent ).getMasterReport();
    elem.setParent( parent );
    assertNotNull( elem.getMasterReport() );
    assertEquals( defn, elem.getMasterReport() );
  }

  public void testSetHRefTarget() {
    Element elem = new Element();
    elem.setHRefTarget( "test_target" );
    assertEquals( "test_target", elem.getStyle().getStyleProperty( ElementStyleKeys.HREF_TARGET ) );
  }

  public void testGetHRefTarget() {
    Element elem = new Element();
    elem.getStyle().setStyleProperty( ElementStyleKeys.HREF_TARGET, "test_target" );
    assertEquals( "test_target", elem.getHRefTarget() );
  }
}
