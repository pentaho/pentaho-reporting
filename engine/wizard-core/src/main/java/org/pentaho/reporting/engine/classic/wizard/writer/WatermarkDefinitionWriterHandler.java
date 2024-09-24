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

package org.pentaho.reporting.engine.classic.wizard.writer;

import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer.BundleWriterState;
import org.pentaho.reporting.engine.classic.wizard.WizardCoreModule;
import org.pentaho.reporting.engine.classic.wizard.model.Length;
import org.pentaho.reporting.engine.classic.wizard.model.WatermarkDefinition;
import org.pentaho.reporting.libraries.docbundle.WriteableDocumentBundle;
import org.pentaho.reporting.libraries.xmlns.common.AttributeList;
import org.pentaho.reporting.libraries.xmlns.writer.XmlWriter;

import java.io.IOException;

public class WatermarkDefinitionWriterHandler {
  public void writeReport( final WriteableDocumentBundle bundle,
                           final BundleWriterState state,
                           final XmlWriter writer,
                           final WatermarkDefinition rootBandDefinition ) throws IOException {
    final AttributeList attList = new AttributeList();
    final String source = rootBandDefinition.getSource();
    if ( source != null ) {
      attList.setAttribute( WizardCoreModule.NAMESPACE, "source", source );
    }

    final Length x = rootBandDefinition.getX();
    if ( x != null ) {
      attList.setAttribute( WizardCoreModule.NAMESPACE, "x", x.toString() );
    }
    final Length y = rootBandDefinition.getY();
    if ( y != null ) {
      attList.setAttribute( WizardCoreModule.NAMESPACE, "y", y.toString() );
    }
    final Length width = rootBandDefinition.getWidth();
    if ( width != null ) {
      attList.setAttribute( WizardCoreModule.NAMESPACE, "width", width.toString() );
    }
    final Length height = rootBandDefinition.getX();
    if ( height != null ) {
      attList.setAttribute( WizardCoreModule.NAMESPACE, "height", height.toString() );
    }

    final boolean visible = rootBandDefinition.isVisible();
    attList.setAttribute( WizardCoreModule.NAMESPACE, "visible", String.valueOf( visible ) );

    final Boolean scale = rootBandDefinition.getScale();
    if ( scale != null ) {
      attList.setAttribute( WizardCoreModule.NAMESPACE, "scale", String.valueOf( scale ) );
    }

    final Boolean keepAspectRatio = rootBandDefinition.getKeepAspectRatio();
    if ( keepAspectRatio != null ) {
      attList.setAttribute( WizardCoreModule.NAMESPACE, "keep-aspect-ratio", String.valueOf( keepAspectRatio ) );
    }

    writer.writeTag( WizardCoreModule.NAMESPACE, "watermark-specification", attList, XmlWriter.CLOSE );
  }
}
