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


package org.pentaho.reporting.libraries.base.util;

import junit.framework.TestCase;

import java.io.StringWriter;

public class CSVQuoterTest extends TestCase {

  public void testForceQuoteForStringOutput() {
    final CSVQuoter quoter = new CSVQuoter( ',', '"', true );
    assertEquals( "\"Classic Cars\"", quoter.doQuoting( "Classic Cars" ) );
  }

  public void testForceQuoteForWriterOutput() throws Exception {
    final CSVQuoter quoter = new CSVQuoter( ',', '"', true );
    final StringWriter writer = new StringWriter();

    quoter.doQuoting( "Classic Cars", writer );

    assertEquals( "\"Classic Cars\"", writer.toString() );
  }

  public void testIsForceQuote() {
    assertTrue( new CSVQuoter( ',', '"', true ).isForceQuote() );
    assertFalse( new CSVQuoter( ',', '"', false ).isForceQuote() );
  }

  public void testDoQuotingEscapesQuotes() {
    final CSVQuoter quoter = new CSVQuoter( ',', '"', false );

    assertEquals( "\"Classic \"\"Cars\"\"\"", quoter.doQuoting( "Classic \"Cars\"" ) );
  }

  public void testDoQuotingWriterEscapesQuotes() throws Exception {
    final CSVQuoter quoter = new CSVQuoter( ',', '"', false );
    final StringWriter writer = new StringWriter();

    quoter.doQuoting( "Classic \"Cars\"", writer );

    assertEquals( "\"Classic \"\"Cars\"\"\"", writer.toString() );
  }

  public void testUndoQuotingQuotedInput() {
    final CSVQuoter quoter = new CSVQuoter( ',', '"', false );

    assertEquals( "Classic, Cars", quoter.undoQuoting( "\"Classic, Cars\"" ) );
  }

  public void testUndoQuotingUnquotedInputContainingSeparator() {
    final CSVQuoter quoter = new CSVQuoter( ',', '"', false );

    assertEquals( "Classic, Cars", quoter.undoQuoting( "Classic, Cars" ) );
  }

  public void testUndoQuotingEscapedSingleQuoteCharacter() {
    final CSVQuoter quoter = new CSVQuoter( ',', '"', false );

    assertEquals( "\"", quoter.undoQuoting( "\"\"\"\"" ) );
  }

  public void testUndoQuotingEscapedConsecutiveQuoteCharacters() {
    final CSVQuoter quoter = new CSVQuoter( ',', '"', false );

    assertEquals( "\"\"", quoter.undoQuoting( "\"\"\"\"\"\"" ) );
  }
}
