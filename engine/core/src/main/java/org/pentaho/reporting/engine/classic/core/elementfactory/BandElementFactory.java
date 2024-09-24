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

package org.pentaho.reporting.engine.classic.core.elementfactory;

import org.pentaho.reporting.engine.classic.core.Band;
import org.pentaho.reporting.engine.classic.core.Element;

/**
 * A element factory that can be used to configure bands. Unlike with the other factories, this factory does not
 * generate new bands on each call to 'createElement'.
 *
 * @author Thomas Morgner
 */
public class BandElementFactory extends TextElementFactory {
  /**
   * The band that is being configured.
   */
  private Band band;

  /**
   * Default Constructor that constructs generic bands.
   */
  public BandElementFactory() {
    this( new Band() );
  }

  /**
   * Default Constructor that configures the given band implementation.
   *
   * @param band
   *          the band that is being configured. Cannot be null.
   */
  public BandElementFactory( final Band band ) {
    if ( band == null ) {
      throw new NullPointerException();
    }
    this.band = band;
  }

  /**
   * Returns the created band or the band that has been specified for configuration.
   *
   * @return the band.
   */
  public Element createElement() {
    applyElementName( band );
    applyStyle( band.getStyle() );
    return band;
  }
}
