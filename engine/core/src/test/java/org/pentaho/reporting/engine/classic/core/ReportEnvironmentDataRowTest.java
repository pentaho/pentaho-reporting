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

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;

public class ReportEnvironmentDataRowTest {
  @Before
  public void setUp() {
    ClassicEngineBoot.getInstance().start();
  }

  @Test
  public void ensure_reportEnvironment_properties_are_published_as_fields() {
    DefaultReportEnvironment re = new DefaultReportEnvironment( ClassicEngineBoot.getInstance().getGlobalConfig() );
    // ensure the unit test does not depend on the configuration of the machine it is run
    re.setLocale( new Locale( "de", "DE", "WeirdLocalAccent" ) );

    ReportEnvironmentDataRow dr = new ReportEnvironmentDataRow( re );
    final HashSet<Object> names = new HashSet<>();
    names.addAll( Arrays.asList( dr.getColumnNames() ) );
    Assert
      .assertTrue( names.containsAll( Arrays.asList( "env::locale", "env::locale-language", "env::locale-short" ) ) );
    Assert.assertEquals( "de_DE_WeirdLocalAccent", dr.get( "env::locale" ) );
    Assert.assertEquals( "de_DE", dr.get( "env::locale-short" ) );
    Assert.assertEquals( "de", dr.get( "env::locale-language" ) );
  }
}
