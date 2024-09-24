/*!
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
* Copyright (c) 2002-2017 Hitachi Vantara..  All rights reserved.
*/

package org.pentaho.reporting.engine.classic.extensions.datasources.kettle;

import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.DataFactory;
import org.pentaho.reporting.engine.classic.core.designtime.DataSourcePlugin;
import org.pentaho.reporting.engine.classic.core.metadata.DefaultDataFactoryMetaData;
import org.pentaho.reporting.engine.classic.core.metadata.MaturityLevel;

import java.util.Locale;

public class EmbeddedKettleDataFactoryMetaData extends DefaultDataFactoryMetaData {


  public static final String DATA_RETRIEVAL_STEP = "output";
  public static final String DATA_CONFIGURATION_STEP = "input";

  private String displayName;
  private byte[] embedded;

  /**
   * Create a new metadata object for the embedded datafactory.
   *
   * @param name        the unique identifier, currently the relative path and file name from the /datasources dir to
   *                    end
   * @param displayName the display name. Could be the file name as well, or something totally different. Probably needs
   *                    to be internationalized in the production code.
   */
  public EmbeddedKettleDataFactoryMetaData( final String name, final String displayName, byte[] embedded ) {
    super( name, "org.pentaho.reporting.engine.classic.extensions.datasources.kettle.KettleDataFactoryBundle",
      "",
      false, // expert
      false, // preferred
      false, // hidden
      false, // deprecated,
      true,  // editable
      false, // free-form
      false, // metadata-source
      MaturityLevel.Production, // experimental
      new KettleDataFactoryCore(),
      ClassicEngineBoot.computeVersionId( 4, 0, 0 ) );

    this.displayName = displayName;
    this.embedded = embedded;
  }

  @Override
  public String getDisplayConnectionName( DataFactory dataFactory ) {
    return null;
  }

  public String getDisplayName( final Locale locale ) {
    return displayName;
  }

  public String getDescription( final Locale locale ) {
    return displayName;
  }

  @Override
  public String getGrouping( Locale locale ) {
    return getDisplayName( locale );
  }

  public byte[] getBytes() {
    return embedded;
  }

  public DataSourcePlugin createEditor() {
    final DataSourcePlugin editor = super.createEditor();
    if ( editor instanceof EmbeddedKettleDataFactoryEditor == false ) {
      throw new IllegalStateException( String.valueOf( editor ) );
    }

    final EmbeddedKettleDataFactoryEditor dataFactoryEditor = (EmbeddedKettleDataFactoryEditor) editor;
    dataFactoryEditor.configure( this.getName() );
    return editor;
  }

  protected String getEditorConfigurationKey() {
    return "org.pentaho.reporting.engine.classic.metadata.datafactory-editor.org.pentaho.reporting.engine.classic"
      + ".extensions.datasources.kettle.KettleDataFactory:EmbeddedTransformationDataSourcePlugin";
  }
}
