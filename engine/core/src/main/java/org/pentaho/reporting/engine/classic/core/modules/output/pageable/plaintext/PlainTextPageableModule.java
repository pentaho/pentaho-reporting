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

package org.pentaho.reporting.engine.classic.core.modules.output.pageable.plaintext;

import org.pentaho.reporting.engine.classic.core.metadata.ElementMetaDataParser;
import org.pentaho.reporting.libraries.base.boot.AbstractModule;
import org.pentaho.reporting.libraries.base.boot.ModuleInitializeException;
import org.pentaho.reporting.libraries.base.boot.SubSystem;
import org.pentaho.reporting.libraries.fonts.encoding.EncodingRegistry;

/**
 * The module definition for the plain text pagable export module.
 *
 * @author Thomas Morgner
 */
public class PlainTextPageableModule extends AbstractModule {
  /**
   * The configuration prefix for all properties.
   */
  public static final String CONFIGURATION_PREFIX =
      "org.pentaho.reporting.engine.classic.core.modules.output.pageable.plaintext.";

  /**
   * A default value of the 'text encoding' property key.
   */
  public static final String ENCODING_DEFAULT = EncodingRegistry.getPlatformDefaultEncoding();

  /**
   * The property to define the encoding of the text.
   */
  public static final String ENCODING = CONFIGURATION_PREFIX + "Encoding";
  /**
   * The property to define the lines per inch of the text.
   */
  public static final String LINES_PER_INCH = CONFIGURATION_PREFIX + "LinesPerInch";
  /**
   * The property to define the characters per inch of the text.
   */
  public static final String CHARS_PER_INCH = CONFIGURATION_PREFIX + "CharsPerInch";
  public static final String PLAINTEXT_EXPORT_TYPE = "pageable/text";

  /**
   * DefaultConstructor. Loads the module specification.
   *
   * @throws ModuleInitializeException
   *           if an error occured.
   */
  public PlainTextPageableModule() throws ModuleInitializeException {
    loadModuleInfo();
  }

  /**
   * Initalizes the module. This method is empty.
   *
   * @throws ModuleInitializeException
   *           if an error occured.
   * @see org.pentaho.reporting.libraries.base.boot.Module#initialize(SubSystem)
   */
  public void initialize( final SubSystem subSystem ) throws ModuleInitializeException {
    ElementMetaDataParser
        .initializeOptionalReportProcessTaskMetaData( "org/pentaho/reporting/engine/classic/core/modules/output/pageable/plaintext/meta-report-process-tasks.xml" );
  }
}
