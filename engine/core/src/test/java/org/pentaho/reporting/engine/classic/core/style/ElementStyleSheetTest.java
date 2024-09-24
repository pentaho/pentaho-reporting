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
 * Copyright (c) 2001 - 2015 Object Refinery Ltd, Hitachi Vantara and Contributors..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.style;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Stroke;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeFalse;
import static org.junit.Assume.assumeTrue;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.ElementAlignment;
import org.pentaho.reporting.engine.classic.core.layout.style.SimpleStyleSheet;

/**
 * @author Andrey Khayrutdinov
 */
public class ElementStyleSheetTest {

  private static Map<Class, Object> defaultValues;

  @BeforeClass
  public static void init() throws Exception {
    ClassicEngineBoot.getInstance().start();

    List<StyleKey> systemKeys = StyleKey.getDefinedStyleKeysList();
    if ( systemKeys.size() < 2 ) {
      assumeTrue( "This test set needs at least 2 keys. Ignoring all cases.", false );
    }

    defaultValues = new HashMap<>();
    defaultValues.put( Integer.class, 1 );
    defaultValues.put( Short.class, (short) 1 );
    defaultValues.put( Float.class, 1f );
    defaultValues.put( Boolean.class, Boolean.TRUE );
    defaultValues.put( String.class, "qwerty" );
    defaultValues.put( Color.class, Color.BLUE );
    defaultValues.put( Stroke.class, new BasicStroke( 100 ) );
    defaultValues.put( BorderStyle.class, BorderStyle.WAVE );
    defaultValues.put( ElementAlignment.class, ElementAlignment.MIDDLE );
    defaultValues.put( BoxSizing.class, BoxSizing.BORDER_BOX );
    defaultValues.put( WhitespaceCollapse.class, WhitespaceCollapse.PRESERVE );
    defaultValues.put( TextWrap.class, TextWrap.WRAP );
    defaultValues.put( FontSmooth.class, FontSmooth.NEVER );
    defaultValues.put( VerticalTextAlign.class, VerticalTextAlign.SUB );
    defaultValues.put( TextDirection.class, TextDirection.RTL );
    defaultValues.put( TableLayout.class, TableLayout.fixed );

    List<Class<?>> withOutDefaults = new ArrayList<>();
    for ( StyleKey key : systemKeys ) {
      Class<?> type = key.getValueType();
      if ( !defaultValues.containsKey( type ) ) {
        withOutDefaults.add( type );
      }
    }

    if ( !withOutDefaults.isEmpty() ) {
      String template = "There are no predefined default values for these types:\n\t%s\nPlease add it to the static map. Ignoring all cases.";
      String message = String.format( template, withOutDefaults );
      assumeTrue( message, false );
    }
  }

  @AfterClass
  public static void cleanUp() {
    defaultValues = null;
  }


  private List<StyleKey> testKeys;

  @Before
  public void setUp() {
    testKeys = new ArrayList<>();
  }

  @After
  public void tearDown() {
    for ( StyleKey testKey : testKeys ) {
      //noinspection deprecation
      StyleKey.removeTestKey( testKey.name );
    }
    testKeys = null;
  }


  @Test
  public void isLocalKey_OnIntactSheet() {
    List<StyleKey> keys = StyleKey.getDefinedStyleKeysList();

    StyleKey key = keys.get( 0 );
    ElementStyleSheet sheet = new ElementStyleSheet();
    assertFalse( "Non-changed sheet has no 'local' keys", sheet.isLocalKey( key ) );
  }

  @Test
  public void isLocalKey_ReturnsFalse_OnUnknownIdentifier() throws Exception {
    List<StyleKey> before = StyleKey.getDefinedStyleKeysList();
    StyleKey key = before.get( 0 );

    ElementStyleSheet sheet = new ElementStyleSheet();
    sheet.setStyleProperty( key, valueFor( key ) );

    final String keyName = "testKey_isLocalKey_ReturnsFalse_OnUnknownIdentifier";
    //noinspection deprecation
    StyleKey syntheticKey = StyleKey.addTestKey( keyName, String.class, false, false );
    testKeys.add( syntheticKey );
    List<StyleKey> after = StyleKey.getDefinedStyleKeysList();
    assertEquals( "The test syntheticKey should have been added", before.size() + 1, after.size() );

    assertFalse( "Unknown keys are ignored", sheet.isLocalKey( syntheticKey ) );
  }

  @Test
  public void isLocalKey_KeyIsTreatedAsLocal_AfterBeingSetExplicitly() {
    List<StyleKey> keys = StyleKey.getDefinedStyleKeysList();

    StyleKey key1 = keys.get( 0 );
    StyleKey key2 = keys.get( 1 );

    ElementStyleSheet sheet = new ElementStyleSheet();
    sheet.setStyleProperty( key1, valueFor( key1 ) );
    assertTrue( "First key was set, should be 'local'", sheet.isLocalKey( key1 ) );
    assertFalse( "Second key was not set, should not be 'local'", sheet.isLocalKey( key2 ) );
  }


  @Test
  public void toArray_OnIntactSheet() {
    ElementStyleSheet sheet = new ElementStyleSheet();
    Object[] objects = sheet.toArray();
    for ( Object object : objects ) {
      assertNull( object );
    }
  }

  @Test
  public void toArray_ReturnsSetValue() {
    List<StyleKey> keys = StyleKey.getDefinedStyleKeysList();
    StyleKey key = keys.get( 0 );

    ElementStyleSheet sheet = new ElementStyleSheet();
    sheet.setStyleProperty( key, valueFor( key ) );

    Object[] objects = sheet.toArray();
    assertEquals( valueFor( key ), objects[ 0 ] );
  }


  @Test
  public void getStyleProperty_OnIntactSheet() {
    List<StyleKey> keys = StyleKey.getDefinedStyleKeysList();

    ElementStyleSheet sheet = new ElementStyleSheet();
    final Float val = 123f;
    Object property = sheet.getStyleProperty( keys.get( 0 ), val );

    assertEquals( "When nothing has been changed, default value should be returned", val, property );
  }

  @Test
  public void getStyleProperty_ReturnsSetValue() {
    List<StyleKey> keys = StyleKey.getDefinedStyleKeysList();
    StyleKey key = keys.get( 0 );

    ElementStyleSheet sheet = new ElementStyleSheet();
    sheet.setStyleProperty( key, valueFor( key ) );

    Object property = sheet.getStyleProperty( key, 2f );
    assertEquals( valueFor( key ), property );
  }


  @Test( expected = NullPointerException.class )
  public void setStyleProperty_RejectsNullKeys() {
    ElementStyleSheet sheet = new ElementStyleSheet();
    sheet.setStyleProperty( null, "" );
  }

  @Test
  public void setStyleProperty_NullValuesAreTreatedAsAbsent() {
    List<StyleKey> keys = StyleKey.getDefinedStyleKeysList();
    StyleKey key = keys.get( 0 );

    ElementStyleSheet sheet = new ElementStyleSheet();
    sheet.setStyleProperty( key, valueFor( key ) );
    sheet.setStyleProperty( key, null );

    assertEquals( valueFor( key ), sheet.getStyleProperty( key, valueFor( key ) ) );
    assertFalse( sheet.isLocalKey( key ) );
  }

  @Test
  public void setAndGetProperties() {
    List<StyleKey> keys = StyleKey.getDefinedStyleKeysList();

    ElementStyleSheet sheet = new ElementStyleSheet();
    setEverySecondKey( sheet, keys );
    assertEverySecondKey( sheet, keys );
  }


  @Test
  public void clonesCorrectly() {
    List<StyleKey> keys = StyleKey.getDefinedStyleKeysList();

    ElementStyleSheet sheet = new ElementStyleSheet();
    setEverySecondKey( sheet, keys );

    ElementStyleSheet clone = sheet.clone();
    assertEverySecondKey( clone, keys );
  }

  @Test
  public void clone_DiscardsCacheEntries() {
    StyleKey key = null;
    List<StyleKey> keysList = StyleKey.getDefinedStyleKeysList();
    for ( StyleKey styleKey : keysList ) {
      if ( styleKey.isInheritable() ) {
        key = styleKey;
        break;
      }
    }
    assumeFalse( key == null );

    ElementStyleSheet parent = new ElementStyleSheet();
    parent.setStyleProperty( key, valueFor( key ) );

    ElementStyleSheet sheet = new ElementStyleSheet();
    sheet.addInherited( parent );
    assertEquals( valueFor( key ), sheet.getStyleProperty( key ) );
    assertFalse( "Cached key should not be marked as local", sheet.isLocalKey( key ) );

    ElementStyleSheet clone = sheet.clone();
    assertNull( clone.getStyleProperty( key ) );
  }


  @Test
  public void derive_PreservingId() {
    List<StyleKey> keys = StyleKey.getDefinedStyleKeysList();

    ElementStyleSheet sheet = new ElementStyleSheet();
    setEverySecondKey( sheet, keys );

    ElementStyleSheet clone = sheet.derive( true );
    assertEquals( sheet.getId(), clone.getId() );
    assertEverySecondKey( clone, keys );
  }

  @Test
  public void derive_DiscardingId() {
    List<StyleKey> keys = StyleKey.getDefinedStyleKeysList();

    ElementStyleSheet sheet = new ElementStyleSheet();
    setEverySecondKey( sheet, keys );

    ElementStyleSheet clone = sheet.derive( false );
    assertNotSame( sheet.getId(), clone.getId() );
    assertEverySecondKey( clone, keys );
  }


  @Test
  public void getDefinedPropertyNamesArray_OnIntactSheet() {
    ElementStyleSheet sheet = new ElementStyleSheet();
    assertEquals( "Non-changed sheet should return empty array of defined properties",
      0, sheet.getDefinedPropertyNamesArray().length );
  }

  @Test
  public void getDefinedPropertyNamesArray_ReturnsNamesOfSetProperties() {
    List<StyleKey> keys = StyleKey.getDefinedStyleKeysList();

    ElementStyleSheet sheet = new ElementStyleSheet();
    setEverySecondKey( sheet, keys );

    StyleKey[] names = sheet.getDefinedPropertyNamesArray();
    assertEquals( keys.size(), names.length );

    for ( int i = 0; i < names.length; i++ ) {
      StyleKey key = keys.get( i );
      if ( i % 2 == 0 ) {
        assertEquals( key, names[ i ] );
      } else {
        assertNull( String.format( "Key[%d]: %s", i, key.name ), names[i] );
      }
    }
  }


  @Test
  public void serialize_IntactSheet() throws Exception {
    ElementStyleSheet sheet = new ElementStyleSheet();

    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    ObjectOutputStream os = new ObjectOutputStream( bos );
    os.writeObject( sheet );

    ObjectInputStream is = new ObjectInputStream( new ByteArrayInputStream( bos.toByteArray() ) );
    ElementStyleSheet clone = (ElementStyleSheet) is.readObject();
    assertNotSame( "IDs are generated each time", sheet.getId(), clone.getId() );
  }

  @Test
  public void serialize_Changed() throws Exception {
    List<StyleKey> keys = StyleKey.getDefinedStyleKeysList();

    ElementStyleSheet sheet = new ElementStyleSheet();
    setEverySecondKey( sheet, keys );

    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    ObjectOutputStream os = new ObjectOutputStream( bos );
    os.writeObject( sheet );

    ObjectInputStream is = new ObjectInputStream( new ByteArrayInputStream( bos.toByteArray() ) );
    ElementStyleSheet clone = (ElementStyleSheet) is.readObject();
    assertEverySecondKey( clone, keys );
  }


  @Test
  public void addAll() {
    List<StyleKey> keys = StyleKey.getDefinedStyleKeysList();

    ElementStyleSheet sheet = new ElementStyleSheet();
    setEverySecondKey( sheet, keys );

    ElementStyleSheet another = new ElementStyleSheet();
    another.addAll( sheet );

    assertEverySecondKey( another, keys );
    for ( int i = 0; i < keys.size(); i += 2 ) {
      StyleKey key = keys.get( i );
      assertTrue( "addAll() adds properties as local", another.isLocalKey( key ) );
    }
  }


  @Test
  public void addInherited_ElementSheet() {
    List<StyleKey> keys = StyleKey.getDefinedStyleKeysList();

    ElementStyleSheet sheet = new ElementStyleSheet();
    setEveryInheritableKey( sheet, keys );

    ElementStyleSheet another = new ElementStyleSheet();
    another.addInherited( sheet );

    for ( int i = 0; i < keys.size(); i++ ) {
      StyleKey key = keys.get( i );
      if ( key.isInheritable() ) {
        Object property = another.getStyleProperty( key );
        assertEquals( String.format( "Key[%d]: %s", i, key.name ),
          defaultValues.get( key.getValueType() ), property );
        assertFalse( "addInherited() doesn't add properties as local", another.isLocalKey( key ) );
      }
    }
  }

  @Test
  public void addInherited_SimpleSheet() {
    List<StyleKey> keys = StyleKey.getDefinedStyleKeysList();

    ElementStyleSheet sheet = new ElementStyleSheet();
    setEveryInheritableKey( sheet, keys );

    SimpleStyleSheet simpleStyleSheet = new SimpleStyleSheet( new ElementStyleSheet() );
    sheet.addInherited( simpleStyleSheet );

    for ( int i = 0; i < keys.size(); i++ ) {
      StyleKey key = keys.get( i );
      if ( key.isInheritable() ) {
        Object property = sheet.getStyleProperty( key );
        assertNull( "Previous values should be overriden", property );
        assertFalse( "addInherited() doesn't add properties as local", sheet.isLocalKey( key ) );
      }
    }
  }

  private static void setEveryInheritableKey( ElementStyleSheet sheet, List<StyleKey> keys ) {
    for ( StyleKey key : keys ) {
      if ( key.isInheritable() ) {
        sheet.setStyleProperty( key, valueFor( key ) );
      }
    }
  }


  @Test
  public void clearsCorrectly() {
    List<StyleKey> keys = StyleKey.getDefinedStyleKeysList();

    ElementStyleSheet sheet = new ElementStyleSheet();
    setEverySecondKey( sheet, keys );

    sheet.clear();
    assertEquals( 0, sheet.getModificationCount() );
    assertEquals( 0, sheet.getChangeTrackerHash() );

    for ( int i = 0; i < keys.size(); i += 2 ) {
      StyleKey key = keys.get( i );
      Object property = sheet.getStyleProperty( key );
      assertNull( property );
    }
  }


  @Test
  public void copyFrom_OnIntactSheet_SameAsClearing() {
    List<StyleKey> keys = StyleKey.getDefinedStyleKeysList();

    ElementStyleSheet sheet = new ElementStyleSheet();
    setEverySecondKey( sheet, keys );

    sheet.copyFrom( new ElementStyleSheet() );
    assertEquals( 0, sheet.getModificationCount() );
    assertEquals( 0, sheet.getChangeTrackerHash() );

    for ( int i = 0; i < keys.size(); i += 2 ) {
      StyleKey key = keys.get( i );
      Object property = sheet.getStyleProperty( key );
      assertNull( property );
    }
  }


  @Test
  public void copyFrom_OnChangedSheet() {
    List<StyleKey> keys = StyleKey.getDefinedStyleKeysList();

    ElementStyleSheet sheet = new ElementStyleSheet();
    setEverySecondKey( sheet, keys );

    ElementStyleSheet another = new ElementStyleSheet();
    another.copyFrom( sheet );
    assertEquals( sheet.getModificationCount(), another.getModificationCount() );
    assertEquals( sheet.getChangeTrackerHash(), another.getChangeTrackerHash() );

    assertEverySecondKey( another, keys );
    for ( int i = 0; i < keys.size(); i += 2 ) {
      StyleKey key = keys.get( i );
      assertTrue( another.isLocalKey( key ) );
    }
  }


  private static Object valueFor( StyleKey key ) {
    return defaultValues.get( key.getValueType() );
  }

  private static void setEverySecondKey( ElementStyleSheet sheet, List<StyleKey> keys ) {
    for ( int i = 0; i < keys.size(); i += 2 ) {
      StyleKey key = keys.get( i );
      sheet.setStyleProperty( key, valueFor( key ) );
    }
  }

  private static void assertEverySecondKey( ElementStyleSheet sheet, List<StyleKey> keys ) {
    for ( int i = 0; i < keys.size(); i += 2 ) {
      StyleKey key = keys.get( i );
      Object property = sheet.getStyleProperty( key );
      Object expected = valueFor( key );
      assertEquals( String.format( "Key[%d]: %s", i, key.name ), expected, property );
    }
  }
}
