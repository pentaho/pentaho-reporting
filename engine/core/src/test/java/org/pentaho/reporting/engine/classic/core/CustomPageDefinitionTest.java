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

package org.pentaho.reporting.engine.classic.core;

import junit.framework.TestCase;

import java.awt.print.PageFormat;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class CustomPageDefinitionTest extends TestCase {
  public CustomPageDefinitionTest( final String s ) {
    super( s );
  }

  public void testSerializeEmpty() throws IOException, ClassNotFoundException {
    final CustomPageDefinition cpd = new CustomPageDefinition();

    final ByteArrayOutputStream bo = new ByteArrayOutputStream();
    final ObjectOutputStream out = new ObjectOutputStream( bo );
    out.writeObject( cpd );
    out.close();

    final ObjectInputStream oin = new ObjectInputStream( new ByteArrayInputStream( bo.toByteArray() ) );
    final Object e2 = oin.readObject();
    assertNotNull( e2 ); // cannot assert equals, as this is not implemented.
    assertEquals( cpd, e2 );
  }

  public void testSerializeFilled() throws IOException, ClassNotFoundException {
    final CustomPageDefinition cpd = new CustomPageDefinition();
    cpd.addPageFormat( new PageFormat(), 0, 0 );
    cpd.addPageFormat( new PageFormat(), 0, 400 );
    cpd.addPageFormat( new PageFormat(), 400, 0 );
    cpd.addPageFormat( new PageFormat(), 400, 400 );

    final ByteArrayOutputStream bo = new ByteArrayOutputStream();
    final ObjectOutputStream out = new ObjectOutputStream( bo );
    out.writeObject( cpd );
    out.close();

    final ObjectInputStream oin = new ObjectInputStream( new ByteArrayInputStream( bo.toByteArray() ) );
    final Object e2 = oin.readObject();
    assertNotNull( e2 ); // cannot assert equals, as this is not implemented.
    assertEquals( cpd, e2 );
  }

}
