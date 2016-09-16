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
 *  Copyright (c) 2006 - 2015 Pentaho Corporation..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.parameters;

import org.junit.Test;
import org.pentaho.reporting.engine.classic.core.ResourceBundleFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 *
 * Created by dima.prokopenko@gmail.com on 9/21/2016.
 */
public class ParameterUtilsTest {

  private static final String CORE_LABEL = "coreLabel";
  private static final String RESOURCE = "resource";
  private static final String EXISTED = "existed";
  private static final String CORE_LABEL_TRANSLATED = "coreLabel=TRANSLATED";
  private static final String FORMAT_DATE = "yyyy mm";
  private static final String FORMAT_DATE_TRANSLATED = "yyyy\\u0020mm=TRANSLATED";
  private static final String ANYKEY_TRANSLATED = "anykey=TRANSLATED";
  private static final String ANYKEY = "anykey";
  private static final String ERROR_MESSAGE = "errorMessage";
  private static final String ERROR_MESSAGE_TRANSLATED = "errorMessage=TRANSLATED";
  private static final String TRANSLATED="TRANSLATED";

  private ParameterContext context = mock( ParameterContext.class );

  @Test
  public void getTranslatedLablelFalseTest() {
    ParameterDefinitionEntry entry = mock( ParameterDefinitionEntry.class );

    // has label
    when( entry.getParameterAttribute(
      eq( ParameterAttributeNames.Core.NAMESPACE ),
      eq( ParameterAttributeNames.Core.LABEL ),
      any()
    ) ).thenReturn( CORE_LABEL );

    // translate is false
    when( entry.getParameterAttribute(
      eq( ParameterAttributeNames.Core.NAMESPACE ),
      eq( ParameterAttributeNames.Core.TRANSLATE_LABEL ),
      any()
    ) ).thenReturn( Boolean.FALSE.toString() );

    String translated = ParameterUtils.getTranslatedLabel( entry, context );
    assertNotNull( translated );
    assertEquals( CORE_LABEL, translated );
  }

  @Test
  public void getTranslatedLabelNullTest() {
    ParameterDefinitionEntry entry = mock( ParameterDefinitionEntry.class );

    // has label
    when( entry.getParameterAttribute(
      eq( ParameterAttributeNames.Core.NAMESPACE ),
      eq( ParameterAttributeNames.Core.LABEL ),
      any()
    ) ).thenReturn( CORE_LABEL );

    // and nothing else

    String translated = ParameterUtils.getTranslatedLabel( entry, context );
    assertNotNull( translated );
    assertEquals( CORE_LABEL, translated );
  }

  @Test
  public void getTranslatedLableNoLabelTest() {
    ParameterDefinitionEntry entry = mock( ParameterDefinitionEntry.class );

    // no label at all
    when( entry.getParameterAttribute(
      eq( ParameterAttributeNames.Core.NAMESPACE ),
      eq( ParameterAttributeNames.Core.LABEL ),
      any()
    ) ).thenReturn( null );

    String translated = ParameterUtils.getTranslatedLabel( entry, context );
    assertNull( translated );
  }

  @Test
  public void getTranslatedLabelTranslateNoResourceTest() {
    ParameterDefinitionEntry entry = mock( ParameterDefinitionEntry.class );

    // has label
    when( entry.getParameterAttribute(
      eq( ParameterAttributeNames.Core.NAMESPACE ),
      eq( ParameterAttributeNames.Core.LABEL ),
      any()
    ) ).thenReturn( CORE_LABEL );

    // translate is true
    when( entry.getParameterAttribute(
      eq( ParameterAttributeNames.Core.NAMESPACE ),
      eq( ParameterAttributeNames.Core.TRANSLATE_LABEL ),
      any()
    ) ).thenReturn( Boolean.TRUE.toString() );

    // but there is not resource file so return the same
    String translated = ParameterUtils.getTranslatedLabel( entry, context );
    assertNotNull( translated );
    assertEquals( CORE_LABEL, translated );
  }

  @Test
  public void testTranslatedLabelPositive() {
    ParameterDefinitionEntry entry = mock( ParameterDefinitionEntry.class );

    // has label
    when( entry.getParameterAttribute(
      eq( ParameterAttributeNames.Core.NAMESPACE ),
      eq( ParameterAttributeNames.Core.LABEL ),
      any()
    ) ).thenReturn( CORE_LABEL );

    // translate is true
    when( entry.getParameterAttribute(
      eq( ParameterAttributeNames.Core.NAMESPACE ),
      eq( ParameterAttributeNames.Core.TRANSLATE_LABEL ),
      any()
    ) ).thenReturn( Boolean.TRUE.toString() );

    // and we have resource name:
    when( entry.getParameterAttribute(
      eq( ParameterAttributeNames.Core.NAMESPACE ),
      eq( ParameterAttributeNames.Core.TRANSLATE_RESOURCE_ID ),
      any()
    ) ).thenReturn( RESOURCE );

    String translated = ParameterUtils.getTranslatedLabel( entry, context );

    // resource not found we can't so fail silently and return key
    assertNotNull( translated );
    assertEquals( CORE_LABEL, translated );
  }

  @Test
  public void testTranslateLabelFromResourceValue() throws IOException {
    ParameterDefinitionEntry entry = mock( ParameterDefinitionEntry.class );

    // has label
    when( entry.getParameterAttribute(
      eq( ParameterAttributeNames.Core.NAMESPACE ),
      eq( ParameterAttributeNames.Core.LABEL ),
      any()
    ) ).thenReturn( CORE_LABEL );

    // translate is true
    when( entry.getParameterAttribute(
      eq( ParameterAttributeNames.Core.NAMESPACE ),
      eq( ParameterAttributeNames.Core.TRANSLATE_LABEL ),
      any()
    ) ).thenReturn( Boolean.TRUE.toString() );

    // and we have resource name:
    when( entry.getParameterAttribute(
      eq( ParameterAttributeNames.Core.NAMESPACE ),
      eq( ParameterAttributeNames.Core.TRANSLATE_RESOURCE_ID ),
      any()
    ) ).thenReturn( EXISTED );

    // ResourceBundle has final methods we can't mock so create real bundle.
    InputStream stream = new ByteArrayInputStream( CORE_LABEL_TRANSLATED.getBytes( StandardCharsets.UTF_8 ) );
    // ok we have bundle finally
    ResourceBundle bundle = new PropertyResourceBundle( stream );

    ResourceBundleFactory factory = mock( ResourceBundleFactory.class );
    when( factory.getResourceBundle( eq( EXISTED ) ) ).thenReturn( bundle );

    when( context.getResourceBundleFactory() ).thenReturn( factory );

    String translated = ParameterUtils.getTranslatedLabel( entry, context );
    assertNotNull( translated );
    assertEquals( TRANSLATED, translated );
  }


  @Test
  public void getTranslatedDateFormatIsNullTest() {
    // always return nulls
    ParameterDefinitionEntry entry = mock( ParameterDefinitionEntry.class );
    String translated = ParameterUtils.getTranslatedDateFormat( entry, context );
    assertNull( translated );
  }

  @Test
  public void getTranslatedDateFormatOnlySetTest() {
    ParameterDefinitionEntry entry = mock( ParameterDefinitionEntry.class );

    when( entry.getParameterAttribute(
      eq( ParameterAttributeNames.Core.NAMESPACE ),
      eq( ParameterAttributeNames.Core.DATA_FORMAT ),
      any()
    ) ).thenReturn( FORMAT_DATE );

    String translated = ParameterUtils.getTranslatedDateFormat( entry, context );
    assertNotNull( translated );
    assertEquals( FORMAT_DATE, translated );
  }

  @Test
  public void getTranslatedDateFormatTranslateNullTest() {
    ParameterDefinitionEntry entry = mock( ParameterDefinitionEntry.class );

    // date format is set
    when( entry.getParameterAttribute(
      eq( ParameterAttributeNames.Core.NAMESPACE ),
      eq( ParameterAttributeNames.Core.DATA_FORMAT ),
      any()
    ) ).thenReturn( FORMAT_DATE );

    // translate value is null
    when( entry.getParameterAttribute(
      eq( ParameterAttributeNames.Core.NAMESPACE ),
      eq( ParameterAttributeNames.Core.TRANSLATE_DATA_FORMAT ),
      any()
    ) ).thenReturn( null );

    // anyway we have a value
    String translated = ParameterUtils.getTranslatedDateFormat( entry, context );
    assertNotNull( translated );
    assertEquals( FORMAT_DATE, translated );
  }

  @Test
  public void getTranslatedDateFormatNoResourceId() {
    ParameterDefinitionEntry entry = mock( ParameterDefinitionEntry.class );

    // date format is set
    when( entry.getParameterAttribute(
      eq( ParameterAttributeNames.Core.NAMESPACE ),
      eq( ParameterAttributeNames.Core.DATA_FORMAT ),
      any()
    ) ).thenReturn( FORMAT_DATE );

    // translate value is YES
    when( entry.getParameterAttribute(
      eq( ParameterAttributeNames.Core.NAMESPACE ),
      eq( ParameterAttributeNames.Core.TRANSLATE_DATA_FORMAT ),
      any()
    ) ).thenReturn( Boolean.TRUE.toString() );

    String translated = ParameterUtils.getTranslatedDateFormat( entry, context );
    assertNotNull( translated );
    assertEquals( FORMAT_DATE, translated );
  }

  @Test
  public void getTranslatedDateFormatFinallyTranslated() throws IOException {
    ParameterDefinitionEntry entry = mock( ParameterDefinitionEntry.class );

    // date format is set
    when( entry.getParameterAttribute(
      eq( ParameterAttributeNames.Core.NAMESPACE ),
      eq( ParameterAttributeNames.Core.DATA_FORMAT ),
      any()
    ) ).thenReturn( FORMAT_DATE );

    // translate value is YES
    when( entry.getParameterAttribute(
      eq( ParameterAttributeNames.Core.NAMESPACE ),
      eq( ParameterAttributeNames.Core.TRANSLATE_DATA_FORMAT ),
      any()
    ) ).thenReturn( Boolean.TRUE.toString() );

    // and we have correct resource id:
    when( entry.getParameterAttribute(
      eq( ParameterAttributeNames.Core.NAMESPACE ),
      eq( ParameterAttributeNames.Core.TRANSLATE_RESOURCE_ID ),
      any()
    ) ).thenReturn( EXISTED );

    // We have to escape space in property key name, otherwise incorrect loading happens.

    InputStream stream = new ByteArrayInputStream( FORMAT_DATE_TRANSLATED.getBytes( StandardCharsets.UTF_8 ) );
    ResourceBundle bundle = new PropertyResourceBundle( stream );
    ResourceBundleFactory factory = mock( ResourceBundleFactory.class );
    when( factory.getResourceBundle( eq( EXISTED ) ) ).thenReturn( bundle );
    when( context.getResourceBundleFactory() ).thenReturn( factory );

    String translated = ParameterUtils.getTranslatedDateFormat( entry, context );
    assertNotNull( translated );
    assertEquals( TRANSLATED, translated );
  }

  @Test
  public void getResourceKeyValueNullTest() {
    ParameterDefinitionEntry entry = mock( ParameterDefinitionEntry.class );
    String translated = ParameterUtils.getResourceKeyValue( ANYKEY, entry, context );
    assertNotNull( translated );
    assertEquals( ANYKEY, translated );
  }

  @Test
  public void getResourceDisplayValueTest() throws IOException {
    ParameterDefinitionEntry entry = mock( ParameterDefinitionEntry.class );

    when( entry.getParameterAttribute(
      eq( ParameterAttributeNames.Core.NAMESPACE ),
      eq( ParameterAttributeNames.Core.TRANSLATE_RESOURCE_ID ),
      any()
    ) ).thenReturn( EXISTED );

    InputStream stream = new ByteArrayInputStream( ANYKEY_TRANSLATED.getBytes( StandardCharsets.UTF_8 ) );
    ResourceBundle bundle = new PropertyResourceBundle( stream );
    ResourceBundleFactory factory = mock( ResourceBundleFactory.class );
    when( factory.getResourceBundle( eq( EXISTED ) ) ).thenReturn( bundle );
    when( context.getResourceBundleFactory() ).thenReturn( factory );

    String translated = ParameterUtils.getResourceKeyValue( ANYKEY, entry, context );

    assertNotNull( translated );
    assertEquals( TRANSLATED, translated );
  }

  @Test
  public void getTranslatedErrorMessageNullTest() {
    ParameterDefinitionEntry entry = mock( ParameterDefinitionEntry.class );
    String translated = ParameterUtils.getTranslatedErrorMessage( entry, context );
    assertNull( translated );
  }

  @Test
  public void getTranslatedErrorMessageSimpleValueTest() {
    ParameterDefinitionEntry entry = mock( ParameterDefinitionEntry.class );
    when( entry.getParameterAttribute(
      eq( ParameterAttributeNames.Core.NAMESPACE ),
      eq( ParameterAttributeNames.Core.ERROR_MESSAGE ),
      any()
    ) ).thenReturn( ERROR_MESSAGE );

    String translated = ParameterUtils.getTranslatedErrorMessage( entry, context );
    assertNotNull( translated );
    assertEquals( ERROR_MESSAGE, translated );
  }

  @Test
  public void getTranslatedErrorMessageResourceNotSetTest() {
    ParameterDefinitionEntry entry = mock( ParameterDefinitionEntry.class );
    when( entry.getParameterAttribute(
      eq( ParameterAttributeNames.Core.NAMESPACE ),
      eq( ParameterAttributeNames.Core.ERROR_MESSAGE ),
      any()
    ) ).thenReturn( ERROR_MESSAGE );
    when( entry.getParameterAttribute(
      eq( ParameterAttributeNames.Core.NAMESPACE ),
      eq( ParameterAttributeNames.Core.TRANSLATE_ERROR_MESSAGE ),
      any()
    ) ).thenReturn( Boolean.TRUE.toString() );

    String translated = ParameterUtils.getTranslatedErrorMessage( entry, context );
    assertNotNull( translated );
    assertEquals( ERROR_MESSAGE, translated );
  }

  @Test
  public void getTranslatedErrorMessagePositiveTest() throws IOException {
    ParameterDefinitionEntry entry = mock( ParameterDefinitionEntry.class );
    when( entry.getParameterAttribute(
      eq( ParameterAttributeNames.Core.NAMESPACE ),
      eq( ParameterAttributeNames.Core.ERROR_MESSAGE ),
      any()
    ) ).thenReturn( ERROR_MESSAGE );
    when( entry.getParameterAttribute(
      eq( ParameterAttributeNames.Core.NAMESPACE ),
      eq( ParameterAttributeNames.Core.TRANSLATE_ERROR_MESSAGE ),
      any()
    ) ).thenReturn( Boolean.TRUE.toString() );

    when( entry.getParameterAttribute(
      eq( ParameterAttributeNames.Core.NAMESPACE ),
      eq( ParameterAttributeNames.Core.TRANSLATE_RESOURCE_ID ),
      any()
    ) ).thenReturn( EXISTED );

    InputStream stream = new ByteArrayInputStream( ERROR_MESSAGE_TRANSLATED.getBytes( StandardCharsets.UTF_8 ) );
    ResourceBundle bundle = new PropertyResourceBundle( stream );
    ResourceBundleFactory factory = mock( ResourceBundleFactory.class );
    when( factory.getResourceBundle( eq( EXISTED ) ) ).thenReturn( bundle );
    when( context.getResourceBundleFactory() ).thenReturn( factory );

    String translated = ParameterUtils.getTranslatedErrorMessage( entry, context );

    assertNotNull( translated );
    assertEquals( TRANSLATED, translated );
  }
}
