/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2028-08-13
 ******************************************************************************/


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
