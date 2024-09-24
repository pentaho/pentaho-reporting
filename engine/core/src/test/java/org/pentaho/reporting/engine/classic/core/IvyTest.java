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
