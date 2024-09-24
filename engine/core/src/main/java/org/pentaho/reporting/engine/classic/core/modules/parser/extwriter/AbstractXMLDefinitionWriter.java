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

import org.pentaho.reporting.engine.classic.core.AbstractReportDefinition;
import org.pentaho.reporting.libraries.xmlns.writer.XmlWriter;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;

/**
 * A base class for writer classes for the JFreeReport XML report files.
 *
 * @author Thomas Morgner
 */
public abstract class AbstractXMLDefinitionWriter {
  /**
   * the document element tag for the extended report format.
   */
  public static final String REPORT_DEFINITION_TAG = "report-definition";

  /**
   * The parser config tag name.
   */
  public static final String PARSER_CONFIG_TAG = "parser-config";

  /**
   * The report config tag name.
   */
  public static final String REPORT_CONFIG_TAG = "report-config";

  /**
   * The styles tag name.
   */
  public static final String STYLES_TAG = "styles";

  /**
   * The templates tag name.
   */
  public static final String TEMPLATES_TAG = "templates";

  /**
   * The functions tag name.
   */
  public static final String FUNCTIONS_TAG = "functions";

  /**
   * The text for the 'compound-object' tag.
   */
  public static final String COMPOUND_OBJECT_TAG = "compound-object";

  /**
   * The text for the 'basic-object' tag.
   */
  public static final String BASIC_OBJECT_TAG = "basic-object";

  /**
   * The datasource tag name.
   */
  public static final String DATASOURCE_TAG = "datasource";

  /**
   * The properties tag name.
   */
  public static final String PROPERTIES_TAG = "properties";

  /**
   * The class attribute name.
   */
  public static final String CLASS_ATTRIBUTE = "class";

  /**
   * The 'configuration' tag name.
   */
  public static final String CONFIGURATION_TAG = "configuration";

  /**
   * The 'output-config' tag name.
   */
  public static final String OUTPUT_TARGET_TAG = "output-config";

  /**
   * Literal text for an XML attribute.
   */
  public static final String WIDTH_ATT = "width";

  /**
   * Literal text for an XML attribute.
   */
  public static final String HEIGHT_ATT = "height";

  /**
   * The 'style' tag name.
   */
  public static final String STYLE_TAG = "style";

  /**
   * The 'compound-key' tag name.
   */
  public static final String COMPOUND_KEY_TAG = "compound-key";

  /**
   * The 'basic-key' tag name.
   */
  public static final String BASIC_KEY_TAG = "basic-key";

  /**
   * The 'extends' tag name.
   */
  public static final String EXTENDS_TAG = "extends";

  /**
   * The template tag.
   */
  public static final String TEMPLATE_TAG = "template";

  /**
   * The 'property' tag name.
   */
  public static final String PROPERTY_TAG = "property";

  /**
   * The 'name' attribute text.
   */
  public static final String NAME_ATTR = "name";

  /**
   * A report writer.
   */
  private final ReportWriterContext reportWriter;
  private final XmlWriter xmlWriter;
  private static final Class[] EMPTY_PARAMETER = new Class[0];

  /**
   * Creates a new writer.
   *
   * @param reportWriter
   *          the report writer.
   * @param xmlWriter
   */
  protected AbstractXMLDefinitionWriter( final ReportWriterContext reportWriter, final XmlWriter xmlWriter ) {
    this.xmlWriter = xmlWriter;
    this.reportWriter = reportWriter;
  }

  /**
   * Returns the report writer.
   *
   * @return The report writer.
   */
  protected ReportWriterContext getReportWriter() {
    return reportWriter;
  }

  protected XmlWriter getXmlWriter() {
    return xmlWriter;
  }

  /**
   * Returns the report.
   *
   * @return The report.
   */
  protected AbstractReportDefinition getReport() {
    return getReportWriter().getReport();
  }

  /**
   * Writes the report definition portion. Every DefinitionWriter handles one or more elements of the JFreeReport object
   * tree, DefinitionWriter traverse the object tree and write the known objects or forward objects to other definition
   * writers.
   *
   * @throws IOException
   *           if there is an I/O problem.
   * @throws ReportWriterException
   *           if the report serialisation failed.
   */
  public abstract void write() throws IOException, ReportWriterException;

  protected static boolean hasPublicDefaultConstructor( final Class c ) {
    try {
      final Constructor constructor = c.getConstructor( EMPTY_PARAMETER );
      if ( Modifier.isPublic( constructor.getModifiers() ) ) {
        return true;
      }
      return false;
    } catch ( NoSuchMethodException e ) {
      return false;
    }
  }
}
