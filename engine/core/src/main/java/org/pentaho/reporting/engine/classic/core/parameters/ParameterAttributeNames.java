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
 * Copyright (c) 2001 - 2019 Object Refinery Ltd, Hitachi Vantara and Contributors.  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.parameters;

public final class ParameterAttributeNames {

  public static class Swing {
    public static final String NAMESPACE = "http://reporting.pentaho.org/namespaces/engine/parameter-attributes/swing";

    public static final String TOOLTIP = "tooltip";
    public static final String LABEL = "label";
    /**
     * @deprecated Not used and replaced by render-type expansion.
     */
    public static final String RENDER_HINT = "render-hint"; // single-line, multi-line, rich-text

    private Swing() {
    }
  }

  public static class Html {
    public static final String NAMESPACE = "http://reporting.pentaho.org/namespaces/engine/parameter-attributes/html";

    public static final String EXTRA_RAW_CONTENT = "extra-raw-content";
    public static final String XML_ID = "xml-id";
    public static final String STYLE_CLASS = "class";
    public static final String TITLE = "title";

    public static final String ONKEYUP = "onkeyup";
    public static final String ONKEYDOWN = "onkeydown";
    public static final String ONKEYPRESSED = "onkeypressed";
    public static final String ONCLICK = "onclick";
    public static final String ONDBLCLICK = "ondblclick";
    public static final String ONMOUSEDOWN = "onmousedown";
    public static final String ONMOUSEUP = "onmouseup";
    public static final String ONMOUSEMOVE = "onmousemove";
    public static final String ONMOUSEOVER = "onmouseover";

    private Html() {
    }
  }

  public static class Core {
    public static final String NAMESPACE = "http://reporting.pentaho.org/namespaces/engine/parameter-attributes/core";
    public static final String LABEL = "label";
    public static final String LABEL_FORMULA = "label-formula";
    public static final String HIDDEN = "hidden";
    public static final String HIDDEN_FORMULA = "hidden-formula";
    public static final String LAYOUT = "parameter-layout";
    public static final String LAYOUT_HORIZONTAL = "horizontal";
    public static final String LAYOUT_VERTICAL = "vertical";

    public static final String TYPE = "parameter-render-type";
    public static final String TYPE_DROPDOWN = "dropdown";
    public static final String TYPE_LIST = "list";
    public static final String TYPE_RADIO = "radio";
    public static final String TYPE_CHECKBOX = "checkbox";
    public static final String TYPE_TOGGLEBUTTON = "togglebutton";
    public static final String TYPE_TEXTBOX = "textbox";
    public static final String TYPE_DATEPICKER = "datepicker";
    public static final String TYPE_MULTILINE = "multi-line";

    public static final String VISIBLE_ITEMS = "parameter-visible-items";
    public static final String DATA_FORMAT = "data-format";
    public static final String DATA_FORMAT_FORMULA = "data-format-formula";

    public static final String POST_PROCESSOR_FORMULA = "post-processor-formula";
    public static final String DISPLAY_VALUE_FORMULA = "display-value-formula";
    public static final String DEFAULT_VALUE_FORMULA = "default-value-formula";

    public static final String PARAMETER_GROUP = "parameter-group";
    public static final String PARAMETER_GROUP_LABEL = "parameter-group-label";

    public static final String RE_EVALUATE_ON_FAILED_VALUES = "re-evaluate-on-failed-values";
    public static final String AUTOFILL_SELECTION = "autofill-selection";

    public static final String ROLE = "role";
    public static final String ROLE_USER_PARAMETER = "user";
    public static final String ROLE_SYSTEM_PARAMETER = "system";
    public static final String ROLE_SCHEDULE_PARAMETER = "schedule";
    public static final String DEPRECATED = "deprecated";
    public static final String PREFERRED = "preferred";

    /**
     * Either server, client or a fixed timezone text in the standard format "+0800"
     */
    public static final String TIMEZONE = "timezone";

    private Core() {
    }

  }

  private ParameterAttributeNames() {
  }
}
