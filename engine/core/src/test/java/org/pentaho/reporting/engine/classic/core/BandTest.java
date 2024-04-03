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
 * Copyright (c) 2000 - 2024 Hitachi Vantara, Simba Management Limited and Contributors...  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.junit.Test;
import org.pentaho.reporting.engine.classic.core.elementfactory.TextFieldElementFactory;
import org.pentaho.reporting.engine.classic.core.filter.types.bands.BandType;
import org.pentaho.reporting.engine.classic.core.testsupport.DebugReportRunner;
import org.pentaho.reporting.engine.classic.core.util.InstanceID;
import org.pentaho.reporting.libraries.base.util.FloatDimension;

public class BandTest extends TestCase {
  public BandTest( final String s ) {
    super( s );
  }

  protected void setUp() throws Exception {
    ClassicEngineBoot.getInstance().start();
  }

  public void testBandCreate() {
    Band b = new Band();
    assertNotNull( b.getDataSource() );
    assertNotNull( b.getStyle() );
    assertNotNull( b.getName() );
    assertTrue( b.isVisible() );
    assertNull( b.getParent() );
    assertNotNull( b.getElementArray() );
    assertTrue( b.getElementCount() == 0 );
    assertTrue( b.getElementType() instanceof BandType );
    assertFalse( b.isPagebreakBeforePrint() );
    assertFalse( b.isPagebreakAfterPrint() );

    InstanceID id = new InstanceID();
    b = new Band( id );
    assertNotNull( b.getTreeLock() );
    assertEquals( id, b.getTreeLock() );
    assertTrue( b.getElementType() instanceof BandType );
    assertFalse( b.isPagebreakBeforePrint() );
    assertFalse( b.isPagebreakAfterPrint() );

    b = new Band( true, true );
    assertTrue( b.getElementType() instanceof BandType );
    assertTrue( b.isPagebreakBeforePrint() );
    assertTrue( b.isPagebreakAfterPrint() );
  }

  public void testAddElementWrongPosition() {
    try {
      Band band = new Band();
      band.addElement( -1, mock( Element.class ) );
      fail( "should throw exception" );
    } catch ( IllegalArgumentException e ) {
      // expected
    }
  }

  public void testAddElementTooBigPosition() {
    try {
      Band band = new Band();
      band.addElement( 10, mock( Element.class ) );
      fail( "should throw exception" );
    } catch ( IllegalArgumentException e ) {
      // expected
    }
  }

  @Test( expected = NullPointerException.class )
  public void testAddNullElement() {
    try {
      Band band = new Band();
      band.addElement( 0, null );
      fail( "should throw exception" );
    } catch ( NullPointerException e ) {
      // expected
    }
  }

  public void testBandMethods() {
    final Band b = new Band();
    assertTrue( b.isVisible() );
    b.setVisible( false );
    assertTrue( b.isVisible() == false );
    b.setVisible( true );
    assertTrue( b.isVisible() );

    try {
      b.setDataSource( null );
      fail();
    } catch ( NullPointerException npe ) {
      // expected, ignored
    }

    b.toString();
  }

  public void testAddElement() {
    final Band b = new Band();
    assertTrue( b.getElementCount() == 0 );
    b.addElement( 0, new Element() );
    assertTrue( b.getElementCount() == 1 );
    b.addElement( new Element() );
    assertTrue( b.getElementCount() == 2 );
    b.addElement( 0, new Element() );
    assertTrue( b.getElementCount() == 3 );
    b.addElement( 2, new Element() );
    assertTrue( b.getElementCount() == 4 );
    try {
      b.addElement( 5, new Element() );
      fail();
    } catch ( IllegalArgumentException iob ) {
      // expected, ignored
    }
    try {
      b.addElement( null );
      fail();
    } catch ( NullPointerException npe ) {
      // expected, ignored
    }
    try {
      b.addElement( b );
      fail();
    } catch ( IllegalArgumentException ia ) {
      // expected, ignored
    }

    try {
      final Band b1 = new Band();
      final Band b2 = new Band();
      final Band b3 = new Band();
      b1.addElement( b2 );
      b2.addElement( b3 );
      b3.addElement( b1 );
      fail();
    } catch ( IllegalArgumentException ia ) {
      // expected, ignored
    }

  }

  public void testRemoveElement() {
    final MasterReport report = new MasterReport();
    report.setName( "A Very Simple Report" );

    TextFieldElementFactory factory = new TextFieldElementFactory();
    factory.setName( "T1" );
    factory.setAbsolutePosition( new Point2D.Float( 0, 0 ) );
    factory.setMinimumSize( new FloatDimension( 150, 20 ) );
    factory.setColor( Color.black );
    factory.setHorizontalAlignment( ElementAlignment.LEFT );
    factory.setVerticalAlignment( ElementAlignment.MIDDLE );
    factory.setNullString( "-" );
    factory.setFieldname( "Column1" );

    final Element element1 = factory.createElement();
    report.getItemBand().addElement( element1 );

    factory = new TextFieldElementFactory();
    factory.setName( "T2" );
    factory.setAbsolutePosition( new Point2D.Float( 200, 0 ) );
    factory.setMinimumSize( new FloatDimension( 150, 20 ) );
    factory.setColor( Color.black );
    factory.setHorizontalAlignment( ElementAlignment.LEFT );
    factory.setVerticalAlignment( ElementAlignment.MIDDLE );
    factory.setNullString( "-" );
    factory.setFieldname( "Column2" );

    final Element element2 = factory.createElement();
    report.getItemBand().addElement( element2 );

    // report.getStyleSheetCollection().debug();

    report.getItemBand().removeElement( element1 );
    report.getItemBand().removeElement( element2 );

    // report.getStyleSheetCollection().debug();

  }

  public void testSerialize() throws Exception {
    final Band e = new Band();
    final ByteArrayOutputStream bo = new ByteArrayOutputStream();
    final ObjectOutputStream out = new ObjectOutputStream( bo );
    out.writeObject( e );

    final ObjectInputStream oin = new ObjectInputStream( new ByteArrayInputStream( bo.toByteArray() ) );
    final Element e2 = (Element) oin.readObject();
    assertNotNull( e2 ); // cannot assert equals, as this is not implemented.
  }

  public void testRemoveBandElement() {
    final MasterReport report = new MasterReport();
    report.setName( "A Very Simple Report" );

    TextFieldElementFactory factory = new TextFieldElementFactory();
    factory.setName( "T1" );
    factory.setAbsolutePosition( new Point2D.Float( 0, 0 ) );
    factory.setMinimumSize( new FloatDimension( 150, 20 ) );
    factory.setColor( Color.black );
    factory.setHorizontalAlignment( ElementAlignment.LEFT );
    factory.setVerticalAlignment( ElementAlignment.MIDDLE );
    factory.setNullString( "-" );
    factory.setFieldname( "Column1" );

    final Element element1 = factory.createElement();
    report.getItemBand().addElement( element1 );

    factory = new TextFieldElementFactory();
    factory.setName( "T2" );
    factory.setAbsolutePosition( new Point2D.Float( 200, 0 ) );
    factory.setMinimumSize( new FloatDimension( 150, 20 ) );
    factory.setColor( Color.black );
    factory.setHorizontalAlignment( ElementAlignment.LEFT );
    factory.setVerticalAlignment( ElementAlignment.MIDDLE );
    factory.setNullString( "-" );
    factory.setFieldname( "Column2" );

    final Element element2 = factory.createElement();
    report.getItemBand().addElement( element2 );

    // report.getStyleSheetCollection().debug();

    report.getItemBand().removeElement( element1 );
    // report.getStyleSheetCollection().debug();
    DebugReportRunner.execGraphics2D( report );
  }

  public void testRemoveElementComplete() {
    final MasterReport report = new MasterReport();
    report.setName( "A Very Simple Report" );

    TextFieldElementFactory factory = new TextFieldElementFactory();
    factory.setName( "T1" );
    factory.setAbsolutePosition( new Point2D.Float( 0, 0 ) );
    factory.setMinimumSize( new FloatDimension( 150, 20 ) );
    factory.setColor( Color.black );
    factory.setHorizontalAlignment( ElementAlignment.LEFT );
    factory.setVerticalAlignment( ElementAlignment.MIDDLE );
    factory.setNullString( "-" );
    factory.setFieldname( "Column1" );

    final Element element1 = factory.createElement();
    report.getItemBand().addElement( element1 );

    factory = new TextFieldElementFactory();
    factory.setName( "T2" );
    factory.setAbsolutePosition( new Point2D.Float( 200, 0 ) );
    factory.setMinimumSize( new FloatDimension( 150, 20 ) );
    factory.setColor( Color.black );
    factory.setHorizontalAlignment( ElementAlignment.LEFT );
    factory.setVerticalAlignment( ElementAlignment.MIDDLE );
    factory.setNullString( "-" );
    factory.setFieldname( "Column2" );

    final Element element2 = factory.createElement();
    report.getItemBand().addElement( element2 );

    // report.getStyleSheetCollection().debug();

    report.getItemBand().removeElement( element1 );
    report.getItemBand().removeElement( element2 );

    // report.getStyleSheetCollection().debug();
    DebugReportRunner.execGraphics2D( report );
  }

  public void testGetDefaultStyleSheet() {
    Band band = new Band();
    assertThat( band.getDefaultStyleSheet(), is( notNullValue() ) );
  }

  public void testAddElements() {
    Band band = new Band();
    try {
      band.addElements( null );
      fail( "should throw exception" );
    } catch ( NullPointerException e ) {
      // expected
    }

    Element elem = mock( Element.class );
    List<Element> elements = new ArrayList<Element>();
    elements.add( elem );
    band.addElements( elements );
    assertEquals( 1, band.getElementCount() );
    assertEquals( elem, band.getElement( 0 ) );
  }

  public void testGetElement() {
    Band band = new Band();
    try {
      band.getElement( null );
      fail( "should throw exception" );
    } catch ( NullPointerException e ) {
      // expected
    }

    Element elem = mock( Element.class );
    doReturn( "test_name" ).when( elem ).getName();
    band.addElement( elem );
    assertEquals( 1, band.getElementCount() );
    assertEquals( elem, band.getElement( "test_name" ) );
  }

  public void testSetElementAt() {
    Band band = new Band();
    band.addElement( mock( Element.class ) );
    Element elem = mock( Element.class );
    try {
      band.setElementAt( -1, elem );
      fail( "should throw exception" );
    } catch ( IllegalArgumentException e ) {
      // expected
    }

    try {
      band.setElementAt( 10, elem );
      fail( "should throw exception" );
    } catch ( IllegalArgumentException e ) {
      // expected
    }

    try {
      band.setElementAt( 0, null );
      fail( "should throw exception" );
    } catch ( NullPointerException e ) {
      // expected
    }

    band.setElementAt( 0, elem );
    assertEquals( 1, band.getElementCount() );
    assertEquals( elem, band.getElement( 0 ) );
    //assertEquals( band, elem.getParentSection() );
    assertNull( elem.getParentSection() );
  }

}
