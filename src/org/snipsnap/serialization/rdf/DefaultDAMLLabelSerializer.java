package org.snipsnap.serialization.rdf;

import java.util.ArrayList;
import java.util.List;
import snipsnap.api.label.LabelContext;
import org.snipsnap.serialization.LabelSerializer;
import org.snipsnap.serialization.SerializerFactory;

/** @author gis */
public class DefaultDAMLLabelSerializer implements LabelSerializer {

    public List getSupportedLabelTypes() {
        return new ArrayList();
    }

    public int getOutputFormat() {
        return SerializerFactory.DAML_OIL;
    }

    public void serialize(LabelContext labelContext) {
    }
}
