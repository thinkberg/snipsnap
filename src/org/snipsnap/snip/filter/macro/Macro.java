/*
 * Class that executes implements macros for filters
 *
 * @author stephan
 * @version $Id$
 */

package com.neotis.snip.filter.macro;

public abstract class Macro {
   public abstract String execute(String[] params, String content) throws IllegalArgumentException;
}
