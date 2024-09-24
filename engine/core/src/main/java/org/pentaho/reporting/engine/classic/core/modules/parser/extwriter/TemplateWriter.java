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
 * Copyright (c) 2001 - 2013 Object Refinery Ltd, Hitachi Vantara and Contributors..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.modules.parser.extwriter;

import org.pentaho.reporting.engine.classic.core.modules.parser.ext.ExtParserModule;
import org.pentaho.reporting.engine.classic.core.modules.parser.ext.factory.templates.TemplateDescription;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.libraries.xmlns.common.AttributeList;
import org.pentaho.reporting.libraries.xmlns.writer.XmlWriter;
import org.pentaho.reporting.libraries.xmlns.writer.XmlWriterSupport;

import java.io.IOException;
import java.util.Iterator;

/**
 * The template writer writes a single template definition to the xml-definition stream. This writer requires report
 * builder hints to be present for all templates.
 *
 * @author Thomas Morgner
 */
public class TemplateWriter extends ObjectWriter {
  /**
   * The template that should be written.
   */
  private TemplateDescription template;
  /**
   * The parent of the current template.
   */
  private TemplateDescription parent;

  /**
   * Creates a new template writer.
   *
   * @param reportWriter
   *          the report writer that is used to coordinate the writing.
   * @param indentLevel
   *          the current indention level.
   * @param template
   *          the template that should be written.
   * @param parent
   *          the parent of the template.
   */
  public TemplateWriter( final ReportWriterContext reportWriter, final XmlWriter indentLevel,
      final TemplateDescription template, final TemplateDescription parent ) {
    super( reportWriter, template, indentLevel );
    if ( template == null ) {
      throw new NullPointerException( "Template is null." );
    }
    if ( parent == null ) {
      throw new NullPointerException( "Parent is null." );
    }
    this.parent = parent;
    this.template = template;
  }

  /**
   * Writes the report definition portion. Every DefinitionWriter handles one or more elements of the JFreeReport object
   * tree, DefinitionWriter traverse the object tree and write the known objects or forward objects to other definition
   * writers.
   *
   * @throws java.io.IOException
   *           if there is an I/O problem.
   * @throws ReportWriterException
   *           if the report serialisation failed.
   */
  public void write() throws IOException, ReportWriterException {
    final AttributeList attList = new AttributeList();
    if ( template.getName() != null ) {
      // dont copy the parent name for anonymous templates ...
      if ( template.getName().equals( parent.getName() ) == false ) {
        attList.setAttribute( ExtParserModule.NAMESPACE, "name", template.getName() );
      }
    }
    attList.setAttribute( ExtParserModule.NAMESPACE, "references", parent.getName() );

    boolean tagWritten = false;
    final XmlWriter writer = getXmlWriter();
    final Iterator it = template.getParameterNames();
    while ( it.hasNext() ) {
      final String name = (String) it.next();
      if ( shouldWriteParameter( name ) ) {
        if ( tagWritten == false ) {
          writer.writeTag( ExtParserModule.NAMESPACE, AbstractXMLDefinitionWriter.TEMPLATE_TAG, attList,
              XmlWriterSupport.OPEN );
          tagWritten = true;
        }
        writeParameter( name );
      }
    }
    if ( tagWritten ) {
      writer.writeCloseTag();
    } else {
      writer.writeTag( ExtParserModule.NAMESPACE, AbstractXMLDefinitionWriter.TEMPLATE_TAG, attList,
          XmlWriterSupport.CLOSE );
    }
  }

  /**
   * Tests, whether the given parameter should be written in this template. This will return false, if the parameter is
   * not set, or the parent contains the same value.
   *
   * @param parameterName
   *          the name of the parameter that should be tested
   * @return true, if the parameter should be written, false otherwise.
   */
  private boolean shouldWriteParameter( final String parameterName ) {
    final Object parameterObject = template.getParameter( parameterName );
    if ( parameterObject == null ) {
      // Log.debug ("Should not write: Parameter is null.");
      return false;
    }
    final Object parentObject = parent.getParameter( parameterName );
    if ( ObjectUtilities.equal( parameterObject, parentObject ) ) {
      // Log.debug ("Should not write: Parameter objects are equal.");
      return false;
    }
    return true;
  }
}
