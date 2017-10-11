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

package org.pentaho.reporting.designer.core.editor.drilldown.model;

import org.pentaho.reporting.libraries.base.util.DebugLog;
import org.pentaho.reporting.libraries.base.util.StringUtils;
import org.pentaho.reporting.libraries.formula.lvalues.DataTable;
import org.pentaho.reporting.libraries.formula.lvalues.FormulaFunction;
import org.pentaho.reporting.libraries.formula.lvalues.LValue;
import org.pentaho.reporting.libraries.formula.lvalues.StaticValue;
import org.pentaho.reporting.libraries.formula.parser.FormulaParser;
import org.pentaho.reporting.libraries.formula.util.FormulaUtil;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;

public class DrillDownModel {
  public static final String DRILL_DOWN_CONFIG_PROPERTY = "drillDownConfig";
  public static final String DRILL_DOWN_PATH_PROPERTY = "drillDownPath";
  public static final String DRILL_DOWN_PARAMETER_PROPERTY = "drillDownParameter";
  public static final String TOOLTIP_FORMULA_PROPERTY = "tooltipFormula";
  public static final String TARGET_FORMULA_PROPERTY = "targetFormula";
  private static final DrillDownParameter[] EMPTY_PARAMS = new DrillDownParameter[ 0 ];

  private PropertyChangeSupport propertyChangeSupport;
  private String drillDownConfig;
  private String drillDownPath;
  private DrillDownParameter[] drillDownParameters;
  private String tooltipFormula;
  private String targetFormula;
  private boolean limitedEditor;

  public DrillDownModel() {
    this.propertyChangeSupport = new PropertyChangeSupport( this );
    this.drillDownParameters = new DrillDownParameter[ 0 ];
  }

  public void addPropertyChangeListener( final PropertyChangeListener listener ) {
    propertyChangeSupport.addPropertyChangeListener( listener );
  }

  public void removePropertyChangeListener( final PropertyChangeListener listener ) {
    propertyChangeSupport.removePropertyChangeListener( listener );
  }

  public void addPropertyChangeListener( final String propertyName, final PropertyChangeListener listener ) {
    propertyChangeSupport.addPropertyChangeListener( propertyName, listener );
  }

  public void removePropertyChangeListener( final String propertyName, final PropertyChangeListener listener ) {
    propertyChangeSupport.removePropertyChangeListener( propertyName, listener );
  }

  public void firePropertyChange( final String propertyName, final Object oldValue, final Object newValue ) {
    propertyChangeSupport.firePropertyChange( propertyName, oldValue, newValue );
  }

  public boolean isLimitedEditor() {
    return limitedEditor;
  }

  public void setLimitedEditor( final boolean limitedEditor ) {
    final boolean oldLimitedEditor = this.limitedEditor;
    this.limitedEditor = limitedEditor;
    firePropertyChange( "limitedEditor", oldLimitedEditor, limitedEditor );
  }

  public void setDrillDownConfig( final String drillDownConfig ) {
    final String oldValue = this.drillDownConfig;
    this.drillDownConfig = drillDownConfig;
    firePropertyChange( DRILL_DOWN_CONFIG_PROPERTY, oldValue, drillDownConfig );
  }

  public String getDrillDownConfig() {
    return drillDownConfig;
  }

  public String getDrillDownPath() {
    return drillDownPath;
  }

  public void setDrillDownPath( final String drillDownPath ) {
    final String oldValue = this.drillDownPath;
    this.drillDownPath = drillDownPath;
    firePropertyChange( DRILL_DOWN_PATH_PROPERTY, oldValue, drillDownPath );
  }

  public DrillDownParameter[] getDrillDownParameter() {
    return drillDownParameters;
  }

  public void setDrillDownParameter( final DrillDownParameter[] drillDownParameters ) {
    final DrillDownParameter[] oldValue = this.drillDownParameters;
    this.drillDownParameters = drillDownParameters.clone();
    firePropertyChange( DRILL_DOWN_PARAMETER_PROPERTY, oldValue, drillDownParameters );
  }

  public String getTooltipFormula() {
    return tooltipFormula;
  }

  public void setTooltipFormula( final String tooltipFormula ) {
    final String oldTooltip = this.tooltipFormula;
    this.tooltipFormula = tooltipFormula;
    firePropertyChange( TOOLTIP_FORMULA_PROPERTY, oldTooltip, tooltipFormula );
  }

  public String getTargetFormula() {
    return targetFormula;
  }

  public void setTargetFormula( final String targetFormula ) {
    final String oldTarget = this.targetFormula;
    this.targetFormula = targetFormula;
    firePropertyChange( TARGET_FORMULA_PROPERTY, oldTarget, targetFormula );
  }

  public void refresh() {
    firePropertyChange( DRILL_DOWN_PATH_PROPERTY, null, drillDownPath );
    firePropertyChange( DRILL_DOWN_CONFIG_PROPERTY, null, drillDownConfig );
    firePropertyChange( DRILL_DOWN_PARAMETER_PROPERTY, null, drillDownParameters );
    firePropertyChange( TOOLTIP_FORMULA_PROPERTY, null, tooltipFormula );
    firePropertyChange( TARGET_FORMULA_PROPERTY, null, targetFormula );
  }

  public void clear() {
    setDrillDownConfig( null );
    setDrillDownPath( null );
    setDrillDownParameter( new DrillDownParameter[ 0 ] );
  }

  public boolean initializeFromFormula( final String formulaWithPrefix,
                                        final boolean formulaFragment ) {
    clear();
    if ( StringUtils.isEmpty( formulaWithPrefix, true ) == false ) {
      final String formula;
      if ( formulaFragment ) {
        formula = formulaWithPrefix;
      } else {
        formula = FormulaUtil.extractFormula( formulaWithPrefix );
      }
      try {
        final FormulaParser parser = new FormulaParser();
        final LValue value = parser.parse( formula );
        if ( value instanceof FormulaFunction ) {
          final FormulaFunction function = (FormulaFunction) value;
          if ( "DRILLDOWN".equals( function.getFunctionName() ) ) { // NON-NLS
            updateModelFromFunction( function );
            return true;
          }
        }
        DebugLog.log( "Fall through on formula " + formula ); //NON-NLS
      } catch ( Exception e ) {
        // plain value ..
        DebugLog.log( "Failed with formula " + formulaWithPrefix, e ); //NON-NLS
      }
    } else {
      DebugLog.log( "formula is empty " + formulaWithPrefix ); //NON-NLS
    }
    return false;
  }

  public String getResultDrillDownFormula( final boolean formulaFragment ) {
    final String formula = getDrillDownFormula();
    if ( formula == null ) {
      return null;
    }
    if ( formulaFragment ) {
      return formula.substring( 1 );
    }
    return formula;
  }

  public String getDrillDownFormula() {
    if ( StringUtils.isEmpty( getDrillDownConfig() ) ) {
      return null;
    }

    final DrillDownParameter[] downParameters = getDrillDownParameter();
    if ( StringUtils.isEmpty( getDrillDownPath() ) && downParameters.length == 0 ) {
      return null;
    }

    final StringBuilder builder = new StringBuilder();
    builder.append( "=DRILLDOWN(" ); // NON-NLS
    builder.append( FormulaUtil.quoteString( getDrillDownConfig() ) );
    builder.append( "; " ); // NON-NLS
    if ( StringUtils.isEmpty( getDrillDownPath() ) ) {
      builder.append( "NA()" ); // NON-NLS
    } else {
      builder.append( FormulaUtil.quoteString( getDrillDownPath() ) );
    }
    builder.append( "; {" ); // NON-NLS

    for ( int i = 0; i < downParameters.length; i++ ) {
      if ( i > 0 ) {
        builder.append( " | " ); // NON-NLS
      }

      final DrillDownParameter downParameter = downParameters[ i ];
      final String paramName = downParameter.getName();
      if ( StringUtils.isEmpty( paramName ) ) {
        builder.append( "NA()" ); // NON-NLS
      } else {
        builder.append( FormulaUtil.quoteString( paramName ) );
      }
      builder.append( "; " ); // NON-NLS
      final String formulaFragment = downParameter.getFormulaFragment();
      if ( StringUtils.isEmpty( formulaFragment ) ) {
        builder.append( "NA()" );
        continue;
      }

      builder.append( formulaFragment );
    }
    builder.append( "})" ); // NON-NLS
    return builder.toString();
  }

  private void updateModelFromFunction( final FormulaFunction function ) {
    final LValue[] lValues = function.getChildValues();
    if ( lValues.length == 0 ) {
      return;
    }

    final LValue configValue = lValues[ 0 ];
    final String configText = extractStringValue( configValue );
    final String pathText;
    if ( lValues.length > 1 ) {
      final LValue pathValue = lValues[ 1 ];
      pathText = extractStringValue( pathValue );
    } else {
      pathText = null;
    }

    final DrillDownParameter[] parameters;
    if ( lValues.length == 3 ) {
      final LValue dataValue = lValues[ 2 ];
      if ( dataValue instanceof DataTable ) {
        final ArrayList<DrillDownParameter> values = new ArrayList<DrillDownParameter>();
        final DataTable paramsStaticValue = (DataTable) dataValue;
        final int colCount = paramsStaticValue.getColumnCount();
        final int rowCount = paramsStaticValue.getRowCount();
        for ( int row = 0; row < rowCount; row++ ) {
          if ( colCount == 0 ) {
            continue;
          }
          final LValue parameterNameValue = paramsStaticValue.getValueAt( row, 0 );
          final String parameterName = extractStringValue( parameterNameValue );
          final String parameterText;
          if ( colCount > 1 ) {
            final LValue parameterTextValue = paramsStaticValue.getValueAt( row, 1 );
            if ( parameterTextValue != null ) {
              parameterText = parameterTextValue.toString();
            } else {
              parameterText = null;
            }
          } else {
            parameterText = null;
          }

          if ( parameterName != null ) {
            values.add( new DrillDownParameter( parameterName, parameterText ) );
          }
        }
        parameters = values.toArray( new DrillDownParameter[ values.size() ] );
      } else {
        parameters = EMPTY_PARAMS;
      }
    } else {
      parameters = EMPTY_PARAMS;
    }

    setDrillDownConfig( configText );
    setDrillDownPath( pathText );
    setDrillDownParameter( parameters );
  }

  private String extractStringValue( final LValue pathValue ) {
    final String pathText;
    if ( pathValue instanceof StaticValue ) {
      // the configuration cannot be computed, if we are expected to successfully edit it.
      final StaticValue pathStaticValue = (StaticValue) pathValue;
      pathText = String.valueOf( pathStaticValue.getValue() );
    } else {
      pathText = null;
    }
    return pathText;
  }

}
