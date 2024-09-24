/*
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
* Copyright (c) 2008 - 2017 Hitachi Vantara and Contributors.  All rights reserved.
*/

package org.pentaho.reporting.libraries.resourceloader;

import junit.framework.TestCase;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Test cases for the ResourceKeyUtils class
 *
 * @author David Kincade
 */
public class ResourceKeyUtilsTest extends TestCase {
  public ResourceKeyUtilsTest() {
  }

  public ResourceKeyUtilsTest( final String string ) {
    super( string );
  }

  protected void setUp() throws Exception {
    LibLoaderBoot.getInstance().start();
  }

  public void testGetFactoryParametersAsStringNoParameter() throws ResourceKeyCreationException {
    final ResourceManager manager = new ResourceManager();
    manager.registerDefaults();
    ResourceKey key = null;
    String stringParameters = null;

    // Test with no factory parameters specified
    key = manager.createKey( "res://org/pentaho/reporting/libraries/resourceloader/test1.properties" );
    assertNotNull( key );
    stringParameters = ResourceKeyUtils.convertFactoryParametersToString( key.getFactoryParameters() );
    assertNull( "Null parameter set should result null", stringParameters );
  }

  public void testGetFactoryParametersAsStringEmptyMap() throws ResourceKeyCreationException {
    final ResourceManager manager = new ResourceManager();
    manager.registerDefaults();
    ResourceKey key = null;
    String stringParameters = null;
    Map<ParameterKey, Object> factoryParameters = new HashMap<ParameterKey, Object>();

    // Test with empty parameter set
    key =
      manager.createKey( "res://org/pentaho/reporting/libraries/resourceloader/test1.properties", factoryParameters );
    assertNotNull( key );
    stringParameters = ResourceKeyUtils.convertFactoryParametersToString( key.getFactoryParameters() );
    assertNull( "Empty parameter set should result in null", stringParameters );
  }

  public void testGetFactoryParametersAsStringOneParameter() throws ResourceKeyCreationException {
    final ResourceManager manager = new ResourceManager();
    manager.registerDefaults();
    ResourceKey key = null;
    String stringParameters = null;
    Map<ParameterKey, Object> factoryParameters = new HashMap<ParameterKey, Object>();

    // Test with one parameter
    factoryParameters.put( new FactoryParameterKey( "this" ), "that" );
    key =
      manager.createKey( "res://org/pentaho/reporting/libraries/resourceloader/test1.properties", factoryParameters );
    assertNotNull( key );
    stringParameters = ResourceKeyUtils.convertFactoryParametersToString( key.getFactoryParameters() );
    assertEquals( "Unexpected results with one parameter", "\"f:this=that\"", stringParameters );
  }

  public void testGetFactoryParametersAsStringOneParameterNull() throws ResourceKeyCreationException {
    final ResourceManager manager = new ResourceManager();
    manager.registerDefaults();
    ResourceKey key = null;
    String stringParameters = null;
    Map<ParameterKey, Object> factoryParameters = new HashMap<ParameterKey, Object>();

    // Test with one parameter that has a null value
    factoryParameters.clear();
    factoryParameters.put( new FactoryParameterKey( "null" ), null );
    key =
      manager.createKey( "res://org/pentaho/reporting/libraries/resourceloader/test1.properties", factoryParameters );
    assertNotNull( key );
    stringParameters = ResourceKeyUtils.convertFactoryParametersToString( key.getFactoryParameters() );
    assertEquals( "Could not handle parameter with a null value", "\"f:null=\"", stringParameters );
  }

  public void testGetFactoryParametersAsStringMultipleParameter() throws ResourceKeyCreationException {
    final ResourceManager manager = new ResourceManager();
    manager.registerDefaults();
    ResourceKey key = null;
    String stringParameters = null;
    Map<ParameterKey, Object> factoryParameters = new LinkedHashMap<ParameterKey, Object>();

    // Test with multiple parameters (and one has a null value)
    factoryParameters.clear();
    factoryParameters.put( new FactoryParameterKey( manager.getClass().getName() ), new Integer( 10 ) );
    factoryParameters.put( new FactoryParameterKey( "this-one_null" ), null );
    factoryParameters.put( new FactoryParameterKey( "this" ), "that" );
    factoryParameters.put( new FactoryParameterKey( "test with spaces" ), " spaces should be preserved " );
    key =
      manager.createKey( "res://org/pentaho/reporting/libraries/resourceloader/test1.properties", factoryParameters );
    assertNotNull( key );
    stringParameters = ResourceKeyUtils.convertFactoryParametersToString( key.getFactoryParameters() );

    assertEquals( "Map is same", "\"f:org.pentaho.reporting.libraries.resourceloader.ResourceManager=10\"" +
      ":\"f:this-one_null=\"" +
      ":\"f:this=that\"" +
      ":\"f:test with spaces= spaces should be preserved \"", stringParameters );
  }

  /**
   * Tests the parsing of a String into a set of parameters
   */
  public void testGetFactoryParametersFromString() {
    Map map = null;

    // Test null string
    map = ResourceKeyUtils.parseFactoryParametersFromString( null );
    assertNull( "The map should be null if the source string is null", map );

    // Test empty string
    map = ResourceKeyUtils.parseFactoryParametersFromString( "" );
    assertNull( "The map should be null if the source string is blank", map );

    // Test invalid string with no equals signs
    try {
      map = ResourceKeyUtils
        .parseFactoryParametersFromString( "this is a test of the string : a colon : and another:one more" );
      fail( "The parsing should fail if the source string is invalid" );
    } catch ( IllegalStateException ise ) {

    }
    // Test a valid string including a null value in the middle
    map = ResourceKeyUtils.parseFactoryParametersFromString( "\"f:this=that\":" +
      "\"f:null=\":\"f:one=1\":\"f: with spaces = more spaces \":\"f:space= "
      + "\":\"f:junk\":\"f:one=won\":\"f:nullagain=\"" );
    assertNotNull( "The map should not be null if the source string is valid", map );
    assertEquals( "The map should have 6 entries - skipping the junk and not containing a duplicate", 6, map.size() );
    assertEquals( "Invalid value for 'this'", "that", map.get( new FactoryParameterKey( "this" ) ) );
    assertEquals( "Invalid value for ' with spaces '", " more spaces ",
      map.get( new FactoryParameterKey( " with spaces " ) ) );
    assertEquals( "Invalid value for 'space'", " ", map.get( new FactoryParameterKey( "space" ) ) );

    assertTrue( "Could not find entry for 'null'", map.containsKey( new FactoryParameterKey( "null" ) ) );
    assertNull( "Invalid value for 'null'", map.get( new FactoryParameterKey( "null" ) ) );
    assertTrue( "Could not find entry for 'nullagain'", map.containsKey( new FactoryParameterKey( "nullagain" ) ) );
    assertNull( "Invalid value for 'nullagain'", map.get( new FactoryParameterKey( "nullagain" ) ) );

    assertTrue( "Invalid value for 'one'", "1".equals( map.get( new FactoryParameterKey( "one" ) ) )
      || "won".equals( map.get( new FactoryParameterKey( "one" ) ) ) );

    assertTrue( "The map should not contain a value for 'junk'",
      !map.containsKey( new FactoryParameterKey( "junk" ) ) );
  }

  public void testGetSchemaFromString() {
    assertNull( ResourceKeyUtils.readSchemaFromString( null ) );
    assertNull( ResourceKeyUtils.readSchemaFromString( "" ) );
    assertNull( ResourceKeyUtils.readSchemaFromString( "invalid string" ) );
    assertEquals( "sample",
      ResourceKeyUtils.readSchemaFromString( ResourceKeyUtils.SERIALIZATION_PREFIX + "sample;" ) );
    assertEquals( "sample2",
      ResourceKeyUtils.readSchemaFromString( ResourceKeyUtils.SERIALIZATION_PREFIX + "sample2;junk" ) );
    assertEquals( "resourcekey2:sample",
      ResourceKeyUtils.readSchemaFromString( ResourceKeyUtils.SERIALIZATION_PREFIX + "resourcekey2:sample;" ) );
    assertEquals( "resourcekey2:sample2",
      ResourceKeyUtils.readSchemaFromString( ResourceKeyUtils.SERIALIZATION_PREFIX + "resourcekey2:sample2;junk" ) );
  }
}
