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

package org.pentaho.reporting.libraries.css.dom;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.libraries.base.config.Configuration;
import org.pentaho.reporting.libraries.base.config.ExtendedConfiguration;
import org.pentaho.reporting.libraries.base.config.ExtendedConfigurationWrapper;
import org.pentaho.reporting.libraries.css.StyleSheetUtility;
import org.pentaho.reporting.libraries.css.keys.font.FontFamilyValues;
import org.pentaho.reporting.libraries.css.keys.font.FontSmooth;
import org.pentaho.reporting.libraries.css.keys.font.FontStyle;
import org.pentaho.reporting.libraries.css.keys.font.FontStyleKeys;
import org.pentaho.reporting.libraries.css.keys.page.PageSize;
import org.pentaho.reporting.libraries.css.values.CSSNumericType;
import org.pentaho.reporting.libraries.css.values.CSSNumericValue;
import org.pentaho.reporting.libraries.css.values.CSSStringType;
import org.pentaho.reporting.libraries.css.values.CSSStringValue;
import org.pentaho.reporting.libraries.css.values.CSSValue;
import org.pentaho.reporting.libraries.fonts.awt.AWTFontRegistry;
import org.pentaho.reporting.libraries.fonts.registry.DefaultFontStorage;
import org.pentaho.reporting.libraries.fonts.registry.FontContext;
import org.pentaho.reporting.libraries.fonts.registry.FontFamily;
import org.pentaho.reporting.libraries.fonts.registry.FontMetrics;
import org.pentaho.reporting.libraries.fonts.registry.FontRecord;
import org.pentaho.reporting.libraries.fonts.registry.FontRegistry;
import org.pentaho.reporting.libraries.fonts.registry.FontStorage;

import java.util.HashMap;
import java.util.HashSet;

public abstract class AbstractOutputMetaData implements LayoutOutputMetaData {
  private static final Log logger = LogFactory.getLog( AbstractOutputMetaData.class );


  private static class FontMetricsKey {
    private transient Integer hashCode;
    private String fontFamily;
    private double fontSize;
    private boolean antiAliased;
    private boolean embedded;
    private String encoding;
    private boolean italics;
    private boolean bold;

    protected FontMetricsKey() {
    }

    protected FontMetricsKey( final FontMetricsKey derived ) {
      this.fontFamily = derived.fontFamily;
      this.fontSize = derived.fontSize;
      this.antiAliased = derived.antiAliased;
      this.embedded = derived.embedded;
      this.encoding = derived.encoding;
      this.italics = derived.italics;
      this.bold = derived.bold;
    }

    public String getFontFamily() {
      return fontFamily;
    }

    public void setFontFamily( final String fontFamily ) {
      this.fontFamily = fontFamily;
      this.hashCode = null;
    }

    public double getFontSize() {
      return fontSize;
    }

    public void setFontSize( final double fontSize ) {
      this.fontSize = fontSize;
      this.hashCode = null;
    }

    public boolean isAntiAliased() {
      return antiAliased;
    }

    public void setAntiAliased( final boolean antiAliased ) {
      this.antiAliased = antiAliased;
      this.hashCode = null;
    }

    public boolean isEmbedded() {
      return embedded;
    }

    public void setEmbedded( final boolean embedded ) {
      this.embedded = embedded;
      this.hashCode = null;
    }

    public String getEncoding() {
      return encoding;
    }

    public void setEncoding( final String encoding ) {
      this.encoding = encoding;
      this.hashCode = null;
    }

    public boolean isItalics() {
      return italics;
    }

    public void setItalics( final boolean italics ) {
      this.italics = italics;
      this.hashCode = null;
    }

    public boolean isBold() {
      return bold;
    }

    public void setBold( final boolean bold ) {
      this.bold = bold;
      this.hashCode = null;
    }

    public boolean equals( final Object o ) {
      if ( this == o ) {
        return true;
      }
      if ( o == null || getClass() != o.getClass() ) {
        return false;
      }

      final FontMetricsKey that = (FontMetricsKey) o;

      if ( hashCode() != that.hashCode() ) {
        return false;
      }
      if ( antiAliased != that.antiAliased ) {
        return false;
      }
      if ( embedded != that.embedded ) {
        return false;
      }
      if ( bold != that.bold ) {
        return false;
      }
      if ( italics != that.italics ) {
        return false;
      }
      if ( that.fontSize != fontSize ) {
        return false;
      }
      if ( encoding != null ? !encoding.equals( that.encoding ) : that.encoding != null ) {
        return false;
      }
      if ( !fontFamily.equals( that.fontFamily ) ) {
        return false;
      }

      return true;
    }

    public int hashCode() {
      final Integer hashCode = this.hashCode;
      if ( hashCode == null ) {
        int result = fontFamily.hashCode();
        final long temp = fontSize != +0.0d ? Double.doubleToLongBits( fontSize ) : 0L;
        result = 29 * result + (int) ( temp ^ ( temp >>> 32 ) );
        result = 29 * result + ( antiAliased ? 1 : 0 );
        result = 29 * result + ( embedded ? 1 : 0 );
        result = 29 * result + ( italics ? 1 : 0 );
        result = 29 * result + ( bold ? 1 : 0 );
        result = 29 * result + ( encoding != null ? encoding.hashCode() : 0 );
        this.hashCode = new Integer( result );
        return result;
      }
      return hashCode.intValue();
    }
  }

  protected static class ReusableFontContext implements FontContext {
    private boolean antiAliased;
    private double fontSize;
    private boolean embedded;
    private String encoding;


    protected ReusableFontContext() {
    }

    public boolean isEmbedded() {
      return embedded;
    }

    public void setEmbedded( final boolean embedded ) {
      this.embedded = embedded;
    }

    public String getEncoding() {
      return encoding;
    }

    public void setEncoding( final String encoding ) {
      this.encoding = encoding;
    }

    public void setAntiAliased( final boolean antiAliased ) {
      this.antiAliased = antiAliased;
    }

    public void setFontSize( final double fontSize ) {
      this.fontSize = fontSize;
    }

    /**
     * This is controlled by the output target and the stylesheet. If the output target does not support aliasing, it
     * makes no sense to enable it and all such requests are ignored.
     *
     * @return
     */
    public boolean isAntiAliased() {
      return antiAliased;
    }

    /**
     * This is defined by the output target. This is not controlled by the stylesheet.
     *
     * @return
     */
    public boolean isFractionalMetrics() {
      return true;
    }

    /**
     * The requested font size. A font may have a fractional font size (ie. 8.5 point). The font size may be influenced
     * by the output target.
     *
     * @return the font size.
     */
    public double getFontSize() {
      return fontSize;
    }
  }

  private FontStorage fontStorage;
  private FontRegistry fontRegistry;
  private HashMap numericFeatures;
  private HashMap fontFamilyMapping;
  private HashSet booleanFeatures;
  private Configuration configuration;
  private ReusableFontContext reusableFontContext;
  private HashMap fontMetricsCache;
  private FontMetricsKey lookupKey;

  protected AbstractOutputMetaData( final Configuration configuration ) {
    this( configuration, new DefaultFontStorage( new AWTFontRegistry() ) );
  }


  protected AbstractOutputMetaData( final Configuration configuration,
                                    final FontStorage fontStorage ) {
    if ( configuration == null ) {
      throw new NullPointerException();
    }
    if ( fontStorage == null ) {
      throw new NullPointerException();
    }

    this.configuration = configuration;
    this.fontRegistry = fontStorage.getFontRegistry();
    this.fontStorage = fontStorage;
    this.booleanFeatures = new HashSet();
    this.numericFeatures = new HashMap();
    this.reusableFontContext = new ReusableFontContext();
    this.fontMetricsCache = new HashMap();
    this.lookupKey = new FontMetricsKey();

    final ExtendedConfiguration extendedConfig = new ExtendedConfigurationWrapper( configuration );

    final double defaultFontSize =
      extendedConfig.getIntProperty( "org.pentaho.reporting.libraries.css.defaults.FontSize", 12 );

    fontFamilyMapping = new HashMap();

    setNumericFeatureValue( OutputProcessorFeature.DEFAULT_FONT_SIZE, defaultFontSize );

    final double fontSmoothThreshold =
      extendedConfig.getIntProperty( "org.pentaho.reporting.libraries.css.defaults.FontSmoothThreshold", 8 );
    setNumericFeatureValue( OutputProcessorFeature.FONT_SMOOTH_THRESHOLD, fontSmoothThreshold );

    final double deviceResolution =
      extendedConfig.getIntProperty( "org.pentaho.reporting.libraries.css.defaults.DeviceResolution", 72 );
    if ( deviceResolution > 0 ) {
      setNumericFeatureValue( OutputProcessorFeature.DEVICE_RESOLUTION, deviceResolution );
    } else {
      setNumericFeatureValue( OutputProcessorFeature.DEVICE_RESOLUTION, 72 );
    }

    setFamilyMapping( FontFamilyValues.SANS_SERIF, new CSSStringValue( CSSStringType.STRING, "SansSerif" ) );
    setFamilyMapping( FontFamilyValues.SERIF, new CSSStringValue( CSSStringType.STRING, "Serif" ) );
    setFamilyMapping( FontFamilyValues.NONE, FontFamilyValues.NONE );
    setFamilyMapping( FontFamilyValues.MONOSPACE, new CSSStringValue( CSSStringType.STRING, "Monospaced" ) );
    setFamilyMapping( FontFamilyValues.FANTASY, new CSSStringValue( CSSStringType.STRING, "Serif" ) );
    setFamilyMapping( FontFamilyValues.CURSIVE, new CSSStringValue( CSSStringType.STRING, "SansSerif" ) );
    setFamilyMapping( null, new CSSStringValue( CSSStringType.STRING, "SansSerif" ) );
  }

  public Configuration getConfiguration() {
    return configuration;
  }

  protected void setFamilyMapping( final CSSValue family, final CSSValue name ) {
    if ( name == null ) {
      throw new NullPointerException();
    }
    fontFamilyMapping.put( family, name );
  }

  protected void addFeature( final OutputProcessorFeature.BooleanOutputProcessorFeature feature ) {
    if ( feature == null ) {
      throw new NullPointerException();
    }
    this.booleanFeatures.add( feature );
  }

  protected void removeFeature( final OutputProcessorFeature.BooleanOutputProcessorFeature feature ) {
    if ( feature == null ) {
      throw new NullPointerException();
    }
    this.booleanFeatures.remove( feature );
  }

  public boolean isFeatureSupported( final OutputProcessorFeature.BooleanOutputProcessorFeature feature ) {
    if ( feature == null ) {
      throw new NullPointerException();
    }
    return this.booleanFeatures.contains( feature );
  }

  protected void setNumericFeatureValue( final OutputProcessorFeature.NumericOutputProcessorFeature feature,
                                         final double value ) {
    if ( feature == null ) {
      throw new NullPointerException();
    }
    numericFeatures.put( feature, new Double( value ) );
  }

  public double getNumericFeatureValue( final OutputProcessorFeature.NumericOutputProcessorFeature feature ) {
    if ( feature == null ) {
      throw new NullPointerException();
    }
    final Double d = (Double) numericFeatures.get( feature );
    if ( d == null ) {
      return 0;
    }
    return d.doubleValue();
  }

  public boolean isContentSupported( final Object content ) {
    return content != null;
  }

  protected FontRegistry getFontRegistry() {
    return fontRegistry;
  }

  protected FontStorage getFontStorage() {
    return fontStorage;
  }

  /**
   * Computes the font-metrics using the given properties.
   * <p/>
   * This method is a implementation detail. Use it in an output target, but be aware that it may change between
   * releases.
   *
   * @param fontFamily   the font family.
   * @param fontSize     the font size.
   * @param bold         a flag indicating whether the font should be displayed in bold.
   * @param italics      a flag indicating whether the font should be displayed in italics.
   * @param encoding     a valid font encoding, can be null to use the default.
   * @param embedded     a flag indicating whether the font is intended for embedded use.
   * @param antiAliasing a flag indicating whether the font should be rendered in aliased mode.
   * @return the font metrics, never null.
   * @throws IllegalArgumentException if the font family was invalid and no default family could be located.
   */
  public FontMetrics getFontMetrics( final String fontFamily,
                                     final double fontSize,
                                     final boolean bold,
                                     final boolean italics,
                                     final String encoding,
                                     final boolean embedded,
                                     final boolean antiAliasing ) throws IllegalArgumentException {
    if ( fontFamily == null ) {
      throw new NullPointerException();
    }

    lookupKey.setAntiAliased( antiAliasing );
    lookupKey.setEncoding( encoding );
    lookupKey.setEmbedded( embedded );
    lookupKey.setFontFamily( fontFamily );
    lookupKey.setFontSize( fontSize );
    lookupKey.setBold( bold );
    lookupKey.setItalics( italics );

    final FontMetrics cached = (FontMetrics) fontMetricsCache.get( lookupKey );
    if ( cached != null ) {
      return cached;
    }

    final FontRegistry registry = getFontRegistry();
    FontFamily family = registry.getFontFamily( fontFamily );
    if ( family == null ) {
      AbstractOutputMetaData.logger.warn( "Unable to lookup the font family: " + fontFamily );

      // Get the default font name
      final CSSValue fallBack = getDefaultFontFamily();
      if ( fallBack == null ) {
        // If this case happens, the output-processor meta-data does not provide a sensible
        // fall-back value. As we cannot continue without a font, we fail here instead of
        // waiting for a NullPointer or other weird error later.
        throw new IllegalArgumentException( "No default family defined, aborting." );
      }

      if ( fallBack instanceof CSSStringValue ) {
        final CSSStringValue svalue = (CSSStringValue) fallBack;
        family = registry.getFontFamily( svalue.getValue() );
      } else {
        family = registry.getFontFamily( fallBack.getCSSText() );
      }
      if ( family == null ) {
        // If this case happens, the output-processor meta-data does not provide a sensible
        // fall-back value. As we cannot continue without a font, we fail here instead of
        // waiting for a NullPointer or other weird error later.
        throw new IllegalArgumentException( "Default family is invalid. Aborting." );
      }
    }


    reusableFontContext.setAntiAliased( antiAliasing );
    reusableFontContext.setFontSize( fontSize );
    reusableFontContext.setEncoding( encoding );
    reusableFontContext.setEmbedded( embedded );

    final FontRecord record = family.getFontRecord( bold, italics );
    final FontMetrics fm = getFontStorage().getFontMetrics( record.getIdentifier(), reusableFontContext );
    if ( fm == null ) {
      // If this case happens, then the previous steps of mapping the font name into sensible
      // defaults failed. The font-system's font-registry is not in sync with the actual font-metrics
      // provider (which indicates that the LibFonts font-system implementation is invalid).
      throw new NullPointerException( "FontMetrics returned from factory is null." );
    }

    fontMetricsCache.put( new FontMetricsKey( lookupKey ), fm );
    return fm;
  }

  /**
   * Returns the font metrics for the font specified in the style sheet.
   * <p/>
   * <B>NOTE: This method will throw an <code>IllegalArgumentException</code> if the specified font family can not be
   * found and the default font family can not be found</B>
   *
   * @param styleSheet ths style sheet from which the font information will be extracted
   * @return FontMetrics for the specified font. If the font family can not be found, the FontMetrics for the default
   * font family will be returned
   * @throws IllegalArgumentException indicated the font metrics could not be determined (this is thrown since methods
   *                                  depending upon this method can not handle a <code>null</code> return).
   */
  public FontMetrics getFontMetrics( final LayoutStyle styleSheet ) throws IllegalArgumentException {
    final CSSValue fontFamily = getNormalizedFontFamilyName( styleSheet.getValue( FontStyleKeys.FONT_FAMILY ) );
    if ( fontFamily == null ) {
      // If this case happens, the stylesheet is not implemented correctly. At that point,
      // we have to assume that the whole engine is no longer behaving valid and therefore we
      // abort early.
      throw new IllegalArgumentException( "No valid font family specified." );
    }
    final String fontName;
    if ( fontFamily instanceof CSSStringValue ) {
      final CSSStringValue svalue = (CSSStringValue) fontFamily;
      fontName = svalue.getValue();
    } else {
      fontName = fontFamily.getCSSText();
    }

    final CSSValue value = styleSheet.getValue( FontStyleKeys.FONT_SIZE );
    final int resolution = (int) getNumericFeatureValue( OutputProcessorFeature.DEVICE_RESOLUTION );
    final double fontSize = StyleSheetUtility.convertLengthToDouble( value, resolution );
    final boolean antiAliasing = FontSmooth.ALWAYS.equals( styleSheet.getValue( FontStyleKeys.FONT_SMOOTH ) );

    final CSSValue boldVal = styleSheet.getValue( FontStyleKeys.FONT_WEIGHT );
    final CSSValue italicsVal = styleSheet.getValue( FontStyleKeys.FONT_STYLE );
    return getFontMetrics( fontName, fontSize, computeBold( boldVal ), computeItalics( italicsVal ), "UTF-8", false,
      antiAliasing );
  }

  private boolean computeItalics( final CSSValue value ) {
    if ( FontStyle.NORMAL.equals( value ) ) {
      return false;
    }
    return true;
  }

  private boolean computeBold( final CSSValue value ) {
    if ( CSSNumericType.NUMBER.equals( value.getType() ) == false ) {
      return false;
    }
    CSSNumericValue nvalue = (CSSNumericValue) value;
    return nvalue.getValue() >= 700;
  }

  public void commit() {
    fontStorage.commit();
  }

  public PageSize getDefaultPageSize() {
    return PageSize.A4;
  }

  /**
   * Resolve one of the built-in fonts.
   *
   * @param name
   * @return
   */
  public CSSValue getNormalizedFontFamilyName( final CSSValue name ) {
    final CSSValue retval = (CSSValue) fontFamilyMapping.get( name );
    if ( retval != null ) {
      return retval;
    }
    if ( name != null ) {
      return name;
    }
    return getDefaultFontFamily();
  }

  public CSSValue getDefaultFontFamily() {
    final CSSValue retval = (CSSValue) fontFamilyMapping.get( null );
    if ( retval == null ) {
      throw new IllegalStateException();
    }
    return retval;
  }
}
