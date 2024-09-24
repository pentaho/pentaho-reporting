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
