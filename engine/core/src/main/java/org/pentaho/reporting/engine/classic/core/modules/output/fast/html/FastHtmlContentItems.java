/*
 * This program is free software; you can redistribute it and/or modify it under the
 *  terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
 *  Foundation.
 *
 *  You should have received a copy of the GNU Lesser General Public License along with this
 *  program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 *  or from the Free Software Foundation, Inc.,
 *  51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 *  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *  See the GNU Lesser General Public License for more details.
 *
 *  Copyright (c) 2006 - 2017 Hitachi Vantara..  All rights reserved.
 */

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
