package org.snipsnap.serialization.rdf;

import java.util.*;
import com.hp.hpl.jena.daml.*;
import java.io.Writer;
import com.hp.hpl.jena.daml.common.*;
import com.hp.hpl.mesa.rdf.jena.model.*;

import org.snipsnap.snip.Snip;
import org.snipsnap.serialization.rdf.vocabulary.*;

public class DAMLSerializer extends RDFSerializerBase {

    public DAMLSerializer(int outputFormat) {
        super(outputFormat);
    }

    protected Model createModel() {
        return new DAMLModelImpl();
    }

    protected void addSingleSnipToModel(Snip snip, Model m) throws RDFException {
        DAMLModelImpl model = (DAMLModelImpl)m;
        String snipName = snip.getName();
        String snipContent = snip.getContent();
        System.out.println("**** SnipDAMLSerializer is serializing snip \"" + snip.getName() + "\" (dummy) ... ");
        // put this into SNIP?
        DAMLClass snipClass = model.createDAMLClass(SNIP.getURI() + "snip");
        DAMLInstance snipInstance = model.createDAMLInstance(snipClass, _uriPrefix + '#' + snip.getName());
        DAMLObjectProperty prop = model.createDAMLObjectProperty(SNIP.getURI() + "name");
        snipInstance.addProperty(prop, snipName);
        prop = model.createDAMLObjectProperty(SNIP.getURI() + "content");
        snipInstance.addProperty(prop, snipContent);

        /*
        DAMLList snipChildren = model.createDAMLList( _uriPrefix + '#' + "snipChildren" );
        // TODO: test if list is empty ...
        Iterator iterator = getChildrenIterator( snip );
        while (iterator.hasNext()) {
            Snip child = (Snip) iterator.next();
            DAMLCommon element = model.createDAMLValue( _uriPrefix + '#' + child.getName(),
                                                        snipClass, null );
            snipChildren.add( element );
        }
        snipChildren.setRestNil();
        snipInstance.addProperty( model.createDAMLObjectProperty( SNIP.getURI()+ "children"), snipChildren);
        // snipResource.addProperty(RDF.predicate, snipChildren);
        */
    }

    protected void writeModel(Model model, Writer writer) throws RDFException {
    }

    protected void recursiveFillModel(Snip snip, Model model, int depth) throws RDFException {
    }
}
