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
