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

import junit.framework.TestCase;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class MasterReportTest extends TestCase {
  public MasterReportTest( final String s ) {
    super( s );
  }

  protected void setUp() throws Exception {
    ClassicEngineBoot.getInstance().start();
  }

  public void testDerive() throws CloneNotSupportedException {
    final MasterReport report = new MasterReport();
    report.setQueryLimit( 10 );
    report.setQueryTimeout( 10 );
    final MasterReport element = (MasterReport) report.derive();
    assertEquals( 10, element.getQueryLimit() );
    assertEquals( 10, element.getQueryTimeout() );
  }

  public void testCreate() throws Exception {
    final MasterReport report = new MasterReport();
    // Report name is null
    report.setName( "MyTestReport" );
    assertNotNull( report.getPageDefinition() );
    assertNotNull( report.getExpressions() );
    assertNotNull( report.getRootGroup() );
    assertEquals( report.getGroupCount(), 1 );
    assertNotNull( report.getItemBand() );
    assertNotNull( report.getName() );
    assertNotNull( report.getPageFooter() );
    assertNotNull( report.getPageHeader() );
    assertNotNull( report.getReportConfiguration() );
    assertNotNull( report.getReportFooter() );
    assertNotNull( report.getReportHeader() );
    assertNotNull( report.getGroup( 0 ) ); // the default group must be defined ...
    assertNotNull( report.clone() );
  }

  public void testSerializeEmptyReport() throws Exception {
    final MasterReport report = new MasterReport();
    final ByteArrayOutputStream bo = new ByteArrayOutputStream();
    final ObjectOutputStream out = new ObjectOutputStream( bo );
    out.writeObject( report );

    final ObjectInputStream oin = new ObjectInputStream( new ByteArrayInputStream( bo.toByteArray() ) );
    final MasterReport e2 = (MasterReport) oin.readObject();
    assertNotNull( e2 ); // cannot assert equals, as this is not implemented.

    // will crash if the serialization fails ..
    e2.getStyle().toArray();
  }

  public void testClone() throws Exception {
    final MasterReport report = new MasterReport();
    assertNotNull( report.clone() );
  }

}
