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

    public void setParameter(String param, String val);

    public void setParameter(String param, Date val);

    public void setParameter(String param, List val);
}
