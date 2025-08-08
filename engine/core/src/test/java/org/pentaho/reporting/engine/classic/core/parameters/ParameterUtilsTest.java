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


package org.pentaho.reporting.engine.classic.core.parameters;

import org.junit.Test;
import org.pentaho.reporting.engine.classic.core.ReportEnvironment;

import java.util.Locale;
import java.sql.Time;
import java.sql.Timestamp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;


public class ParameterUtilsTest {

  private ReportEnvironment re = mock( ReportEnvironment.class );

  @Test
  public void getLocale_NullTest() {
    when( re.getLocale() ).thenReturn( null );
    Locale result = ParameterUtils.getLocale( re );
    assertNotNull( result );
  }

  @Test
  public void getLocale_ValidTest() {
    when( re.getLocale() ).thenReturn( new Locale( "just.for.test" ) );
    Locale result = ParameterUtils.getLocale( re );
    assertEquals( "just.for.test", result.getLanguage() );
  }

@Test
public void testIsTimeSelectorApplicable() {
    // Test with java.util.Date
    assertTrue(ParameterUtils.isTimeSelectorApplicable(java.util.Date.class));

    // Test with java.sql.Date
    assertTrue(ParameterUtils.isTimeSelectorApplicable(java.sql.Date.class));

    // Test with java.sql.Time
    assertTrue(ParameterUtils.isTimeSelectorApplicable(Time.class));

    // Test with java.sql.Timestamp
    assertTrue(ParameterUtils.isTimeSelectorApplicable(Timestamp.class));

    // Test with non-date types
    assertFalse(ParameterUtils.isTimeSelectorApplicable(String.class));
    assertFalse(ParameterUtils.isTimeSelectorApplicable(Integer.class));
    assertFalse(ParameterUtils.isTimeSelectorApplicable(Object.class));

    // Test null case last
    assertFalse(ParameterUtils.isTimeSelectorApplicable(null));
}

}
