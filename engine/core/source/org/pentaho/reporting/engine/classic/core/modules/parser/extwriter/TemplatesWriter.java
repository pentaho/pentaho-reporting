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

package org.pentaho.reporting.engine.classic.core.modules.parser.extwriter;

import java.io.IOException;

import org.pentaho.reporting.libraries.xmlns.writer.XmlWriter;

/**
 * The templates writer is responsible to write the templates section.
 *
 * @author Thomas Morgner
 * @deprecated No longer used.
 */
public class TemplatesWriter extends AbstractXMLDefinitionWriter
{
  /**
   * Creates a new writer.
   *
   * @param reportWriter the report writer.
   * @param writer       the current indention level.
   */
  public TemplatesWriter(final ReportWriter reportWriter, final XmlWriter writer)
  {
    super(reportWriter, writer);
  }

  /**
   * Writes the templates (not yet supported).
   *
   * @throws IOException           if there is an I/O problem.
   * @throws ReportWriterException if there is a problem writing the report.
   */
  public void write()
      throws IOException, ReportWriterException
  {
    // templates are no longer written here. The templates are written as fully
    // resolved template declarations in the elements instead.
  }
}
