package snipsnap.api.label;

import snipsnap.api.snip.Snip;
import snipsnap.api.label.Label;

/**
 * A class holding context information about a Label. Subclasses can add all sorts of things so that
 * LabelSerializers can make use of it.
 *
 * @author gis
 */

public class LabelContext {
  public snipsnap.api.label.Label label;
  public snipsnap.api.snip.Snip snip;

  public LabelContext() {
  }

  public LabelContext(snipsnap.api.snip.Snip snip, snipsnap.api.label.Label label) {
    this.snip = snip;
    this.label = label;
  }

  public snipsnap.api.snip.Snip getSnip() {
    return snip;     
  }

  public Label getLabel() {
    return label;
  }
}
