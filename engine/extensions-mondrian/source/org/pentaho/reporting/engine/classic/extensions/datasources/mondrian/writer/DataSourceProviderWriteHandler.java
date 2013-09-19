package org.pentaho.reporting.engine.classic.extensions.datasources.mondrian.writer;

import java.io.IOException;

import org.pentaho.reporting.engine.classic.core.modules.parser.extwriter.ReportWriterContext;
import org.pentaho.reporting.engine.classic.core.modules.parser.extwriter.ReportWriterException;
import org.pentaho.reporting.engine.classic.extensions.datasources.mondrian.DataSourceProvider;
import org.pentaho.reporting.libraries.xmlns.writer.XmlWriter;

/**
 * Todo: Document me!
 * <p/>
 * Date: 25.08.2009
 * Time: 18:56:51
 *
 * @author Thomas Morgner.
 */
public interface DataSourceProviderWriteHandler
{
  /**
   * Writes a data-source into a XML-stream.
   *
   * @param reportWriter the writer context that holds all factories.
   * @param xmlWriter    the XML writer that will receive the generated XML data.
   * @param dataFactory  the data factory that should be written.
   * @throws java.io.IOException if any error occured
   * @throws org.pentaho.reporting.engine.classic.core.modules.parser.extwriter.ReportWriterException
   *                             if the data factory cannot be written.
   */
  public void write(final ReportWriterContext reportWriter,
                    final XmlWriter xmlWriter,
                    final DataSourceProvider dataFactory)
      throws IOException, ReportWriterException;
}
