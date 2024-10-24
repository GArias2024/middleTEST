package com.ecodex.midd.config;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.XMLStreamException;

import org.apache.cxf.staxutils.StaxUtils;
import org.apache.cxf.ws.policy.WSPolicyFeature;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.io.Resource;
import org.w3c.dom.Element;


public class WsPolicyLoader {
  private static final Logger LOGGER =
      LogManager.getLogger(WsPolicyLoader.class);

  private Resource wsPolicy;

  public WsPolicyLoader(Resource resource) {
    this.wsPolicy = resource;
  }

  public WSPolicyFeature loadPolicyFeature() {
    LOGGER.info("Loading policy from resource: [{}]", this.wsPolicy);
    WSPolicyFeature policyFeature = new WSPolicyFeature();
    policyFeature.setEnabled(false); // Se pone en false de momento, en true da error.

    InputStream is = null;
    try {
      is = this.wsPolicy.getInputStream();
    } catch (IOException ioe) {
      throw new UncheckedIOException(
          String.format("ws policy [%s] cannot be read!", this.wsPolicy), ioe);
    }
    if (is == null) {
      throw new RuntimeException(
          String.format("ws policy [%s] cannot be read! InputStream is nulL!",
              this.wsPolicy));
    }
    List<Element> policyElements = new ArrayList<>();
    try {
      Element e = StaxUtils.read(is).getDocumentElement();
      LOGGER.info("adding policy element [{}]", e);
      policyElements.add(e);
    } catch (XMLStreamException ex) {
      throw new RuntimeException("cannot parse policy " + this.wsPolicy,
          ex);
    }
    // policyFeature.getPolicyElements().addAll(policyElements);
    policyFeature.setPolicyElements(policyElements);
    return policyFeature;
  }

}
