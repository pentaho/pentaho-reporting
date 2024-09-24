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
