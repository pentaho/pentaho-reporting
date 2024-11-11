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
