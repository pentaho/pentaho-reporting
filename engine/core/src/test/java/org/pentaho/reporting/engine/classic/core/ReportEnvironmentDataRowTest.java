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
 *  Copyright (c) 2006 - 2019 Hitachi Vantara..  All rights reserved.
 */

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
