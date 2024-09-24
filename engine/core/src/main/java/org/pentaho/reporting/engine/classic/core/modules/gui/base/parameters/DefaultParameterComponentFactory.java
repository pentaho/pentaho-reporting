/*!
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
 * Copyright (c) 2002-2017 Hitachi Vantara..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.modules.gui.base.parameters;

import java.util.Date;

import org.pentaho.reporting.engine.classic.core.ReportDataFactoryException;
import org.pentaho.reporting.engine.classic.core.parameters.ListParameter;
import org.pentaho.reporting.engine.classic.core.parameters.ParameterAttributeNames;
import org.pentaho.reporting.engine.classic.core.parameters.ParameterContext;
import org.pentaho.reporting.engine.classic.core.parameters.ParameterDefinitionEntry;
import org.pentaho.reporting.engine.classic.core.parameters.ParameterValues;
import org.pentaho.reporting.libraries.designtime.swing.KeyedComboBoxModel;

public class DefaultParameterComponentFactory implements ParameterComponentFactory {

  public DefaultParameterComponentFactory() {
  }

  public ParameterComponent create( final ParameterDefinitionEntry entry, final ParameterContext parameterContext,
      final ParameterUpdateContext updateContext ) {
    if ( entry instanceof ListParameter ) {
      return createListParameter( (ListParameter) entry, parameterContext, updateContext );
    }
    return createTextComponent( entry, parameterContext, updateContext );
  }

  public ParameterComponent createListParameter( final ListParameter listParameter,
      final ParameterContext parameterContext, final ParameterUpdateContext updateContext ) {

    final String type =
        listParameter.getParameterAttribute( ParameterAttributeNames.Core.NAMESPACE, ParameterAttributeNames.Core.TYPE,
            parameterContext );

    if ( type == null || "textbox".equals( type ) ) { // NON-NLS
      return createTextComponent( listParameter, parameterContext, updateContext );
    }
    if ( "dropdown".equals( type ) ) { // NON-NLS
      return new DropDownParameterComponent( listParameter, updateContext, parameterContext );
    }

    if ( "list".equals( type ) ) { // NON-NLS
      return new ListParameterComponent( listParameter, updateContext, parameterContext );
    }
    if ( "checkbox".equals( type ) ) { // NON-NLS
      return new CheckBoxParameterComponent( listParameter, updateContext, parameterContext );
    }
    if ( "radio".equals( type ) ) { // NON-NLS
      return new RadioButtonParameterComponent( listParameter, updateContext, parameterContext );
    }
    if ( "togglebutton".equals( type ) ) { // NON-NLS
      return new ButtonParameterComponent( listParameter, updateContext, parameterContext );
    }

    return createTextComponent( listParameter, parameterContext, updateContext );
  }

  private ParameterComponent createTextComponent( final ParameterDefinitionEntry entry,
      final ParameterContext parameterContext, final ParameterUpdateContext updateContext ) {
    final String type =
        entry.getParameterAttribute( ParameterAttributeNames.Core.NAMESPACE, ParameterAttributeNames.Core.TYPE,
            parameterContext );

    if ( "datepicker".equals( type ) && Date.class.isAssignableFrom( entry.getValueType() ) ) { // NON-NLS
      return new DatePickerParameterComponent( entry, parameterContext, updateContext );
    }
    if ( "multi-line".equals( type ) ) { // NON-NLS
      return new TextAreaParameterComponent( entry, parameterContext, updateContext );
    } else {
      return new TextFieldParameterComponent( entry, parameterContext, updateContext );
    }
  }

  public static KeyedComboBoxModel<Object, Object> createModel( final ListParameter parameter,
      final ParameterContext parameterContext ) throws ReportDataFactoryException {
    final ParameterValues paramValues = parameter.getValues( parameterContext );
    final int count = paramValues.getRowCount();
    final Object[] keys = new Object[count];
    final Object[] values = new Object[count];
    for ( int i = 0; i < count; i++ ) {
      final Object key = paramValues.getKeyValue( i );
      keys[i] = key;
      values[i] = paramValues.getTextValue( i );
    }

    final KeyedComboBoxModel<Object, Object> model = new KeyedComboBoxModel<Object, Object>();
    model.setData( keys, values );
    return model;
  }
}
