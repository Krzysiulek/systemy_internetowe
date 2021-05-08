package jerseyrest.utils;

import javax.ws.rs.core.Link;
import javax.ws.rs.core.UriBuilder;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.namespace.QName;
import java.net.URI;
import java.util.Iterator;
import java.util.Map;

public class LinkParser {
    public static class JaxbAdapter extends XmlAdapter<Link.JaxbLink, Link> {
        public JaxbAdapter() {
        }

        public Link unmarshal(Link.JaxbLink v) {
            Link.Builder lb = Link.fromUri(v.getUri());
            Iterator var3 = v.getParams()
                             .entrySet()
                             .iterator();

            while (var3.hasNext()) {
                Map.Entry<QName, Object> e = (Map.Entry) var3.next();
                lb.param(((QName) e.getKey()).getLocalPart(),
                         e.getValue()
                          .toString());
            }

            return lb.build();
        }

        public Link.JaxbLink marshal(Link v) {
            Link.JaxbLink jl = new Link.JaxbLink(removeParameterFromURI(v.getUri()));
            Iterator var3 = v.getParams()
                             .entrySet()
                             .iterator();

            while (var3.hasNext()) {
                Map.Entry<String, String> e = (Map.Entry) var3.next();
                String name = (String) e.getKey();
                jl.getParams()
                  .put(new QName("", name), e.getValue());
            }

            return jl;
        }
    }

    public static URI removeParameterFromURI(URI uri) {
        UriBuilder uriBuilder = UriBuilder.fromUri(uri);
        return uriBuilder.replaceQuery(null)
                         .build();
    }

}


