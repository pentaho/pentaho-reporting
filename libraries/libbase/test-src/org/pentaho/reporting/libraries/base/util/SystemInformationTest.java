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
 *  Copyright (c) 2006 - 2017 Pentaho Corporation..  All rights reserved.
 */

package org.pentaho.reporting.libraries.base.util;

import org.apache.commons.logging.Log;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@PrepareForTest( { SystemInformation.class } )
@RunWith( PowerMockRunner.class )
public class SystemInformationTest {

  private SystemInformation mockSI;
  private Log mockLogger;

  @Before
  public void setup() {
    mockSI = mock( SystemInformation.class );
    mockLogger = mock( Log.class );
    when( mockSI.getLogger() ).thenReturn( mockLogger );
    Mockito.doCallRealMethod().when( mockSI ).logSystemInformation();
    PowerMockito.mockStatic( SystemInformation.class );
  }

  @Test
  public void testCallToGetOtherPropertiesWithLoggingLevelInfo() {
    when( mockLogger.isInfoEnabled() ).thenReturn( true );
    mockSI.logSystemInformation();
    PowerMockito.verifyStatic( Mockito.times( 1 ) );
    SystemInformation.getOtherProperties();
  }

  @Test
  public void testCallToGetOtherPropertiesWithLoggingLevelWarn() {
    when( mockLogger.isInfoEnabled() ).thenReturn( false );
    mockSI.logSystemInformation();
    PowerMockito.verifyStatic( Mockito.times( 1 ) );
    SystemInformation.getOtherProperties();
  }

  @After
  public void destroy() {
    mockSI = null;
    mockLogger = null;
  }
}
