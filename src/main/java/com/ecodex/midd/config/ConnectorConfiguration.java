package com.ecodex.midd.config;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Properties;

import javax.xml.ws.soap.SOAPBinding;

import org.apache.cxf.Bus;
import org.apache.cxf.bus.spring.SpringBus;
import org.apache.cxf.helpers.IOUtils;
import org.apache.cxf.jaxws.EndpointImpl;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;
import org.apache.cxf.spring.boot.autoconfigure.CxfAutoConfiguration;
import org.apache.cxf.ws.policy.WSPolicyFeature;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

import eu.domibus.connector.lib.spring.configuration.CxfTrustKeyStoreConfigurationProperties;
import eu.domibus.connector.lib.spring.configuration.StoreConfigurationProperties;
import eu.domibus.connector.ws.backend.delivery.webservice.DomibusConnectorBackendDeliveryWSService;
import eu.domibus.connector.ws.backend.webservice.DomibusConnectorBackendWSService;
import eu.domibus.connector.ws.backend.webservice.DomibusConnectorBackendWebService;
import eu.domibus.connector.ws.gateway.delivery.webservice.DomibusConnectorGatewayDeliveryWebService;

@Configuration
@ComponentScan("com.ecodex.midd")
@ConditionalOnClass(SpringBus.class)
//@EnableConfigurationProperties(value = ConnectorLinkWSProperties.class)
@AutoConfigureAfter(CxfAutoConfiguration.class)
//@ConditionalOnProperty(prefix = ConnectorLinkWSProperties.PREFIX,
//    name = "enabled", matchIfMissing = true)
public class ConnectorConfiguration {

  private class ProcessMessageAfterReceivedFromConnectorInterceptor
      extends AbstractPhaseInterceptor<Message> {
    ProcessMessageAfterReceivedFromConnectorInterceptor() {
      super(Phase.POST_INVOKE);
    }

    @Override
    public void handleMessage(Message message) {
      LOGGER.trace(
          "ProcessMessageAfterReceivedFromConnectorInterceptor: handleMessage: invoking backendSubmissionService.processMessageAfterDeliveredToBackend");

      InputStream in = message.getContent(InputStream.class);
      byte payload[] = null;
      try {
        payload = IOUtils.readBytesFromStream(in);
      } catch (IOException e) {
        LOGGER.error("Exception on handleMessage Interceptor :: {}",
            e.getMessage());
      }
      ByteArrayInputStream bin = new ByteArrayInputStream(payload);
      message.setContent(InputStream.class, bin);
    }
  }

  @Configuration
  @ConditionalOnMissingBean({SpringBus.class})
  @ImportResource({"classpath:META-INF/cxf/cxf.xml"})
  protected static class SpringBusConfiguration {
    protected SpringBusConfiguration() {}
  }

  private static final Logger LOGGER =
      LogManager.getLogger(ConnectorConfiguration.class);

  @Autowired
  private Bus cxfBus;

  @Autowired
  ConnectorLinkWSProperties connectorLinkWsProperties;

  @Bean(name = "connector")
  public DomibusConnectorBackendWebService connectorWsClient() {
    JaxWsProxyFactoryBean jaxWsProxyFactoryBean = new JaxWsProxyFactoryBean();
    jaxWsProxyFactoryBean
        .setServiceClass(DomibusConnectorBackendWebService.class);
    jaxWsProxyFactoryBean.setBus(this.cxfBus);
    jaxWsProxyFactoryBean
        .setAddress(this.connectorLinkWsProperties.getConnectorAddress());
    jaxWsProxyFactoryBean
        .setServiceName(DomibusConnectorBackendWSService.SERVICE);
    jaxWsProxyFactoryBean.setEndpointName(
        DomibusConnectorBackendWSService.DomibusConnectorBackendWebService);
    jaxWsProxyFactoryBean
        .setWsdlURL(DomibusConnectorBackendWSService.WSDL_LOCATION.toString());
    jaxWsProxyFactoryBean.setBindingId(SOAPBinding.SOAP11HTTP_MTOM_BINDING);

    jaxWsProxyFactoryBean.getInInterceptors()
        .add(new ProcessMessageAfterReceivedFromConnectorInterceptor());

    // Da error si no se establece en false el policyFeature.setEnabled(false);
    jaxWsProxyFactoryBean.getFeatures()
        .add(this.policyLoader().loadPolicyFeature());

    if (jaxWsProxyFactoryBean.getProperties() == null) {
      jaxWsProxyFactoryBean.setProperties(new HashMap<>());
    }
    jaxWsProxyFactoryBean.getProperties().put("mtom-enabled", true);
    jaxWsProxyFactoryBean.getProperties().put("security.encryption.properties",
        this.connectorWsLinkEncryptionProperties());
    jaxWsProxyFactoryBean.getProperties().put("security.encryption.username",
        this.connectorLinkWsProperties.getCxf().getEncryptAlias());

    jaxWsProxyFactoryBean.getProperties().put("security.signature.properties",
        this.connectorWsLinkEncryptionProperties());
    jaxWsProxyFactoryBean.getProperties().put("security.callback-handler",
        new DefaultWsCallbackHandler());

    DomibusConnectorBackendWebService domibusConnectorBackendWebService =
        jaxWsProxyFactoryBean
            .create(DomibusConnectorBackendWebService.class);

    LOGGER.info("Registered WS Client for [{}]",
        DomibusConnectorBackendWebService.class);

    return domibusConnectorBackendWebService;
  }

  @Bean
  public Properties connectorWsLinkEncryptionProperties() {
    Properties props = new Properties();

    CxfTrustKeyStoreConfigurationProperties cxf =
        this.connectorLinkWsProperties.getCxf();
    StoreConfigurationProperties cxfKeyStore =
        this.connectorLinkWsProperties.getCxf().getKeyStore();

    props.put("org.apache.wss4j.crypto.provider",
        "org.apache.wss4j.common.crypto.Merlin");
    props.put("org.apache.wss4j.crypto.merlin.keystore.type", "jks");
    props.put("org.apache.wss4j.crypto.merlin.keystore.file",
        cxfKeyStore.getPathUrlAsString());
    props.put("org.apache.wss4j.crypto.merlin.keystore.password",
        cxfKeyStore.getPassword());
    props.put("org.apache.wss4j.crypto.merlin.keystore.alias",
        cxf.getPrivateKey().getAlias());
    props.put("org.apache.wss4j.crypto.merlin.keystore.private.password",
        cxf.getPrivateKey().getPassword());

    props.put("org.apache.wss4j.crypto.merlin.truststore.type", "jks");
    props.put("org.apache.wss4j.crypto.merlin.truststore.file",
        cxf.getTrustStore().getPathUrlAsString());
    props.put("org.apache.wss4j.crypto.merlin.truststore.password",
        cxf.getTrustStore().getPassword());

    return props;
  }

//  @Bean
//  public EcodexConnectorClientDeliveryWsImpl domibusConnectorClientDeliveryWsImpl() {
//    return new EcodexConnectorClientDeliveryWsImpl();
//  }
//
//
//
//  @Bean
//  public EndpointImpl domibusConnectorDeliveryServiceEndpoint() {
//    EndpointImpl endpoint = new EndpointImpl(this.cxfBus,
//        this.domibusConnectorClientDeliveryWsImpl());
//    endpoint.setAddress(this.connectorLinkWsProperties.getPublishAddress());
//    endpoint.setWsdlLocation(
//        DomibusConnectorBackendDeliveryWSService.WSDL_LOCATION.toString());
//    endpoint.setServiceName(DomibusConnectorBackendDeliveryWSService.SERVICE);
//    endpoint.setEndpointName(
//        DomibusConnectorBackendDeliveryWSService.DomibusConnectorBackendDeliveryWebService);
//
//    WSPolicyFeature wsPolicyFeature = this.policyLoader().loadPolicyFeature();
//    endpoint.getFeatures().add(wsPolicyFeature);
//
//
//    endpoint.getProperties().put("mtom-enabled", true);
//    endpoint.getProperties().put("security.encryption.properties",
//        this.connectorWsLinkEncryptionProperties());
//    endpoint.getProperties().put("security.encryption.username",
//        this.connectorLinkWsProperties.getCxf().getEncryptAlias());
//
//    endpoint.getProperties().put("security.signature.properties",
//        this.connectorWsLinkEncryptionProperties());
//
//    endpoint.publish();
//
//    LOGGER.info("Published WebService {} under {}",
//        DomibusConnectorGatewayDeliveryWebService.class,
//        endpoint.getPublishedEndpointUrl());
//
//    return endpoint;
//  }

  @Bean
  public WsPolicyLoader policyLoader() {
    return new WsPolicyLoader(this.connectorLinkWsProperties.getWsPolicy());

  }

}
