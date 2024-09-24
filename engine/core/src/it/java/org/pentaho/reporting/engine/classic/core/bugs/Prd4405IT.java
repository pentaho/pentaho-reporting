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

package org.pentaho.reporting.engine.classic.core.bugs;

import junit.framework.TestCase;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.libraries.base.util.MemoryByteArrayOutputStream;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class Prd4405IT extends TestCase {
  public Prd4405IT() {
  }

  public void setUp() throws Exception {
    ClassicEngineBoot.getInstance().start();
  }

  protected MasterReport postProcess( final MasterReport originalReport ) throws Exception {
    final byte[] bytes = serializeReportObject( originalReport );
    return deserializeReportObject( bytes );
  }

  private byte[] serializeReportObject( final MasterReport report ) throws IOException {
    // we don't test whether our demo models are serializable :)
    // clear all report properties, which may cause trouble ...
    final MemoryByteArrayOutputStream bo = new MemoryByteArrayOutputStream();
    final ObjectOutputStream oout = new ObjectOutputStream( bo );
    oout.writeObject( report );
    oout.close();
    return bo.toByteArray();
  }

  private MasterReport deserializeReportObject( final byte[] data ) throws IOException, ClassNotFoundException {
    final ByteArrayInputStream bin = new ByteArrayInputStream( data );
    final ObjectInputStream oin = new ObjectInputStream( bin );
    final MasterReport report2 = (MasterReport) oin.readObject();
    assertNotNull( report2 );
    return report2;
  }

  public void testConfiguration() throws Exception {
    final MasterReport report = postProcess( new MasterReport() );
    final String key = getClass().getName() + ";" + System.identityHashCode( report );
    final String value = "" + System.identityHashCode( report );
    ClassicEngineBoot.getInstance().getEditableConfig().setConfigProperty( key, value );
    assertEquals( value, report.getConfiguration().getConfigProperty( key ) );
  }
}
