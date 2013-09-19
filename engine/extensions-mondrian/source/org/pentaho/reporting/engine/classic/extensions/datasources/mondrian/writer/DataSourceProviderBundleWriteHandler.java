package org.pentaho.reporting.engine.classic.extensions.datasources.mondrian.writer;

import java.io.IOException;

import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer.BundleWriterState;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer.BundleWriterException;
import org.pentaho.reporting.engine.classic.core.modules.parser.extwriter.ReportWriterException;
import org.pentaho.reporting.engine.classic.extensions.datasources.mondrian.DataSourceProvider;
import org.pentaho.reporting.libraries.docbundle.WriteableDocumentBundle;
import org.pentaho.reporting.libraries.xmlns.writer.XmlWriter;

/**
 * Todo: Document me!
 * <p/>
 * Date: 25.08.2009
 * Time: 18:56:51
 *
 * @author Thomas Morgner.
 */
public interface DataSourceProviderBundleWriteHandler
{
  /**
   * Writes a data-source into a XML-stream.
   *
   * @param bundle             the document bundle that is produced.
   * @param state              the current writer state.
   * @param xmlWriter          the XML writer that will receive the generated XML data.
   * @param dataSourceProvider the data factory that should be written.
   * @throws java.io.IOException if any error occured
   */
  public void write(final WriteableDocumentBundle bundle,
                    final BundleWriterState state,
                    final XmlWriter xmlWriter,
                    final DataSourceProvider dataSourceProvider)
      throws IOException, BundleWriterException;
}