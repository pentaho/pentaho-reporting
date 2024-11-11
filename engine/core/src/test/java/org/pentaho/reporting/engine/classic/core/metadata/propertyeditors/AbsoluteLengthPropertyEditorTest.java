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
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

public class AbsoluteLengthPropertyEditorTest {

  private AbsoluteLengthPropertyEditor editor;

  @Before
  public void setUp() {
    editor = new AbsoluteLengthPropertyEditor();
  }

  @Test
  public void testSettingValue() {
    editor.setValue( null );
    assertThat( editor.getValue(), is( nullValue() ) );

    editor.setValue( 1.5f );
    assertThat( editor.getValue(), is( instanceOf( Float.class ) ) );
    assertThat( (Float) editor.getValue(), is( equalTo( 1.5f ) ) );

    editor.setValue( -1.5f );
    assertThat( editor.getValue(), is( nullValue() ) );

    editor.setValue( "incorrect" );
    assertThat( editor.getValue(), is( nullValue() ) );
  }

  @Test
  public void testSettingValueAsText() {
    editor.setAsText( null );
    assertThat( editor.getAsText(), is( nullValue() ) );

    editor.setAsText( "auto" );
    assertThat( editor.getAsText(), is( nullValue() ) );
    editor.setAsText( "1.5" );
    assertThat( editor.getAsText(), is( equalTo( "1.5" ) ) );
    editor.setAsText( "-1.5" );
    assertThat( editor.getAsText(), is( equalTo( "1.5" ) ) );

    editor.setAsText( "incorrect" );
    assertThat( editor.getAsText(), is( nullValue() ) );
  }

  @Test
  public void testGetTags() {
    assertThat( editor.getTags(), is( nullValue() ) );
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
