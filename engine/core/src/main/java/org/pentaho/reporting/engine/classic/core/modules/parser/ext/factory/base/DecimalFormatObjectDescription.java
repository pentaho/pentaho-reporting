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

package org.pentaho.reporting.engine.classic.core.modules.parser.ext.factory.base;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

/**
 * An object-description for a <code>DecimalFormat</code> object.
 *
 * @author Thomas Morgner
 */
public class DecimalFormatObjectDescription extends BeanObjectDescription {

  /**
   * Creates a new object description.
   */
  public DecimalFormatObjectDescription() {
    this( DecimalFormat.class );
  }

  /**
   * Creates a new object description.
   *
   * @param className
   *          the class.
   */
  public DecimalFormatObjectDescription( final Class className ) {
    super( className, false );
    setParameterDefinition( "localizedPattern", String.class );
    setParameterDefinition( "pattern", String.class );
    setParameterDefinition( "decimalFormatSymbols", DecimalFormatSymbols.class );
    setParameterDefinition( "decimalSeparatorAlwaysShown", Boolean.TYPE );
    setParameterDefinition( "groupingSize", Integer.TYPE );
    setParameterDefinition( "groupingUsed", Boolean.TYPE );
    setParameterDefinition( "maximumFractionDigits", Integer.TYPE );
    setParameterDefinition( "maximumIntegerDigits", Integer.TYPE );
    setParameterDefinition( "minimumFractionDigits", Integer.TYPE );
    setParameterDefinition( "minimumIntegerDigits", Integer.TYPE );
    setParameterDefinition( "multiplier", Integer.TYPE );
    setParameterDefinition( "negativePrefix", String.class );
    setParameterDefinition( "negativeSuffix", String.class );
    // setParameterDefinition("parseBigDecimal", Boolean.TYPE);
    setParameterDefinition( "parseIntegerOnly", Boolean.TYPE );
    setParameterDefinition( "positivePrefix", String.class );
    setParameterDefinition( "positiveSuffix", String.class );
    ignoreParameter( "localizedPattern" );
    ignoreParameter( "pattern" );
  }

  /**
   * Creates a new object description.
   *
   * @param className
   *          the class.
   * @param init
   *          initialise
   * @deprecated should no longer be used...
   */
  public DecimalFormatObjectDescription( final Class className, final boolean init ) {
    this( className );
  }

  /**
   * Sets the parameters of this description object to match the supplied object.
   *
   * @param o
   *          the object (should be an instance of <code>DecimalFormat</code>).
   * @throws ObjectFactoryException
   *           if there is a problem while reading the properties of the given object.
   */
  public void setParameterFromObject( final Object o ) throws ObjectFactoryException {
    super.setParameterFromObject( o );
    final DecimalFormat format = (DecimalFormat) o;
    // setParameter("localizedPattern", format.toLocalizedPattern());
    setParameter( "pattern", format.toPattern() );
  }

  /**
   * Creates an object (<code>DecimalFormat</code>) based on this description.
   *
   * @return The object.
   */
  public Object createObject() {
    final DecimalFormat format = (DecimalFormat) super.createObject();
    if ( getParameter( "pattern" ) != null ) {
      format.applyPattern( (String) getParameter( "pattern" ) );
    }
    // if (getParameter("localizedPattern") != null) {
    // format.applyLocalizedPattern((String) getParameter("localizedPattern"));
    // }
    return format;
  }
}
