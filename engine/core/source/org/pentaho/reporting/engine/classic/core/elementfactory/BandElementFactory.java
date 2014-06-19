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
* Copyright (c) 2001 - 2013 Object Refinery Ltd, Pentaho Corporation and Contributors..  All rights reserved.
*/

package org.pentaho.reporting.engine.classic.core.elementfactory;

import org.pentaho.reporting.engine.classic.core.Band;
import org.pentaho.reporting.engine.classic.core.Element;

/**
 * A element factory that can be used to configure bands. Unlike with the other factories, this factory does not
 * generate new bands on each call to 'createElement'.
 *
 * @author Thomas Morgner
 */
public class BandElementFactory extends TextElementFactory
{
  /**
   * The band that is being configured.
   */
  private Band band;

  /**
   * Default Constructor that constructs generic bands.
   */
  public BandElementFactory()
  {
    this(new Band());
  }

  /**
   * Default Constructor that configures the given band implementation.
   *
   * @param band the band that is being configured. Cannot be null.
   */
  public BandElementFactory(final Band band)
  {
    if (band == null)
    {
      throw new NullPointerException();
    }
    this.band = band;
  }

  /**
   * Returns the created band or the band that has been specified for configuration.
   *
   * @return the band.
   */
  public Element createElement()
  {
    applyElementName(band);
    applyStyle(band.getStyle());
    return band;
  }
}
