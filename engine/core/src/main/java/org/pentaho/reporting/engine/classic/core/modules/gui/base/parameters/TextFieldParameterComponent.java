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

package org.pentaho.reporting.engine.classic.core.modules.gui.base.parameters;

import java.text.Format;
import java.util.Locale;
import java.util.TimeZone;

import javax.swing.JComponent;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.pentaho.reporting.engine.classic.core.modules.gui.base.ParameterReportControllerPane;
import org.pentaho.reporting.engine.classic.core.parameters.ParameterAttributeNames;
import org.pentaho.reporting.engine.classic.core.parameters.ParameterContext;
import org.pentaho.reporting.engine.classic.core.parameters.ParameterDefinitionEntry;
import org.pentaho.reporting.engine.classic.core.util.beans.ConverterRegistry;

public class TextFieldParameterComponent extends JTextField implements ParameterComponent {
  private class TextUpdateHandler implements ChangeListener {
    private TextUpdateHandler() {
    }

    public void stateChanged( final ChangeEvent e ) {
      initialize();
    }
  }

  private ParameterUpdateContext updateContext;
  private String parameterName;
  private TextComponentEditHandler handler;
  private Format format;

  public TextFieldParameterComponent( final ParameterDefinitionEntry entry, final ParameterContext parameterContext,
      final ParameterUpdateContext updateContext ) {
    this.updateContext = updateContext;
    this.parameterName = entry.getName();

    final String formatString = entry.getTranslatedParameterAttribute( ParameterAttributeNames.Core.NAMESPACE,
      ParameterAttributeNames.Core.DATA_FORMAT, parameterContext );
    final String timeZoneSpec =
        entry.getParameterAttribute( ParameterAttributeNames.Core.NAMESPACE, ParameterAttributeNames.Core.TIMEZONE,
            parameterContext );
    final Locale locale = parameterContext.getResourceBundleFactory().getLocale();
    final TimeZone timeZone =
        TextComponentEditHandler.createTimeZone( timeZoneSpec, parameterContext.getResourceBundleFactory()
            .getTimeZone() );

    format = TextComponentEditHandler.createFormat( formatString, locale, timeZone, entry.getValueType() );
    handler = new TextComponentEditHandler( entry.getValueType(), entry.getName(), this, updateContext, format );

    setColumns( 60 );
    getDocument().addDocumentListener( handler );
    addActionListener( handler );

    updateContext.addChangeListener( new TextUpdateHandler() );
  }

  public JComponent getUIComponent() {
    return this;
  }

  public void initialize() {
    handler.setAdjustingToExternalInput( true );
    try {
      final Object value = updateContext.getParameterValue( parameterName );
      if ( value != null ) {
        try {
          if ( format != null ) {
            setText( format.format( value ) );
          } else {
            setText( ConverterRegistry.toAttributeValue( value ) );
          }
        } catch ( Exception e ) {
          // ignore illegal values, set them as plain text.
          setText( value.toString() );
          setBackground( ParameterReportControllerPane.ERROR_COLOR );
        }
      } else {
        setText( null );
      }
    } finally {
      handler.setAdjustingToExternalInput( false );
    }
  }
}
