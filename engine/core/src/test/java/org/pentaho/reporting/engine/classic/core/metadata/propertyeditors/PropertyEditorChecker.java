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
 * Copyright (c) 2000 - 2017 Hitachi Vantara, Simba Management Limited and Contributors...  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.metadata.propertyeditors;

import java.beans.PropertyEditor;

import static junit.framework.Assert.fail;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.Matchers.arrayContainingInAnyOrder;
import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertThat;

public class PropertyEditorChecker {

  private PropertyEditor editor;
  private Object[] correctValues;

  public PropertyEditorChecker(PropertyEditor editor, Object... correctValues ) {
    this.editor = editor;
    this.correctValues = correctValues;
  }

  protected void checkSettingValue() {
    editor.setValue( null );
    assertThat( editor.getValue(), is( nullValue() ) );

    for ( Object value : correctValues ) {
      editor.setValue( value );
      assertThat( editor.getValue(), is( equalTo( value ) ) );
    }

    editor.setValue( "incorrect" );
    assertThat( editor.getValue(), is( nullValue() ) );
  }

  protected void checkSettingValueAsText() {
    editor.setValue( null );
    assertThat( editor.getAsText(), is( nullValue() ) );

    for ( Object value : correctValues ) {
      editor.setAsText( value.toString() );
      assertThat( editor.getAsText(), is( equalTo( value.toString() ) ) );
    }

    try {
      editor.setAsText( "incorrect" );
      fail( "Should be thrown IllegalArgumentException." );
    } catch ( IllegalArgumentException e ) {
      // expected
    }
  }

  protected void checkSettingValueAsTextWithoutException() {
    editor.setValue( null );
    assertThat( editor.getAsText(), is( nullValue() ) );

    for ( Object value : correctValues ) {
      editor.setAsText( value.toString() );
      assertThat( editor.getAsText(), is( equalTo( value.toString() ) ) );
    }

    editor.setAsText( "incorrect" );
    assertThat( editor.getAsText(), is( nullValue() ) );
  }

  protected void checkGetTags() {
    String[] expected = new String[correctValues.length];
    for ( int i = 0; i < correctValues.length; i++ ) {
      expected[i] = correctValues[i].toString();
    }
    assertThat( editor.getTags(), is( arrayContainingInAnyOrder( expected ) ) );
  }

  protected void checkGetTagsWithNull() {
    String[] expected = new String[correctValues.length + 1];
    for ( int i = 0; i < correctValues.length; i++ ) {
      expected[i] = correctValues[i].toString();
    }
    expected[correctValues.length] = null;
    assertThat( editor.getTags(), is( arrayContainingInAnyOrder( expected ) ) );
  }

  protected void checkIsPaintable() {
    assertThat( editor.isPaintable(), is( equalTo( false ) ) );
  }

  protected void checkGetJavaInitializationString() {
    editor.setValue( null );
    assertThat( editor.getJavaInitializationString(), is( equalTo( "null" ) ) );
    for ( Object value : correctValues ) {
      editor.setValue( value );
      assertThat( editor.getJavaInitializationString(), is( containsString( value.getClass().getName() ) ) );
    }
  }

  protected void checkGetJavaInitializationStringWithNull() {
    assertThat( editor.getJavaInitializationString(), is( nullValue() ) );
  }

  protected void checkGetCustomEditor() {
    assertThat( editor.getCustomEditor(), is( nullValue() ) );
  }

  protected void checkSupportsCustomEditor() {
    assertThat( editor.supportsCustomEditor(), is( equalTo( false ) ) );
  }

  public void checkAll() {
    checkSettingValue();
    checkSettingValueAsText();
    checkGetTags();
    checkIsPaintable();
    checkGetCustomEditor();
    checkGetJavaInitializationString();
    checkSupportsCustomEditor();
  }
}
