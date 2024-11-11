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


package org.pentaho.reporting.ui.datasources.jdbc.ui;

import org.pentaho.ui.database.event.DataHandler;

public class XulDatabaseHandler extends DataHandler
{
  private boolean confirmed;

  public XulDatabaseHandler()
  {
    setName("dataHandler");
  }

  public boolean isConfirmed()
  {
    return confirmed;
  }

  public void setConfirmed(final boolean confirmed)
  {
    this.confirmed = confirmed;
  }

  public void onOK()
  {
    super.onOK();
    confirmed = true;
  }

  public void onCancel()
  {
    super.onCancel();
    confirmed = false;
  }
}
