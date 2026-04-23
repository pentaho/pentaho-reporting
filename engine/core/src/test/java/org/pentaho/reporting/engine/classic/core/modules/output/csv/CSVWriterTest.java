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

public class CSVWriterTest extends TestCase {

  public void testAlwaysDoQuotesProperty() {
    final CSVWriter writer = new CSVWriter();

    assertFalse( writer.isAlwaysDoQuotes() );

    writer.setAlwaysDoQuotes( true );

    assertTrue( writer.isAlwaysDoQuotes() );
  }

  public void testDefaultEnclosureIsDoubleQuote() {
    final CSVWriter writer = new CSVWriter();

    assertEquals( '"', writer.getEnclosure() );
  }

  public void testSetAndGetEnclosure() {
    final CSVWriter writer = new CSVWriter();
    writer.setEnclosure( '\'' );

    assertEquals( '\'', writer.getEnclosure() );
  }
}
