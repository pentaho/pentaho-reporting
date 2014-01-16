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

package org.pentaho.reporting.engine.classic.core.modules.output.fast.html;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;

import org.pentaho.reporting.engine.classic.core.Band;
import org.pentaho.reporting.engine.classic.core.ReportDefinition;
import org.pentaho.reporting.engine.classic.core.ReportElement;
import org.pentaho.reporting.engine.classic.core.function.ExpressionRuntime;
import org.pentaho.reporting.engine.classic.core.layout.output.OutputProcessorMetaData;
import org.pentaho.reporting.engine.classic.core.modules.output.fast.template.FastGridLayout;
import org.pentaho.reporting.engine.classic.core.modules.output.table.base.SheetLayout;
import org.pentaho.reporting.engine.classic.core.util.InstanceID;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

public class FastHtmlPrinter
{
  private SheetLayout sharedSheetLayout;
  private OutputStream outputStream;

  public FastHtmlPrinter(final SheetLayout sharedSheetLayout,
                         final OutputStream outputStream)
  {
    this.sharedSheetLayout = sharedSheetLayout;
    this.outputStream = outputStream;
  }

  public void close() throws IOException
  {


  }

  public void init(final OutputProcessorMetaData metaData,
                   final ResourceManager resourceManager,
                   final ReportDefinition report)
  {


  }

  public void print(final ExpressionRuntime runtime,
                    final FastGridLayout gridLayout,
                    final HashMap<InstanceID, ReportElement> elements)
  {


  }

  public void startSection(final Band band)
  {


  }

  public void endSection(final Band band)
  {


  }
}
