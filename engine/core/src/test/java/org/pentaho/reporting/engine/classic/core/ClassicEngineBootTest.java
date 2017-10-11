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

package org.pentaho.reporting.engine.classic.core;

import junit.framework.Assert;
import junit.framework.TestCase;
import org.mockito.Mockito;
import org.pentaho.reporting.libraries.base.versioning.ProjectInformation;

public class ClassicEngineBootTest extends TestCase {
  protected void setUp() throws Exception {
    ClassicEngineBoot.getInstance().start();
  }

  public void testIsValidVersion() {
    // Set current version to 3.3.3
    final ProjectInformation projectInfo = Mockito.mock( ProjectInformation.class );
    Mockito.when( projectInfo.getReleaseMajor() ).thenReturn( "3" );
    Mockito.when( projectInfo.getReleaseMinor() ).thenReturn( "3" );
    Mockito.when( projectInfo.getReleaseMilestone() ).thenReturn( "3" );

    final int[] trunk = parseVersionId( ClassicEngineBoot.VERSION_TRUNK );
    Assert.assertEquals( "TRUNK version must be valid", ClassicEngineBoot.VersionValidity.VALID, ClassicEngineBoot
        .getInstance().isValidVersion( trunk[0], trunk[1], trunk[2], projectInfo ) );

    Assert.assertEquals( "The same version must be valid", ClassicEngineBoot.VersionValidity.VALID, ClassicEngineBoot
        .getInstance().isValidVersion( 3, 3, 3, projectInfo ) );

    Assert.assertEquals( "Older version must be valid", ClassicEngineBoot.VersionValidity.VALID, ClassicEngineBoot
        .getInstance().isValidVersion( 2, 2, 2, projectInfo ) );

    Assert.assertEquals( "Newer major version must be release invalid",
        ClassicEngineBoot.VersionValidity.INVALID_RELEASE, ClassicEngineBoot.getInstance().isValidVersion( 4, 3, 3,
            projectInfo ) );

    Assert.assertEquals( "Newer minor version must be release invalid",
        ClassicEngineBoot.VersionValidity.INVALID_RELEASE, ClassicEngineBoot.getInstance().isValidVersion( 3, 4, 3,
            projectInfo ) );

    Assert.assertEquals( "Newer patch version must be patch invalid", ClassicEngineBoot.VersionValidity.INVALID_PATCH,
        ClassicEngineBoot.getInstance().isValidVersion( 3, 3, 4, projectInfo ) );
  }

  public void testTrunkIsValid() {
    // Set current version to 3.3.3
    final ProjectInformation projectInfo = Mockito.mock( ProjectInformation.class );
    Mockito.when( projectInfo.getReleaseMajor() ).thenReturn( "999" );
    Mockito.when( projectInfo.getReleaseMinor() ).thenReturn( "999" );
    Mockito.when( projectInfo.getReleaseMilestone() ).thenReturn( "999" );

    final int[] trunk = parseVersionId( ClassicEngineBoot.VERSION_TRUNK );
    Assert.assertEquals( "TRUNK version must be valid", ClassicEngineBoot.VersionValidity.VALID, ClassicEngineBoot
        .getInstance().isValidVersion( trunk[0], trunk[1], trunk[2], projectInfo ) );

    Assert.assertEquals( "The same version must be valid", ClassicEngineBoot.VersionValidity.VALID, ClassicEngineBoot
        .getInstance().isValidVersion( 3, 3, 3, projectInfo ) );

    Assert.assertEquals( "Older version must be valid", ClassicEngineBoot.VersionValidity.VALID, ClassicEngineBoot
        .getInstance().isValidVersion( 2, 2, 2, projectInfo ) );

    Assert.assertEquals( "Newer major version must be release valid", ClassicEngineBoot.VersionValidity.VALID,
        ClassicEngineBoot.getInstance().isValidVersion( 4, 3, 3, projectInfo ) );

    Assert.assertEquals( "Newer minor version must be release valid", ClassicEngineBoot.VersionValidity.VALID,
        ClassicEngineBoot.getInstance().isValidVersion( 3, 4, 3, projectInfo ) );

    Assert.assertEquals( "Newer patch version must be patch valid", ClassicEngineBoot.VersionValidity.VALID,
        ClassicEngineBoot.getInstance().isValidVersion( 3, 3, 4, projectInfo ) );
  }

  private int[] parseVersionId( int versionId ) {
    if ( versionId <= 0 || versionId > 999000000 ) {
      versionId = ClassicEngineBoot.VERSION_TRUNK;
    }

    int[] version = new int[3];
    version[0] = versionId % 1000; // patch
    version[1] = ( versionId / 1000 ) % 1000; // minor
    version[2] = ( versionId / 1000000 ); // major

    return version;
  }

}
