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

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URISyntaxException;
import java.net.URL;

import org.pentaho.reporting.libraries.resourceloader.ResourceData;
import org.pentaho.reporting.libraries.resourceloader.ResourceKey;
import org.pentaho.reporting.libraries.resourceloader.ResourceKeyCreationException;
import org.pentaho.reporting.libraries.resourceloader.ResourceLoadingException;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;
import pt.webdetails.cgg.scripts.ScriptResourceLoader;
import pt.webdetails.cgg.scripts.ScriptResourceNotFoundException;

public class ResourceBundleScriptLoader implements ScriptResourceLoader
{
  private ResourceManager bundleManager;
  private ResourceKey parentKey;

  public ResourceBundleScriptLoader(ResourceManager bundleManager,
                                    ResourceKey parentKey)
  {
    this.bundleManager = bundleManager;
    this.parentKey = parentKey;
  }

  public Reader getSystemLibraryScript(final String script) throws IOException, ScriptResourceNotFoundException
  {
    throw new ScriptResourceNotFoundException();
  }

  public Reader getContextLibraryScript(final String script) throws IOException, ScriptResourceNotFoundException
  {
    return new InputStreamReader(getContextResource(script));
  }

  public String getContextResourceURI(final String script) throws IOException, ScriptResourceNotFoundException
  {
    try
    {
      ResourceKey resourceKey = bundleManager.deriveKey(parentKey, script);
      ResourceData load = bundleManager.load(resourceKey);
      ResourceKey key = load.getKey();
      URL url = bundleManager.toURL(key);
      if (url != null)
      {
        return url.toURI().toASCIIString();
      }
      return "bundle://" + script;
    }
    catch (ResourceKeyCreationException | ResourceLoadingException | URISyntaxException e)
    {
      throw new ScriptResourceNotFoundException(e);
    }
  }

  public InputStream getContextResource(final String script) throws IOException, ScriptResourceNotFoundException
  {
    try
    {
      ResourceKey resourceKey = bundleManager.deriveKey(parentKey, script);
      ResourceData load = bundleManager.load(resourceKey);
      return load.getResourceAsStream(bundleManager);
    }
    catch (ResourceKeyCreationException | ResourceLoadingException e)
    {
      throw new ScriptResourceNotFoundException(e);
    }
  }
}
