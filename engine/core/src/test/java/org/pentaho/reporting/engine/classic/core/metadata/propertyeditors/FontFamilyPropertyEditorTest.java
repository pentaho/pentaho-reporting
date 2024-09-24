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
