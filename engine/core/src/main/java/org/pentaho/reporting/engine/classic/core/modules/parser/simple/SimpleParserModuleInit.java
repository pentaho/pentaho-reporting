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

package org.pentaho.reporting.engine.classic.core.modules.parser.simple;

import org.pentaho.reporting.libraries.base.boot.ModuleInitializeException;
import org.pentaho.reporting.libraries.base.boot.ModuleInitializer;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.libraries.xmlns.parser.ParserEntityResolver;

import java.net.URL;

/**
 * Handles the initalisation of the simple parser module. This contains support for the simple report definition format.
 *
 * @author Thomas Morgner
 */
public class SimpleParserModuleInit implements ModuleInitializer {

  /**
   * the document element tag for the simple report format.
   */
  public static final String SIMPLE_REPORT_TAG = "report";

  /**
   * the Public ID for the simple version of JFreeReport XML definitions.
   */
  public static final String PUBLIC_ID_SIMPLE = "-//JFreeReport//DTD report definition//EN//simple/version 0.8.5";

  /**
   * the Public ID for the simple version of JFreeReport XML definitions (pre 0.8.5).
   */
  private static final String PUBLIC_ID_SIMPLE_084 = "-//JFreeReport//DTD report definition//EN//simple";
  public static final String SYSTEM_ID = "http://jfreereport.sourceforge.net/report-085.dtd";

  /**
   * DefaultConstructor. Does nothing.
   */
  public SimpleParserModuleInit() {
  }

  /**
   * Initializes the simple parser and registers this handler with the parser base module.
   *
   * @throws ModuleInitializeException
   *           if initializing the module failes.
   */
  public void performInit() throws ModuleInitializeException {
    final ParserEntityResolver res = ParserEntityResolver.getDefaultResolver();

    final URL urlReportDTD =
        ObjectUtilities.getResource(
            "org/pentaho/reporting/engine/classic/core/modules/parser/simple/resources/report-085.dtd",
            SimpleParserModuleInit.class );

    res.setDTDLocation( SimpleParserModuleInit.PUBLIC_ID_SIMPLE, SimpleParserModuleInit.SYSTEM_ID, urlReportDTD );
    res.setDTDLocation( SimpleParserModuleInit.PUBLIC_ID_SIMPLE_084, SimpleParserModuleInit.SYSTEM_ID, urlReportDTD );
    res.setDeprecatedDTDMessage( SimpleParserModuleInit.PUBLIC_ID_SIMPLE_084,
        "The given public identifier for the XML document is deprecated. "
            + "Please use the current document type declaration instead: \n" + "  <!DOCTYPE report PUBLIC \n"
            + "      \"-//JFreeReport//DTD report definition//EN//simple/version 0.8.5\"\n"
            + "      \"http://jfreereport.sourceforge.net/report-085.dtd\">" );
  }
}
