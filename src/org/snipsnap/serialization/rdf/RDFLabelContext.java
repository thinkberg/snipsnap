package org.snipsnap.serialization.rdf;

import com.hp.hpl.mesa.rdf.jena.model.*;
import org.snipsnap.serialization.LabelContext;

/** @author gis */
public class RDFLabelContext extends LabelContext {
    public Model model;
    public Resource snipResource;
    public String uriPrefix;
}
