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
 * Copyright (c) 2001 - 2013 Object Refinery Ltd, Hitachi Vantara and Contributors..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.modules.parser.ext.factory.base;

import java.text.DateFormatSymbols;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * An object-description for a <code>SimpleDateFormat</code> object.
 *
 * @author Thomas Morgner
 */
public class SimpleDateFormatObjectDescription extends BeanObjectDescription {

  /**
   * Creates a new object description.
   */
  public SimpleDateFormatObjectDescription() {
    this( SimpleDateFormat.class );
  }

  /**
   * Creates a new object description.
   *
   * @param className
   *          the class.
   */
  public SimpleDateFormatObjectDescription( final Class className ) {
    this( className, true );
  }

  /**
   * Creates a new object description.
   *
   * @param className
   *          the class.
   * @param init
   *          initialise?
   */
  public SimpleDateFormatObjectDescription( final Class className, final boolean init ) {
    super( className, false );
    setParameterDefinition( "2DigitYearStart", Date.class );
    setParameterDefinition( "calendar", Calendar.class );
    setParameterDefinition( "dateFormatSymbols", DateFormatSymbols.class );
    setParameterDefinition( "lenient", Boolean.TYPE );
    setParameterDefinition( "numberFormat", NumberFormat.class );
    setParameterDefinition( "timeZone", TimeZone.class );
    setParameterDefinition( "localizedPattern", String.class );
    setParameterDefinition( "pattern", String.class );
    ignoreParameter( "localizedPattern" );
    ignoreParameter( "pattern" );
  }

  /**
   * Sets the parameters of this description object to match the supplied object.
   *
   * @param o
   *          the object.
   * @throws ObjectFactoryException
   *           if there is a problem while reading the properties of the given object.
   */
  public void setParameterFromObject( final Object o ) throws ObjectFactoryException {
    super.setParameterFromObject( o );
    final SimpleDateFormat format = (SimpleDateFormat) o;
    setParameter( "pattern", format.toPattern() );
  }

  /**
   * Creates an object based on this description.
   *
   * @return The object.
   */
  public Object createObject() {
    final SimpleDateFormat format = (SimpleDateFormat) super.createObject();
    if ( getParameter( "pattern" ) != null ) {
      format.applyPattern( (String) getParameter( "pattern" ) );
    }
    return format;
  }

}
