/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


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
