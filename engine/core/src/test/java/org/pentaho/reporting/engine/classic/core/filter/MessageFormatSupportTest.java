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


package org.pentaho.reporting.engine.classic.core.filter;

import junit.framework.TestCase;
import org.pentaho.reporting.engine.classic.core.StaticDataRow;
import org.pentaho.reporting.engine.classic.core.util.IntegerCache;
import org.pentaho.reporting.libraries.base.util.CSVTokenizer;

public class MessageFormatSupportTest extends TestCase {
  public MessageFormatSupportTest( final String s ) {
    super( s );
  }

  public void testQuotedExample() {
    final String example = "$(\"customer.firstName\") $(\"customer.lastName\")";
    final MessageFormatSupport support = new MessageFormatSupport();
    support.setFormatString( example );
    support.performFormat( new StaticDataRow() );
    assertEquals( "CompiledFormat", "{0} {1}", support.getCompiledFormat() );
  }

  public void testCSVTokenizer() {
    final String example = "\"Test\"";
    final CSVTokenizer tokenizer = new CSVTokenizer( example, false );
    assertTrue( "Tokenizer has at least one element", tokenizer.hasMoreTokens() );
    assertEquals( tokenizer.nextToken(), "Test" );
  }

  public void testComplexReplacement() {
    final MessageFormatSupport support = new MessageFormatSupport();
    support.setFormatString( "$(null,number,integer), $(dummy), $(null,date), $(null,number,integer)" );
    final StaticDataRow sdr = new StaticDataRow( new String[] { "null", "dummy" }, new String[] { null, "Content" } );

    final String text = support.performFormat( sdr );
    assertEquals( "Expected content w/o nullString", "<null>, Content, <null>, <null>", text );

    support.setNullString( "-" );
    final String ntext = support.performFormat( sdr );
    assertEquals( "Expected content w nullString", "-, Content, -, -", ntext );
  }

  public void testNestedPattern() {
    final MessageFormatSupport support = new MessageFormatSupport();
    support.setFormatString( "$(null,choice,0#$(null)|0<$(dummy))" );
    final StaticDataRow sdr =
        new StaticDataRow( new String[] { "null", "dummy" }, new Object[] { IntegerCache.getInteger( 0 ),
          IntegerCache.getInteger( 1 ) } );

    final String text = support.performFormat( sdr );
    assertEquals( "Expected content ", "0", text );

    final StaticDataRow sdr2 =
        new StaticDataRow( new String[] { "null", "dummy" }, new Object[] { IntegerCache.getInteger( 1 ),
          IntegerCache.getInteger( 2 ) } );
    support.setNullString( "-" );
    final String ntext = support.performFormat( sdr2 );
    assertEquals( "Expected content w nullString", "2", ntext );
  }
}
