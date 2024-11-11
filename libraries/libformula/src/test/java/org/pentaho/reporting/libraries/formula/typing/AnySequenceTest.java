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


package org.pentaho.reporting.libraries.formula.typing;

import junit.framework.TestCase;
import org.pentaho.reporting.libraries.formula.EvaluationException;
import org.pentaho.reporting.libraries.formula.common.TestFormulaContext;
import org.pentaho.reporting.libraries.formula.lvalues.DefaultDataTable;
import org.pentaho.reporting.libraries.formula.lvalues.StaticValue;
import org.pentaho.reporting.libraries.formula.typing.sequence.AnySequence;

public class AnySequenceTest extends TestCase {
  public AnySequenceTest() {
  }

  public AnySequenceTest( final String s ) {
    super( s );
  }

  public void testIterate() throws EvaluationException {
    final TestFormulaContext formulaContext = new TestFormulaContext();
    final AnySequence emptySequence = new AnySequence( formulaContext );
    assertFalse( emptySequence.hasNext() );

    final AnySequence singleNullValueSequence = new AnySequence( new StaticValue( null ), formulaContext );
    assertTrue( singleNullValueSequence.hasNext() );
    assertNull( singleNullValueSequence.next() );
    assertFalse( singleNullValueSequence.hasNext() );

    final AnySequence singleValueSequence = new AnySequence( new StaticValue( "test" ), formulaContext );
    assertTrue( singleValueSequence.hasNext() );
    assertEquals( "test", singleValueSequence.next() );
    assertFalse( singleValueSequence.hasNext() );

    final DefaultDataTable rowTable = new DefaultDataTable();
    rowTable.setObject( 0, 0, new StaticValue( "Test" ) );
    rowTable.setObject( 1, 0, new StaticValue( "Test2" ) );
    final AnySequence rowArraySequence = new AnySequence( rowTable.getAsArray(), formulaContext );
    assertTrue( rowArraySequence.hasNext() );
    assertEquals( "Test", rowArraySequence.next() );
    assertTrue( rowArraySequence.hasNext() );
    assertEquals( "Test2", rowArraySequence.next() );
    assertFalse( rowArraySequence.hasNext() );

    final DefaultDataTable colTable = new DefaultDataTable();
    colTable.setObject( 0, 0, new StaticValue( "Test" ) );
    colTable.setObject( 0, 1, new StaticValue( "Test2" ) );
    final AnySequence colArraySequence = new AnySequence( colTable.getAsArray(), formulaContext );
    assertTrue( colArraySequence.hasNext() );
    assertEquals( "Test", colArraySequence.next() );
    assertTrue( colArraySequence.hasNext() );
    assertEquals( "Test2", colArraySequence.next() );
    assertFalse( colArraySequence.hasNext() );

    final DefaultDataTable colRowTable = new DefaultDataTable();
    colRowTable.setObject( 0, 0, new StaticValue( "Test" ) );
    colRowTable.setObject( 0, 1, new StaticValue( "Test2" ) );
    colRowTable.setObject( 1, 0, new StaticValue( "Test3" ) );
    colRowTable.setObject( 1, 1, new StaticValue( "Test4" ) );
    final AnySequence colRowArraySequence = new AnySequence( colRowTable.getAsArray(), formulaContext );
    assertTrue( colRowArraySequence.hasNext() );
    assertEquals( "Test", colRowArraySequence.next() );
    assertTrue( colRowArraySequence.hasNext() );
    assertEquals( "Test2", colRowArraySequence.next() );
    assertTrue( colRowArraySequence.hasNext() );
    assertEquals( "Test3", colRowArraySequence.next() );
    assertTrue( colRowArraySequence.hasNext() );
    assertEquals( "Test4", colRowArraySequence.next() );
    assertFalse( colRowArraySequence.hasNext() );
  }
}
