package org.pentaho.reporting.ui.datasources.cda;

import java.awt.Window;
import javax.swing.JDialog;
import javax.swing.JFrame;

import org.pentaho.reporting.engine.classic.core.DataFactory;
import org.pentaho.reporting.engine.classic.core.designtime.DataFactoryChangeRecorder;
import org.pentaho.reporting.engine.classic.core.designtime.DataSourcePlugin;
import org.pentaho.reporting.engine.classic.core.designtime.DesignTimeContext;
import org.pentaho.reporting.engine.classic.core.metadata.DataFactoryMetaData;
import org.pentaho.reporting.engine.classic.core.metadata.DataFactoryRegistry;
import org.pentaho.reporting.engine.classic.extensions.datasources.cda.CdaDataFactory;

public class CdaDataSourcePlugin implements DataSourcePlugin
{
  public CdaDataSourcePlugin()
  {
  }

  public DataFactory performEdit(final DesignTimeContext context,
                                 final DataFactory input,
                                 final String queryName,
                                 final DataFactoryChangeRecorder changeRecorder)
  {
    final CdaDataSourceEditor editor;
    final Window window = context.getParentWindow();
    if (window instanceof JDialog)
    {
      editor = new CdaDataSourceEditor(context, (JDialog) window);
    }
    else if (window instanceof JFrame)
    {
      editor = new CdaDataSourceEditor(context, (JFrame) window);
    }
    else
    {
      editor = new CdaDataSourceEditor(context);
    }
    return editor.performConfiguration((CdaDataFactory) input, queryName);
  }

  public boolean canHandle(final DataFactory dataFactory)
  {
    return dataFactory instanceof CdaDataFactory;
  }

  public DataFactoryMetaData getMetaData()
  {
    return DataFactoryRegistry.getInstance().getMetaData(CdaDataFactory.class.getName());
  }
}
