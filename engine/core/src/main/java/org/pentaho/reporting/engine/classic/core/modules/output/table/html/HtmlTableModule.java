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
 * Copyright (c) 2001 - 2024 Object Refinery Ltd, Hitachi Vantara and Contributors..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.modules.output.table.html;

import org.pentaho.reporting.engine.classic.core.metadata.ElementMetaDataParser;
import org.pentaho.reporting.libraries.base.boot.AbstractModule;
import org.pentaho.reporting.libraries.base.boot.ModuleInitializeException;
import org.pentaho.reporting.libraries.base.boot.SubSystem;

/**
 * The module definition for the Html table export module.
 *
 * @author Thomas Morgner
 */
public class HtmlTableModule extends AbstractModule {
  public static final String USE_TABLE_LAYOUT_FIXED =
      "org.pentaho.reporting.engine.classic.core.modules.output.table.html.UseTableLayoutFixed";
  /**
   * the fileencoding for the main html file.
   */
  public static final String ENCODING = "org.pentaho.reporting.engine.classic.core.modules.output.table.html.Encoding";
  /**
   * a default value for the fileencoding of the main html file.
   */
  public static final String ENCODING_DEFAULT = "UTF-8";

  /**
   * The property key to define whether to build a html body fragment.
   */
  public static final String BODY_FRAGMENT =
      "org.pentaho.reporting.engine.classic.core.modules.output.table.html.BodyFragment";

  /**
   * Key for allowing raw link targets (must contain the value "true" to be considered <code>true</code>
   */
  public static final String EMPTY_CELLS_USE_CSS =
      "org.pentaho.reporting.engine.classic.core.modules.output.table.html.EmptyCellsUseCSS";

  /**
   * Key for specifying whether common borders of all cells are replicated on the table-row elements. This prevents some
   * layouting errors in Mozilla.
   */
  public static final String TABLE_ROW_BORDER_DEFINITION =
      "org.pentaho.reporting.engine.classic.core.modules.output.table.html.TableRowBorderDefinition";

  /**
   * Key for specifying whether columns widths should be given in percentages instead of absolute widths. This way, the
   * generated HTML page will consume all space that is available.
   */
  public static final String PROPORTIONAL_COLUMN_WIDTHS =
      "org.pentaho.reporting.engine.classic.core.modules.output.table.html.ProportionalColumnWidths";

  /**
   * Key for forcing a copy of external images (must contain the value "true" to be considered <code>true</code>
   */
  public static final String COPY_EXTERNAL_IMAGES =
      "org.pentaho.reporting.engine.classic.core.modules.output.table.html.CopyExternalImages";

  /**
   * Key for specifying that style information should be inlined instead of externalized (must contain the value "true"
   * to be considered <code>true</code>
   */
  public static final String INLINE_STYLE =
      "org.pentaho.reporting.engine.classic.core.modules.output.table.html.InlineStyles";

  /**
   * Key for specifying that style information should be externalized instead of inline (must contain the value "true"
   * to be considered <code>true</code>
   */
  public static final String EXTERNALIZE_STYLE =
      "org.pentaho.reporting.engine.classic.core.modules.output.table.html.ExternalStyle";

  /**
   * Key for forcing buffer writing (must contain the value "true" to be considered <code>true</code>
   */
  public static final String FORCE_BUFFER_WRITING =
      "org.pentaho.reporting.engine.classic.core.modules.output.table.html.ForceBufferedWriting";

  /**
   * Key for allowing raw link targets (must contain the value "true" to be considered <code>true</code>
   */
  public static final String ALLOW_RAW_LINK_TARGETS =
      "org.pentaho.reporting.engine.classic.core.modules.output.table.html.AllowRawLinkTargets";

  /**
   * Key for specifying the HTML TITLE
   */
  public static final String TITLE = "org.pentaho.reporting.engine.classic.core.modules.output.table.html.Title";

  /**
   * Key for specifying the Subject in the HTML header
   */
  public static final String SUBJECT = "org.pentaho.reporting.engine.classic.core.modules.output.table.html.Subject";

  /**
   * Key for specifying the Author in the HTML header
   */
  public static final String AUTHOR = "org.pentaho.reporting.engine.classic.core.modules.output.table.html.Author";

  /**
   * Key for specifying the Keywords in the HTML header
   */
  public static final String KEYWORDS = "org.pentaho.reporting.engine.classic.core.modules.output.table.html.Keywords";

  /**
   * Key for specifying that images should be embedded as BASE64 Objects in HTML reports (must contain the value "true"
   * to be considered <code>true</code>
   */
  public static final String BASE64_IMAGES =
    "org.pentaho.reporting.engine.classic.core.modules.output.table.html.Base64Images";

  public static final String TABLE_HTML_STREAM_EXPORT_TYPE = "table/html;page-mode=stream";
  public static final String TABLE_HTML_FLOW_EXPORT_TYPE = "table/html;page-mode=flow";
  public static final String TABLE_HTML_PAGE_EXPORT_TYPE = "table/html;page-mode=page";
  public static final String ZIP_HTML_EXPORT_TYPE = "application/zip;content=table/html;page-mode=flow";

  /**
   * DefaultConstructor. Loads the module specification.
   *
   * @throws org.pentaho.reporting.libraries.base.boot.ModuleInitializeException
   *           if an error occured.
   */
  public HtmlTableModule() throws ModuleInitializeException {
    loadModuleInfo();
  }

  /**
   * Initializes the module. Use this method to perform all initial setup operations. This method is called only once in
   * a modules lifetime. If the initializing cannot be completed, throw a ModuleInitializeException to indicate the
   * error,. The module will not be available to the system.
   *
   * @param subSystem
   *          the subSystem.
   * @throws ModuleInitializeException
   *           if an error ocurred while initializing the module.
   */
  public void initialize( final SubSystem subSystem ) throws ModuleInitializeException {
    ElementMetaDataParser
        .initializeOptionalReportProcessTaskMetaData( "org/pentaho/reporting/engine/classic/core/modules/output/table/html/meta-report-process-tasks.xml" );
  }
}
