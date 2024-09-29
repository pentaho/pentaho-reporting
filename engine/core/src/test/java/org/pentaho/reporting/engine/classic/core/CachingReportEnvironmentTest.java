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


package org.pentaho.reporting.engine.classic.core;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.Collections;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import org.junit.Before;
import org.junit.Test;

public class CachingReportEnvironmentTest {

  private static final String KEY = "test_key";
  private static final String VALUE = "test_val";

  private CachingReportEnvironment env;
  private ReportEnvironment backend;

  @Before
  public void setUp() {
    backend = mock( ReportEnvironment.class );
    env = new CachingReportEnvironment( backend );
  }

  @Test
  public void testGetNullEnvironmentProperty() {
    doReturn( null ).when( backend ).getEnvironmentProperty( KEY );
    Object result = env.getEnvironmentProperty( KEY );
    assertThat( result, is( nullValue() ) );

    result = env.getEnvironmentProperty( KEY );
    assertThat( (String) result, is( nullValue() ) );

    verify( backend ).getEnvironmentProperty( KEY );
  }

  @Test
  public void testGetEnvironmentProperty() {
    doReturn( VALUE ).when( backend ).getEnvironmentProperty( KEY );
    Object result = env.getEnvironmentProperty( KEY );
    assertThat( (String) result, is( equalTo( VALUE ) ) );

    result = env.getEnvironmentProperty( KEY );
    assertThat( (String) result, is( equalTo( VALUE ) ) );

    verify( backend ).getEnvironmentProperty( KEY );
  }

  @Test
  public void testGetURLEncoding() {
    doReturn( VALUE ).when( backend ).getURLEncoding();
    String result = env.getURLEncoding();
    assertThat( result, is( equalTo( VALUE ) ) );

    result = env.getURLEncoding();
    assertThat( result, is( equalTo( VALUE ) ) );

    verify( backend ).getURLEncoding();
  }

  @Test
  public void testGetLocale() {
    Locale locale = new Locale( "test_test" );
    doReturn( locale ).when( backend ).getLocale();
    Locale result = env.getLocale();
    assertThat( result, is( equalTo( locale ) ) );

    result = env.getLocale();
    assertThat( result, is( equalTo( locale ) ) );

    verify( backend ).getLocale();
  }

  @Test
  public void testTimeZone() {
    TimeZone tz = mock( TimeZone.class );
    doReturn( tz ).when( backend ).getTimeZone();
    TimeZone result = env.getTimeZone();
    assertThat( result, is( equalTo( tz ) ) );

    result = env.getTimeZone();
    assertThat( result, is( equalTo( tz ) ) );

    verify( backend ).getTimeZone();
  }

  @Test
  public void testGetUrlExtraParameter() {
    Map<String, String[]> params = Collections.emptyMap();
    doReturn( params ).when( backend ).getUrlExtraParameter();
    Map<String, String[]> result = env.getUrlExtraParameter();
    assertThat( result, is( equalTo( params ) ) );
    verify( backend ).getUrlExtraParameter();
  }
}
