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
 * Copyright (c) 2009 Pentaho Corporation.  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.extensions.swt;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.widgets.Display;
import org.pentaho.reporting.libraries.base.config.Configuration;

public class Util
{
  private static ImageRegistry image_registry;
  private static Clipboard clipboard;

  private Util()
  {
  }

  public static URL newURL(final String url_name)
  {
    try
    {
      return new URL(url_name);
    }
    catch (MalformedURLException e)
    {
      throw new RuntimeException("Malformed URL " + url_name, e);
    }
  }

  public static synchronized ImageRegistry getImageRegistry()
  {
    if (image_registry == null)
    {
      image_registry = new ImageRegistry();
      image_registry.put(
          "folder",
          ImageDescriptor.createFromURL(newURL("file:icons/folder.gif")));
      image_registry.put(
          "file",
          ImageDescriptor.createFromURL(newURL("file:icons/file.gif")));
    }
    return image_registry;
  }

  public static synchronized Clipboard getClipboard()
  {
    if (clipboard == null)
    {
      clipboard = new Clipboard(Display.getCurrent());
    }

    return clipboard;
  }

  public static void printConfiguraiton(final Configuration configuration)
  {
    System.err.println("print a configuraiton:");
    final Enumeration e = configuration.getConfigProperties();
    int counter = 0;
    while (e.hasMoreElements())
    {
      final String key = (String) e.nextElement();
      System.err.println(key + ':' + configuration.getConfigProperty(key));
      counter++;
    }
    System.err.println("# of properties: " + counter);
  }
}

