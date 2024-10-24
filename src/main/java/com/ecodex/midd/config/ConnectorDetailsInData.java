package com.ecodex.midd.config;


public class ConnectorDetailsInData {

  private static ConnectorDetailsInData minstance = null;

  public static ConnectorDetailsInData getInstance() {
    return minstance;
  }

  public static ConnectorDetailsInData getInstance(String service,
      String serviceType,
      String action, String fromRole, String fromType, String toRole,
      String toType) {
    if (minstance == null) {
      minstance = new ConnectorDetailsInData(service, serviceType,
          action, fromRole, fromType, toRole,
          toType);
    }
    return minstance;
  }

  private String service;

  private String serviceType;

  private String action;

  private String fromPartyRole;

  private String fromPartyIdType;

  private String toPartyRole;

  private String toPartyIdType;



  public ConnectorDetailsInData(String service, String serviceType,
      String action, String fromRole, String fromType, String toRole,
      String toType) {
    this.service = service;
    this.serviceType = serviceType;
    this.action = action;
    this.fromPartyRole = fromRole;
    this.fromPartyIdType = fromType;
    this.toPartyRole = toRole;
    this.toPartyIdType = toType;

  }

  public String getAction() {
    return this.action;
  }

  public String getFromPartyIdType() {
    return this.fromPartyIdType;
  }

  public String getFromPartyRole() {
    return this.fromPartyRole;
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

  public void setAction(String action) {
    this.action = action;
  }

  public void setFromPartyIdType(String fromPartyIdType) {
    this.fromPartyIdType = fromPartyIdType;
  }

  public void setFromPartyRole(String fromPartyRole) {
    this.fromPartyRole = fromPartyRole;
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

}
