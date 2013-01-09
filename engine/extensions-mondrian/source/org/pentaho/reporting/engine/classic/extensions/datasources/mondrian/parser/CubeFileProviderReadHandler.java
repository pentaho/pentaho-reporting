package org.pentaho.reporting.engine.classic.extensions.datasources.mondrian.parser;

import org.pentaho.reporting.engine.classic.extensions.datasources.mondrian.CubeFileProvider;
import org.pentaho.reporting.libraries.xmlns.parser.XmlReadHandler;

/**
 * Todo: Document me!
 * <p/>
 * Date: 25.08.2009
 * Time: 10:06:56
 *
 * @author Thomas Morgner.
 */
public interface CubeFileProviderReadHandler extends XmlReadHandler
{
  public CubeFileProvider getProvider();
}