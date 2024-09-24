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

package org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Enumeration;

import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.BundleNamespaces;
import org.pentaho.reporting.libraries.base.config.HierarchicalConfiguration;
import org.pentaho.reporting.libraries.base.config.ModifiableConfiguration;
import org.pentaho.reporting.libraries.docbundle.WriteableDocumentBundle;
import org.pentaho.reporting.libraries.xmlns.common.AttributeList;
import org.pentaho.reporting.libraries.xmlns.writer.DefaultTagDescription;
import org.pentaho.reporting.libraries.xmlns.writer.XmlWriter;
import org.pentaho.reporting.libraries.xmlns.writer.XmlWriterSupport;

/**
 * Writes the settings file. For now, it does not write any runtime information. The runtime information needs to be
 * filled into the writer-state by the writer-handlers. Therefore this file-writer must be the last one that is written
 * into a bundle.
 * <p/>
 * There can be only one settings file per report bundle and the settings have to be located in the root of the bundle.
 *
 * @author Thomas Morgner
 */
public class SettingsFileWriter implements BundleWriterHandler {
  public SettingsFileWriter() {
  }

  /**
   * Returns a relatively high processing order indicating this BundleWriterHandler should be one of the last processed
   *
   * @return the relative processing order for this BundleWriterHandler
   */
  public int getProcessingOrder() {
    return 100000;
  }

  public String writeReport( final WriteableDocumentBundle bundle, final BundleWriterState state ) throws IOException,
    BundleWriterException {
    if ( bundle == null ) {
      throw new NullPointerException();
    }
    if ( state == null ) {
      throw new NullPointerException();
    }

    final OutputStream outputStream = new BufferedOutputStream( bundle.createEntry( "settings.xml", "text/xml" ) );
    final DefaultTagDescription tagDescription = BundleWriterHandlerRegistry.getInstance().createWriterTagDescription();
    final XmlWriter writer =
        new XmlWriter( new OutputStreamWriter( outputStream, "UTF-8" ), tagDescription, "  ", "\n" );
    writer.writeXmlDeclaration( "UTF-8" );

    final AttributeList rootAttributes = new AttributeList();
    rootAttributes.addNamespaceDeclaration( "", BundleNamespaces.SETTINGS );

    writer.writeTag( BundleNamespaces.SETTINGS, "settings", rootAttributes, XmlWriterSupport.OPEN );

    writeConfiguration( state, writer );
    writeRuntimeInformation( state, writer );

    writer.writeCloseTag();
    writer.close();

    return "settings.xml";
  }

  protected void writeRuntimeInformation( final BundleWriterState state, final XmlWriter writer ) throws IOException {
    if ( state == null ) {
      throw new NullPointerException();
    }
    if ( writer == null ) {
      throw new NullPointerException();
    }
    writer.writeTag( BundleNamespaces.SETTINGS, "runtime", XmlWriterSupport.OPEN );
    writer.writeCloseTag();
  }

  protected void writeConfiguration( final BundleWriterState state, final XmlWriter writer ) throws IOException {
    if ( state == null ) {
      throw new NullPointerException();
    }
    if ( writer == null ) {
      throw new NullPointerException();
    }

    final MasterReport report = state.getMasterReport();
    final ModifiableConfiguration rawConfig = report.getReportConfiguration();
    if ( rawConfig instanceof HierarchicalConfiguration ) {
      writer.writeTag( BundleNamespaces.SETTINGS, "configuration", XmlWriterSupport.OPEN );
      final HierarchicalConfiguration configuration = (HierarchicalConfiguration) rawConfig;
      final Enumeration keys = configuration.getConfigProperties();
      while ( keys.hasMoreElements() ) {
        final String key = (String) keys.nextElement();
        final String value = configuration.getConfigProperty( key );
        if ( value != null && configuration.isLocallyDefined( key ) ) {
          writer.writeTag( BundleNamespaces.SETTINGS, "property", "name", key, XmlWriterSupport.OPEN );
          writer.writeTextNormalized( value, true );
          writer.writeCloseTag();
        }
      }

      writer.writeCloseTag();
    }
  }
}
