/**
 * This file has been automatically generated using Grover (https://github.com/rmrschub/grover).
 * It contains static constants for the terms in the ACTN vocabulary.
 */
package de.dfki.resc28.sodalite.vocabularies;

import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.shared.PrefixMapping;

public final class LTS 
{
  public static final String PREFIX = "LTS";
  public static final PrefixMapping NAMESPACE = PrefixMapping.Factory.create().setNsPrefix(PREFIX, CONSTANTS.NS);
  
  /** 
   * Classes as org.apache.jena.rdf.model.Resource
   */
  public static final Resource StateMachine = resource(CONSTANTS.CLASS_StateMachine);
  public static final Resource State = resource(CONSTANTS.CLASS_State);
  public static final Resource Initial = resource(CONSTANTS.CLASS_Initial);
  public static final Resource Simple = resource(CONSTANTS.CLASS_Simple);
  public static final Resource Transition = resource(CONSTANTS.CLASS_Transition);
  
  /** 
   * Properties as org.apache.jena.rdf.model.Property
   */
  public static final Property model = property(CONSTANTS.PROP_model);
  public static final Property source = property(CONSTANTS.PROP_source);
  public static final Property target = property(CONSTANTS.PROP_target);
  public static final Property label = property(CONSTANTS.PROP_label);
  public static final Property stateIndicator = property(CONSTANTS.PROP_stateIndicator);

  /**
   * Returns a Jena resource for the given namespace name 
   * @param nsName  the full namespace name of a vocabulary element as a string
   * @return the vocabulary element with given namespace name as a org.apache.jena.rdf.model.Resource
   */
  private static final Resource resource(String nsName)
  {
    return ResourceFactory.createResource(nsName); 
  }

  /**
   * Returns a Jena property for the given namespace name
   * @param nsName  the full namespace name of a vocabulary element as a string
   * @return the vocabulary element with given namespace name as a org.apache.jena.rdf.model.Property
   */
  private static final Property property(String nsName)
  { 
    return ResourceFactory.createProperty(nsName);
  }

  private static final class CONSTANTS 
  {
    /**
     * Vocabulary namespace URI as string 
     */
    private static final String NS = "http://www.dfki.de/resc01/ns/lts#";

    /**
     * Local and namespace names of RDF(S) classes as strings 
     */
    private static final String CLASS_LNAME_StateMachine = "StateMachine";
    private static final String CLASS_StateMachine = nsName(CLASS_LNAME_StateMachine);
    private static final String CLASS_LNAME_State = "State";
    private static final String CLASS_State = nsName(CLASS_LNAME_State);
    private static final String CLASS_LNAME_Initial = "Initial";
    private static final String CLASS_Initial = nsName(CLASS_LNAME_Initial);
    private static final String CLASS_LNAME_Simple = "Simple";
    private static final String CLASS_Simple = nsName(CLASS_LNAME_Simple);
    private static final String CLASS_LNAME_Transition = "Transition";
    private static final String CLASS_Transition = nsName(CLASS_LNAME_Transition);
    /**
     * Local and namespace names of RDF(S) properties as strings 
     */
    private static final String PROP_LNAME_model = "model";
    private static final String PROP_model = nsName(PROP_LNAME_model);
    private static final String PROP_LNAME_source = "source";
    private static final String PROP_source = nsName(PROP_LNAME_source);
    private static final String PROP_LNAME_target = "target";
    private static final String PROP_target = nsName(PROP_LNAME_target);
    private static final String PROP_LNAME_label = "label";
    private static final String PROP_label = nsName(PROP_LNAME_label);
    private static final String PROP_LNAME_stateIndicator = "stateIndicator";
    private static final String PROP_stateIndicator = nsName(PROP_LNAME_stateIndicator);
    
    /**
     * Returns the full namespace name of a vocabulary element as a string
     * @param localName  the local name of a vocabulary element as a string
     * @return the full namespace name of a vocabulary element as a string
     */
    private static String nsName(String localName) 
    {
      return NS + localName;
    }
  }
}