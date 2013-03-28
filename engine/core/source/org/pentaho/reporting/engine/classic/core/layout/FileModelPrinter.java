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
 * Copyright (c) 2001 - 2009 Object Refinery Ltd, Pentaho Corporation and Contributors..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.layout;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class FileModelPrinter extends ModelPrinter
{
  private BufferedWriter writer;

  public FileModelPrinter() throws IOException
  {
    this("model-dump-");
  }

  public FileModelPrinter(final String prefix) throws IOException
  {
    final File file = File.createTempFile(prefix, ".txt");
    writer = new BufferedWriter(new FileWriter(file));
  }

  public FileModelPrinter(final String prefix, final File tempDir) throws IOException
  {
    final File file = File.createTempFile(prefix, ".txt", tempDir);
    writer = new BufferedWriter(new FileWriter(file));
  }

  protected void print(final String s)
  {
    try
    {
      writer.write(s);
      writer.write("\n");
    }
    catch (IOException e)
    {
      throw new IllegalStateException(e);
    }
  }

  public void close() throws IOException
  {
    writer.close();
  }
}
