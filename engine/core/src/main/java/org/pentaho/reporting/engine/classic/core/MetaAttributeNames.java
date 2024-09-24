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

package org.pentaho.reporting.engine.classic.core;

public class MetaAttributeNames {
  private MetaAttributeNames() {
  }

  public static class Numeric {
    private Numeric() {
    }

    public static final String NAMESPACE = "http://reporting.pentaho.org/namespaces/engine/meta-attributes/numeric";

    public static final String CURRENCY = "currency";
    public static final String SCALE = "scale";
    public static final String PRECISION = "precision";
    public static final String SIGNED = "signed";
  }

  public static class Database {
    private Database() {
    }

    public static final String NAMESPACE = "http://reporting.pentaho.org/namespaces/engine/meta-attributes/database";

    public static final String TABLE = "table";
    public static final String SCHEMA = "schema";
    public static final String CATALOG = "catalog";
  }

  public static class Formatting {
    private Formatting() {
    }

    public static final String NAMESPACE = "http://reporting.pentaho.org/namespaces/engine/meta-attributes/formatting";

    public static final String DISPLAY_SIZE = "display-size";
    public static final String LABEL = "label";
    public static final String HIDE_DUPLICATE_ITEMS = "hide-duplicate-items";
    public static final String FORMAT = "format";
  }

  public static class Core {

    private Core() {
    }

    public static final String NAMESPACE = "http://reporting.pentaho.org/namespaces/engine/meta-attributes/core";
    /**
     * The field/column name
     */
    public static final String NAME = "name";
    public static final String TYPE = "value-type";

    /**
     * Either "parameter", "expression" or "table"
     */
    public static final String SOURCE = "source";
    public static final String SOURCE_VALUE_TABLE = "table";
    public static final String SOURCE_VALUE_ENVIRONMENT = "environment";
    public static final String SOURCE_VALUE_PARAMETER = "parameter";
    public static final String SOURCE_VALUE_EXPRESSION = "expression";
    /**
     * Either "fact", "dimension", "key" or "attribute"
     */
    public static final String ROLE = "role";
    /**
     * Either "normalized" or "raw"
     */
    public static final String CROSSTAB_MODE = "crosstab-mode";

    public static final String CROSSTAB_VALUE_NORMALIZED = "normalized";
    public static final String CROSSTAB_VALUE_RAW = "raw";

    public static final String INDEXED_COLUMN = "indexed-column";
  }

  public static class Expressions {
    public static final String CLASS = "class";

    public static final String NAMESPACE = "http://reporting.pentaho.org/namespaces/engine/meta-attributes/expressions";

    private Expressions() {
    }

  }

  public static class Parameters {
    public static final String INCLUDE_IN_WIZARD = "include-in-wizard";

    public static final String NAMESPACE = "http://reporting.pentaho.org/namespaces/engine/meta-attributes/parameters";

    private Parameters() {
    }

  }

  public static class Style {
    private Style() {
    }

    public static final String NAMESPACE = "http://reporting.pentaho.org/namespaces/engine/meta-attributes/style";

    public static final String FONTFAMILY = "font-family";
    public static final String FONTSIZE = "font-size";
    public static final String BOLD = "bold";
    public static final String ITALIC = "italic";
    public static final String UNDERLINE = "underline";
    public static final String STRIKETHROUGH = "strikethrough";
    public static final String COLOR = "color";
    public static final String BACKGROUND_COLOR = "background-color";
    public static final String HORIZONTAL_ALIGNMENT = "horizontal-alignment";
    public static final String VERTICAL_ALIGNMENT = "vertical-alignment";

  }
}
