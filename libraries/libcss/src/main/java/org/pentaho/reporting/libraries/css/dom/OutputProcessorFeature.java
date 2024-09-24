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

package org.pentaho.reporting.libraries.css.dom;

import java.io.Serializable;


/**
 * Creation-Date: 14.12.2005, 13:47:47
 *
 * @author Thomas Morgner
 */
public abstract class OutputProcessorFeature implements Serializable {
  public static final class BooleanOutputProcessorFeature extends OutputProcessorFeature {
    public BooleanOutputProcessorFeature( final String name ) {
      super( name );
    }
  }

  public static final class NumericOutputProcessorFeature extends OutputProcessorFeature {
    public NumericOutputProcessorFeature( final String name ) {
      super( name );
    }
  }

  /**
   * Defines, whether the output target allows background images. The 'excel' export and the plain-text export are known
   * to ignore background images.
   */
  public static final BooleanOutputProcessorFeature BACKGROUND_IMAGE =
    new BooleanOutputProcessorFeature( "background-image" );

  /**
   * Defines, whether the output uses fractional metrics. Integer metrics might be faster, but they are also
   * inaccurate.
   */
  public static final BooleanOutputProcessorFeature FONT_FRACTIONAL_METRICS =
    new BooleanOutputProcessorFeature( "font-fractional-metrics" );

  /**
   * Defines, whether the output target allows the configuration of anti-aliasing of fonts.
   * <p/>
   * The Graphics2D is one of the targets that support this feature, while the PDF-export ignores aliasing requests.
   */
  public static final BooleanOutputProcessorFeature FONT_SUPPORTS_ANTI_ALIASING =
    new BooleanOutputProcessorFeature( "font-anti-aliasing" );

  /**
   * Defines, whether the output system will support paddings. If this feature indicator is set, all the paddings will
   * automatically compute to zero. The output targets will not generate paddings, even if the report-element explicitly
   * defined them.
   */
  public static final BooleanOutputProcessorFeature DISABLE_PADDING =
    new BooleanOutputProcessorFeature( "disable-padding" );

  /**
   * Defines, that the output system will try to emulate paddings. This is a technical key indicating that the generated
   * output-format is not capable to express paddings. The table-export will therefore generate artifical boundaries to
   * generate the visual effect of the defined paddings.
   */
  public static final BooleanOutputProcessorFeature EMULATE_PADDING =
    new BooleanOutputProcessorFeature( "emulate-padding" );

  /**
   * Defines the minimum size for the font smoothing. Fonts below that size will not have aliasing enabled, as this may
   * render the font unreadable.
   */
  public static final NumericOutputProcessorFeature FONT_SMOOTH_THRESHOLD =
    new NumericOutputProcessorFeature( "font-smooth-threshold" );

  /**
   * Defines the device resolution in Pixel-per-inch. This is a hint to make scaling of images more effective. LibLayout
   * still uses the default 72dpi resolution defined by Java for all computations.
   */
  public static final NumericOutputProcessorFeature DEVICE_RESOLUTION =
    new NumericOutputProcessorFeature( "device-resolution" );

  public static final NumericOutputProcessorFeature DEFAULT_FONT_SIZE =
    new NumericOutputProcessorFeature( "default-font-size" );

  private String name;
  private int hashCode;

  protected OutputProcessorFeature( final String name ) {
    if ( name == null ) {
      throw new NullPointerException();
    }
    this.name = name;
    this.hashCode = name.hashCode();
  }

  public boolean equals( final Object o ) {
    if ( this == o ) {
      return true;
    }
    if ( o == null || getClass() != o.getClass() ) {
      return false;
    }

    final OutputProcessorFeature that = (OutputProcessorFeature) o;

    return name.equals( that.name );
  }

  public int hashCode() {
    return hashCode;
  }
}
