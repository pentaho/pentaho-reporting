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

package org.pentaho.reporting.libraries.base.util;

import junit.framework.TestCase;

public class IOUtilsTest extends TestCase {
  public IOUtilsTest( final String s ) {
    super( s );
  }

  public void testGetRelativePath() {
    assertEquals( "../Desktop/samples/test.file", IOUtils.getInstance().createRelativePath
      ( "/User/users/Desktop/samples/test.file", "/User/users/Downloads/someotherfile.xml" ) );

    assertEquals( "../test.file", IOUtils.getInstance().createRelativePath
      ( "/User/users/test.file", "/User/users/Downloads/someotherfile.xml" ) );

    assertEquals( "../../test.file", IOUtils.getInstance().createRelativePath
      ( "/User/users/test.file", "/User/users/Downloads/test/someotherfile.xml" ) );

    assertEquals( "/User/users/test.file", IOUtils.getInstance().createRelativePath
      ( "/User/users/test.file", "/AUser/users/Downloads/someotherfile.xml" ) );
  }

  public void testGetAbsolutePath() {
    assertEquals( "content.xml", IOUtils.getInstance().getAbsolutePath( "content.xml", "" ) ); // expect: content.xml
    assertEquals( "directory/content.xml",
      IOUtils.getInstance().getAbsolutePath( "content.xml", "directory/" ) ); // expect: directory/content.xml
    assertEquals( "content.xml",
      IOUtils.getInstance().getAbsolutePath( "content.xml", "directory" ) ); // expect: content.xml
    assertEquals( "content.xml", IOUtils.getInstance().getAbsolutePath( "../content.xml", "" ) ); // expect: content.xml
    assertEquals( "content.xml",
      IOUtils.getInstance().getAbsolutePath( "../content.xml", "directory/" ) ); // expect: content.xml
    assertEquals( "content.xml",
      IOUtils.getInstance().getAbsolutePath( "../content.xml", "directory" ) ); // expect: content.xml
    assertEquals( "content.xml", IOUtils.getInstance().getAbsolutePath( "/content.xml", "" ) ); // expect: content.xml
    assertEquals( "content.xml",
      IOUtils.getInstance().getAbsolutePath( "/content.xml", "directory/" ) ); // expect: content.xml
    assertEquals( "content.xml",
      IOUtils.getInstance().getAbsolutePath( "/content.xml", "directory" ) ); // expect: content.xml
    assertEquals( "content.xml",
      IOUtils.getInstance().getAbsolutePath( "/../content.xml", "" ) ); // expect: content.xml
    assertEquals( "content.xml",
      IOUtils.getInstance().getAbsolutePath( "/../content.xml", "directory/" ) ); // expect: content.xml
    assertEquals( "content.xml",
      IOUtils.getInstance().getAbsolutePath( "/../content.xml", "directory" ) ); // expect: content.xml
    assertEquals( "content.xml",
      IOUtils.getInstance().getAbsolutePath( "test/../content.xml", "" ) ); // expect: content.xml
    assertEquals( "directory/content.xml",
      IOUtils.getInstance().getAbsolutePath( "test/../content.xml", "directory/" ) ); // expect: directory/content.xml
    assertEquals( "content.xml",
      IOUtils.getInstance().getAbsolutePath( "test/../content.xml", "directory" ) ); // expect: content.xml

    assertEquals( "content/", IOUtils.getInstance().getAbsolutePath( "/content/", "" ) ); // expect: content.xml
    assertEquals( "content/",
      IOUtils.getInstance().getAbsolutePath( "/content/", "directory/" ) ); // expect: content.xml
    assertEquals( "content/",
      IOUtils.getInstance().getAbsolutePath( "/content/", "directory" ) ); // expect: content.xml

  }
}
