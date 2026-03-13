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


package org.pentaho.reporting.engine.classic.core.modules.output.csv;

import junit.framework.TestCase;

import java.io.IOException;
import java.io.StringWriter;

public class CSVQuoterTest extends TestCase {
  private static final String CLASSIC_CARS = "Classic Cars";

  public void testDoQuotingWithoutForce() {
    final CSVQuoter quoter = new CSVQuoter( "," );

    assertEquals( CLASSIC_CARS, quoter.doQuoting( CLASSIC_CARS ) );
    assertEquals( "\"Classic, Cars\"", quoter.doQuoting( "Classic, Cars" ) );
  }

  public void testDoQuotingWithForce() {
    final CSVQuoter quoter = new CSVQuoter( ",", true );

    assertEquals( "\"Classic Cars\"", quoter.doQuoting( CLASSIC_CARS ) );
  }

  public void testDoQuotingEscapesQuotes() {
    final CSVQuoter quoter = new CSVQuoter( "," );

    assertEquals( "\"Classic \"\"Cars\"\"\"", quoter.doQuoting( "Classic \"Cars\"" ) );
  }

  public void testUndoQuotingQuotedInput() {
    final CSVQuoter quoter = new CSVQuoter( "," );

    assertEquals( CLASSIC_CARS, quoter.undoQuoting( "\"Classic Cars\"" ) );
  }

  public void testUndoQuotingUnquotedInput() {
    final CSVQuoter quoter = new CSVQuoter( "," );

    assertEquals( CLASSIC_CARS, quoter.undoQuoting( CLASSIC_CARS ) );
  }

  public void testUndoQuotingUnquotedInputContainingSeparator() {
    final CSVQuoter quoter = new CSVQuoter( "," );

    assertEquals( "Classic, Cars", quoter.undoQuoting( "Classic, Cars" ) );
  }

  public void testUndoQuotingEscapedSingleQuoteCharacter() {
    final CSVQuoter quoter = new CSVQuoter( "," );

    assertEquals( "\"", quoter.undoQuoting( "\"\"\"\"" ) );
  }

  public void testUndoQuotingEscapedConsecutiveQuoteCharacters() {
    final CSVQuoter quoter = new CSVQuoter( "," );

    assertEquals( "\"\"", quoter.undoQuoting( "\"\"\"\"\"\"" ) );
  }

  // --- enclosure char tests ---

  public void testDefaultEnclosureIsDoubleQuote() {
    final CSVQuoter quoter = new CSVQuoter( "," );

    assertEquals( '"', quoter.getEnclosure() );
  }

  public void testGetSetEnclosure() {
    final CSVQuoter quoter = new CSVQuoter( "," );
    quoter.setEnclosure( '\'' );

    assertEquals( '\'', quoter.getEnclosure() );
  }

  public void testDoQuotingWithCustomEnclosureEnclosesSeparator() {
    final CSVQuoter quoter = new CSVQuoter( ",", '\'' );

    assertEquals( CLASSIC_CARS, quoter.doQuoting( CLASSIC_CARS ) );
    assertEquals( "'Classic, Cars'", quoter.doQuoting( "Classic, Cars" ) );
  }

  public void testDoQuotingWithCustomEnclosureAndForce() {
    final CSVQuoter quoter = new CSVQuoter( ",", '\'', true );

    assertEquals( "'Classic Cars'", quoter.doQuoting( CLASSIC_CARS ) );
  }

  public void testDoQuotingEscapesCustomEnclosure() {
    final CSVQuoter quoter = new CSVQuoter( ",", '\'' );

    assertEquals( "'Classic ''Cars'''", quoter.doQuoting( "Classic 'Cars'" ) );
  }

  public void testUndoQuotingWithCustomEnclosure() {
    final CSVQuoter quoter = new CSVQuoter( ",", '\'' );

    assertEquals( CLASSIC_CARS, quoter.undoQuoting( "'Classic Cars'" ) );
  }

  public void testUndoQuotingEscapedSingleCustomEnclosureCharacter() {
    final CSVQuoter quoter = new CSVQuoter( ",", '\'' );

    assertEquals( "'", quoter.undoQuoting( "''''" ) );
  }

  public void testUndoQuotingEscapedConsecutiveCustomEnclosureCharacters() {
    final CSVQuoter quoter = new CSVQuoter( ",", '\'' );

    assertEquals( "''", quoter.undoQuoting( "''''''" ) );
  }

  public void testSetEnclosureUpdatesEscaping() {
    final CSVQuoter quoter = new CSVQuoter( "," );
    quoter.setEnclosure( '\'' );

    assertEquals( "'Classic ''Cars'''", quoter.doQuoting( "Classic 'Cars'" ) );
  }

  public void testDefaultForceEnclosureIsFalse() {
    final CSVQuoter quoter = new CSVQuoter( "," );

    assertFalse( quoter.isForceEnclosure() );
  }

  public void testSetForceEnclosure() {
    final CSVQuoter quoter = new CSVQuoter( "," );
    quoter.setForceEnclosure( true );

    assertTrue( quoter.isForceEnclosure() );
  }

  public void testStreamingDoQuotingEnclosesSeparator() throws IOException {
    final CSVQuoter quoter = new CSVQuoter( "," );
    final StringWriter writer = new StringWriter();
    quoter.doQuoting( "Classic, Cars", writer );

    assertEquals( "\"Classic, Cars\"", writer.toString() );
  }

  public void testStreamingDoQuotingNoEnclosureNeeded() throws IOException {
    final CSVQuoter quoter = new CSVQuoter( "," );
    final StringWriter writer = new StringWriter();
    quoter.doQuoting( CLASSIC_CARS, writer );

    assertEquals( CLASSIC_CARS, writer.toString() );
  }

  public void testStreamingDoQuotingWithCustomEnclosure() throws IOException {
    final CSVQuoter quoter = new CSVQuoter( ",", '\'' );
    final StringWriter writer = new StringWriter();
    quoter.doQuoting( "Classic, Cars", writer );

    assertEquals( "'Classic, Cars'", writer.toString() );
  }

  public void testStreamingDoQuotingEscapesCustomEnclosure() throws IOException {
    final CSVQuoter quoter = new CSVQuoter( ",", '\'' );
    final StringWriter writer = new StringWriter();
    quoter.doQuoting( "Classic 'Cars'", writer );

    assertEquals( "'Classic ''Cars'''", writer.toString() );
  }

  public void testStreamingDoQuotingWithForce() throws IOException {
    final CSVQuoter quoter = new CSVQuoter( ",", '\'', true );
    final StringWriter writer = new StringWriter();
    quoter.doQuoting( CLASSIC_CARS, writer );

    assertEquals( "'Classic Cars'", writer.toString() );
  }
}
