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
 * Copyright (c) 2009 Pentaho Corporation..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.wizard.ui.xul.components;

import org.pentaho.reporting.engine.classic.core.designtime.DesignTimeContext;
import org.pentaho.reporting.engine.classic.core.wizard.DataAttributeContext;
import org.pentaho.reporting.engine.classic.wizard.ui.xul.Messages;
import org.pentaho.reporting.engine.classic.wizard.ui.xul.WizardEditorModel;
import org.pentaho.reporting.libraries.base.util.DebugLog;
import org.pentaho.ui.xul.XulDomContainer;
import org.pentaho.ui.xul.XulEventSourceAdapter;
import org.pentaho.ui.xul.XulException;
import org.pentaho.ui.xul.binding.BindingFactory;
import org.pentaho.ui.xul.components.XulImage;
import org.pentaho.ui.xul.components.XulLabel;
import org.pentaho.ui.xul.containers.XulGrid;
import org.pentaho.ui.xul.containers.XulRow;
import org.pentaho.ui.xul.containers.XulRows;
import org.pentaho.ui.xul.dom.Document;

public abstract class AbstractWizardStep extends XulEventSourceAdapter implements WizardStep
{
  protected static final Messages messages = Messages.getInstance();

  public static final String VALID_PROPERTY_NAME = "valid"; //$NON-NLS-1$
  public static final String PREVIEWABLE_PROPERTY_NAME = "previewable"; //$NON-NLS-1$
  public static final String FINISHABLE_PROPERTY_NAME = "finishable"; //$NON-NLS-1$

  public static final String STEP_GRID_ID = "step_grid"; //$NON-NLS-1$
  public static final String STEP_ROWS_ID = "step_rows"; //$NON-NLS-1$

  public static final String XUL_ROW_TYPE = "row"; //$NON-NLS-1$
  public static final String XUL_IMAGE_TYPE = "image";  //$NON-NLS-1$
  public static final String XUL_LABEL_TYPE = "label"; //$NON-NLS-1$

  public static final String STEP_IMAGE_SRC = "images/24x24_chevron_green.png"; //$NON-NLS-1$
  public static final String SPACER_IMAGE_SRC = "images/empty_spacer.png"; //$NON-NLS-1$

  private boolean valid;
  private boolean previewable;
  private boolean finishable;
  private WizardEditorModel editorModel;
  private DesignTimeContext designTimeContext;
  private BindingFactory bf;
  private Document document;
  private XulImage stepImage;
  private XulLabel stepLabel;

  protected AbstractWizardStep()
  {
    super();
  }

  public DataAttributeContext getAttributeContext()
  {
    return editorModel.getAttributeContext();
  }

  public void setEditorModel(final WizardEditorModel editorModel)
  {
    this.editorModel = editorModel;
  }

  public WizardEditorModel getEditorModel()
  {
    return editorModel;
  }

  /**
   * Checks, whether the step is currently valid. This returns false as soon as any of the properties changed.
   *
   * @return true, if the model matches the step's internal state, false otherwise.
   */
  public boolean isValid()
  {
    return valid;
  }

  protected void setValid(final boolean valid)
  {
    final boolean oldValid = this.valid;
    this.valid = valid;

    this.firePropertyChange(VALID_PROPERTY_NAME, oldValid, this.valid);
  }

  public void setPreviewable(final boolean previewable)
  {
    final boolean oldValue = this.previewable;
    this.previewable = previewable;

    this.firePropertyChange(PREVIEWABLE_PROPERTY_NAME, oldValue, this.previewable);
  }

  public boolean isPreviewable()
  {
    return previewable;
  }

  public void setFinishable(final boolean finishable)
  {
    final boolean oldValue = this.finishable;
    this.finishable = finishable;

    this.firePropertyChange(FINISHABLE_PROPERTY_NAME, oldValue, this.finishable);
  }

  public boolean isFinishable()
  {
    return isPreviewable();
  }

  public void setDesignTimeContext(final DesignTimeContext designTimeContext)
  {
    this.designTimeContext = designTimeContext;
  }

  public DesignTimeContext getDesignTimeContext()
  {
    return designTimeContext;
  }

  /* (non-Javadoc)
   * @see org.pentaho.reporting.engine.classic.wizard.ui.xul.components.WizardStep#setBindingFactory(org.pentaho.ui.xul.binding.BindingFactory)
   */
  public void setBindingFactory(final BindingFactory bf)
  {
    this.bf = bf;
  }

  public BindingFactory getBindingFactory()
  {
    return bf;
  }

  public Document getDocument()
  {
    return document;
  }

  public void setDocument(final Document document)
  {
    this.document = document;
  }

  /**
   * @throws XulException
   */
  public void createPresentationComponent(final XulDomContainer mainWizardContainer) throws XulException
  {

    // get the grid itself so we can update it later
    final XulGrid stepGrid = (XulGrid) mainWizardContainer.getDocumentRoot().getElementById(STEP_GRID_ID);

    // grab the rows and add a new row to it
    final XulRows stepRows = (XulRows) mainWizardContainer.getDocumentRoot().getElementById(STEP_ROWS_ID);
    final XulRow stepRow = (XulRow) mainWizardContainer.getDocumentRoot().createElement(XUL_ROW_TYPE);
    stepRows.addChild(stepRow);

    // Create and add the activeImage to the row (goes in the first column)
    stepImage = (XulImage) mainWizardContainer.getDocumentRoot().createElement(XUL_IMAGE_TYPE);
    stepImage.setSrc(STEP_IMAGE_SRC);
    stepImage.setId(this.getStepName());
    stepImage.setVisible(false);
    stepRow.addChild(stepImage);

    // Create and add the text label to the row (goes in the second column)
    stepLabel = (XulLabel) mainWizardContainer.getDocumentRoot().createElement(XUL_LABEL_TYPE);
    stepLabel.setValue(this.getStepName());
    stepLabel.setFlex(1);
    stepLabel.setDisabled(true);
    stepRow.addChild(stepLabel);


    stepGrid.update();
  }

  /* (non-Javadoc)
   * @see org.pentaho.reporting.engine.classic.wizard.ui.xul.components.WizardStep#stepActivating()
   */
  public void stepActivating()
  {
    stepImage.setVisible(true);
    stepLabel.setDisabled(false);
    DebugLog.log("Activating: Step - " + getStepName()); //$NON-NLS-1$
  }

  /* (non-Javadoc)
   * @see org.pentaho.reporting.engine.classic.wizard.ui.xul.components.WizardStep#stepDeactivating()
   */
  public boolean stepDeactivating()
  {
    DebugLog.log("Deactivating: Step - " + getStepName()); //$NON-NLS-1$
    stepImage.setVisible(false);
    stepLabel.setDisabled(true);
    return true;
  }
}
