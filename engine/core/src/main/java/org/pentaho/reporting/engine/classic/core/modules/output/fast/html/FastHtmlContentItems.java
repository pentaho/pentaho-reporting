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


package org.pentaho.reporting.engine.classic.core.modules.output.fast.html;

import org.pentaho.reporting.engine.classic.core.modules.output.table.html.URLRewriter;
import org.pentaho.reporting.libraries.repository.ContentLocation;
import org.pentaho.reporting.libraries.repository.NameGenerator;

public class FastHtmlContentItems {
  private ContentLocation dataLocation;
  private NameGenerator dataNameGenerator;
  private ContentLocation contentLocation;
  private NameGenerator contentNameGenerator;
  private URLRewriter urlRewriter;

  public FastHtmlContentItems() {
  }

  public ContentLocation getDataLocation() {
    return dataLocation;
  }

  public void setDataLocation( final ContentLocation dataLocation ) {
    this.dataLocation = dataLocation;
  }

  public NameGenerator getDataNameGenerator() {
    return dataNameGenerator;
  }

  public void setDataNameGenerator( final NameGenerator dataNameGenerator ) {
    this.dataNameGenerator = dataNameGenerator;
  }

  public ContentLocation getContentLocation() {
    return contentLocation;
  }

  public void setContentLocation( final ContentLocation contentLocation ) {
    this.contentLocation = contentLocation;
  }

  public NameGenerator getContentNameGenerator() {
    return contentNameGenerator;
  }

  public void setContentNameGenerator( final NameGenerator contentNameGenerator ) {
    this.contentNameGenerator = contentNameGenerator;
  }

  public URLRewriter getUrlRewriter() {
    return urlRewriter;
  }

  public void setUrlRewriter( final URLRewriter urlRewriter ) {
    this.urlRewriter = urlRewriter;
  }

  public void setContentWriter( final ContentLocation targetRoot, final NameGenerator nameGenerator ) {
    this.contentLocation = targetRoot;
    this.contentNameGenerator = nameGenerator;
  }

  public void setDataWriter( final ContentLocation targetRoot, final NameGenerator nameGenerator ) {
    this.dataLocation = targetRoot;
    this.dataNameGenerator = nameGenerator;
  }
}
