package org.snipsnap.serialization;

import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;
import org.snipsnap.util.Service;
import org.radeox.util.logging.Logger;

/** @author gis */
public class LabelSerializerFactory {

    /** Creates an instance of a LabelSerializerFactory for the given outputFormat (as specified in @see SerializerFactory) */
    public LabelSerializerFactory(int outputFormat) {
        m_outputFormat = String.valueOf(outputFormat);
    }

    public LabelSerializer createSerializerFor(String labelType) {
        Class serClass = getClassFor(labelType);
        if (serClass == null)
            return null;
        try {
            LabelSerializer serializer = (LabelSerializer)serClass.newInstance();
            return serializer;
        }
        catch (Exception e) { }
        return null;
    }

    /**
     * All instances of this factory share a map of default label serializers, that will be used
     * when no specific serializer for a given label-type is available. This is how to add those.
     * @param format	the output format as specified in @see SerializerFactory
     */
    public static synchronized void addDefaultLabelSerializer(String labelType, int format, Class cls) {
        if (s_defaultClasses == null)
            s_defaultClasses = new HashMap();
        Class[] interfaces = cls.getInterfaces();
        boolean ok = false;
        for (int i = 0; i < interfaces.length; i++) {
            if (interfaces[i].getName() == "org.snipsnap.serialization.LabelSerializer;") {
                ok = true;
                break;
            }
        }
        if (!ok) {
            Logger.debug("LabelSerializerFactory::addDefaultLabelSerializer: supplied Class does not implement LabelSerializer interface");
            return;
        }
        s_defaultClasses.put(getKey(labelType, String.valueOf(format)), cls);
    }

    private Class getClassFor(String labelType) {
        if (m_classMap.isEmpty()) // optimistically, we load them when we don't have any
                loadClasses();
        String key = getKey(labelType, m_outputFormat);
        Class cls = (Class)m_classMap.get(key);
        if (cls == null && s_defaultClasses != null)
            cls = (Class)s_defaultClasses.get(key);
        return cls;
    }

    private void loadClasses() {
        m_classMap.clear();
        try {
            // TODO: overload Service.providers() to take a String as argument
            Iterator it = Service.providers(Class.forName("org.snipsnap.serialization.LabelSerializer"));
            while (it.hasNext()) {
                try {
                    LabelSerializer ls = (LabelSerializer)it.next();
                    int outputFormat = ls.getOutputFormat();
                    Iterator typesIt = ls.getSupportedLabelTypes().iterator();
                    while (typesIt.hasNext()) {
                        String key = getKey((String)typesIt.next(), m_outputFormat);
                        m_classMap.put(key, ls.getClass());
                    }
                }
                catch (Exception e) // be tolerant
                { }
            }
        }
        catch (ClassNotFoundException e) { }
    }

    private static String getKey(String type, String m_outputFormat) {
        return m_outputFormat + "_" + type;
    }

    private String m_outputFormat;
    private static Map s_defaultClasses = null;
    private Map m_classMap = new HashMap();
}
