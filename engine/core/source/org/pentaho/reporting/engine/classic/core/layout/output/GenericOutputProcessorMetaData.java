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
* Copyright (c) 2001 - 2013 Object Refinery Ltd, Pentaho Corporation and Contributors..  All rights reserved.
*/

package org.pentaho.reporting.engine.classic.core.layout.output;

/**
 * A generic dummy class that reports the export-descriptor "none/none".
 *
 * @author Thomas Morgner
 */
public final class GenericOutputProcessorMetaData extends AbstractOutputProcessorMetaData
{
  private String exportDescriptor;

  public GenericOutputProcessorMetaData()
  {
    this("none/none");
  }

  public GenericOutputProcessorMetaData(final String exportDescriptor)
  {
    if (exportDescriptor == null)
    {
      throw new NullPointerException();
    }
    this.exportDescriptor = exportDescriptor;
  }

  /**
   * The export descriptor is a string that describes the output characteristics. For libLayout outputs, it should start
   * with the output class (one of 'pageable', 'flow' or 'stream'), followed by '/liblayout/' and finally followed by
   * the output type (ie. PDF, Print, etc).
   *
   * @return the export descriptor.
   */
  public String getExportDescriptor()
  {
    return exportDescriptor;
  }
}
