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

import java.awt.Color;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.JTextComponent;

import org.pentaho.reporting.engine.classic.core.util.beans.BeanException;
import org.pentaho.reporting.engine.classic.core.util.beans.ConverterRegistry;

public class TextComponentEditHandler implements Runnable, DocumentListener, ActionListener {
  private Class type;
  private String keyName;
  private JTextComponent textComponent;
  private ParameterUpdateContext updateContext;
  private Format formatter;
  private Color color;
  private boolean inProgress;
  private boolean adjustingToExternalInput;

  public TextComponentEditHandler( final Class type, final String keyName, final JTextComponent textComponent,
      final ParameterUpdateContext updateContext, final Format formatter ) {
    this.type = type;
    this.keyName = keyName;
    this.textComponent = textComponent;
    this.updateContext = updateContext;
    this.formatter = formatter;
    this.color = this.textComponent.getBackground();
    if ( color == null ) {
      color = SystemColor.text;
    }
  }

  public boolean isAdjustingToExternalInput() {
    return adjustingToExternalInput;
  }

  public void setAdjustingToExternalInput( final boolean adjustingToExternalInput ) {
    this.adjustingToExternalInput = adjustingToExternalInput;
  }

  protected Class getType() {
    return type;
  }

  /**
   * Gives notification that there was an insert into the document. The range given by the DocumentEvent bounds the
   * freshly inserted region.
   *
   * @param e
   *          the document event
   */
  public void insertUpdate( final DocumentEvent e ) {
    convertParameterValue();
  }

  /**
   * Gives notification that a portion of the document has been removed. The range is given in terms of what the view
   * last saw (that is, before updating sticky positions).
   *
   * @param e
   *          the document event
   */
  public void removeUpdate( final DocumentEvent e ) {
    convertParameterValue();
  }

  /**
   * Gives notification that an attribute or set of attributes changed.
   *
   * @param e
   *          the document event
   */
  public void changedUpdate( final DocumentEvent e ) {
    convertParameterValue();
  }

  private void convertParameterValue() {
    if ( inProgress ) {
      return;
    }
    inProgress = true;
    SwingUtilities.invokeLater( this );
  }

  public void run() {
    convert( false );
  }

  protected void convert( final boolean autoUpdate ) {
    try {
      final String text = textComponent.getText();
      textComponent.setBackground( color );
      updateContext.setParameterValue( keyName, convertValue( text ), autoUpdate );
    } catch ( BeanException e ) {
      // ignore, do not update (yet).
      textComponent.setBackground( Color.RED );
    } finally {
      inProgress = false;
    }
  }

  protected Object convertValue( final String text ) throws BeanException {
    if ( text == null ) {
      return null;
    }
    if ( formatter != null ) {
      try {
        final Object o = formatter.parseObject( text );
        // this magic converts the date or number value to the real type.
        // the formatter always returns doubles/bigdecimals or java.util.Dates
        // but we may need sql-dates, long-objects etc ..
        final String asText = ConverterRegistry.toAttributeValue( o );
        return ConverterRegistry.toPropertyValue( asText, getType() );
      } catch ( ParseException e ) {
        throw new BeanException( "Failed to format object" );
      }
    }
    if ( Object.class == type || String.class == type ) {
      return text;
    }

    return ConverterRegistry.toPropertyValue( text, type );
  }

  /**
   * Invoked when an action occurs.
   */
  public void actionPerformed( final ActionEvent e ) {
    convert( true );
  }

  public static TimeZone createTimeZone( final String selectedItem, final TimeZone defaultValue ) {
    if ( selectedItem == null ) {
      return defaultValue;
    }
    if ( "server".equals( selectedItem ) || "client".equals( selectedItem ) ) {
      return defaultValue;
    }

    final String id = String.valueOf( selectedItem );
    if ( "utc".equals( id ) ) {
      return TimeZone.getTimeZone( "UTC" );
    }
    final TimeZone timeZone = TimeZone.getTimeZone( id );
    if ( "GMT".equals( timeZone.getID() ) && "GMT".equals( id ) == false ) {
      // Handle timezones that are not understood by the current JVM.
      return defaultValue;
    }
    return timeZone;
  }

  public static Format createFormat( final String formatString, final Locale locale, final TimeZone timeZone,
      final Class parameterType ) {
    if ( formatString == null || parameterType == null ) {
      return null;
    }

    if ( Number.class.isAssignableFrom( parameterType ) ) {
      try {
        final DecimalFormat decimalFormat = new DecimalFormat( formatString, new DecimalFormatSymbols( locale ) );
        decimalFormat.setParseBigDecimal( true );
        return decimalFormat;
      } catch ( Exception e ) {
        // well, seems that was not a number format then ...
      }
    }
    if ( Date.class.isAssignableFrom( parameterType ) ) {
      try {
        final SimpleDateFormat dateFormat = new SimpleDateFormat( formatString, locale );
        dateFormat.setLenient( false );
        dateFormat.setTimeZone( timeZone );
        return dateFormat;
      } catch ( Exception e ) {
        // well, seems that was not a date format either ...
      }
    }

    return null;
  }
}
