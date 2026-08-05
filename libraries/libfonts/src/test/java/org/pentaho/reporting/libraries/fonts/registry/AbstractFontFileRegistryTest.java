/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 - 2026 by Pentaho Canada Inc. : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2030-06-15
 ******************************************************************************/



package org.pentaho.reporting.libraries.fonts.registry;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

/**
 * @author Andrey Khayrutdinov
 */
public class AbstractFontFileRegistryTest {

  private AbstractFontFileRegistry registry;

  @Before
  public void setUp() {
    registry = new DummyFontFileRegistry();
    registry = spy( registry );
  }

  @Test
  public void registerWindowsFontPaths_WithSlashes() {
    stubWindows( "c:/qwerty;c:/Windows/System32;c:/asdfg" );

    registry.registerDefaultFontPath();

    assertRegistered( "c:\\Windows\\Fonts" );
  }

  @Test
  public void registerWindowsFontPaths_WithBackslashes() {
    stubWindows( "c:\\qwerty;c:\\Windows\\System32;c:\\asdfg" );

    registry.registerDefaultFontPath();

    assertRegistered( "c:\\Windows\\Fonts" );
  }

  /**
   * The Pentaho Server start scripts replace 'java.library.path' with their own native library folder, leaving no
   * 'System32' entry to derive the font directory from.
   */
  @Test
  public void registerWindowsFontPaths_WithoutSystem32OnLibraryPath() {
    stubWindows( "c:\\pentaho-server\\pentaho-solutions\\native-lib\\win64" );
    doReturn( "c:\\Windows" ).when( registry ).safeSystemGetEnv( "WINDIR" );

    registry.registerDefaultFontPath();

    assertRegistered( "c:\\Windows\\Fonts" );
  }

  @Test
  public void registerWindowsFontPaths_IncludesPerUserFonts() {
    stubWindows( "c:\\Windows\\System32" );
    doReturn( "c:\\Users\\pentaho\\AppData\\Local" ).when( registry ).safeSystemGetEnv( "LOCALAPPDATA" );

    registry.registerDefaultFontPath();

    assertRegistered( "c:\\Users\\pentaho\\AppData\\Local\\Microsoft\\Windows\\Fonts" );
  }

  private void stubWindows( String libraryPath ) {
    doReturn( "windows" ).when( registry ).safeSystemGetProperty( eq( "os.name" ), anyString() );
    doReturn( "\\" ).when( registry ).safeSystemGetProperty( eq( "file.separator" ), anyString() );
    doReturn( ";" ).when( registry ).safeSystemGetProperty( eq( "path.separator" ), anyString() );
    doReturn( libraryPath ).when( registry ).safeSystemGetProperty( eq( "java.library.path" ), nullable( String.class ) );
    doReturn( null ).when( registry ).safeSystemGetEnv( anyString() );

    doNothing().when( registry ).loadFromCache( anyString() );
    doNothing().when( registry ).storeToCache( anyString() );
    doNothing().when( registry ).registerFontPath( any( File.class ), anyString() );
  }

  private void assertRegistered( String expectedFontPath ) {
    ArgumentCaptor<File> captor = ArgumentCaptor.forClass( File.class );
    verify( registry, atLeastOnce() ).registerFontPath( captor.capture(), anyString() );

    // java.io.File keeps the foreign separator when this test runs on Linux, so compare normalised paths.
    List<String> registered = new ArrayList<String>();
    for ( File path : captor.getAllValues() ) {
      registered.add( path.getPath().replace( '/', '\\' ).toUpperCase() );
    }
    assertTrue( "expected " + expectedFontPath + " among " + registered,
      registered.contains( expectedFontPath.toUpperCase() ) );
  }
}
