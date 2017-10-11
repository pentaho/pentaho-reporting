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

package org.pentaho.reporting.engine.classic.core.metadata.builder;

import org.pentaho.reporting.engine.classic.core.metadata.DataFactoryCore;
import org.pentaho.reporting.engine.classic.core.metadata.DefaultDataFactoryCore;

public class DataFactoryMetaDataBuilder extends MetaDataBuilder<DataFactoryMetaDataBuilder> {
  private Class<?> editorClass;
  private boolean editable;
  private boolean freeformQuery;
  private boolean formattingMetadataSource;
  private DataFactoryCore dataFactoryCore;

  public DataFactoryMetaDataBuilder() {
    dataFactoryCore = new DefaultDataFactoryCore();
  }

  public DataFactoryMetaDataBuilder editable( final boolean editable ) {
    this.editable = editable;
    return self();
  }

  public DataFactoryMetaDataBuilder editorClass( final Class<?> editorClass ) {
    this.editorClass = editorClass;
    return self();
  }

  public DataFactoryMetaDataBuilder freeformQuery( final boolean freeformQuery ) {
    this.freeformQuery = freeformQuery;
    return self();
  }

  public DataFactoryMetaDataBuilder formattingMetadataSource( final boolean formattingMetadataSource ) {
    this.formattingMetadataSource = formattingMetadataSource;
    return self();
  }

  public DataFactoryMetaDataBuilder dataFactoryCore( final DataFactoryCore dataFactoryCore ) {
    this.dataFactoryCore = dataFactoryCore;
    return self();
  }

  public Class<?> getEditorClass() {
    return editorClass;
  }

  public boolean isEditable() {
    return editable;
  }

  public boolean isFreeformQuery() {
    return freeformQuery;
  }

  public boolean isFormattingMetadataSource() {
    return formattingMetadataSource;
  }

  public DataFactoryCore getDataFactoryCore() {
    return dataFactoryCore;
  }

  protected DataFactoryMetaDataBuilder self() {
    return this;
  }
}
