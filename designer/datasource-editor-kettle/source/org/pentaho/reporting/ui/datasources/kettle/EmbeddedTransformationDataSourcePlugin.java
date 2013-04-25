package org.pentaho.reporting.ui.datasources.kettle;

import java.awt.Window;

import javax.swing.JDialog;
import javax.swing.JFrame;

import org.pentaho.reporting.engine.classic.core.DataFactory;
import org.pentaho.reporting.engine.classic.core.designtime.DataFactoryChangeRecorder;
import org.pentaho.reporting.engine.classic.core.designtime.DesignTimeContext;
import org.pentaho.reporting.engine.classic.core.metadata.DataFactoryMetaData;
import org.pentaho.reporting.engine.classic.core.metadata.DataFactoryRegistry;
import org.pentaho.reporting.engine.classic.extensions.datasources.kettle.EmbeddedKettleDataFactoryEditor;
import org.pentaho.reporting.engine.classic.extensions.datasources.kettle.KettleDataFactory;

public class EmbeddedTransformationDataSourcePlugin extends KettleDataSourcePlugin implements EmbeddedKettleDataFactoryEditor
{
  private String metaDataId;

  public EmbeddedTransformationDataSourcePlugin()
  {
  }

  public void configure(final String metaDataId)
  {
    this.metaDataId = metaDataId;
  }

  
  public DataFactory performEdit(final DesignTimeContext context,
                                 final DataFactory dataFactory,
                                 final String queryName,
                                 final DataFactoryChangeRecorder changeRecorder)
  {

    KettleDataFactory factory = (dataFactory == null) ? new KettleDataFactory() : (KettleDataFactory)dataFactory;
    factory.setMetadata(getMetaData());

    final KettleDataSourceDialog editor = createEmbeddedKettleDataSourceDialog(context);
    return editor.performConfiguration(context, factory, queryName);
  
  }

  protected KettleDataSourceDialog createEmbeddedKettleDataSourceDialog(final DesignTimeContext context)
  {
    final KettleDataSourceDialog editor;
    final Window window = context.getParentWindow();
    if (window instanceof JDialog)
    {
      editor = new EmbeddedKettleDataSourceDialog(context, (JDialog) window, metaDataId);
    }
    else if (window instanceof JFrame)
    {
      editor = new EmbeddedKettleDataSourceDialog(context, (JFrame) window, metaDataId);
    }
    else
    {
      editor = new EmbeddedKettleDataSourceDialog(context, metaDataId);
    }
    return editor;
  }

  public DataFactoryMetaData getMetaData()
  {
    return DataFactoryRegistry.getInstance().getMetaData(metaDataId);
  }
}
