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
}
