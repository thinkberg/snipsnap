package org.snipsnap.serialization.rdf;

import java.util.List;
import java.util.ArrayList;
import com.hp.hpl.mesa.rdf.jena.model.*;
import com.hp.hpl.mesa.rdf.jena.vocabulary.RDF;
import com.hp.hpl.mesa.rdf.jena.common.ResourceImpl;

import snipsnap.api.snip.Snip;
import snipsnap.api.label.Label;
import snipsnap.api.label.LabelContext;
import org.snipsnap.serialization.LabelSerializer;
import org.snipsnap.serialization.SerializerFactory;
import org.snipsnap.serialization.rdf.vocabulary.LABEL;
import org.snipsnap.serialization.rdf.vocabulary.SNIP;

/**
 * Implements the serialization of SnipLabels to RDF.
 * @author gis
 */
public class RDFTypeLabelSerializer implements LabelSerializer {

    public void serialize(snipsnap.api.label.LabelContext labelContext) {
        if (!(labelContext instanceof RDFLabelContext))
            throw new RuntimeException("RDFTypeLabelSerializer expects an RDFLabelContext!");
        RDFLabelContext rdfLabelContext = (RDFLabelContext) labelContext;
        snipsnap.api.label.Label label = rdfLabelContext.label;
        Resource snipResource = rdfLabelContext.snipResource;
		try {
            snipResource.addProperty(RDF.type, label.getValue());
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
        s_supportedTypes.add("TypeLabel");
    }
}
