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
