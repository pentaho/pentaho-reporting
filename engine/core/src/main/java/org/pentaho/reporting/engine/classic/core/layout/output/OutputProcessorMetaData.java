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
