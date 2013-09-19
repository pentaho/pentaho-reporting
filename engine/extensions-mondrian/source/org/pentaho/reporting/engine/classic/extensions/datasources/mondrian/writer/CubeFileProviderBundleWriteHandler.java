package org.pentaho.reporting.engine.classic.extensions.datasources.mondrian.writer;

import java.io.IOException;

import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer.BundleWriterException;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer.BundleWriterState;
import org.pentaho.reporting.engine.classic.extensions.datasources.mondrian.CubeFileProvider;
import org.pentaho.reporting.libraries.docbundle.WriteableDocumentBundle;
import org.pentaho.reporting.libraries.xmlns.writer.XmlWriter;

/**
 * Todo: Document me!
 * <p/>
 * Date: 25.08.2009
 * Time: 19:19:37
 *
 * @author Thomas Morgner.
 */
public interface CubeFileProviderBundleWriteHandler
{
  public void write(final WriteableDocumentBundle bundle,
                    final BundleWriterState state,
                    final XmlWriter xmlWriter,
                    final CubeFileProvider cubeFileProvider)
      throws IOException, BundleWriterException;

}
