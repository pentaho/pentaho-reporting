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
* Copyright (c) 2008 - 2017 Hitachi Vantara and Contributors.  All rights reserved.
*/

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
