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

import org.junit.Assert;
import org.junit.Test;
import org.pentaho.reporting.libraries.base.util.DebugLog;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.security.CodeSource;

public class IvyTest {
  @Test
  public void ivy_Is_Broken_And_Pulls_In_Old_XML_API_Jars() {
    try {
      // all implementation must support this method and this feature, according to the spec.
      // This code will fail with an AbstractMethodError if an old version of the XML-API is
      // on the classpath.
      DocumentBuilderFactory.newInstance().setFeature( XMLConstants.FEATURE_SECURE_PROCESSING, true );
    } catch ( ParserConfigurationException e ) {
      // ignored, we are fishing for AbstractMethodError here.
    }
  }

  @Test
  public void report_xml_apis_location() {
    Class<?> aClass = DocumentBuilderFactory.newInstance().getClass();
    CodeSource cs = aClass.getProtectionDomain().getCodeSource();
    String url = aClass.toString() + " - " + ( ( cs == null ) ? "From Bootstrap" : cs.getLocation() );
    try {
      ivy_Is_Broken_And_Pulls_In_Old_XML_API_Jars();
      DebugLog.log( url );
    } catch ( Throwable t ) {
      Assert.fail( "Offending code found. in jar " + url );
    }
  }
}
