/*
 * Class that executes implements macros for filters
 *
 * @author stephan
 * @version $Id$
 */

package com.neotis.snip.filter.macro;

import com.neotis.snip.Snip;

public abstract class Macro {
   public abstract String execute(String[] params, String content, Snip snip) throws IllegalArgumentException;
}
