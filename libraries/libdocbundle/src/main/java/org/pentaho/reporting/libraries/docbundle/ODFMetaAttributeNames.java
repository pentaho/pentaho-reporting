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

package org.pentaho.reporting.libraries.docbundle;

public class ODFMetaAttributeNames {
  private ODFMetaAttributeNames() {
  }

  public static class Office {
    private Office() {
    }

    public static final String NAMESPACE = "urn:oasis:names:tc:opendocument:xmlns:office:1.0";

  }

  public static class Meta {
    private Meta() {
    }

    public static final String NAMESPACE = "urn:oasis:names:tc:opendocument:xmlns:meta:1.0";
    public static final String INITIAL_CREATOR = "initial-creator";
    public static final String CREATION_DATE = "creation-date";
    public static final String PRINTED_BY = "printed-by";
    public static final String PRINT_DATE = "print-date";
    public static final String EDITING_DURATION = "editing-duration";
    public static final String KEYWORDS = "keywords";
    public static final String GENERATOR = "generator";
    public static final String USER_DEFINED = "user-defined";

  }

  public static class DublinCore {
    private DublinCore() {
    }

    public static final String NAMESPACE = "http://purl.org/dc/elements/1.1/";
    public static final String TITLE = "title";
    public static final String DESCRIPTION = "description";
    public static final String CREATOR = "creator";
    public static final String DATE = "date";
    public static final String SUBJECT = "subject";
    public static final String LANGUAGE = "language";
  }
}
