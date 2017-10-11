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

package org.pentaho.reporting.libraries.docbundle.metadata;

import java.io.Serializable;

public class OfficeDocumentStatistic implements Serializable {
  private int tableCount;
  private int imageCount;
  private int objectCount;
  private int pageCount;
  private int paragraphCount;
  private int wordCount;
  private int characterCount;

  public OfficeDocumentStatistic() {
  }

  public int getTableCount() {
    return tableCount;
  }

  public void setTableCount( final int tableCount ) {
    this.tableCount = tableCount;
  }

  public int getImageCount() {
    return imageCount;
  }

  public void setImageCount( final int imageCount ) {
    this.imageCount = imageCount;
  }

  public int getObjectCount() {
    return objectCount;
  }

  public void setObjectCount( final int objectCount ) {
    this.objectCount = objectCount;
  }

  public int getPageCount() {
    return pageCount;
  }

  public void setPageCount( final int pageCount ) {
    this.pageCount = pageCount;
  }

  public int getParagraphCount() {
    return paragraphCount;
  }

  public void setParagraphCount( final int paragraphCount ) {
    this.paragraphCount = paragraphCount;
  }

  public int getWordCount() {
    return wordCount;
  }

  public void setWordCount( final int wordCount ) {
    this.wordCount = wordCount;
  }

  public int getCharacterCount() {
    return characterCount;
  }

  public void setCharacterCount( final int characterCount ) {
    this.characterCount = characterCount;
  }
}
