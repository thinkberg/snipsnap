package org.snipsnap.serialization;

import java.io.Writer;
import java.util.Properties;
import snipsnap.api.snip.Snip;

public abstract class Serializer {

    public Serializer(int outputFormat) {
        m_outputFormat = outputFormat;
        m_labelSerializerFactory = new LabelSerializerFactory(outputFormat);
        m_props = new Properties();
    }

    public void configure(Properties props) {
        m_props = props;
    }

    /**
     * serialize the given Snip to the given Writer
     * @param snip      the Snip to serialize (perhaps only the "entry point" or "root snip")
     * @param writer    a Writer to write generated RDF to
     */
    public abstract void serialize(snipsnap.api.snip.Snip snip, Writer writer);

    /**
     * serialize the given Snip to the given Writer
     * @param snip      the Snip to serialize (perhaps only the "entry point" or "root snip")
     * @param writer    a Writer to write generated RDF to
     * @param depth     How many levels of snips shall be serialized. Set to -1 if you want to serialize ALL of them.
     */
    public abstract void serialize(snipsnap.api.snip.Snip snip, Writer writer, int depth);

    public int getOutputFormat() {
        return m_outputFormat;
    }

    protected LabelSerializerFactory getLabelSerializerFactory() {
        return m_labelSerializerFactory;
    }

    private LabelSerializerFactory m_labelSerializerFactory;
    private int m_outputFormat;
    protected Properties m_props;
}
