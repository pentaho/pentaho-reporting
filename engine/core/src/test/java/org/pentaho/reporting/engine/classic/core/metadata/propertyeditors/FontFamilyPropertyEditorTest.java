/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


package org.pentaho.reporting.engine.classic.core.metadata.propertyeditors;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.Matchers.arrayContainingInAnyOrder;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.pentaho.reporting.libraries.fonts.awt.AWTFontRegistry;

public class FontFamilyPropertyEditorTest {

  private FontFamilyPropertyEditor editor;

  @Before
  public void setUp() {
    editor = new FontFamilyPropertyEditor();
  }

  @Test
  public void testSettingValue() {
    editor.setValue( null );
    assertThat( editor.getValue(), is( nullValue() ) );

    editor.setValue( -1.5f );
    assertThat( editor.getValue(), is( nullValue() ) );

    editor.setValue( "test_font" );
    assertThat( (String) editor.getValue(), is( equalTo( "test_font" ) ) );
  }

  @Test
  public void testSettingValueAsText() {
    editor.setAsText( null );
    assertThat( editor.getAsText(), is( nullValue() ) );

    editor.setAsText( "auto" );
    assertThat( editor.getAsText(), is( equalTo( "auto" ) ) );
  }

  @Test
  public void testGetTags() {
    assertThat( editor.getTags(), is( arrayContainingInAnyOrder( new AWTFontRegistry().getRegisteredFamilies() ) ) );
  }

  @Test
  public void testIsPaintable() {
    assertThat( editor.isPaintable(), is( equalTo( false ) ) );
  }

  @Test
  public void testGetJavaInitializationStringWithNull() {
    assertThat( editor.getJavaInitializationString(), is( nullValue() ) );
  }

  @Test
  public void testGetCustomEditor() {
    assertThat( editor.getCustomEditor(), is( nullValue() ) );
  }

  @Test
  public void testSupportsCustomEditor() {
    assertThat( editor.supportsCustomEditor(), is( equalTo( false ) ) );
  }
}
