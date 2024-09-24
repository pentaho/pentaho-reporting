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

package org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer;

import org.pentaho.reporting.engine.classic.core.AttributeNames;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.BundleNamespaces;
import org.pentaho.reporting.engine.classic.core.modules.parser.ext.ExtParserModule;
import org.pentaho.reporting.engine.classic.core.style.css.ElementStyleDefinition;
import org.pentaho.reporting.engine.classic.core.util.NoCloseOutputStream;
import org.pentaho.reporting.libraries.xmlns.common.AttributeList;
import org.pentaho.reporting.libraries.xmlns.writer.DefaultTagDescription;
import org.pentaho.reporting.libraries.xmlns.writer.XmlWriter;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

public class StyleDefinitionWriter {
  public StyleDefinitionWriter() {
  }

  public void write( final File file, final ElementStyleDefinition styleDefinition ) throws IOException {
    final FileOutputStream fout = new FileOutputStream( file );
    final BufferedOutputStream bout = new BufferedOutputStream( fout );
    try {
      write( bout, styleDefinition );
    } finally {
      bout.close();
    }
  }

  public void write( final OutputStream outputStream, final ElementStyleDefinition styleDefinition ) throws IOException {
    final DefaultTagDescription tagDescription = BundleWriterHandlerRegistry.getInstance().createWriterTagDescription();
    final XmlWriter writer =
        new XmlWriter( new OutputStreamWriter( new NoCloseOutputStream( outputStream ), "UTF-8" ), tagDescription,
            "  ", "\n" );
    writer.writeXmlDeclaration( "UTF-8" );

    final AttributeList rootAttributes = new AttributeList();
    rootAttributes.addNamespaceDeclaration( "", BundleNamespaces.STYLE );
    rootAttributes.addNamespaceDeclaration( "layout", BundleNamespaces.LAYOUT );
    rootAttributes.addNamespaceDeclaration( "core", AttributeNames.Core.NAMESPACE );
    rootAttributes.addNamespaceDeclaration( "html", AttributeNames.Html.NAMESPACE );
    rootAttributes.addNamespaceDeclaration( "swing", AttributeNames.Swing.NAMESPACE );
    rootAttributes.addNamespaceDeclaration( "pdf", AttributeNames.Pdf.NAMESPACE );
    rootAttributes.addNamespaceDeclaration( "designtime", AttributeNames.Designtime.NAMESPACE );
    rootAttributes.addNamespaceDeclaration( "crosstab", AttributeNames.Crosstab.NAMESPACE );
    rootAttributes.addNamespaceDeclaration( "pentaho", AttributeNames.Pentaho.NAMESPACE );
    rootAttributes.addNamespaceDeclaration( "table", AttributeNames.Table.NAMESPACE );
    rootAttributes.addNamespaceDeclaration( "page", ExtParserModule.NAMESPACE );

    writer.writeTag( BundleNamespaces.STYLE, "style-definition", rootAttributes, XmlWriter.OPEN );
    StyleFileWriter.writeStyleDefinition( writer, styleDefinition );
    writer.writeCloseTag();
    writer.flush();
    writer.close();
  }
}
