package org.snipsnap.serialization.rdf;

import java.io.Writer;
import java.util.*;

import com.hp.hpl.mesa.rdf.jena.mem.ModelMem;
import com.hp.hpl.mesa.rdf.jena.model.*;
import com.hp.hpl.mesa.rdf.jena.vocabulary.*;
import com.hp.hpl.mesa.rdf.jena.common.*;

import org.snipsnap.snip.Links;
import org.snipsnap.snip.Snip;
import org.snipsnap.snip.SnipLink;
import org.snipsnap.snip.attachment.*;
import org.snipsnap.snip.label.*;
import org.snipsnap.serialization.LabelSerializer;
import org.snipsnap.serialization.LabelSerializerFactory;
import org.snipsnap.serialization.rdf.vocabulary.*;

public class RDFSerializer extends RDFSerializerBase {

  public RDFSerializer(int outputFormat) {
    super(outputFormat);
  }

  /** Hook for subclasses */
  protected Model createModel() {
    return new ModelMem();
  }

  /**
   * Subclasses should only need to reimplement this method to add one single snip to the model,
   * and eventually createModel().
   */
  protected void addSingleSnipToModel(Snip snip, Model model) throws RDFException {
    Resource rootSnipResource = addSnipResource(model, snip);
    // add the Snip's comments to the model:
    rootSnipResource.addProperty(SNIP.comments, addCommentsBag(model, snip));
    // add the Snip's SnipLinks to the model:
    rootSnipResource.addProperty(SNIP.snipLinks, addSnipLinksBag(model, snip));
    // add the Snip's Attachments to the model:
    rootSnipResource.addProperty(SNIP.attachments, addAttachmentsBag(model, snip));
    // add the Snip's Labels to the model:
    addLabelsToModel(snip, model);
  }

  protected final void addLabelsToModel(Snip snip, Model model) {
    LabelSerializerFactory factory = getLabelSerializerFactory();
    Labels labels = snip.getLabels();
    Iterator it = labels.getAll().iterator();
    Map serializers = new HashMap();
    while (it.hasNext()) {
      Label label = (Label) it.next();
      String labelType = label.getType();
      LabelSerializer ls = (LabelSerializer) serializers.get(labelType);
      if (ls == null) {
        ls = factory.createSerializerFor(labelType);
        serializers.put(labelType, ls);
      }
      ls.serialize(getLabelContext(label, snip, model));
    }
  }

  /** May be reimplemented, but typically not necessary */
  protected void writeModel(Model model, Writer writer) throws RDFException {
    // get an RDFWriter for the used RDF format (e.g. "RDF/XML-ABBREV"):
    RDFWriter rdfWriter = model.getWriter(_rdfFormat);
    // set xml:base attribute to the local URI used:
    rdfWriter.setProperty("xmlBase", _uriPrefix);
    // set xmlns:s namespace attribute for the Snip Schema:
    rdfWriter.setNsPrefix("s", SNIP.getURI());
    // finally, write out the model:
    rdfWriter.write(model, writer, _uriPrefix);
  }

  protected final void recursiveFillModel(Snip snip, Model model, int depth) throws RDFException {
    addSingleSnipToModel(snip, model);
    // depth = -1 -> unlimited recursion. 0 = don't process children
    if (depth-- == 0)
      return;
    Iterator iterator = getLinksIterator(snip);
    if (iterator != null) {
      while (iterator.hasNext()) {
        recursiveFillModel((Snip) iterator.next(), model, depth);
      }
    }
  }

  private Bag addCommentsBag(Model model, Snip snip) {
    Bag commentsBag = null;
    try {
      commentsBag = model.createBag();
      // TODO: test if bag is empty ...
      Iterator iterator = getCommentsIterator(snip);
      if (iterator != null) {
        while (iterator.hasNext()) {
          Snip comment = (Snip) iterator.next();
          // add comment to model as new resource:
          Resource commentResource = addCommentResource(model, comment, snip);
          // add comment to the comments Bag:
          commentsBag.add(commentResource);
        }
      }
    } catch (RDFException e) {
      e.printStackTrace();
    }
    return commentsBag;
  }

  private Bag addSnipLinksBag(Model model, Snip snip) {
    Bag snipLinksBag = null;
    try {
      snipLinksBag = model.createBag();
      // TODO: test if list of links is empty?
      Iterator iterator = getLinksIterator(snip);
      if (iterator != null) {
        while (iterator.hasNext()) {
          String snipLink = (String) iterator.next();
          // add snipLink to the comments Bag:
          snipLinksBag.add(new ResourceImpl(_uriPrefix + '#' + snipLink));
        }
      }
    } catch (RDFException e) {
      e.printStackTrace();
    }
    return snipLinksBag;
  }

  private Bag addAttachmentsBag(Model model, Snip snip) {
    Bag snipAttachmentsBag = null;
    try {
      snipAttachmentsBag = model.createBag();
      // TODO: test if list of attachments is empty?
      Iterator iterator = getAttachmentsIterator(snip);
      if (iterator != null) {
        while (iterator.hasNext()) {
          Attachment attachment = (Attachment) iterator.next();
          Resource attachmentResource = addAttachmentResource(model, attachment, snip);
          // add attachment to the attachments Bag:
          snipAttachmentsBag.add(attachmentResource);
        }
      }
    } catch (RDFException e) {
      e.printStackTrace();
    }
    return snipAttachmentsBag;
  }

  private Resource addSnipResource(Model model, Snip snip) {
    Resource snipResource = null;
    try {
      snipResource = model.createResource(getSnipResURI(snip));
      snipResource.addProperty(RDF.type, SNIP.Snip);
      snipResource.addProperty(SNIP.name, snip.getName());
      snipResource.addProperty(SNIP.content, snip.getContent());
      snipResource.addProperty(SNIP.cUser, snip.getCUser());
      snipResource.addProperty(SNIP.oUser, snip.getOUser());
      snipResource.addProperty(SNIP.mUser, snip.getMUser());
      snipResource.addProperty(SNIP.mTime, snip.getMTime());
      snipResource.addProperty(SNIP.cTime, snip.getCTime());
    } catch (RDFException e) {
      e.printStackTrace();
    }
    return snipResource;
  }

  private Resource addCommentResource(Model model, Snip comment, Snip commentedSnip) {
    Resource commentResource = null;
    try {
      commentResource = model.createResource(getSnipResURI(comment));
      commentResource.addProperty(RDF.type, SNIP.Comment);
      commentResource.addProperty(SNIP.name, comment.getName());
      commentResource.addProperty(SNIP.content, comment.getContent());
      commentResource.addProperty(SNIP.cUser, comment.getCUser());
      commentResource.addProperty(SNIP.oUser, comment.getOUser());
      commentResource.addProperty(SNIP.mUser, comment.getMUser());
      commentResource.addProperty(SNIP.mTime, comment.getMTime());
      commentResource.addProperty(SNIP.cTime, comment.getCTime());
      commentResource.addProperty(SNIP.commentedSnip, getSnipResource(model, commentedSnip));
    } catch (RDFException e) {
      e.printStackTrace();
    }
    return commentResource;
  }

  private Resource addAttachmentResource(Model model, Attachment att, Snip snip) {
    Resource attachmentResource = null;
    try {
      attachmentResource = model.createResource(getAttachmentURL(att, snip));
      attachmentResource.addProperty(RDF.type, SNIP.Attachment);
      attachmentResource.addProperty(SNIP.fileName, att.getName());
      attachmentResource.addProperty(SNIP.contentType, att.getContentType());
      attachmentResource.addProperty(SNIP.size, att.getSize());
      attachmentResource.addProperty(SNIP.date, att.getDate());
    } catch (RDFException e) {
      e.printStackTrace();
    }
    return attachmentResource;
  }

}
