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
import static org.mockito.Mockito.mock;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.metadata.ElementMetaData;
import org.pentaho.reporting.engine.classic.core.metadata.ElementType;
import org.pentaho.reporting.engine.classic.core.metadata.ElementTypeRegistry;

public class ElementTypePropertyEditorTest {

  private ElementTypePropertyEditor editor;
  private ElementMetaData metaData;
  private String[] correctTypes;

  @Before
  public void init() throws InstantiationException {
    ClassicEngineBoot.getInstance().start();
    editor = new ElementTypePropertyEditor();
    metaData = ElementTypeRegistry.getInstance().getElementType("vertical-line");
    
    final ElementMetaData[] datas = ElementTypeRegistry.getInstance().getAllElementTypes();
    correctTypes = new String[datas.length];
    for ( int i = 0; i < datas.length; i++ ) {
      correctTypes[i] = datas[i].getName();
    }
  }
  
  @After
  public void release() {
    editor = null;
	metaData = null;
	correctTypes = null;
  }

  @Test
  public void testSettingValue() {
    editor.setValue( null );
    assertThat( editor.getValue(), is( nullValue() ) );

    ElementType type = mock( ElementType.class );
    editor.setValue( type );
    assertThat( editor.getValue(), is( instanceOf( ElementType.class ) ) );
    assertThat( (ElementType) editor.getValue(), is( equalTo( type ) ) );

    editor.setValue( "incorrect" );
    assertThat( editor.getValue(), is( nullValue() ) );
  }

  @Test
  public void testSettingValueAsText() {
    editor.setValue( null );
    assertThat( editor.getAsText(), is( nullValue() ) );

    editor.setAsText( metaData.getName() );
    assertThat( editor.getAsText(), is( equalTo( metaData.getName() ) ) );
  }

  @Test
  public void testGetTags() {
    assertThat( editor.getTags(), is( arrayContainingInAnyOrder( correctTypes ) ) );
  }

  @Test
  public void testIsPaintable() {
    assertThat( editor.isPaintable(), is( equalTo( false ) ) );
  }

  @Test
  public void testGetJavaInitializationString() {
    assertThat( editor.getJavaInitializationString(), is( equalTo( "null" ) ) );
    ElementType type = mock( ElementType.class );
    editor.setValue( type );
    assertThat( editor.getJavaInitializationString(), is( equalTo( type.getClass().getName() + "()" ) ) );
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
