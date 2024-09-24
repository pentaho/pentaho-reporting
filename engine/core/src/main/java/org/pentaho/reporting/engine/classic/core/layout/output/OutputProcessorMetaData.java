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

package org.pentaho.reporting.engine.classic.core.layout.output;

import org.pentaho.reporting.engine.classic.core.ReportAttributeMap;
import org.pentaho.reporting.engine.classic.core.layout.text.ExtendedBaselineInfo;
import org.pentaho.reporting.engine.classic.core.style.StyleSheet;
import org.pentaho.reporting.libraries.base.config.Configuration;
import org.pentaho.reporting.libraries.fonts.registry.FontMetrics;

/**
 * The output processor metadata contains global configuration settings for a give report processing run. It is tightly
 * coupled to its output-processor and generally not interchangeable with output-processor-metadata objects from other
 * output-processors.
 * <p/>
 * The output processor meta-data object is statefull and must not be reused between different report processing runs.
 *
 * @author Thomas Morgner
 */
public interface OutputProcessorMetaData {
  public void initialize( final Configuration configuration );

  public boolean isFeatureSupported( OutputProcessorFeature.BooleanOutputProcessorFeature feature );

  public double getNumericFeatureValue( OutputProcessorFeature.NumericOutputProcessorFeature feature );

  public boolean isContentSupported( Object content );

  public FontMetrics getFontMetrics( StyleSheet styleSheet );

  public ExtendedBaselineInfo getBaselineInfo( int codePoint, StyleSheet styleSheet );

  public String getNormalizedFontFamilyName( final String name );

  /**
   * The export descriptor is a string that describes the output characteristics. For libLayout outputs, it should start
   * with the output class (one of 'pageable', 'flow' or 'stream'), followed by '/liblayout/' and finally followed by
   * the output type (ie. PDF, Print, etc).
   *
   * @return the export descriptor.
   */
  public String getExportDescriptor();

  public Configuration getConfiguration();

  public void commit();

  /**
   * Checks whether this element provides some extra content that is not part of the visible layout structure. This can
   * be embedded scripts, anchors etc.
   *
   * @param style
   * @param attributes
   * @return
   */
  public boolean isExtraContentElement( final StyleSheet style, final ReportAttributeMap attributes );
}
