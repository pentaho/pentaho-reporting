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
 *  Copyright (c) 2006 - 2016 Pentaho Corporation..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

import static org.hamcrest.CoreMatchers.hasItems;

public class ReportEnvironmentDataRowTest {
  @Before
  public void setUp() {
    ClassicEngineBoot.getInstance().start();
  }

  @Test
  public void ensure_reportEnvironment_properties_are_published_as_fields() {
    DefaultReportEnvironment re = new DefaultReportEnvironment( ClassicEngineBoot.getInstance().getGlobalConfig() );
    ReportEnvironmentDataRow dr = new ReportEnvironmentDataRow( re );
    Assert.assertThat( Arrays.asList( dr.getColumnNames() ),
        hasItems( "env::locale", "env::locale-language", "env::locale-short" ) );
    Assert.assertEquals( "en_US", dr.get( "env::locale" ) );
    Assert.assertEquals( "en_US", dr.get( "env::locale-short" ) );
    Assert.assertEquals( "en", dr.get( "env::locale-language" ) );
  }
}
