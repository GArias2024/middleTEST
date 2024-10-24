package com.ecodex.midd.config;


import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

//import com.protech.isupport.batch.ecodex.datamodel.ConnectorDetailsInData;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import eu.domibus.connector.lib.spring.configuration.CxfTrustKeyStoreConfigurationProperties;


@Component
@ConfigurationProperties(prefix = ConnectorLinkWSProperties.PREFIX)
@Validated
@PropertySource({"classpath:/connector.properties"})
@Valid
public class ConnectorLinkWSProperties {

  private static final Logger LOGGER =
      LogManager.getLogger(ConnectorLinkWSProperties.class);

  public static final String PREFIX = "domibus.connector-link.ws";
  public static final String ENABLED_PROPERTY_NAME = "enabled";
  public static final String PUSH_ENABLED_PROPERTY_NAME = "pushEnabled";

  /**
   * Boolean as String value. May be "true" or "false". If left empty "false" is
   * assumed. Indicates if the client should offer a webservice for the
   * domibusConnector to push messages to the client. If set, the client
   * libraries should run in a web container, or the client application in
   * standalone mode.
   */
  private boolean pushEnabled;

  private boolean enabled;

  /**
   * The URL of the domibusConnector. More specific the
   * DomibusConnectorBackendWebService address.
   */
  @NotNull
  private String connectorAddress;

  @NotNull
  private String service;

  @NotNull
  private String serviceType;

  @NotNull
  private String action;

  @NotNull
  private String fromPartyRole;

  @NotNull
  private String fromPartyIdType;

  @NotNull
  private String toPartyRole;

  @NotNull
  private String toPartyIdType;

  /**
   * Adress of the push webservice. Relativ path of the webservice the client
   * offers for the domibusConnector in case the push mode is enabled.
   */
  @NotNull
  private String publishAddress = "/domibusConnectorDeliveryWebservice";

  /**
   * Definition xml of the webservice-security. By default the library offers
   * the file "wsdl/backend.policy.xml" on the classpath that matches the
   * settings used by the domibusConnector.
   */
  private Resource wsPolicy = new ClassPathResource("wsdl/backend.policy.xml");


  @NestedConfigurationProperty
  @NotNull
  private CxfTrustKeyStoreConfigurationProperties cxf;

  public String getAction() {
    return this.action;
  }



  public String getConnectorAddress() {
    return this.connectorAddress;
  }

  public CxfTrustKeyStoreConfigurationProperties getCxf() {
    return this.cxf;
  }

  public boolean getEnabled() {
    return this.enabled;
  }

  public String getFromPartyIdType() {
    return this.fromPartyIdType;
  }

  public String getFromPartyRole() {
    return this.fromPartyRole;
  }

  public String getPublishAddress() {
    return this.publishAddress;
  }

  public boolean getPushEnabled() {
    return this.pushEnabled;
  }

  public String getService() {
    return this.service;
  }

  public String getServiceType() {
    return this.serviceType;
  }

  public String getToPartyIdType() {
    return this.toPartyIdType;
  }

  public String getToPartyRole() {
    return this.toPartyRole;
  }


  public Resource getWsPolicy() {
    return this.wsPolicy;
  }

  /**
   * Maps the configured properties for key-/truststore and keys to the crypto
   * Properties also see https://ws.apache.org/wss4j/config.html
   *
   * @return the wss Properties
   */
  public Properties getWssProperties() {
    Properties p = this.mapCertAndStoreConfigPropertiesToMerlinProperties();
    LOGGER.debug("getSignatureProperties() are: [{}]", p);
    return p;
  }

  @PostConstruct
  public void init() {

    ConnectorDetailsInData.getInstance(this.getService(), this.getServiceType(),
        this.getAction(), this.getFromPartyRole(), this.getFromPartyIdType(),
        this.getToPartyRole(),
        this.getToPartyIdType());
  }

  /**
   * Maps the own configured properties to the crypto Properties also see
   * https://ws.apache.org/wss4j/config.html
   *
   * @return the wss Properties
   */
  public Properties mapCertAndStoreConfigPropertiesToMerlinProperties() {
    Properties p = new Properties();
    p.setProperty("org.apache.wss4j.crypto.provider",
        "org.apache.wss4j.common.crypto.Merlin");
    p.setProperty("org.apache.wss4j.crypto.merlin.keystore.type", "jks");
    p.setProperty("org.apache.wss4j.crypto.merlin.keystore.password",
        this.getCxf().getKeyStore().getPassword());
    LOGGER.debug("setting [org.apache.wss4j.crypto.merlin.keystore.file={}]",
        this.getCxf().getKeyStore().getPath());
    try {
      p.setProperty("org.apache.wss4j.crypto.merlin.keystore.file",
          this.getCxf().getKeyStore().getPathUrlAsString());
    } catch (Exception e) {
      throw new RuntimeException(
          "Error with property: [" + PREFIX + ".key.store.path]\n" +
              "value is [" + this.getCxf().getKeyStore().getPath() + "]");
    }
    p.setProperty("org.apache.wss4j.crypto.merlin.keystore.alias",
        this.getCxf().getPrivateKey().getAlias());
    p.setProperty("org.apache.wss4j.crypto.merlin.keystore.private.password",
        this.getCxf().getPrivateKey().getPassword());
    p.setProperty("org.apache.wss4j.crypto.merlin.truststore.password",
        this.getCxf().getTrustStore().getPassword());
    try {
      LOGGER.debug(
          "setting [org.apache.wss4j.crypto.merlin.truststore.file={}]",
          this.getCxf().getTrustStore().getPath());
      p.setProperty("org.apache.wss4j.crypto.merlin.truststore.file",
          this.getCxf().getTrustStore().getPathUrlAsString());
    } catch (Exception e) {
      LOGGER.info("Trust Store Property: [" + PREFIX + ".trust.store.path]" +
          "\n cannot be processed. Using the configured key store [{}] as trust store",
          p.getProperty("org.apache.wss4j.crypto.merlin.keystore.file"));

      p.setProperty("org.apache.wss4j.crypto.merlin.truststore.file",
          p.getProperty("org.apache.wss4j.crypto.merlin.keystore.file"));
      p.setProperty("org.apache.wss4j.crypto.merlin.truststore.password",
          p.getProperty("org.apache.wss4j.crypto.merlin.keystore.password"));
    }
//     p.setProperty("org.apache.wss4j.crypto.merlin.load.cacerts",
//     Boolean.toString(getCxf().isLoadCaCerts()));

    return p;
  }

  public void setAction(String action) {
    this.action = action;
  }

  public void setConnectorAddress(String connectorAddress) {
    this.connectorAddress = connectorAddress;
  }

  public void setCxf(CxfTrustKeyStoreConfigurationProperties cxf) {
    this.cxf = cxf;
  }

  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }

  public void setFromPartyIdType(String fromPartyIdType) {
    this.fromPartyIdType = fromPartyIdType;
  }

  public void setFromPartyRole(String fromPartyRole) {
    this.fromPartyRole = fromPartyRole;
  }

  public void setPublishAddress(String publishAddress) {
    this.publishAddress = publishAddress;
  }

  public void setPushEnabled(boolean pushEnabled) {
    this.pushEnabled = pushEnabled;
  }

  public void setService(String service) {
    this.service = service;
  }

  public void setServiceType(String serviceType) {
    this.serviceType = serviceType;
  }

  public void setToPartyIdType(String toPartyIdType) {
    this.toPartyIdType = toPartyIdType;
  }

  public void setToPartyRole(String toPartyRole) {
    this.toPartyRole = toPartyRole;
  }

  public void setWsPolicy(Resource wsPolicy) {
    this.wsPolicy = wsPolicy;
  }
}

