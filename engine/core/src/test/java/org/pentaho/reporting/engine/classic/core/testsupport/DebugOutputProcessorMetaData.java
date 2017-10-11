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

package org.pentaho.reporting.engine.classic.core.testsupport;

import org.pentaho.reporting.engine.classic.core.layout.output.AbstractOutputProcessorMetaData;
import org.pentaho.reporting.engine.classic.core.layout.output.OutputProcessorFeature;
import org.pentaho.reporting.engine.classic.core.testsupport.font.LocalFontRegistry;
import org.pentaho.reporting.libraries.base.config.Configuration;
import org.pentaho.reporting.libraries.fonts.monospace.MonospaceFontRegistry;
import org.pentaho.reporting.libraries.fonts.registry.DefaultFontStorage;
import org.pentaho.reporting.libraries.fonts.registry.FontStorage;

public class DebugOutputProcessorMetaData extends AbstractOutputProcessorMetaData {
  private static LocalFontRegistry localFontRegistry;

  public DebugOutputProcessorMetaData() {
    this( getLocalFontStorage() );
  }

  public DebugOutputProcessorMetaData( final FontStorage fontStorage ) {
    super( fontStorage );
  }

  public void initialize( final Configuration configuration ) {
    super.initialize( configuration );
    addFeature( OutputProcessorFeature.FAST_FONTRENDERING );
    addFeature( OutputProcessorFeature.BACKGROUND_IMAGE );
    addFeature( OutputProcessorFeature.PAGE_SECTIONS );
    addFeature( OutputProcessorFeature.PAGEBREAKS );
    addFeature( OutputProcessorFeature.SPACING_SUPPORTED );
    addFeature( OutputProcessorFeature.WATERMARK_SECTION );
    if ( getFontRegistry() instanceof MonospaceFontRegistry ) {
      removeFeature( OutputProcessorFeature.LEGACY_LINEHEIGHT_CALC );
    }
  }

  public void setDesignTime( boolean designTime ) {
    if ( designTime ) {
      addFeature( OutputProcessorFeature.DESIGNTIME );
    } else {
      removeFeature( OutputProcessorFeature.DESIGNTIME );
    }
  }

  /**
   * The export descriptor is a string that describes the output characteristics. For libLayout outputs, it should start
   * with the output class (one of 'pageable', 'flow' or 'stream'), followed by '/liblayout/' and finally followed by
   * the output type (ie. PDF, Print, etc).
   *
   * @return the export descriptor.
   */
  public String getExportDescriptor() {
    return "pageable/debug";
  }

  public static synchronized FontStorage getLocalFontStorage() {
    if ( localFontRegistry == null ) {
      localFontRegistry = new LocalFontRegistry();
      localFontRegistry.initialize();
    }
    return new DefaultFontStorage( localFontRegistry );
  }
}
