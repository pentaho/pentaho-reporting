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
 * Copyright (c) 2009 Pentaho Corporation.  All rights reserved.
 */

package org.pentaho.reporting.ui.datasources.kettle;

import java.awt.Component;
import java.awt.Container;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.Action;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.event.ListSelectionListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.ReportDataFactoryException;
import org.pentaho.reporting.engine.classic.core.designtime.DesignTimeContext;
import org.pentaho.reporting.engine.classic.core.metadata.DataFactoryMetaData;
import org.pentaho.reporting.engine.classic.core.metadata.DataFactoryRegistry;
import org.pentaho.reporting.engine.classic.extensions.datasources.kettle.EmbeddedKettleTransformationProducer;
import org.pentaho.reporting.engine.classic.extensions.datasources.kettle.KettleDataFactory;
import org.pentaho.reporting.engine.classic.extensions.datasources.kettle.KettleTransformationProducer;
import org.pentaho.reporting.libraries.base.util.StackableRuntimeException;

/**
 * @author Gretchen Moran
 */
public class EmbeddedKettleDataSourceDialog extends KettleDataSourceDialog
{
  private static final long serialVersionUID = 5030572665265231736L;

  private static final Log logger = LogFactory.getLog(EmbeddedKettleDataSourceDialog.class);

  private String datasourceId = null;
  private JPanel datasourcePanel;
  
  /**
   * This listener is registered with the XUL dialog. 
   * 
   * @author gmoran
   *
   */
  protected class PreviewChangeListener implements PropertyChangeListener
  {

    @Override
    public void propertyChange(PropertyChangeEvent evt) 
    {
      
      if (evt.getPropertyName().equals("validate"))
      {
        getPreviewAction().setEnabled((Boolean)evt.getNewValue());
      }
      
    }
    
  }
  
  private class EmbeddedQueryNameListSelectionListener extends QueryNameListSelectionListener
  {
    private EmbeddedQueryNameListSelectionListener()
    {
    }

    protected void handleSelection(final KettleQueryEntry value)
    {
      final DesignTimeContext designTimeContext = getDesignTimeContext();
      final Action editParameterAction = getEditParameterAction();
      try
      {
        setPanelEnabled(true, datasourcePanel);
      
        final KettleEmbeddedQueryEntry selectedQuery = (KettleEmbeddedQueryEntry) value;
        updateQueryName(selectedQuery.getName());
        selectedQuery.refreshQueryUIComponents(datasourcePanel, designTimeContext, new PreviewChangeListener());

        editParameterAction.setEnabled(true);
      }
      catch (Exception e1)
      {
        designTimeContext.error(e1);
        editParameterAction.setEnabled(false);
      }
      catch (Throwable t1)
      {
        designTimeContext.error(new StackableRuntimeException("Fatal error", t1));
        editParameterAction.setEnabled(false);
      }
    }
  }

  public EmbeddedKettleDataSourceDialog(final DesignTimeContext designTimeContext, final JDialog parent, String id)
  {
    super(designTimeContext, parent);
    datasourceId = id;
    setTitle(getDialogTitle());
  }

  public EmbeddedKettleDataSourceDialog(final DesignTimeContext designTimeContext, final JFrame parent, String id)
  {
    super(designTimeContext, parent);
    datasourceId = id;
    setTitle(getDialogTitle());
  }

  public EmbeddedKettleDataSourceDialog(final DesignTimeContext designTimeContext, String id)
  {
    super(designTimeContext);
    datasourceId = id;
    setTitle(getDialogTitle());
  }

  @Override
  protected JPanel createDatasourcePanel() 
  {
    datasourcePanel = new JPanel();
    return datasourcePanel;
  }

  private void refreshQueryUIComponents() throws ReportDataFactoryException
  {
    if (datasourcePanel.getComponentCount() <= 0 )
    {
  
      KettleEmbeddedQueryEntry entry = new KettleEmbeddedQueryEntry(null,datasourceId,null);
      entry.refreshQueryUIComponents(datasourcePanel, getDesignTimeContext(), new PreviewChangeListener());
      setPanelEnabled(false, datasourcePanel);
      
    }
  }
  
  protected String getDialogTitle(){

    if (datasourceId == null)
    {
      return "";
    }

    DataFactoryMetaData meta = DataFactoryRegistry.getInstance().getMetaData(datasourceId);
    String displayName = meta.getDisplayName(getLocale());
    return Messages.getString("KettleEmbeddedDataSourceDialog.Title", displayName);
    
  }

  protected String getDialogId()
  {
    return "EmbeddedKettleDataSourceDialog";
  }

  public KettleDataFactory performConfiguration(DesignTimeContext context, final KettleDataFactory dataFactory,
                                                final String queryName)
  {
    loadData(dataFactory, queryName);
    if ((dataFactory == null) || (!dataFactory.queriesAreHomogeneous()))
    {
      // allow caller to render the default dialog... we are done here
      return super.performConfiguration(context, dataFactory, queryName);
    } else
    {
      try {
        
        refreshQueryUIComponents();
        if (performEdit() == false)
        {
          return null;
        }
        
      } catch(Exception e){
        // attempt to fall back to the default dialog...
        return super.performConfiguration(context, dataFactory, queryName);
      }
    }
    
    final KettleDataFactory kettleDataFactory = new KettleDataFactory();
    kettleDataFactory.setMetadata(dataFactory.getMetaData());
    for (final KettleQueryEntry queryEntry: getQueryEntries())
    {
      final KettleTransformationProducer producer = queryEntry.createProducer();
      kettleDataFactory.setQuery(queryEntry.getName(), producer);
    }

    return kettleDataFactory;

  }
  
  protected KettleQueryEntry createQueryEntry(String queryName, KettleTransformationProducer producer)
  {
    KettleQueryEntry entry = null;
    
    if (datasourceId == null)
    {
      entry = new KettleQueryEntry(queryName);
    }
    else
    {
      byte[] raw = null;
      if ((producer != null) && (producer instanceof EmbeddedKettleTransformationProducer))
      {
        EmbeddedKettleTransformationProducer prod = (EmbeddedKettleTransformationProducer) producer;
        raw = prod.getTransformationRaw();
      }
      
      entry = new KettleEmbeddedQueryEntry(queryName, datasourceId, raw);
    }
    return entry;
  }
  
  protected ListSelectionListener getQueryNameListener()
  {
    return new EmbeddedQueryNameListSelectionListener();
  }
  
  /**
   * This method makes it possible to control any panel that gets rendered via XUL, without 
   * having to create hooks or listeners into the XUL dialog. The presence of a query object 
   * dictates whether the panel should be enabled or disabled.
   * 
   * @param enable enable/disable the configuration panel
   * @param c 
   */
  private void setPanelEnabled(boolean enable, Component c)
  {
    if (null == c)
    {
        return;
    }
        
    Container container = null;
    if (c instanceof Container)
    {
      container = (Container)c;
    }
    
    if (container != null)
    {
      Component[] components = container.getComponents();
      for (int i = 0; i < container.getComponentCount(); i++) 
      {
        Component component = components[i];
        setPanelEnabled(enable, component);
      }
      
    }
    c.setEnabled(enable);
  }

  @Override
  protected void clearComponents() {
    final KettleEmbeddedQueryEntry kettleQueryEntry = (KettleEmbeddedQueryEntry) getSelectedQuery();
    kettleQueryEntry.clear();
    super.clearComponents();
    
  }
  
  
}
