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

package org.pentaho.reporting.engine.classic.extensions.legacy.charts.writer;

import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer.BundleWriterException;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer.BundleWriterState;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer.elements.AbstractElementWriteHandler;
import org.pentaho.reporting.engine.classic.extensions.legacy.charts.LegacyChartElementModule;
import org.pentaho.reporting.libraries.docbundle.WriteableDocumentBundle;
import org.pentaho.reporting.libraries.xmlns.common.AttributeList;
import org.pentaho.reporting.libraries.xmlns.writer.XmlWriter;

import java.io.IOException;

@Deprecated
public class LegacyChartWriteHandler extends AbstractElementWriteHandler {
  public LegacyChartWriteHandler() {
  }

  /**
   * Writes a single element as XML structure.
   *
   * @param bundle    the bundle to which to write to.
   * @param state     the current write-state.
   * @param xmlWriter the xml writer.
   * @param element   the element.
   * @throws IOException           if an IO error occured.
   * @throws BundleWriterException if an Bundle writer.
   */
  public void writeElement( final WriteableDocumentBundle bundle,
                            final BundleWriterState state,
                            final XmlWriter xmlWriter,
                            final Element element )
    throws IOException, BundleWriterException {
    final AttributeList attList = createMainAttributes( element, xmlWriter );
    if ( xmlWriter.isNamespaceDefined( LegacyChartElementModule.NAMESPACE ) == false ) {
      attList.addNamespaceDeclaration( "legacy-charts", LegacyChartElementModule.NAMESPACE );
    }
    xmlWriter.writeTag( LegacyChartElementModule.NAMESPACE, "legacy-chart", attList, XmlWriter.OPEN );
    writeElementBody( bundle, state, element, xmlWriter );
    xmlWriter.writeCloseTag();
  }

}
