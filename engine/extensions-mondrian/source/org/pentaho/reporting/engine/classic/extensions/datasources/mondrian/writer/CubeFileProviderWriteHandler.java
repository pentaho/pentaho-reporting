package org.pentaho.reporting.engine.classic.extensions.datasources.mondrian.writer;

import java.io.IOException;

import org.pentaho.reporting.engine.classic.core.modules.parser.extwriter.ReportWriterContext;
import org.pentaho.reporting.engine.classic.core.modules.parser.extwriter.ReportWriterException;
import org.pentaho.reporting.engine.classic.extensions.datasources.mondrian.CubeFileProvider;
import org.pentaho.reporting.libraries.xmlns.writer.XmlWriter;

/**
 * Todo: Document me!
 * <p/>
 * Date: 25.08.2009
 * Time: 19:19:01
 *
 * @author Thomas Morgner.
 */
public interface CubeFileProviderWriteHandler
{
  public void write(final ReportWriterContext reportWriter,
                    final XmlWriter xmlWriter,
                    final CubeFileProvider cubeFileProvider)
      throws IOException, ReportWriterException;

}
