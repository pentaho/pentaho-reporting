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

package org.pentaho.reporting.engine.classic.extensions.datasources.mondrian.writer;

import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer.BundleWriterException;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer.BundleWriterState;
import org.pentaho.reporting.engine.classic.core.modules.parser.extwriter.ReportWriterContext;
import org.pentaho.reporting.engine.classic.core.modules.parser.extwriter.ReportWriterException;
import org.pentaho.reporting.engine.classic.extensions.datasources.mondrian.CubeFileProvider;
import org.pentaho.reporting.engine.classic.extensions.datasources.mondrian.MondrianDataFactoryModule;
import org.pentaho.reporting.libraries.base.util.StringUtils;
import org.pentaho.reporting.libraries.docbundle.WriteableDocumentBundle;
import org.pentaho.reporting.libraries.xmlns.writer.XmlWriter;

import java.io.IOException;

public class DefaultCubeFileProviderWriteHandler
  implements CubeFileProviderBundleWriteHandler, CubeFileProviderWriteHandler {
  public DefaultCubeFileProviderWriteHandler() {
  }

  public void write( final WriteableDocumentBundle bundle,
                     final BundleWriterState state,
                     final XmlWriter xmlWriter,
                     final CubeFileProvider cubeFileProvider ) throws IOException, BundleWriterException {
    write( xmlWriter, cubeFileProvider );
  }

  public void write( final ReportWriterContext reportWriter,
                     final XmlWriter xmlWriter,
                     final CubeFileProvider cubeFileProvider ) throws IOException, ReportWriterException {
    write( xmlWriter, cubeFileProvider );
  }

  protected void write( final XmlWriter writer, final CubeFileProvider provider ) throws IOException {
    writer.writeTag( MondrianDataFactoryModule.NAMESPACE, "cube-file", XmlWriter.OPEN );
    writer.writeTag( MondrianDataFactoryModule.NAMESPACE, "cube-filename", XmlWriter.OPEN );
    writer.writeTextNormalized( provider.getDesignTimeFile(), false );
    writer.writeCloseTag();
    if ( StringUtils.isEmpty( provider.getCubeConnectionName() ) == false ) {
      writer.writeTag( MondrianDataFactoryModule.NAMESPACE, "cube-connection-name", XmlWriter.OPEN );
      writer.writeTextNormalized( provider.getCubeConnectionName(), false );
      writer.writeCloseTag();
    }
    writer.writeCloseTag();
  }

}
