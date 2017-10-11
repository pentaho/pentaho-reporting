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

package org.pentaho.reporting.designer.core.util.undo;

import junit.framework.TestCase;
import org.pentaho.reporting.designer.core.ReportDesignerBoot;
import org.pentaho.reporting.designer.core.auth.GlobalAuthenticationStore;
import org.pentaho.reporting.designer.core.editor.ReportRenderContext;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.util.InstanceID;

/**
 * Todo: Document Me
 *
 * @author Thomas Morgner
 * @noinspection HardCodedStringLiteral
 */
public class UndoManagerTest extends TestCase {
  public UndoManagerTest() {
  }

  public UndoManagerTest( final String s ) {
    super( s );
  }

  @Override
  protected void setUp() throws Exception {
    ReportDesignerBoot.getInstance().start();
  }

  public void testUndo() {
    final UndoManager manager = new UndoManager();
    assertFalse( manager.isRedoPossible() );
    assertFalse( manager.isUndoPossible() );
    final MasterReport report = new MasterReport();
    final ReportRenderContext rrc = new ReportRenderContext( report, report, null, new GlobalAuthenticationStore() );
    // must return silently
    manager.undo( rrc );
    // must return silently
    manager.redo( rrc );
  }

  public void testRecords() {
    final UndoManager manager = new UndoManager();
    final MasterReport report = new MasterReport();
    final ReportRenderContext rrc = new ReportRenderContext( report, report, null, new GlobalAuthenticationStore() );

    final InstanceID id = report.getObjectID();
    manager.addChange( "name1", new AttributeEditUndoEntry( id, "test-ns", "test", null, "new" ) );
    manager.addChange( "name2", new AttributeEditUndoEntry( id, "test-ns", "test2", null, "groovy" ) );
    manager.addChange( "name3", new AttributeEditUndoEntry( id, "test-ns", "test", "new", "other" ) );

    report.setAttribute( "test-ns", "test", "other" );
    report.setAttribute( "test-ns", "test2", "groovy" );

    assertFalse( manager.isRedoPossible() );
    assertTrue( manager.isUndoPossible() );
    assertEquals( "Attr test = other", "other", report.getAttribute( "test-ns", "test" ) );
    assertEquals( "Attr test2 = groovy", "groovy", report.getAttribute( "test-ns", "test2" ) );

    manager.undo( rrc );
    assertTrue( manager.isRedoPossible() );
    assertTrue( manager.isUndoPossible() );
    assertEquals( "Attr test = new", "new", report.getAttribute( "test-ns", "test" ) );
    assertEquals( "Attr test2 = groovy", "groovy", report.getAttribute( "test-ns", "test2" ) );

    manager.redo( rrc );
    assertFalse( manager.isRedoPossible() );
    assertTrue( manager.isUndoPossible() );
    assertEquals( "Attr test = other", "other", report.getAttribute( "test-ns", "test" ) );
    assertEquals( "Attr test2 = groovy", "groovy", report.getAttribute( "test-ns", "test2" ) );

    manager.undo( rrc );
    assertTrue( manager.isRedoPossible() );
    assertTrue( manager.isUndoPossible() );
    assertEquals( "Attr test = new", "new", report.getAttribute( "test-ns", "test" ) );
    assertEquals( "Attr test2 = groovy", "groovy", report.getAttribute( "test-ns", "test2" ) );

    manager.undo( rrc );
    assertTrue( manager.isRedoPossible() );
    assertTrue( manager.isUndoPossible() );
    assertEquals( "Attr test = new", "new", report.getAttribute( "test-ns", "test" ) );
    assertEquals( "Attr test2 = <null>", null, report.getAttribute( "test-ns", "test2" ) );

    manager.undo( rrc );
    assertTrue( manager.isRedoPossible() );
    assertFalse( manager.isUndoPossible() );
    assertEquals( "Attr test = <null>", null, report.getAttribute( "test-ns", "test" ) );
    assertEquals( "Attr test2 = <null>", null, report.getAttribute( "test-ns", "test2" ) );

  }
}
