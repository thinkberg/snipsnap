package org.snipsnap.serialization;

import snipsnap.api.label.*;

import java.util.List;

/**
 * @author gis This is the interface all Label handlers have to implement. Whenever a label,
 * attached to a Snip is to be serialized, a LabelSerializer supporting this type
 * of label is used and its @see serialize( Label, Serializer ) method is called.
 * @see Serializer
 */
public interface LabelSerializer {
    /**
     * We don't want RDF/Jena specifics in this interface, so pass the serializer around and let LabelSerializer cast
     * it to specific subclasses
     */
    public void serialize(snipsnap.api.label.LabelContext labelContext);

    /**
     * A LabelSerializer may be able to support more than one type of Labels.
     * Here it returns the List of Label types (i.e. the class names as Strings).
     * @return List A list of strings containing the supported label types
     */
    public List getSupportedLabelTypes();

    /**
     * @return int The supported output-format constant as specified in
     * @see SerializerFactory
     */
    public int getOutputFormat();
}
