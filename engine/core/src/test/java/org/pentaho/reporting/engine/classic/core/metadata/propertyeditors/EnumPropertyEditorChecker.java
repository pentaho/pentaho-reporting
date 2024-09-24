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
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.Matchers.arrayContainingInAnyOrder;
import static org.junit.Assert.assertThat;

@SuppressWarnings( "rawtypes" )
public class EnumPropertyEditorChecker {

  private EnumPropertyEditor editor;
  private Class<? extends Enum> baseClass;
  private Enum correctValue;

  public EnumPropertyEditorChecker( EnumPropertyEditor editor, Class<? extends Enum> baseClass ) {
    this.editor = editor;
    this.baseClass = baseClass;
    this.correctValue = baseClass.getEnumConstants()[0];
  }

  protected void checkSettingValue() {
    assertThat( editor.getValue(), is( nullValue() ) );

    editor.setValue( correctValue );
    assertThat( editor.getValue(), is( instanceOf( baseClass ) ) );
    assertThat( baseClass.cast( editor.getValue() ), is( equalTo( correctValue ) ) );

    editor.setValue( "incorrect" );
    assertThat( editor.getValue(), is( nullValue() ) );
  }

  protected void checkSettingValueAsText() {
    editor.setAsText( null );
    assertThat( editor.getAsText(), is( nullValue() ) );

    editor.setAsText( correctValue.name() );
    assertThat( editor.getValue(), is( instanceOf( baseClass ) ) );
    assertThat( baseClass.cast( editor.getValue() ), is( equalTo( correctValue ) ) );
    assertThat( editor.getAsText(), is( equalTo( correctValue.name() ) ) );

    editor.setValue( "incorrect" );
    assertThat( editor.getValue(), is( nullValue() ) );
    assertThat( editor.getAsText(), is( nullValue() ) );
  }

  protected void checkGetTags() {
    String[] expected = new String[baseClass.getEnumConstants().length + 1];
    for ( int i = 0; i < baseClass.getEnumConstants().length; i++ ) {
      expected[i] = baseClass.getEnumConstants()[i].name();
    }
    expected[baseClass.getEnumConstants().length] = null;
    assertThat( editor.getTags(), is( arrayContainingInAnyOrder( expected ) ) );
  }

  protected void checkIsPaintable() {
    assertThat( editor.isPaintable(), is( equalTo( false ) ) );
  }

  protected void checkGetJavaInitializationString() {
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
