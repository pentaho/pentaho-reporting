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

public class CompatibilityLevelPropertyEditorTest {

  private CompatibilityLevelPropertyEditor editor;

  @Before
  public void setUp() {
    editor = new CompatibilityLevelPropertyEditor();
  }

  @Test
  public void testSettingValue() {
    editor.setValue( null );
    assertThat( editor.getValue(), is( nullValue() ) );

    editor.setValue( 15 );
    assertThat( editor.getValue(), is( instanceOf( Integer.class ) ) );
    assertThat( (Integer) editor.getValue(), is( equalTo( 15 ) ) );

    editor.setValue( "incorrect" );
    assertThat( (Integer) editor.getValue(), is( equalTo( 15 ) ) );
  }

  @Test
  public void testSettingValueAsText() {
    editor.setAsText( null );
    assertThat( editor.getAsText(), is( nullValue() ) );

    editor.setAsText( "3.8.0" );
    assertThat( editor.getAsText(), is( equalTo( "3.8.0" ) ) );

    editor.setAsText( "-1" );
    assertThat( editor.getAsText(), is( nullValue() ) );

    editor.setAsText( "1.1.1" );
    assertThat( editor.getAsText(), is( equalTo( "1.1.1" ) ) );
  }

  @Test
  public void testGetTags() {
    editor.setValue( null );
    assertThat( editor.getTags(), is( equalTo( new String[] { null, "3.8.0", "3.8.3", "3.9.0", "3.9.1", "4.0.0" } ) ) );

    editor.setAsText( "1.1.1" );
    assertThat( editor.getTags(), is( equalTo( new String[] { null, "1.1.1", "3.8.0", "3.8.3", "3.9.0", "3.9.1",
      "4.0.0" } ) ) );
  }

  @Test
  public void testIsPaintable() {
    assertThat( editor.isPaintable(), is( equalTo( false ) ) );
  }

  @Test
  public void testGetJavaInitializationStringWithNull() {
    assertThat( editor.getJavaInitializationString(), is( equalTo( "null" ) ) );
    editor.setValue( 10 );
    assertThat( editor.getJavaInitializationString(), is( equalTo( "10" ) ) );
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
