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
 * Copyright (c) 2000 - 2009 Pentaho Corporation, World Wide Web Consortium,.  All rights reserved.
 */

package org.w3c.flute.parser.selectors;

import org.w3c.css.sac.CombinatorCondition;
import org.w3c.css.sac.Condition;

/**
 * @version $Revision$
 * @author  Philippe Le Hegaret
 */
public class AndConditionImpl implements CombinatorCondition {

    Condition firstCondition;
    Condition secondCondition;

    /**
     * Creates a new AndConditionImpl
     */
    public AndConditionImpl(Condition firstCondition, Condition secondCondition) {
        this.firstCondition = firstCondition;
	this.secondCondition = secondCondition;
    }
    
    /**
     * An integer indicating the type of <code>Condition</code>.
     */    
    public short getConditionType() {
	return Condition.SAC_AND_CONDITION;
    }

    /**
     * Returns the first condition.
     */    
    public Condition getFirstCondition() {
	return firstCondition;
    }

    /**
     * Returns the second condition.
     */    
    public Condition getSecondCondition() {
	return secondCondition;
    }
}
