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
 *  Copyright (c) 2006 - 2024 Hitachi Vantara..  All rights reserved.
 */

package org.pentaho.reporting.libraries.fonts.registry;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.io.File;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

/**
 * @author Andrey Khayrutdinov
 */
public class AbstractFontFileRegistryTest {

  private AbstractFontFileRegistry registry;

  @Before
  public void setUp() throws Exception {
    registry = new DummyFontFileRegistry();
    registry = spy( registry );
  }

  @Test
  public void registerWindowsFontPaths_WithSlashes() throws Exception {
    assertRegistersWindowsFontPaths( "c:/qwerty;c:/Windows/System32;c:/asdfg", "c:\\Windows\\Fonts" );
  }

  @Test
  public void registerWindowsFontPaths_WithBackslashes() throws Exception {
    assertRegistersWindowsFontPaths( "c:\\qwerty;c:\\Windows\\System32;c:\\asdfg", "c:\\Windows\\Fonts" );
  }

  private void assertRegistersWindowsFontPaths( String directories, String expectedFontPath ) {
    doReturn( "windows" ).when( registry ).safeSystemGetProperty( eq( "os.name" ), anyString() );
    doReturn( "\\" ).when( registry ).safeSystemGetProperty( eq( "file.separator" ), anyString() );
    doReturn( ";" ).when( registry ).safeSystemGetProperty( eq( "path.separator" ), anyString() );
    doReturn( directories ).when( registry ).safeSystemGetProperty( eq( "java.library.path" ), nullable( String.class ) );

    doNothing().when( registry ).loadFromCache( anyString() );
    doNothing().when( registry ).storeToCache( anyString() );
    doNothing().when( registry ).registerFontFile( any( File.class ), anyString() );

    ArgumentCaptor<File> captor = ArgumentCaptor.forClass( File.class );

    registry.registerDefaultFontPath();
    verify( registry, atLeastOnce() )
      .registerFontPath( captor.capture(), ArgumentCaptor.forClass( String.class ).capture() );

    String actual = captor.getAllValues().get( 0 ).getAbsolutePath();
    // this test is likely to be run on Linux by CI
    // since we cannot prevent inserting slash as a file separator by java.io.File,
    // we are forced to replace it manually
    actual = actual.replaceAll( "/", "\\\\" ).toUpperCase();
    // Linux also adds current dir to the path it cannot recognize, so let's check the end of the resulting path
    assertTrue( actual, actual.endsWith( expectedFontPath.toUpperCase() ) );
  }
}
