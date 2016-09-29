/**
 * This file has been automatically generated using Grover (https://github.com/rmrschub/grover).
 * It contains static constants for the terms in the ACTN vocabulary.
 */
package de.dfki.resc28.sodalite.vocabularies;

import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.shared.PrefixMapping;

public final class ACTN 
{
  public static final String PREFIX = "ACTN";
  public static final PrefixMapping NAMESPACE = PrefixMapping.Factory.create().setNsPrefix(PREFIX, CONSTANTS.NS);

  /** 
   * Classes as org.apache.jena.rdf.model.Resource
   */
  public static final Resource ActionableResource = resource(CONSTANTS.CLASS_ActionableResource);
  public static final Resource Action = resource(CONSTANTS.CLASS_Action);
  public static final Resource ListAction = resource(CONSTANTS.CLASS_ListAction);

  /** 
   * Properties as org.apache.jena.rdf.model.Property
   */
  public static final Property action = property(CONSTANTS.PROP_action);
  public static final Property affordedBy = property(CONSTANTS.PROP_affordedBy);


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
    private static final String NS = "http://www.dfki.de/resc01/ns/actions#";

    /**
     * Local and namespace names of RDF(S) classes as strings 
     */
    private static final String CLASS_LNAME_ActionableResource = "ActionableResource";
    private static final String CLASS_ActionableResource = nsName(CLASS_LNAME_ActionableResource);
    private static final String CLASS_LNAME_Action = "Action";
    private static final String CLASS_Action = nsName(CLASS_LNAME_Action);
    private static final String CLASS_LNAME_ListAction = "ListAction";
    private static final String CLASS_ListAction = nsName(CLASS_LNAME_ListAction);
    /**
     * Local and namespace names of RDF(S) properties as strings 
     */
    private static final String PROP_LNAME_action = "action";
    private static final String PROP_action = nsName(PROP_LNAME_action);
    private static final String PROP_LNAME_affordedBy = "affordedBy";
    private static final String PROP_affordedBy = nsName(PROP_LNAME_affordedBy);
    private static final String PROP_LNAME_affords = "affords";
    private static final String PROP_affords = nsName(PROP_LNAME_affords);
 
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