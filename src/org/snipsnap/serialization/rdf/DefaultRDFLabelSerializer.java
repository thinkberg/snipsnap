package org.snipsnap.serialization.rdf;

import java.util.ArrayList;
import java.util.List;
import org.snipsnap.serialization.LabelSerializer;
import org.snipsnap.serialization.LabelContext;
import org.snipsnap.serialization.SerializerFactory;

/** @author gis */
public class DefaultRDFLabelSerializer implements LabelSerializer {

    public int getOutputFormat() {
        return SerializerFactory.RDF_10;
    }

    public List getSupportedLabelTypes() {
        return new ArrayList();
    }

    public void serialize(LabelContext labelContext) {
    }
}
