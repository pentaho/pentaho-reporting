package org.pentaho.reporting.engine.classic.extensions.datasources.kettle;

import java.util.Locale;

import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.DataFactory;
import org.pentaho.reporting.engine.classic.core.designtime.DataSourcePlugin;
import org.pentaho.reporting.engine.classic.core.metadata.DefaultDataFactoryMetaData;

public class EmbeddedKettleDataFactoryMetaData extends DefaultDataFactoryMetaData
{
  

  public static final String DATA_RETRIEVAL_STEP = "output";
  public static final String DATA_CONFIGURATION_STEP = "input";

  private String displayName;
  private byte[] embedded;

  /**
   * Create a new metadata object for the embedded datafactory.
   * @param name the unique identifier, currently the relative path and file name from the /datasources dir to end
   * @param displayName the display name. Could be the file name as well, or something totally different. Probably
   *                    needs to be internationalized in the production code.
   */
  public EmbeddedKettleDataFactoryMetaData(final String name, final String displayName, byte[] embedded )
  {
    super(name, "org.pentaho.reporting.engine.classic.extensions.datasources.kettle.KettleDataFactoryBundle",
        "",
        false, // expert
        false, // preferred
        false, // hidden
        false, // deprecated,
        true,  // editable
        false, // free-form
        false, // metadata-source
        false, // experimental
        new KettleDataFactoryCore(),
        ClassicEngineBoot.computeVersionId(4, 0, 0));

    this.displayName = displayName;
    this.embedded = embedded;
  }

  @Override
  public String getDisplayConnectionName(DataFactory dataFactory) {
    return null;
  }

  public String getDisplayName(final Locale locale)
  {
    return displayName;
  }

  public String getDescription(final Locale locale)
  {
    return displayName;
  }

  @Override
  public String getGrouping(Locale locale) {
    return getDisplayName(locale);
  }

  public byte[] getBytes(){
    return embedded;
  }
  
  public DataSourcePlugin createEditor()
  {
    final DataSourcePlugin editor = super.createEditor();
    if (editor instanceof EmbeddedKettleDataFactoryEditor == false)
    {
      throw new IllegalStateException(String.valueOf(editor));
    }

    final EmbeddedKettleDataFactoryEditor dataFactoryEditor = (EmbeddedKettleDataFactoryEditor) editor;
    dataFactoryEditor.configure(this.getName());
    return editor;
  }

  protected String getEditorConfigurationKey()
  {
    return "org.pentaho.reporting.engine.classic.metadata.datafactory-editor.org.pentaho.reporting.engine.classic.extensions.datasources.kettle.KettleDataFactory:EmbeddedTransformationDataSourcePlugin";
  }
}
