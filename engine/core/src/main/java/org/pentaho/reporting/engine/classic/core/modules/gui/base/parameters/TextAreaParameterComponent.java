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
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.pentaho.reporting.engine.classic.core.modules.gui.base.ParameterReportControllerPane;
import org.pentaho.reporting.engine.classic.core.parameters.ParameterAttributeNames;
import org.pentaho.reporting.engine.classic.core.parameters.ParameterContext;
import org.pentaho.reporting.engine.classic.core.parameters.ParameterDefinitionEntry;
import org.pentaho.reporting.engine.classic.core.util.beans.ConverterRegistry;

public class TextAreaParameterComponent extends JScrollPane implements ParameterComponent {
  private class TextUpdateHandler implements ChangeListener {
    private TextUpdateHandler() {
    }

    public void stateChanged( final ChangeEvent e ) {
      initialize();
    }
  }

  private ParameterUpdateContext updateContext;
  private String parameterName;
  private JTextArea textArea;
  private TextComponentEditHandler handler;
  private Format format;

  public TextAreaParameterComponent( final ParameterDefinitionEntry entry, final ParameterContext parameterContext,
      final ParameterUpdateContext updateContext ) {
    this.updateContext = updateContext;

    final String formatString = entry.getTranslatedParameterAttribute( ParameterAttributeNames.Core.NAMESPACE,
      ParameterAttributeNames.Core.DATA_FORMAT, parameterContext );

    final String timeZoneSpec =
        entry.getParameterAttribute( ParameterAttributeNames.Core.NAMESPACE, ParameterAttributeNames.Core.TIMEZONE,
            parameterContext );
    final Locale locale = parameterContext.getResourceBundleFactory().getLocale();
    final TimeZone timeZone =
        TextComponentEditHandler.createTimeZone( timeZoneSpec, parameterContext.getResourceBundleFactory()
            .getTimeZone() );

    textArea = new JTextArea();

    format = TextComponentEditHandler.createFormat( formatString, locale, timeZone, entry.getValueType() );
    handler = new TextComponentEditHandler( entry.getValueType(), entry.getName(), textArea, updateContext, format );

    textArea.getDocument().addDocumentListener( handler );
    textArea.setColumns( 60 );
    textArea.setRows( 10 );

    setViewportView( textArea );

    parameterName = entry.getName();
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
            textArea.setText( format.format( value ) );
          } else {
            textArea.setText( ConverterRegistry.toAttributeValue( value ) );
          }
        } catch ( Exception e ) {
          // ignore illegal values, set them as plain text.
          textArea.setText( value.toString() );
          setBackground( ParameterReportControllerPane.ERROR_COLOR );
        }
      } else {
        textArea.setText( null );
      }
    } finally {
      handler.setAdjustingToExternalInput( false );
    }
  }
}
