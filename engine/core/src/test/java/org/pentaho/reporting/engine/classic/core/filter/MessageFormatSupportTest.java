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
