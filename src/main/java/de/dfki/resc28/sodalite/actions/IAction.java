/*
 * This file is part of Sodalite. It is subject to the license terms in
 * the LICENSE file found in the top-level directory of this distribution.
 * You may not use this file except in compliance with the License.
 */
package de.dfki.resc28.sodalite.actions;

import java.io.InputStream;
import java.util.Set;

import javax.ws.rs.core.Response;

import org.apache.jena.rdf.model.Model;

public interface IAction 
{	
	public abstract Model performTasks(Model consumable);
	
	public abstract Model updateState(Model consumable);
	
	public abstract Response describe(String acceptType);
	
	public abstract boolean isApplicable();
	
	public abstract Response execute(InputStream consumableStream, String contentType, String acceptType);
	
	public abstract boolean isValidConsumable(Model consumable);
	
	public abstract Set<String> getAllowedMethods();
}
