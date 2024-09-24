/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2028-08-13
 ******************************************************************************/

package org.pentaho.reporting.engine.classic.core.modules.gui.base.parameters;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import java.awt.Color;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import javax.swing.text.JTextComponent;

import org.junit.Test;
import org.pentaho.reporting.engine.classic.core.util.beans.BeanException;

@SuppressWarnings( "rawtypes" )
public class TextComponentEditHandlerTest {


  @Test
  public void testCreateFormat() {
    String formatString = null;
    Locale locale = new Locale( "test_test" );
    TimeZone timeZone = TimeZone.getDefault();
    Class parameterType = null;

    Format format = TextComponentEditHandler.createFormat( formatString, locale, timeZone, parameterType );
    assertThat( format, is( nullValue() ) );

    formatString = "0.0.0";
    format = TextComponentEditHandler.createFormat( formatString, locale, timeZone, parameterType );
    assertThat( format, is( nullValue() ) );

    parameterType = Number.class;
    format = TextComponentEditHandler.createFormat( formatString, locale, timeZone, parameterType );
    assertThat( format, is( nullValue() ) );

    formatString = "#,###,##0.00";
    format = TextComponentEditHandler.createFormat( formatString, locale, timeZone, parameterType );
    assertThat( format, is( instanceOf( DecimalFormat.class ) ) );

    parameterType = Date.class;
    formatString = "o";
    format = TextComponentEditHandler.createFormat( formatString, locale, timeZone, parameterType );
    assertThat( format, is( nullValue() ) );

    formatString = "dd.MM.yyyy";
    format = TextComponentEditHandler.createFormat( formatString, locale, timeZone, parameterType );
    assertThat( format, is( instanceOf( SimpleDateFormat.class ) ) );
  }

  @Test
  public void testCreateTimeZone() {
    TimeZone defaultTimeZone = mock( TimeZone.class );
    TimeZone timezone = TextComponentEditHandler.createTimeZone( null, defaultTimeZone );
    assertThat( timezone, is( equalTo( defaultTimeZone ) ) );

    timezone = TextComponentEditHandler.createTimeZone( "server", defaultTimeZone );
    assertThat( timezone, is( equalTo( defaultTimeZone ) ) );

    timezone = TextComponentEditHandler.createTimeZone( "client", defaultTimeZone );
    assertThat( timezone, is( equalTo( defaultTimeZone ) ) );

    timezone = TextComponentEditHandler.createTimeZone( "utc", defaultTimeZone );
    assertThat( timezone, is( not( equalTo( defaultTimeZone ) ) ) );
    assertThat( timezone.getID(), is( equalTo( "UTC" ) ) );

    timezone = TextComponentEditHandler.createTimeZone( "GMT", defaultTimeZone );
    assertThat( timezone, is( not( equalTo( defaultTimeZone ) ) ) );
    assertThat( timezone.getID(), is( equalTo( "GMT" ) ) );
  }

  @Test
  public void testRun() throws BeanException {
    String value = "test val";
    Class type = String.class;
    String keyName = "key";
    JTextComponent textComponent = mock( JTextComponent.class );
    ParameterUpdateContext updateContext = mock( ParameterUpdateContext.class );
    // Format formatter = new DecimalFormat( "#,###,##0.00" );

    doReturn( value ).when( textComponent ).getText();
    doReturn( Color.YELLOW ).when( textComponent ).getBackground();

    TextComponentEditHandler handler =
        spy( new TextComponentEditHandler( type, keyName, textComponent, updateContext, null ) );

    handler.run();

    verify( textComponent ).setBackground( Color.YELLOW );
    verify( updateContext ).setParameterValue( keyName, value, false );

    doThrow( BeanException.class ).when( handler ).convertValue( value );
    handler.run();
    verify( textComponent ).setBackground( Color.RED );
  }

  @Test( expected = BeanException.class )
  public void testConvertValueWithFormatter() throws BeanException {
    Class type = String.class;
    String keyName = "key";
    JTextComponent textComponent = mock( JTextComponent.class );
    ParameterUpdateContext updateContext = mock( ParameterUpdateContext.class );
    Format formatter = new DecimalFormat( "#,###,##0.00" );

    TextComponentEditHandler handler =
        new TextComponentEditHandler( type, keyName, textComponent, updateContext, formatter );

    Object result = handler.convertValue( null );
    assertThat( result, is( nullValue() ) );

    result = handler.convertValue( "512,000.897" );
    assertThat( (String) result, is( equalTo( "512000.897" ) ) );

    handler.convertValue( "error" );
  }

  @Test
  public void testConvertStringValueWithoutFormatter() throws BeanException {
    Class type = String.class;
    String keyName = "key";
    JTextComponent textComponent = mock( JTextComponent.class );
    ParameterUpdateContext updateContext = mock( ParameterUpdateContext.class );

    TextComponentEditHandler handler = new TextComponentEditHandler( type, keyName, textComponent, updateContext, null );

    Object result = handler.convertValue( "test val" );
    assertThat( (String) result, is( equalTo( "test val" ) ) );
  }

  @Test
  public void testConvertNumberValueWithoutFormatter() throws BeanException {
    Class type = Number.class;
    String keyName = "key";
    JTextComponent textComponent = mock( JTextComponent.class );
    ParameterUpdateContext updateContext = mock( ParameterUpdateContext.class );

    TextComponentEditHandler handler = new TextComponentEditHandler( type, keyName, textComponent, updateContext, null );

    Object result = handler.convertValue( "123" );
    assertThat( result, is( instanceOf( BigDecimal.class ) ) );
  }
}
