package org.snipsnap.serialization.rdf;

import com.hp.hpl.mesa.rdf.jena.model.*;
import snipsnap.api.label.LabelContext;

/** @author gis */

public class RDFLabelContext extends snipsnap.api.label.LabelContext {
    public Model model;
    public Resource snipResource;
    public String uriPrefix;
}
