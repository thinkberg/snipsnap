package org.snipsnap.serialization.rdf;

import java.util.List;
import java.util.ArrayList;
import com.hp.hpl.mesa.rdf.jena.model.*;
import com.hp.hpl.mesa.rdf.jena.vocabulary.RDF;
import com.hp.hpl.mesa.rdf.jena.common.ResourceImpl;

import org.snipsnap.snip.Snip;
import org.snipsnap.snip.label.Label;
import org.snipsnap.serialization.LabelContext;
import org.snipsnap.serialization.LabelSerializer;
import org.snipsnap.serialization.SerializerFactory;
import org.snipsnap.serialization.rdf.vocabulary.LABEL;
import org.snipsnap.serialization.rdf.vocabulary.SNIP;

/**
 * Implements the serialization of SnipLabels to RDF.
 * @author gis
 */
public class RDFSnipLabelSerializer implements LabelSerializer {

    private Bag labelsBag = null;

    public void serialize(LabelContext labelContext) {
        if (!(labelContext instanceof RDFLabelContext))
            throw new RuntimeException("RDFSnipLabelSerializer expects an RDFLabelContext!");
        RDFLabelContext rdfLabelContext = (RDFLabelContext) labelContext;
        Model model = rdfLabelContext.model;
        Label label = rdfLabelContext.label;
        Snip snip = rdfLabelContext.snip;
        Resource snipResource = rdfLabelContext.snipResource;
		try {
			if (labelsBag == null) {
        		labelsBag = model.createBag();
                snipResource.addProperty(LABEL.snipLabels, labelsBag);
			}
			Resource labelResource = model.createResource(label.getName());
			labelResource.addProperty(RDF.type, LABEL.SnipLabel);
			labelResource.addProperty(SNIP.value, new ResourceImpl(rdfLabelContext.uriPrefix + "#" + label.getValue()));
			labelsBag.add(labelResource);
        } catch (RDFException re) {
            re.printStackTrace();
        }
	}

    public List getSupportedLabelTypes() {
        return s_supportedTypes;
    }

    public int getOutputFormat() {
        return SerializerFactory.RDF_10;
    }

    private static List s_supportedTypes;

    static {
        s_supportedTypes = new ArrayList();
        s_supportedTypes.add("SnipLabel");
    }
}
