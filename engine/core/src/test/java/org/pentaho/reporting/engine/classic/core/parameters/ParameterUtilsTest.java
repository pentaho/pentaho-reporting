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

package org.pentaho.reporting.engine.classic.core.parameters;

import org.junit.Test;
import org.pentaho.reporting.engine.classic.core.ReportEnvironment;

import java.util.Locale;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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
}
