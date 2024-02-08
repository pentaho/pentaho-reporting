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
 * Copyright (c) 2001 - 2018 Object Refinery Ltd, Hitachi Vantara and Contributors..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.layout.output;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.ClassicEngineCoreModule;
import org.pentaho.reporting.engine.classic.core.InvalidReportStateException;
import org.pentaho.reporting.engine.classic.core.ReportAttributeMap;
import org.pentaho.reporting.engine.classic.core.layout.text.ExtendedBaselineInfo;
import org.pentaho.reporting.engine.classic.core.layout.text.LegacyFontMetrics;
import org.pentaho.reporting.engine.classic.core.layout.text.TextUtility;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleKeys;
import org.pentaho.reporting.engine.classic.core.style.StyleSheet;
import org.pentaho.reporting.engine.classic.core.style.TextStyleKeys;
import org.pentaho.reporting.engine.classic.core.util.InstanceID;
import org.pentaho.reporting.libraries.base.config.Configuration;
import org.pentaho.reporting.libraries.base.config.ExtendedConfiguration;
import org.pentaho.reporting.libraries.base.config.ExtendedConfigurationWrapper;
import org.pentaho.reporting.libraries.base.util.LFUMap;
import org.pentaho.reporting.libraries.base.util.StringUtils;
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

public abstract class AbstractOutputProcessorMetaData implements OutputProcessorMetaData {
  private static final Log logger = LogFactory.getLog( AbstractOutputProcessorMetaData.class );

  private static class FontMetricsKey {
    private int hashCode;
    private boolean hashValid;
    private String fontFamily;
    private double fontSize;
    private boolean antiAliased;
    private boolean embedded;
    private String encoding;
    private boolean italics;
    private boolean bold;

    private FontMetricsKey() {
    }

    private FontMetricsKey( final FontMetricsKey derived ) {
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
      this.hashValid = false;
    }

    public double getFontSize() {
      return fontSize;
    }

    public void setFontSize( final double fontSize ) {
      this.fontSize = fontSize;
      this.hashValid = false;
    }

    public boolean isAntiAliased() {
      return antiAliased;
    }

    public void setAntiAliased( final boolean antiAliased ) {
      this.antiAliased = antiAliased;
      this.hashValid = false;
    }

    public boolean isEmbedded() {
      return embedded;
    }

    public void setEmbedded( final boolean embedded ) {
      this.embedded = embedded;
      this.hashValid = false;
    }

    public String getEncoding() {
      return encoding;
    }

    public void setEncoding( final String encoding ) {
      this.encoding = encoding;
      this.hashValid = false;
    }

    public boolean isItalics() {
      return italics;
    }

    public void setItalics( final boolean italics ) {
      this.italics = italics;
      this.hashValid = false;
    }

    public boolean isBold() {
      return bold;
    }

    public void setBold( final boolean bold ) {
      this.bold = bold;
      this.hashValid = false;
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
      if ( hashValid == false ) {
        int result = fontFamily.hashCode();
        final long temp = fontSize != +0.0d ? Double.doubleToLongBits( fontSize ) : 0L;
        result = 29 * result + (int) ( temp ^ ( temp >>> 32 ) );
        result = 29 * result + ( antiAliased ? 1 : 0 );
        result = 29 * result + ( embedded ? 1 : 0 );
        result = 29 * result + ( italics ? 1 : 0 );
        result = 29 * result + ( bold ? 1 : 0 );
        result = 29 * result + ( encoding != null ? encoding.hashCode() : 0 );
        this.hashCode = result;
        this.hashValid = true;
        return result;
      }
      return hashCode;
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

  private static class CacheKey {
    private InstanceID instanceId;
    private String styleClass;

    protected CacheKey() {
    }

    protected CacheKey( final InstanceID instanceId, final String styleClass ) {
      if ( instanceId == null ) {
        throw new NullPointerException();
      }
      if ( styleClass == null ) {
        throw new NullPointerException();
      }
      this.instanceId = instanceId;
      this.styleClass = styleClass;
    }

    public void reuse( final InstanceID instanceId, final String styleClass ) {
      if ( instanceId == null ) {
        throw new NullPointerException();
      }
      if ( styleClass == null ) {
        throw new NullPointerException();
      }
      this.instanceId = instanceId;
      this.styleClass = styleClass;
    }

    public Object getInstanceId() {
      return instanceId;
    }

    public String getStyleClass() {
      return styleClass;
    }

    public boolean equals( final Object o ) {
      if ( this == o ) {
        return true;
      }
      if ( o == null || getClass() != o.getClass() ) {
        return false;
      }

      final CacheKey cacheKey = (CacheKey) o;

      if ( !instanceId.equals( cacheKey.instanceId ) ) {
        return false;
      }
      if ( !styleClass.equals( cacheKey.styleClass ) ) {
        return false;
      }

      return true;
    }

    public int hashCode() {
      int result = instanceId.hashCode();
      result = 31 * result + styleClass.hashCode();
      return result;
    }

    public String toString() {
      return "CacheKey{" + "instanceId=" + instanceId + ", styleClass='" + styleClass + '\'' + '}';
    }
  }

  private static class StyleCacheEntry {
    private long changeTracker;
    private FontMetrics metrics;

    private StyleCacheEntry( final long changeTracker, final FontMetrics metrics ) {
      this.changeTracker = changeTracker;
      this.metrics = metrics;
    }

    public long getChangeTracker() {
      return changeTracker;
    }

    public FontMetrics getMetrics() {
      return metrics;
    }
  }

  private FontStorage fontStorage;
  private FontRegistry fontRegistry;
  private HashMap<OutputProcessorFeature.NumericOutputProcessorFeature, Double> numericFeatures;
  private HashMap<String, String> fontFamilyMapping;
  private HashSet<OutputProcessorFeature.BooleanOutputProcessorFeature> booleanFeatures;
  private Configuration configuration;
  private ReusableFontContext reusableFontContext;

  private HashMap<FontMetricsKey, FontMetrics> fontMetricsCache;
  private LFUMap<FontMetricsKey, ExtendedBaselineInfo> baselinesCache;
  private LFUMap<CacheKey, StyleCacheEntry> fontMetricsByStyleCache;

  private FontMetricsKey lookupKey;

  private double defaultFontSize;
  private double fontSmoothThreshold;
  private double deviceResolution;
  private CacheKey lookupCacheKey;

  protected AbstractOutputProcessorMetaData() {
    this( new DefaultFontStorage( new AWTFontRegistry() ) );
  }

  protected AbstractOutputProcessorMetaData( final FontStorage fontStorage ) {
    if ( fontStorage == null ) {
      throw new NullPointerException();
    }

    this.booleanFeatures = new HashSet<OutputProcessorFeature.BooleanOutputProcessorFeature>();
    this.numericFeatures = new HashMap<OutputProcessorFeature.NumericOutputProcessorFeature, Double>();
    this.reusableFontContext = new ReusableFontContext();
    this.fontMetricsCache = new HashMap<FontMetricsKey, FontMetrics>(); // needs to be a strong reference ..
    this.baselinesCache = new LFUMap<FontMetricsKey, ExtendedBaselineInfo>( 200 );
    this.lookupKey = new FontMetricsKey();
    this.lookupCacheKey = new CacheKey();
    this.fontMetricsByStyleCache = new LFUMap<CacheKey, StyleCacheEntry>( 200 );
    this.fontFamilyMapping = new HashMap<String, String>();

    this.fontRegistry = fontStorage.getFontRegistry();
    this.fontStorage = fontStorage;
    setFamilyMapping( null, "SansSerif" );
  }

  public void initialize( final Configuration configuration ) {
    if ( configuration == null ) {
      throw new NullPointerException();
    }
    this.configuration = configuration;

    final ExtendedConfiguration extendedConfig = new ExtendedConfigurationWrapper( configuration );

    final double defaultFontSize =
        extendedConfig.getIntProperty( "org.pentaho.reporting.engine.classic.core.layout.defaults.FontSize", 12 );
    setNumericFeatureValue( OutputProcessorFeature.DEFAULT_FONT_SIZE, defaultFontSize );

    final double fontSmoothThreshold =
        extendedConfig.getIntProperty( "org.pentaho.reporting.engine.classic.core.layout.defaults.FontSmoothThreshold",
            8 );
    setNumericFeatureValue( OutputProcessorFeature.FONT_SMOOTH_THRESHOLD, fontSmoothThreshold );

    if ( extendedConfig.getBoolProperty(
        "org.pentaho.reporting.engine.classic.core.layout.fontrenderer.UseMaxCharBounds", true ) == false ) {
      addFeature( OutputProcessorFeature.LEGACY_LINEHEIGHT_CALC );
    }
    if ( extendedConfig.getBoolProperty( "org.pentaho.reporting.engine.classic.core.layout.AlwaysPrintFirstLineOfText",
        true ) ) {
      addFeature( OutputProcessorFeature.ALWAYS_PRINT_FIRST_LINE_OF_TEXT );
    }
    if ( extendedConfig.getBoolProperty( ClassicEngineCoreModule.COMPLEX_TEXT_CONFIG_OVERRIDE_KEY, false ) == true ) {
      addFeature( OutputProcessorFeature.COMPLEX_TEXT );
    }
    if ( extendedConfig.getBoolProperty( "org.pentaho.reporting.engine.classic.core.FixImageResolutionMapping", true ) ) {
      addFeature( OutputProcessorFeature.IMAGE_RESOLUTION_MAPPING );
    }
    if ( extendedConfig.getBoolProperty( "org.pentaho.reporting.engine.classic.core.UseNativeScaling", true ) ) {
      addFeature( OutputProcessorFeature.PREFER_NATIVE_SCALING );
    }
    if ( extendedConfig.getBoolProperty( "org.pentaho.reporting.engine.classic.core.DetectExtraContent", true ) ) {
      addFeature( OutputProcessorFeature.DETECT_EXTRA_CONTENT );
    }
    if ( extendedConfig.getBoolProperty( "org.pentaho.reporting.engine.classic.core.legacy.StrictCompatibility", false ) ) {
      addFeature( OutputProcessorFeature.STRICT_COMPATIBILITY );
      addFeature( OutputProcessorFeature.PRD_3750 );
    }

    final double deviceResolution =
        extendedConfig.getIntProperty( "org.pentaho.reporting.engine.classic.core.layout.DeviceResolution", 72 );
    if ( deviceResolution > 0 ) {
      setNumericFeatureValue( OutputProcessorFeature.DEVICE_RESOLUTION, deviceResolution );
    } else {
      setNumericFeatureValue( OutputProcessorFeature.DEVICE_RESOLUTION, 72 );
    }
    if ( "true".equals( configuration
        .getConfigProperty( "org.pentaho.reporting.engine.classic.core.WatermarkPrintedOnTopOfContent" ) ) ) {
      addFeature( OutputProcessorFeature.WATERMARK_PRINTED_ON_TOP );
    }

  }

  public Configuration getConfiguration() {
    if ( configuration == null ) {
      throw new InvalidReportStateException( "Initialize() has not been called yet." );
    }
    return configuration;
  }

  protected void setFamilyMapping( final String family, final String name ) {
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

  public void addNumericFeature( final OutputProcessorFeature.NumericOutputProcessorFeature feature, double val ) {
    if ( feature == null ) {
      throw new NullPointerException();
    }
    this.numericFeatures.put( feature, val );
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
    if ( OutputProcessorFeature.DEFAULT_FONT_SIZE.equals( feature ) ) {
      numericFeatures.put( OutputProcessorFeature.DEFAULT_FONT_SIZE, new Double( value ) );
      this.defaultFontSize = value;
    } else if ( OutputProcessorFeature.FONT_SMOOTH_THRESHOLD.equals( feature ) ) {
      numericFeatures.put( OutputProcessorFeature.FONT_SMOOTH_THRESHOLD, new Double( value ) );
      this.fontSmoothThreshold = value;
    } else if ( OutputProcessorFeature.DEVICE_RESOLUTION.equals( feature ) ) {
      numericFeatures.put( OutputProcessorFeature.DEVICE_RESOLUTION, new Double( value ) );
      this.deviceResolution = value;
    } else {
      numericFeatures.put( feature, new Double( value ) );
    }
  }

  public double getNumericFeatureValue( final OutputProcessorFeature.NumericOutputProcessorFeature feature ) {
    if ( feature == null ) {
      throw new NullPointerException();
    }

    if ( OutputProcessorFeature.DEFAULT_FONT_SIZE == feature ) {
      return this.defaultFontSize;
    } else if ( OutputProcessorFeature.FONT_SMOOTH_THRESHOLD == feature ) {
      return fontSmoothThreshold;
    } else if ( OutputProcessorFeature.DEVICE_RESOLUTION == feature ) {
      return this.deviceResolution;
    }

    final Double d = numericFeatures.get( feature );
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
   * @param name
   *          the raw name, maybe null.
   * @return the normalized name, but never null.
   */
  public String getNormalizedFontFamilyName( final String name ) {
    final String normalizedFontFamily = fontFamilyMapping.get( name );
    if ( normalizedFontFamily == null ) {
      if ( name == null ) {
        throw new IllegalStateException( "There is no default mapping for <null> fonts defined." );
      }
      return name;
    }
    return normalizedFontFamily;
  }

  /**
   * Computes the font-metrics using the given properties.
   * <p/>
   * This method is a implementation detail. Use it in an output target, but be aware that it may change between
   * releases.
   *
   * @param fontFamily
   *          the font family.
   * @param fontSize
   *          the font size.
   * @param bold
   *          a flag indicating whether the font should be displayed in bold.
   * @param italics
   *          a flag indicating whether the font should be displayed in italics.
   * @param encoding
   *          a valid font encoding, can be null to use the default.
   * @param embedded
   *          a flag indicating whether the font is intended for embedded use.
   * @param antiAliasing
   *          a flag indicating whether the font should be rendered in aliased mode.
   * @return the font metrics, never null.
   * @throws IllegalArgumentException
   *           if the font family was invalid and no default family could be located.
   */
  public FontMetrics getFontMetrics( final String fontFamily, final double fontSize, final boolean bold,
      final boolean italics, final String encoding, final boolean embedded, final boolean antiAliasing )
    throws IllegalArgumentException {
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

    final FontMetrics cached = fontMetricsCache.get( lookupKey );
    if ( cached != null ) {
      return cached;
    }

    final FontRegistry registry = getFontRegistry();
    FontFamily family = registry.getFontFamily( fontFamily );
    if ( family == null ) {
      AbstractOutputProcessorMetaData.logger.warn( "Unable to lookup the font family: " + fontFamily );

      // Get the default font name
      final String fallBack = getNormalizedFontFamilyName( null );
      if ( fallBack == null ) {
        // If this case happens, the output-processor meta-data does not provide a sensible
        // fall-back value. As we cannot continue without a font, we fail here instead of
        // waiting for a NullPointer or other weird error later.
        throw new IllegalArgumentException( "No default family defined, aborting." );
      }

      family = registry.getFontFamily( fallBack );
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

    if ( isFeatureSupported( OutputProcessorFeature.LEGACY_LINEHEIGHT_CALC ) ) {
      // Wrap the font metrics into the legacy-metrics ..
      final LegacyFontMetrics legacyFontMetrics = new LegacyFontMetrics( fm, fontSize );
      fontMetricsCache.put( new FontMetricsKey( lookupKey ), legacyFontMetrics );
      return legacyFontMetrics;
    }

    fontMetricsCache.put( new FontMetricsKey( lookupKey ), fm );
    return fm;
  }

  protected boolean getAutoCorrectFontMetrics() {
    return false;
  }

  public ExtendedBaselineInfo getBaselineInfo( final int codePoint, final StyleSheet styleSheet ) {
    final FontMetrics fontMetrics = getFontMetrics( styleSheet );
    if ( fontMetrics.isUniformFontMetrics() ) {
      final String fontFamily =
          getNormalizedFontFamilyName( (String) styleSheet.getStyleProperty( TextStyleKeys.FONT ) );
      if ( fontFamily == null ) {
        // If this case happens, the stylesheet is not implemented correctly. At that point,
        // we have to assume that the whole engine is no longer behaving valid and therefore we
        // abort early.
        throw new IllegalArgumentException( "No valid font family specified." );
      }

      final double fontSize = styleSheet.getDoubleStyleProperty( TextStyleKeys.FONTSIZE, defaultFontSize );

      final boolean antiAliasing = RenderUtility.isFontSmooth( styleSheet, this );
      final String encoding = (String) styleSheet.getStyleProperty( TextStyleKeys.FONTENCODING );
      final boolean embedded =
          isFeatureSupported( OutputProcessorFeature.EMBED_ALL_FONTS )
              || styleSheet.getBooleanStyleProperty( TextStyleKeys.EMBEDDED_FONT );
      final boolean bold = styleSheet.getBooleanStyleProperty( TextStyleKeys.BOLD, false );
      final boolean italics = styleSheet.getBooleanStyleProperty( TextStyleKeys.ITALIC, false );

      lookupKey.setAntiAliased( antiAliasing );
      lookupKey.setEncoding( encoding );
      lookupKey.setEmbedded( embedded );
      lookupKey.setFontFamily( fontFamily );
      lookupKey.setFontSize( fontSize );
      lookupKey.setBold( bold );
      lookupKey.setItalics( italics );

      final ExtendedBaselineInfo cached = baselinesCache.get( lookupKey );
      if ( cached != null ) {
        return cached;
      }
    }

    ExtendedBaselineInfo baselineInfo = null;
    // To Differentiate Excel calls - [PRD-5435]
    if ( this.getAutoCorrectFontMetrics() ) {
      baselineInfo = TextUtility.createPaddedBaselineInfo( 'x', fontMetrics, null );
    } else {
      baselineInfo = TextUtility.createBaselineInfo( 'x', fontMetrics, null );
    }

    if ( fontMetrics.isUniformFontMetrics() ) {
      baselinesCache.put( new FontMetricsKey( lookupKey ), baselineInfo );
    }
    return baselineInfo;
  }

  /**
   * Returns the font metrics for the font specified in the style sheet.
   * <p/>
   * <B>NOTE: This method will throw an <code>IllegalArgumentException</code> if the specified font family can not be
   * found and the default font family can not be found</B>
   *
   * @param styleSheet
   *          ths style sheet from which the font information will be extracted
   * @return FontMetrics for the specified font. If the font family can not be found, the FontMetrics for the default
   *         font family will be returned
   * @throws IllegalArgumentException
   *           indicated the font metrics could not be determined (this is thrown since methods depending upon this
   *           method can not handle a <code>null</code> return).
   */
  public FontMetrics getFontMetrics( final StyleSheet styleSheet ) throws IllegalArgumentException {
    lookupCacheKey.reuse( styleSheet.getId(), styleSheet.getClass().getName() );
    final StyleCacheEntry o = fontMetricsByStyleCache.get( lookupCacheKey );
    if ( o != null ) {
      if ( o.getChangeTracker() == styleSheet.getChangeTracker() ) {
        return o.getMetrics();
      }
    }

    final String fontFamily = getNormalizedFontFamilyName( (String) styleSheet.getStyleProperty( TextStyleKeys.FONT ) );
    if ( fontFamily == null ) {
      // If this case happens, the stylesheet is not implemented correctly. At that point,
      // we have to assume that the whole engine is no longer behaving valid and therefore we
      // abort early.
      throw new IllegalArgumentException( "No valid font family specified." );
    }

    final double fontSize = styleSheet.getDoubleStyleProperty( TextStyleKeys.FONTSIZE, defaultFontSize );

    final boolean antiAliasing = RenderUtility.isFontSmooth( styleSheet, this );
    final String encoding = (String) styleSheet.getStyleProperty( TextStyleKeys.FONTENCODING );
    final boolean embedded =
        isFeatureSupported( OutputProcessorFeature.EMBED_ALL_FONTS )
            || styleSheet.getBooleanStyleProperty( TextStyleKeys.EMBEDDED_FONT );
    final boolean bold = styleSheet.getBooleanStyleProperty( TextStyleKeys.BOLD, false );
    final boolean italics = styleSheet.getBooleanStyleProperty( TextStyleKeys.ITALIC, false );

    final FontMetrics metrics = getFontMetrics( fontFamily, fontSize, bold, italics, encoding, embedded, antiAliasing );
    final CacheKey key = new CacheKey( styleSheet.getId(), styleSheet.getClass().getName() );
    fontMetricsByStyleCache.put( key, new StyleCacheEntry( styleSheet.getChangeTracker(), metrics ) );
    return metrics;
  }

  public void commit() {
    fontStorage.commit();
    fontMetricsByStyleCache.clear();
  }

  /**
   * Checks whether this element provides some extra content that is not part of the visible layout structure. This can
   * be embedded scripts, anchors etc.
   *
   * @param style
   * @param attributes
   * @return
   */
  public boolean isExtraContentElement( final StyleSheet style, final ReportAttributeMap attributes ) {
    if ( isFeatureSupported( OutputProcessorFeature.DETECT_EXTRA_CONTENT ) == false ) {
      return false;
    }
    if ( StringUtils.isEmpty( (String) style.getStyleProperty( ElementStyleKeys.ANCHOR_NAME ) ) == false ) {
      return true;
    }
    if ( StringUtils.isEmpty( (String) style.getStyleProperty( ElementStyleKeys.HREF_TARGET ) ) == false ) {
      return true;
    }
    return false;
  }
}
