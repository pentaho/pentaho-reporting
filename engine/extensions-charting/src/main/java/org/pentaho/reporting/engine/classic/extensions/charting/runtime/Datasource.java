/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 - 2026 by Pentaho Canada Inc. : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2030-06-15
 ******************************************************************************/



/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.pentaho.reporting.engine.classic.extensions.charting.runtime;

import java.util.Date;
import java.util.List;

/**
 * Exists for compatibility with CCC
 *
 * @author pdpi
 */
public interface Datasource {

  public String execute();

  public void setParameter( String param, String val );

  public void setParameter( String param, Date val );

  public void setParameter( String param, List val );
}
