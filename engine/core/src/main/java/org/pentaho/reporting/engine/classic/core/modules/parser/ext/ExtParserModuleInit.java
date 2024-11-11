/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


package org.pentaho.reporting.engine.classic.core.modules.parser.ext;

import org.pentaho.reporting.libraries.base.boot.ModuleInitializeException;
import org.pentaho.reporting.libraries.base.boot.ModuleInitializer;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.libraries.xmlns.parser.ParserEntityResolver;

import java.net.URL;

/**
 * Performs the module initialization for the extended parser.
 *
 * @author Thomas Morgner
 */
public class ExtParserModuleInit implements ModuleInitializer {
  /**
   * the Public ID for the extensible version of JFreeReport XML definitions.
   */
  public static final String PUBLIC_ID_EXTENDED = "-//JFreeReport//DTD report definition//EN//extended/version 0.8.5";
  /**
   * the Public ID for the old extensible version of JFreeReport XML definitions.
   */
  public static final String PUBLIC_ID_EXTENDED_084 = "-//JFreeReport//DTD report definition//EN//extended";
  public static final String SYSTEM_ID = "http://jfreereport.sourceforge.net/extreport-085.dtd";

  /**
   * Default Constructor.
   */
  public ExtParserModuleInit() {
  }

  /**
   * Initializes the ext-parser and registers it at the parser base module.
   *
   * @throws org.pentaho.reporting.libraries.base.boot.ModuleInitializeException
   *           if an error ocurres.
   */
  public void performInit() throws ModuleInitializeException {
    final ParserEntityResolver res = ParserEntityResolver.getDefaultResolver();

    final URL urlExtReportDTD =
        ObjectUtilities.getResource(
            "org/pentaho/reporting/engine/classic/core/modules/parser/ext/resources/extreport-085.dtd",
            ExtParserModuleInit.class );
    res.setDTDLocation( ExtParserModuleInit.PUBLIC_ID_EXTENDED, ExtParserModuleInit.SYSTEM_ID, urlExtReportDTD );
    res.setDTDLocation( ExtParserModuleInit.PUBLIC_ID_EXTENDED_084, ExtParserModuleInit.SYSTEM_ID, urlExtReportDTD );
    res.setDeprecatedDTDMessage( ExtParserModuleInit.PUBLIC_ID_EXTENDED_084,
        "The given public identifier for the XML document is deprecated. "
            + "Please use the current document type declaration instead: \n" + "  <!DOCTYPE report PUBLIC \n"
            + "      \"-//JFreeReport//DTD report definition//EN//extended/version 0.8.5\"\n"
            + "      \"http://jfreereport.sourceforge.net/extreport-085.dtd\">" );

  }
}
