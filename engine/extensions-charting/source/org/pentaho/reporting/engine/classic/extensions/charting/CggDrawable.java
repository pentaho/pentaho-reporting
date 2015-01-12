/*
 * This program is free software; you can redistribute it and/or modify it under the
 *  terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
 *  Foundation.
 *
 *  You should have received a copy of the GNU Lesser General Public License along with this
 *  program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 *  or from the Free Software Foundation, Inc.,
 *  51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 *  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *  See the GNU Lesser General Public License for more details.
 *
 *  Copyright (c) 2006 - 2013 Pentaho Corporation..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.extensions.charting;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.RenderedImage;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.bridge.DocumentLoader;
import org.apache.batik.bridge.GVTBuilder;
import org.apache.batik.gvt.GraphicsNode;
import org.pentaho.reporting.engine.classic.core.DataRow;
import org.pentaho.reporting.engine.classic.core.ResourceBundleFactory;
import org.pentaho.reporting.engine.classic.core.imagemap.ImageMap;
import org.pentaho.reporting.engine.classic.core.style.StyleSheet;
import org.pentaho.reporting.engine.classic.core.util.ReportDrawable;
import org.pentaho.reporting.libraries.base.config.Configuration;
import org.pentaho.reporting.libraries.resourceloader.modules.factory.svg.HeadlessSVGUserAgent;
import org.pentaho.reporting.libraries.resourceloader.modules.factory.svg.SVGDrawable;
import org.w3c.dom.Document;
import pt.webdetails.cgg.ScriptCreationException;
import pt.webdetails.cgg.ScriptExecuteException;
import pt.webdetails.cgg.scripts.ScriptType;

public class CggDrawable implements ReportDrawable
{
  private RawCgg rawCgg;
  private String scriptFile;
  private ScriptType scriptType;
  private HashMap<String, Object> params;

  public CggDrawable(final RawCgg rawCgg, final String scriptFile)
  {
    if (rawCgg == null)
    {
      throw new NullPointerException();
    }
    if (scriptFile == null)
    {
      throw new NullPointerException();
    }
    this.scriptFile = scriptFile;
    this.rawCgg = rawCgg;
    this.scriptType = ScriptType.SVG;
    this.params = new HashMap<>();
  }

  public void setParameter(Map<String,Object> parameters)
  {
    this.params.clear();
    this.params.putAll(parameters);
  }

  public void setParameter(DataRow parameters)
  {
    this.params.clear();
    for(String columnName: parameters.getColumnNames())
    {
      this.params.put(columnName, parameters.get(columnName));
    }
  }

  public Map<String, Object> getParameter()
  {
    //noinspection unchecked
    return (Map<String, Object>) params.clone();
  }

  public ScriptType getScriptType()
  {
    return scriptType;
  }

  public void setScriptType(final ScriptType scriptType)
  {
    this.scriptType = scriptType;
  }

  public void draw(final Graphics2D graphics2D, final Rectangle2D bounds)
  {
    try
    {
      this.rawCgg.draw(scriptFile, scriptType.toString(), null, (int) bounds.getWidth(), (int) bounds.getHeight(), params);
      Object rawObject = this.rawCgg.getRawObject();
      if (rawObject instanceof Document)
      {
        final HeadlessSVGUserAgent userAgent = new HeadlessSVGUserAgent();
        final DocumentLoader loader = new DocumentLoader(userAgent);
        final BridgeContext ctx = new BridgeContext(userAgent, loader);
        final GVTBuilder builder = new GVTBuilder();
        final GraphicsNode node = builder.build(ctx, (Document) rawObject);
        SVGDrawable d = new SVGDrawable(node);
        d.draw(graphics2D, bounds);
      }
      else if (rawObject instanceof RenderedImage)
      {
        graphics2D.drawRenderedImage((RenderedImage) rawObject, new AffineTransform());
      }
    }
    catch (ScriptCreationException | FileNotFoundException | ScriptExecuteException e)
    {
      e.printStackTrace();
    }
  }

  public void setConfiguration(final Configuration config)
  {

  }

  public void setStyleSheet(final StyleSheet style)
  {

  }

  public void setResourceBundleFactory(final ResourceBundleFactory bundleFactory)
  {

  }

  public ImageMap getImageMap(final Rectangle2D bounds)
  {
    return null;
  }
}
