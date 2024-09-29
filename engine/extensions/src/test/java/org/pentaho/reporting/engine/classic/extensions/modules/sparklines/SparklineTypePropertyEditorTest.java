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


package org.pentaho.reporting.engine.classic.extensions.modules.sparklines;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.collection.IsArrayContainingInAnyOrder.arrayContainingInAnyOrder;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class SparklineTypePropertyEditorTest {

  private static final String[] VALID_TYPES = new String[] { "line", "bar", "pie" };

  private SparklineTypePropertyEditor editor = new SparklineTypePropertyEditor();

  @Test
  public void testSetValue() {
    editor.setValue( "incorrect_value" );
    assertThat( editor.getValue(), is( nullValue() ) );

    for ( String type : VALID_TYPES ) {
      editor.setValue( type );
      assertThat( (String) editor.getValue(), is( equalTo( type ) ) );
      editor.setAsText( type );
      assertThat( editor.getAsText(), is( equalTo( type ) ) );
    }
  }

  @Test
  public void testIsPaintable() {
    assertThat( editor.isPaintable(), is( equalTo( false ) ) );
  }

  @Test
  public void testGetJavaInitializationString() {
    assertThat( editor.getJavaInitializationString(), is( nullValue() ) );
  }

  @Test
  public void testGetTags() {
    String[] tags = editor.getTags();
    assertThat( tags, is( arrayContainingInAnyOrder( VALID_TYPES ) ) );
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
