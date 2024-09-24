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

package org.pentaho.reporting.libraries.base.versioning;

import org.junit.Assert;
import org.junit.Test;
import org.pentaho.reporting.libraries.base.LibBaseInfo;

public class VersionHelperTest {
  protected static class MockVersionHelper extends VersionHelper {
    public MockVersionHelper() {
      super( LibBaseInfo.getInstance() );
    }

    public void parseVersion( final String version ) {
      super.parseVersion( version );
    }
  }

  public VersionHelperTest() {
  }

  // Generic trunk-snapshot label as used during development run.
  @Test
  public void testParseTrunkSnapshot() {
    MockVersionHelper versionHelper = new MockVersionHelper();
    versionHelper.parseVersion( "TRUNK-SNAPSHOT" );
    Assert.assertEquals( "999", versionHelper.getReleaseMajor() );
    Assert.assertEquals( "999", versionHelper.getReleaseMinor() );
    Assert.assertEquals( "999", versionHelper.getReleaseMilestone() );
    Assert.assertEquals( "0", versionHelper.getReleasePatch() );
    Assert.assertEquals( "SNAPSHOT", versionHelper.getReleaseBuildNumber() );
  }

  // Generic trunk-snapshot label as used when compiled on CI.
  @Test
  public void testParseTrunkSnapshotDev() {
    MockVersionHelper versionHelper = new MockVersionHelper();
    versionHelper.parseVersion( "TRUNK-SNAPSHOT.development" );
    Assert.assertEquals( "999", versionHelper.getReleaseMajor() );
    Assert.assertEquals( "999", versionHelper.getReleaseMinor() );
    Assert.assertEquals( "999", versionHelper.getReleaseMilestone() );
    Assert.assertEquals( "0", versionHelper.getReleasePatch() );
    Assert.assertEquals( "SNAPSHOT", versionHelper.getReleaseBuildNumber() );
  }

  // Generic snapshot label as used when working on CI release branches.
  @Test
  public void testParse51SnapshotPure() {
    MockVersionHelper versionHelper = new MockVersionHelper();
    versionHelper.parseVersion( "5.1-SNAPSHOT" );
    Assert.assertEquals( "5", versionHelper.getReleaseMajor() );
    Assert.assertEquals( "1", versionHelper.getReleaseMinor() );
    Assert.assertEquals( "999", versionHelper.getReleaseMilestone() );
    Assert.assertEquals( "0", versionHelper.getReleasePatch() );
    Assert.assertEquals( "SNAPSHOT", versionHelper.getReleaseBuildNumber() );
  }

  // Generic snapshot label as used when working on CI release branches.
  @Test
  public void testParse51SnapshotDev() {
    MockVersionHelper versionHelper = new MockVersionHelper();
    versionHelper.parseVersion( "5.1-SNAPSHOT.development" );
    Assert.assertEquals( "5", versionHelper.getReleaseMajor() );
    Assert.assertEquals( "1", versionHelper.getReleaseMinor() );
    Assert.assertEquals( "999", versionHelper.getReleaseMilestone() );
    Assert.assertEquals( "0", versionHelper.getReleasePatch() );
    Assert.assertEquals( "SNAPSHOT", versionHelper.getReleaseBuildNumber() );
  }

  // Generic snapshot label as used when working on CI branches pre-release of SP (as used in IDE).
  @Test
  public void testParse510SnapshotPure() {
    MockVersionHelper versionHelper = new MockVersionHelper();
    versionHelper.parseVersion( "5.1.0-SNAPSHOT" );
    Assert.assertEquals( "5", versionHelper.getReleaseMajor() );
    Assert.assertEquals( "1", versionHelper.getReleaseMinor() );
    Assert.assertEquals( "0", versionHelper.getReleaseMilestone() );
    Assert.assertEquals( "0", versionHelper.getReleasePatch() );
    Assert.assertEquals( "SNAPSHOT", versionHelper.getReleaseBuildNumber() );
  }

  // Generic snapshot label as used when working on CI branches pre-release of SP.
  @Test
  public void testParse510SnapshotDev() {
    MockVersionHelper versionHelper = new MockVersionHelper();
    versionHelper.parseVersion( "5.1.0-SNAPSHOT.development" );
    Assert.assertEquals( "5", versionHelper.getReleaseMajor() );
    Assert.assertEquals( "1", versionHelper.getReleaseMinor() );
    Assert.assertEquals( "0", versionHelper.getReleaseMilestone() );
    Assert.assertEquals( "0", versionHelper.getReleasePatch() );
    Assert.assertEquals( "SNAPSHOT", versionHelper.getReleaseBuildNumber() );
  }

  // Version label used in release versions on and after 5.0 release
  @Test
  public void testParse510ReleasePure() {
    MockVersionHelper versionHelper = new MockVersionHelper();
    versionHelper.parseVersion( "5.1.3.2-752.-1" );
    Assert.assertEquals( "5", versionHelper.getReleaseMajor() );
    Assert.assertEquals( "1", versionHelper.getReleaseMinor() );
    Assert.assertEquals( "3", versionHelper.getReleaseMilestone() );
    Assert.assertEquals( "2", versionHelper.getReleasePatch() );
    Assert.assertEquals( "752", versionHelper.getReleaseBuildNumber() );
  }

}
