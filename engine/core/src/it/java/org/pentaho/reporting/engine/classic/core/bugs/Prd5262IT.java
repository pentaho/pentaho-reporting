/*
 * This program is free software; you can redistribute it and/or modify it under the
 *  terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
 *  Foundation.
 *
 *  You should have received a copy of the GNU Lesser General Public License along with this
 *  program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 *  or from the Free Software Foundation, Inc.,
 *  51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 *  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *  See the GNU Lesser General Public License for more details.
 *
 *  Copyright (c) 2006 - 2017 Hitachi Vantara..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.bugs;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.pentaho.reporting.engine.classic.core.AttributeNames;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.DataRow;
import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.ResourceBundleFactory;
import org.pentaho.reporting.engine.classic.core.filter.ResourceMessageFormatFilter;
import org.pentaho.reporting.engine.classic.core.filter.types.DateFieldType;
import org.pentaho.reporting.engine.classic.core.filter.types.MessageType;
import org.pentaho.reporting.engine.classic.core.function.ExpressionRuntime;
import org.pentaho.reporting.engine.classic.core.function.ProcessingContext;
import org.pentaho.reporting.engine.classic.core.testsupport.DebugExpressionRuntime;

import javax.swing.table.DefaultTableModel;
import java.util.Date;
import java.util.ListResourceBundle;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.TimeZone;

public class Prd5262IT {
  private ExpressionRuntime runtime;

  @Before
  public void setUp() throws Exception {
    ClassicEngineBoot.getInstance().start();

    runtime = createRuntime();
  }

  protected ExpressionRuntime createRuntime() throws ReportProcessingException {
    ResourceBundle b = new ListResourceBundle() {
      protected Object[][] getContents() {
        return new Object[][] { { "format", "$(date,date,yyyy-MM-dd'T'HH:mm:ss,SSSZZZ)" } };
      }
    };

    ResourceBundleFactory f = Mockito.mock( ResourceBundleFactory.class );
    Mockito.when( f.getLocale() ).thenReturn( Locale.US );
    Mockito.when( f.getTimeZone() ).thenReturn( TimeZone.getTimeZone( "PST" ) );
    Mockito.when( f.getResourceBundle( "test" ) ).thenReturn( b );

    ProcessingContext pc = Mockito.mock( ProcessingContext.class );
    Mockito.when( pc.getResourceBundleFactory() ).thenReturn( f );

    DataRow r = Mockito.mock( DataRow.class );
    Mockito.when( r.get( "number" ) ).thenReturn( new Double( 123456.78901 ) );
    Mockito.when( r.get( "date" ) ).thenReturn( new Date( 1234567890123l ) );

    return new DebugExpressionRuntime( r, new DefaultTableModel(), 0, pc );
  }

  @Test
  public void testMessageFieldsAcceptLocaleAndTimeZone() throws Exception {
    Element element = new Element();
    element.setAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.VALUE,
        "$(date,date,yyyy-MM-dd'T'HH:mm:ss,SSSZZZ)" );

    MessageType t = new MessageType();
    Assert.assertEquals( "2009-02-13T15:31:30,123-0800", t.getValue( runtime, element ) );
  }

  @Test
  public void testDateFieldsAcceptLocaleAndTimeZone() throws Exception {
    Element element = new Element();
    element.setAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.FIELD, "date" );
    element.setAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.FORMAT_STRING,
        "yyyy-MM-dd'T'HH:mm:ss,SSSZZZ" );

    DateFieldType t = new DateFieldType();
    Assert.assertEquals( "2009-02-13T15:31:30,123-0800", t.getValue( runtime, element ) );
  }

  @Test
  public void testResourceMessageFormatFilterAcceptLocaleAndTimeZone() throws Exception {
    ResourceMessageFormatFilter t = new ResourceMessageFormatFilter();
    t.setResourceIdentifier( "test" );
    t.setFormatKey( "format" );

    Assert.assertEquals( "2009-02-13T15:31:30,123-0800", t.getValue( runtime, new Element() ) );
  }

}
