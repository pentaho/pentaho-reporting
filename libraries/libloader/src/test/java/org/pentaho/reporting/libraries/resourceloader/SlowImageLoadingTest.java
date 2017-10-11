/*!
* This program is free software; you can redistribute it and/or modify it under the
* terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
* Foundation.
*
* You should have received a copy of the GNU Lesser General Public License along with this
* program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
* or from the Free Software Foundation, Inc.,
* 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
*
* This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
* without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
* See the GNU Lesser General Public License for more details.
*
* Copyright (c) 2002-2017 Hitachi Vantara..  All rights reserved.
*/

package org.pentaho.reporting.libraries.resourceloader;

import junit.framework.TestCase;

public class SlowImageLoadingTest extends TestCase {
  public SlowImageLoadingTest() {
  }

  // This test case relies on a dummy servlet to test caching in libLoader.  Comment out
  // due to test app server dependencies
  public void testLoad() throws Exception {
    //    final HierarchicalConfiguration globalConfig = (HierarchicalConfiguration) LibLoaderBoot.getInstance()
    // .getGlobalConfig();
    //    globalConfig.setConfigProperty("org.pentaho.reporting.libraries.resourceloader.config.url.FixedCacheDelay",
    // "0");
    //    globalConfig.setConfigProperty("org.pentaho.reporting.libraries.resourceloader.config.url
    // .FixBrokenWebServiceDateHeader", "true");
    //    ResourceManager mgr = new ResourceManager();
    //    mgr.registerDefaults();
    //    System.out.println("Start");
    //    mgr.load(mgr.createKey(new URL("http://localhost:8080/test")));
    //    System.out.println("load");
    //    mgr.load(mgr.createKey(new URL("http://localhost:8080/test")));
    //    System.out.println("reload");
    //    mgr.load(mgr.createKey(new URL("http://localhost:8080/test")));
    //    System.out.println("reload");
  }
}
