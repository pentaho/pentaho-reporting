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
 * Copyright (c) 2001 - 2018 Object Refinery Ltd, Hitachi Vantara and Contributors.  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core;

/**
 * A constant collection holding default attribute names.
 *
 * @author Thomas Morgner
 */
public class AttributeNames {
  private AttributeNames() {
  }

  public static class Xml {
    public static final String NAMESPACE = "http://www.w3.org/XML/1998/namespace";
    public static final String ID = "id";

    private Xml() {
    }
  }

  public static class Designtime {
    public static final String NAMESPACE = "http://reporting.pentaho.org/namespaces/report-designer/2.0";

    public static final String VERTICAL_GUIDE_LINES_ATTRIBUTE = "VerticalGuideLines";
    public static final String HORIZONTAL_GUIDE_LINES_ATTRIBUTE = "HorizontalGuideLines";
    public static final String HIDE_IN_LAYOUT_GUI_ATTRIBUTE = "hideInLayoutGUI";
  }

  public static class Crosstab {
    public static final String NAMESPACE = "http://reporting.pentaho.org/namespaces/engine/attributes/crosstab";
    public static final String DETAIL_MODE = "detail-mode";
    public static final String COLUMN_FIELD = "column-field";
    public static final String ROW_FIELD = "row-field";
    public static final String NORMALIZATION_MODE = "normalization-mode";

    public static final String HEADER_POSITION = "header-position"; // Int, count of group instance for butterfly header
    public static final String SUMMARY_POSITION = "summary-position"; // Begin; End - both row and col groups

    public static final String PRINT_DETAIL_HEADER = "print-detail-header"; // boolean
    public static final String PRINT_COLUMN_TITLE_HEADER = "print-column-title-header"; // boolean
    public static final String PRINT_SUMMARY = "print-summary"; // boolean

    public static final String HEADER_STYLE = "header-style"; // spanned, repeat
    public static final String PADDING_FIELDS = "padding-fields";

    private Crosstab() {
    }
  }

  public static class Html {
    public static final String NAMESPACE = "http://reporting.pentaho.org/namespaces/engine/attributes/html";

    public static final String ONKEYUP = "onkeyup";
    public static final String ONKEYDOWN = "onkeydown";
    public static final String ONKEYPRESSED = "onkeypressed";
    public static final String ONCLICK = "onclick";
    public static final String ONDBLCLICK = "ondblclick";
    public static final String ONMOUSEDOWN = "onmousedown";
    public static final String ONMOUSEUP = "onmouseup";
    public static final String ONMOUSEMOVE = "onmousemove";
    public static final String ONMOUSEOVER = "onmouseover";
    public static final String ONMOUSEOUT = "onmouseout";
    public static final String ONMOUSEENTER = "onmouseenter";

    public static final String NAME = "name";
    public static final String XML_ID = "xml-id";
    public static final String STYLE_CLASS = "class";
    public static final String EXTRA_RAW_CONTENT = "extra-raw-content";
    public static final String EXTRA_RAW_FOOTER_CONTENT = "extra-raw-footer-content";
    public static final String EXTRA_RAW_HEADER_CONTENT = "extra-raw-header-content";
    public static final String SUPPRESS_CONTENT = "surpress-content";
    public static final String IMAGE_MAP_OVERRIDE = "usemap";

    public static final String TITLE = "title";
    public static final String ALT = "alt";

    private Html() {
    }
  }

  public static class Pdf {
    public static final String NAMESPACE = "http://reporting.pentaho.org/namespaces/engine/attributes/pdf";
    public static final String SCRIPT_ACTION = "scriptAction";

    private Pdf() {
    }
  }

  public static class Excel {
    public static final String NAMESPACE = "http://reporting.pentaho.org/namespaces/engine/attributes/excel";
    public static final String PAGE_HEADER_CENTER = "page-header-center";
    public static final String PAGE_FOOTER_CENTER = "page-footer-center";
    public static final String PAGE_HEADER_LEFT = "page-header-left";
    public static final String PAGE_FOOTER_LEFT = "page-footer-left";
    public static final String PAGE_HEADER_RIGHT = "page-header-right";
    public static final String PAGE_FOOTER_RIGHT = "page-footer-right";
    public static final String FIELD_FORMULA = "formula";
    public static final String FREEZING_LEFT_POSITION = "freezing-left-position";
    public static final String FREEZING_TOP_POSITION = "freezing-top-position";

    private Excel() {
    }
  }

  public static class Swing {
    public static final String NAMESPACE = "http://reporting.pentaho.org/namespaces/engine/attributes/swing";
    public static final String ACTION = "action";
    public static final String TOOLTIP = "tooltip";

    private Swing() {
    }
  }

  public static class Internal {
    public static final String NAMESPACE = "http://reporting.pentaho.org/namespaces/engine/attributes/internal";

    public static final String EXCEL_CELL_FORMAT_AUTOCOMPUTE = "excel-cellformat-auto-compute";
    public static final String QUERY = "query";
    public static final String QUERY_LIMIT = "query-limit";
    public static final String QUERY_LIMIT_INHERITANCE = "query-limit-inheritance";
    public static final String QUERY_LIMIT_USER = "query-limit-user";
    public static final String QUERY_TIMEOUT = "query-timeout";
    public static final String FILEFORMAT = "file-format";
    public static final String GENERATED_AGGREGATE_FUNCTION = "auto-generated-aggregate-function";
    public static final String DESIGN_TIME_QUERY_TIMEOUT = "design-time-query-timeout";
    public static final String PREPROCESSORS = "pre-processors";
    public static final String STRUCTURE_FUNCTIONS = "structure-functions";
    public static final String SHARED_CONNECTIONS = "shared-connections";
    public static final String COMAPTIBILITY_LEVEL = "compatibility-level";
    public static final String COMPUTED_STYLE = "computed-style";

    public static final String FAST_EXPORT_DYNAMIC_STASH = "fast-export-dynamic-style-stash";
    public static final String FAST_EXPORT_ELEMENT_STASH = "fast-export-element-style-stash";
    public static final String COMPUTED_SORT_CONSTRAINTS = "computed-sort-constraints";

    private Internal() {
    }
  }

  public static class Table {
    public static final String NAMESPACE = "http://reporting.pentaho.org/namespaces/engine/attributes/table";

    public static final String COLSPAN = "colspan";
    public static final String ROWSPAN = "rowspan";

    private Table() {
    }
  }

  public static class Core {
    public static final String NAMESPACE = "http://reporting.pentaho.org/namespaces/engine/attributes/core";

    public static final String RESOURCE_IDENTIFIER = "resource-identifier";
    public static final String NULL_VALUE = "null-value";
    public static final String FORMATTING_SET_MANUALLY = "formatting-set-manually";
    public static final String MESSAGE_NULL_VALUE = "message-null-value";
    public static final String VALUE = "value";
    public static final String FIELD = "field";

    public static final String FORMAT_STRING = "format-string";
    public static final String NAME = "name";

    public static final String ARC_WIDTH = "arc-width";
    public static final String ARC_HEIGHT = "arc-height";
    public static final String SOURCE = "source";
    public static final String CONTENT_BASE = "content-base";
    public static final String ELEMENT_TYPE = "element-type";
    public static final String PAGE_DEFINITION = "page-definition";
    public static final String GROUP_FIELDS = "group-fields";

    public static final String IMAGE_ENCODING_TYPE = "image-encoding-type";
    public static final String IMAGE_ENCODING_QUALITY = "image-encoding-quality";

    public static final String LOCK_PREFERRED_OUTPUT_TYPE = "lock-preferred-output-type";
    public static final String PREFERRED_OUTPUT_TYPE = "preferred-output-type";

    public static final String BUNDLE = "bundle";
    public static final String TARGET_TYPE = "target-type";
    public static final String RICH_TEXT_TYPE = "rich-text-type";
    public static final String SUBREPORT_ACTIVE = "subreport-active";

    public static final String AUTO_SUBMIT_PARAMETER = "auto-submit-parameter";
    public static final String AUTO_SUBMIT_DEFAULT = "auto-submit-default";
    public static final String SHOW_PARAMETER_UI = "show-parameter-ui";
    public static final String PARAMETER_UI_LAYOUT = "parameter-layout";

    public static final String IMAGE_MAP = "image-map";
    public static final String DATA_CACHE = "data-cache";

    public static final String STYLE_SHEET_REFERENCE = "style-sheet-reference";
    public static final String STYLE_SHEET = "style-sheet";
    public static final String STYLE_CLASS = "style-class";

    public static final String COMPLEX_TEXT = "complex-text";
    public static final String AUTOSORT = "auto-sort";
    public static final String SORT_ORDER = "sort-order";
    public static final String WATERMARK_PRINTED_ON_TOP = "printed-on-top";

    private Core() {
    }

  }

  public static class Wizard {
    public static final String NAMESPACE = "http://reporting.pentaho.org/namespaces/engine/attributes/wizard";

    /**
     * Adds structural information that this field/element is a label for a field. The content of the attribute denotes
     * a field-name that can be looked up in the dataschema. The field's dataschema will be used to configure the label.
     */
    public static final String LABEL_FOR = "label-for";
    /**
     * A marker to indicate that the band carrying this marker should receive the generated content. All defined content
     * in the band will be removed before the generation starts.
     */
    public static final String GENERATED_CONTENT_MARKER = "generated-content-marker";
    public static final String ALLOW_METADATA_STYLING = "allow-metadata-styling";
    public static final String ALLOW_METADATA_ATTRIBUTES = "allow-metadata-attributes";
    public static final String ONLY_SHOW_CHANGING_VALUES = "only-show-changing-values";

    public static final String AGGREGATION_GROUP = "aggregation-group";
    public static final String AGGREGATION_TYPE = "aggregation-type";

    public static final String GRID_STYLE = "grid-style";
    public static final String GRID_WIDTH = "grid-width";
    public static final String GRID_COLOR = "grid-color";

    public static final String PADDING_LEFT = "padding-left";
    public static final String PADDING_RIGHT = "padding-right";
    public static final String PADDING_BOTTOM = "padding-bottom";
    public static final String PADDING_TOP = "padding-top";

    public static final String ENABLE = "enable";

    public static final String ENABLE_STYLE_BOLD = "enable-style-bold";
    public static final String ENABLE_STYLE_ITALICS = "enable-style-italics";
    public static final String ENABLE_STYLE_UNDERLINE = "enable-style-underline";
    public static final String ENABLE_STYLE_STRIKETHROUGH = "enable-style-strikethrough";
    public static final String ENABLE_STYLE_FONTFAMILY = "enable-style-fontfamily";
    public static final String ENABLE_STYLE_FONTSIZE = "enable-style-fontsize";
    public static final String ENABLE_STYLE_COLOR = "enable-style-color";
    public static final String ENABLE_STYLE_BACKGROUND_COLOR = "enable-style-background-color";
    public static final String ENABLE_STYLE_VALIGNMENT = "enable-style-valignment";
    public static final String ENABLE_STYLE_ALIGNMENT = "enable-style-alignment";

    public static final String CACHED_WIZARD_FORMAT_DATA = "CachedWizardFormatData";
    public static final String CACHED_WIZARD_FIELD_DATA = "CachedWizardFieldData";
    public static final String PRESERVE_USER_STYLING = "preserve-user-styling";

    private Wizard() {
    }
  }

  public static class Pentaho {
    public static final String NAMESPACE = "http://reporting.pentaho.org/namespaces/engine/attributes/pentaho";

    private Pentaho() {
    }

    public static final String VISIBLE = "visible";
    public static final String STAGING_MODE = "staging-mode";
    public static final String REPORT_CACHE = "report-cache";
    public static final String CONTENT_CACHE_KEY = "content-cache-key";
    public static final String DYNAMIC_REPORT_CACHE = "dynamic-report-cache";
  }

}
