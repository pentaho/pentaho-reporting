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


package org.pentaho.reporting.engine.classic.core.modules.gui.base.parameters;

import org.pentaho.reporting.engine.classic.core.ReportDataFactoryException;
import org.pentaho.reporting.engine.classic.core.parameters.*;
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
    final String displayTimeSelector =
            entry.getParameterAttribute( ParameterAttributeNames.Core.NAMESPACE,
                    ParameterAttributeNames.Core.DISPLAY_TIME_SELECTOR,
                    parameterContext );

    if ( "datepicker".equals( type ) && ParameterUtils.isTimeSelectorApplicable(entry.getValueType()) ) { // NON-NLS
      return new DatePickerParameterComponent( entry, parameterContext, updateContext,
              Boolean.parseBoolean(displayTimeSelector) );
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
