package org.snipsnap.serialization.rdf;

import java.util.Properties;
import java.util.Iterator;
import java.util.List;

import com.hp.hpl.mesa.rdf.jena.model.Resource;
import com.hp.hpl.mesa.rdf.jena.model.Model;

import snipsnap.api.snip.Snip;
import org.snipsnap.snip.Links;
import snipsnap.api.snip.SnipLink;
import snipsnap.api.label.Label;
import org.snipsnap.snip.attachment.*;
import org.snipsnap.serialization.Serializer;
import snipsnap.api.label.LabelContext;
import java.io.Writer;
import com.hp.hpl.mesa.rdf.jena.model.RDFException;

public abstract class RDFSerializerBase extends Serializer {

    RDFSerializerBase(int outputFormat) {
        super(outputFormat);
    }

    // properties for Namespace and RDF output format:
    protected String _uriPrefix;
    protected String _rdfFormat;

    /**
     * set the namespace for the generated RDF. If not set, a default URI is used (http://snipsnap.org/rdf/default)
     * @param uri the namespace URI to use (must not end with '#' or '/')
     */
    public void setURIPrefix(String uri) {
        // TODO: use URI checker to test if given URI is valid ...
        if (uri.length() > 0 && (uri.endsWith("#") || uri.endsWith("/"))) {
            _uriPrefix = uri.substring(0, uri.length() - 1);
        }
        else
            _uriPrefix = uri;
    }

    /**
     * get the associated namespace URI
     * @return the URI used in generated RDF
     */
    public String getURIPrefix() {
        return _uriPrefix;
    }

    /**
     * set the RDF output format for the generated RDF ("RDF/XML", "RDF/XML-ABBREV", "N-TRIPLE", "N3").
     * If not set, the default "RDF/XML-ABBREV" is used
     * @param format the RDF output format to use
     */
    public void setRDFFormat(String format) {
        // TODO: use of constant values (?) ...
        _rdfFormat = format;
    }

    /** Hook for subclasses to use a custom LabelContext */
    protected LabelContext getLabelContext(snipsnap.api.label.Label label, Snip snip, Model model) {
        m_labelContext.snip = snip;
        m_labelContext.snipResource = getSnipResource(model, snip);
        m_labelContext.uriPrefix = _uriPrefix;
        m_labelContext.model = model;
        m_labelContext.label = label;
        return m_labelContext;
    }

    public void configure(Properties props) {
        super.configure(props);
        _uriPrefix = m_props.getProperty("uri.prefix", "http://snipsnap.org/rdf/default");
        _rdfFormat = m_props.getProperty("rdf.format", "RDF/XML-ABBREV");
    }

    /**
     * serialize the single given Snip to the given Writer
     * @param snip      the Snip to serialize (perhaps only the "entry point" or "root snip")
     * @param writer    a Writer to write generated RDF to
     */
    public final void serialize(snipsnap.api.snip.Snip snip, Writer writer) {
        serialize(snip, writer, 0); // just one level
    }

    /**
     * recursively serialize the given Snip to the given Writer
     * @param snip      the Snip to serialize (perhaps only the "entry point" or "root snip")
     * @param writer    a Writer to write generated RDF to
     * @param depth		How many levels of snips shall be serialized. Set to -1 if you want to serialize ALL of them.
     */
    public final void serialize(Snip startSnip, Writer writer, int depth) {
        if (startSnip == null || writer == null) {
            throw new RuntimeException("snip and writer must not be null!");
        }
        try {
            Model model = createModel();
            recursiveFillModel(startSnip, model, depth);
            writeModel(model, writer); // flush the model down the drain
        }
        catch (RDFException e) {
            e.printStackTrace();
        }
    }

    protected abstract Model createModel();
    protected abstract void recursiveFillModel(snipsnap.api.snip.Snip snip, Model model, int depth) throws RDFException;
    protected abstract void writeModel(Model model, Writer writer) throws RDFException;

    protected final static Iterator getCommentsIterator(Snip snip) {
        List comments = snip.getComments().getComments();
        if (comments == null) {
            return null;
        } else {
        	return comments.iterator();
        }
    }

    protected final static Iterator getLinksIterator(Snip snip) {
        Links snipLinks = snip.getSnipLinks();
        if (snipLinks == null) {
            return null;
        } else {
        	return snipLinks.iterator();
        }
    }

    protected final static Iterator getAttachmentsIterator(Snip snip) {
        Attachments attachments = snip.getAttachments();
        if (attachments == null) {
            return null;
        } else {
        	return attachments.iterator();
        }
    }

    protected Resource getSnipResource(Model model, snipsnap.api.snip.Snip snip) {
        Resource snipResource = null;
        try {
            snipResource = model.getResource(getSnipResURI(snip));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return snipResource;
    }

    protected String getSnipResURI(snipsnap.api.snip.Snip snip) {
        return _uriPrefix + '#' + snip.getNameEncoded();
    }

    protected String getAttachmentURL(Attachment att, snipsnap.api.snip.Snip snip) {
        String url = _uriPrefix.substring(0, _uriPrefix.lastIndexOf("/rdf"));
        url = url.concat("/space/" + SnipLink.encode(snip.getName()) + "/" + SnipLink.encode(att.getName()));
        return url;
    }

    protected RDFLabelContext m_labelContext = new RDFLabelContext();
}
