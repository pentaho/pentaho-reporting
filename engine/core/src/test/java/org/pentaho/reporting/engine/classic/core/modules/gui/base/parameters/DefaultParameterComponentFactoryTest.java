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

package org.pentaho.reporting.engine.classic.core.modules.gui.base.parameters;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import org.junit.Test;
import org.pentaho.reporting.engine.classic.core.ResourceBundleFactory;
import org.pentaho.reporting.engine.classic.core.parameters.ListParameter;
import org.pentaho.reporting.engine.classic.core.parameters.ParameterAttributeNames;
import org.pentaho.reporting.engine.classic.core.parameters.ParameterContext;
import org.pentaho.reporting.engine.classic.core.parameters.ParameterValues;
import org.pentaho.reporting.engine.classic.core.parameters.PlainParameter;
import org.pentaho.reporting.libraries.designtime.swing.KeyedComboBoxModel;

public class DefaultParameterComponentFactoryTest {

  private DefaultParameterComponentFactory factory = new DefaultParameterComponentFactory();

  @Test
  public void testCreateModel() throws Exception {
    ListParameter parameter = mock( ListParameter.class );
    ParameterContext parameterContext = mock( ParameterContext.class );
    ParameterValues paramValues = mock( ParameterValues.class );

    doReturn( paramValues ).when( parameter ).getValues( parameterContext );
    doReturn( 2 ).when( paramValues ).getRowCount();
    doReturn( "key_0" ).when( paramValues ).getKeyValue( 0 );
    doReturn( "key_1" ).when( paramValues ).getKeyValue( 1 );
    doReturn( "val_0" ).when( paramValues ).getTextValue( 0 );
    doReturn( "val_1" ).when( paramValues ).getTextValue( 1 );

    KeyedComboBoxModel<Object, Object> result =
        DefaultParameterComponentFactory.createModel( parameter, parameterContext );

    assertThat( result, is( notNullValue() ) );
    assertThat( result.getSize(), is( equalTo( 2 ) ) );
    assertThat( (String) result.getKeyAt( 0 ), is( equalTo( "key_0" ) ) );
    assertThat( (String) result.getElementAt( 0 ), is( equalTo( "val_0" ) ) );
    assertThat( (String) result.getKeyAt( 1 ), is( equalTo( "key_1" ) ) );
    assertThat( (String) result.getElementAt( 1 ), is( equalTo( "val_1" ) ) );
  }

  @Test
  public void testCreateTextComponent() {
    PlainParameter entry = mock( PlainParameter.class );
    ParameterContext parameterContext = mock( ParameterContext.class );
    ParameterUpdateContext updateContext = mock( ParameterUpdateContext.class );
    ResourceBundleFactory resourceBundleFactory = mock( ResourceBundleFactory.class );
    Locale locale = new Locale( "test_test" );

    doReturn( resourceBundleFactory ).when( parameterContext ).getResourceBundleFactory();
    doReturn( locale ).when( resourceBundleFactory ).getLocale();
    doReturn( TimeZone.getDefault() ).when( resourceBundleFactory ).getTimeZone();

    doReturn( "field" ).when( entry ).getParameterAttribute( ParameterAttributeNames.Core.NAMESPACE,
        ParameterAttributeNames.Core.TYPE, parameterContext );
    ParameterComponent comp = factory.create( entry, parameterContext, updateContext );
    assertThat( comp, is( instanceOf( TextFieldParameterComponent.class ) ) );

    doReturn( "datepicker" ).when( entry ).getParameterAttribute( ParameterAttributeNames.Core.NAMESPACE,
        ParameterAttributeNames.Core.TYPE, parameterContext );
    doReturn( Date.class ).when( entry ).getValueType();
    comp = factory.create( entry, parameterContext, updateContext );
    assertThat( comp, is( instanceOf( DatePickerParameterComponent.class ) ) );

    doReturn( "multi-line" ).when( entry ).getParameterAttribute( ParameterAttributeNames.Core.NAMESPACE,
        ParameterAttributeNames.Core.TYPE, parameterContext );
    comp = factory.create( entry, parameterContext, updateContext );
    assertThat( comp, is( instanceOf( TextAreaParameterComponent.class ) ) );
  }

  @Test
  public void testCreateListParameter() {
    ListParameter entry = mock( ListParameter.class );
    ParameterContext parameterContext = mock( ParameterContext.class );
    ParameterUpdateContext updateContext = mock( ParameterUpdateContext.class );
    ResourceBundleFactory resourceBundleFactory = mock( ResourceBundleFactory.class );
    Locale locale = new Locale( "test_test" );

    doReturn( resourceBundleFactory ).when( parameterContext ).getResourceBundleFactory();
    doReturn( locale ).when( resourceBundleFactory ).getLocale();
    doReturn( TimeZone.getDefault() ).when( resourceBundleFactory ).getTimeZone();

    ParameterComponent comp = factory.create( entry, parameterContext, updateContext );
    doReturn( null ).when( entry ).getParameterAttribute( ParameterAttributeNames.Core.NAMESPACE,
        ParameterAttributeNames.Core.TYPE, parameterContext );
    assertThat( comp, is( instanceOf( TextFieldParameterComponent.class ) ) );

    doReturn( "textbox" ).when( entry ).getParameterAttribute( ParameterAttributeNames.Core.NAMESPACE,
        ParameterAttributeNames.Core.TYPE, parameterContext );
    comp = factory.create( entry, parameterContext, updateContext );
    assertThat( comp, is( instanceOf( TextFieldParameterComponent.class ) ) );

    doReturn( "dropdown" ).when( entry ).getParameterAttribute( ParameterAttributeNames.Core.NAMESPACE,
        ParameterAttributeNames.Core.TYPE, parameterContext );
    comp = factory.create( entry, parameterContext, updateContext );
    assertThat( comp, is( instanceOf( DropDownParameterComponent.class ) ) );

    doReturn( "list" ).when( entry ).getParameterAttribute( ParameterAttributeNames.Core.NAMESPACE,
        ParameterAttributeNames.Core.TYPE, parameterContext );
    comp = factory.create( entry, parameterContext, updateContext );
    assertThat( comp, is( instanceOf( ListParameterComponent.class ) ) );

    doReturn( "checkbox" ).when( entry ).getParameterAttribute( ParameterAttributeNames.Core.NAMESPACE,
        ParameterAttributeNames.Core.TYPE, parameterContext );
    comp = factory.create( entry, parameterContext, updateContext );
    assertThat( comp, is( instanceOf( CheckBoxParameterComponent.class ) ) );

    doReturn( "radio" ).when( entry ).getParameterAttribute( ParameterAttributeNames.Core.NAMESPACE,
        ParameterAttributeNames.Core.TYPE, parameterContext );
    comp = factory.create( entry, parameterContext, updateContext );
    assertThat( comp, is( instanceOf( RadioButtonParameterComponent.class ) ) );

    doReturn( "togglebutton" ).when( entry ).getParameterAttribute( ParameterAttributeNames.Core.NAMESPACE,
        ParameterAttributeNames.Core.TYPE, parameterContext );
    comp = factory.create( entry, parameterContext, updateContext );
    assertThat( comp, is( instanceOf( ButtonParameterComponent.class ) ) );

    doReturn( "tt" ).when( entry ).getParameterAttribute( ParameterAttributeNames.Core.NAMESPACE,
        ParameterAttributeNames.Core.TYPE, parameterContext );
    comp = factory.create( entry, parameterContext, updateContext );
    assertThat( comp, is( instanceOf( TextFieldParameterComponent.class ) ) );
  }
}
