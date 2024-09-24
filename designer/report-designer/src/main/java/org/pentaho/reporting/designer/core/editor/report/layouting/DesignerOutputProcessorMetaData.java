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

package org.pentaho.reporting.designer.core.editor.report.layouting;

import org.pentaho.reporting.engine.classic.core.layout.output.AbstractOutputProcessorMetaData;
import org.pentaho.reporting.engine.classic.core.layout.output.OutputProcessorFeature;
import org.pentaho.reporting.libraries.base.config.Configuration;
import org.pentaho.reporting.libraries.fonts.awt.AWTFontRegistry;
import org.pentaho.reporting.libraries.fonts.registry.DefaultFontStorage;

public class DesignerOutputProcessorMetaData extends AbstractOutputProcessorMetaData {
  public DesignerOutputProcessorMetaData() {
    super( new DefaultFontStorage( new AWTFontRegistry() ) );
    addFeature( OutputProcessorFeature.DESIGNTIME );
  }

  public void initialize( final Configuration configuration ) {
    super.initialize( configuration );
    addFeature( OutputProcessorFeature.FAST_FONTRENDERING );
    addFeature( OutputProcessorFeature.BACKGROUND_IMAGE );
    addFeature( OutputProcessorFeature.SPACING_SUPPORTED );
    addFeature( OutputProcessorFeature.WATERMARK_SECTION );
    addFeature( OutputProcessorFeature.DESIGNTIME );
    addFeature( OutputProcessorFeature.PAGE_SECTIONS );
    addFeature( OutputProcessorFeature.DIRECT_RICHTEXT_RENDERING );
  }

  public String getExportDescriptor() {
    return "pageable/report-designer"; // NON-NLS
  }
}
