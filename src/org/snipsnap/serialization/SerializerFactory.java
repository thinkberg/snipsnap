package org.snipsnap.serialization;

import org.snipsnap.serialization.rdf.RDFSerializer;
import org.snipsnap.serialization.rdf.DAMLSerializer;

public class SerializerFactory {

  private SerializerFactory() {
  }

  // constants:
  public final static int RDF_10 = 1;
  public final static int DAML_OIL = 2;

  public static Serializer createSerializer(int outputFormat) throws UnknownFormatException {
    Serializer ser = null;
    switch (outputFormat) {
      case RDF_10:
        ser = new RDFSerializer(outputFormat);
        break;
      case DAML_OIL:
        ser = new DAMLSerializer(outputFormat);
        break;
      default:
        String msg = "Sorry, no Serializer available for the given output format: " + outputFormat;
        throw new UnknownFormatException(msg);
    }
    return ser;
  }
}
