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

package org.pentaho.reporting.engine.classic.extensions.modules.sparklines;

import java.awt.Color;

import org.pentaho.reporting.engine.classic.core.style.StyleKey;

/**
 * This class collects all the custom <code>StyleKey</code>s usable for Sparkline graphs.<b/> The main color of graphs
 * is carried by the common "paint" StyleKey.
 *
 * @author Thomas Morgner
 */
public class SparklineStyleKeys {
  /**
   * This StyleKey represents the color of bars above the average of datapoints (for bar graphs) or the color for the
   * highest slice (for pie graphs)
   */
  public static final StyleKey HIGH_COLOR = StyleKey.getStyleKey( "-x-pentaho-sparklines-highcolor", Color.class );
  /**
   * This StyleKey represents the color for the medium slice (for pie graphs)
   */
  public static final StyleKey MEDIUM_COLOR = StyleKey.getStyleKey( "-x-pentaho-sparklines-mediumcolor", Color.class );
  /**
   * This StyleKey represents the color for the lower slice (for pie graphs)
   */
  public static final StyleKey LOW_COLOR = StyleKey.getStyleKey( "-x-pentaho-sparklines-lowcolor", Color.class );
  /**
   * This StyleKey represents the color of the last bar for Sparkline bar graphs. It overrides the current bar color for
   * the last bar.
   */
  public static final StyleKey LAST_COLOR = StyleKey.getStyleKey( "-x-pentaho-sparklines-lastcolor", Color.class );

  private SparklineStyleKeys() {
  }
}
