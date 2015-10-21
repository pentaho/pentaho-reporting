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
* Copyright (c) 2000 - 2013 Pentaho Corporation, Simba Management Limited and Contributors...  All rights reserved.
*/

package org.pentaho.reporting.engine.classic.core;

import junit.framework.TestCase;
import org.pentaho.reporting.engine.classic.core.elementfactory.TextFieldElementFactory;
import org.pentaho.reporting.engine.classic.core.testsupport.DebugReportRunner;
import org.pentaho.reporting.libraries.base.util.FloatDimension;

import java.awt.*;
import java.awt.geom.Point2D;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class BandTest extends TestCase {
  public BandTest( final String s ) {
    super( s );
  }

  protected void setUp() throws Exception {
    ClassicEngineBoot.getInstance().start();
  }

  public void testBandCreate() {
    final Band b = new Band();
    assertNotNull( b.getDataSource() );
    assertNotNull( b.getStyle() );
    assertNotNull( b.getName() );
    assertTrue( b.isVisible() );
    assertNull( b.getParent() );
    assertNotNull( b.getElementArray() );
    assertTrue( b.getElementCount() == 0 );
    //    assertNotNull(b.getElements());
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

    //report.getStyleSheetCollection().debug();

    report.getItemBand().removeElement( element1 );
    report.getItemBand().removeElement( element2 );

    //report.getStyleSheetCollection().debug();

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

    //report.getStyleSheetCollection().debug();

    report.getItemBand().removeElement( element1 );
    //report.getStyleSheetCollection().debug();
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

    //report.getStyleSheetCollection().debug();

    report.getItemBand().removeElement( element1 );
    report.getItemBand().removeElement( element2 );

    //report.getStyleSheetCollection().debug();
    DebugReportRunner.execGraphics2D( report );

  }

}
