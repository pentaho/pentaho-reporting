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

import org.w3c.css.sac.ConditionalSelector;
import org.w3c.css.sac.Selector;
import org.w3c.css.sac.SimpleSelector;
import org.w3c.css.sac.Condition;

/**
 * @version $Revision$
 * @author  Philippe Le Hegaret
 */
public class ConditionalSelectorImpl implements ConditionalSelector {

    SimpleSelector simpleSelector;
    Condition      condition;

    /**
     * An integer indicating the type of <code>Selector</code>
     */
    public short getSelectorType() {
	return Selector.SAC_CONDITIONAL_SELECTOR;
    }
    

    /**
     * Creates a new ConditionalSelectorImpl
     */
    public ConditionalSelectorImpl(SimpleSelector simpleSelector,
				   Condition condition) {
        this.simpleSelector = simpleSelector;
	this.condition      = condition;
    }
    

    /**
     * Returns the simple selector.
     * <p>The simple selector can't be a <code>ConditionalSelector</code>.</p>
     */    
    public SimpleSelector getSimpleSelector() {
	return simpleSelector;
    }

    /**
     * Returns the condition to be applied on the simple selector.
     */    
    public Condition getCondition() {
	return condition;
    }
}

