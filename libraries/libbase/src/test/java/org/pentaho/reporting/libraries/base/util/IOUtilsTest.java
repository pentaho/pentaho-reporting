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
