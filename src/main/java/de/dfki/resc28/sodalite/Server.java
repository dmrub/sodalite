/*
 * This file is part of Sodalite. It is subject to the license terms in
 * the LICENSE file found in the top-level directory of this distribution.
 * You may not use this file except in compliance with the License.
 */
package de.dfki.resc28.sodalite;

import java.util.Set;

import javax.ws.rs.core.Application;

public abstract class Server extends Application 
{
	public static String dataEndpoint = "http://localhost:3030/DTrack2/data";
	public static String queryEndpoint = "http://localhost:3030/DTrack2/sparql";
	
	@Override
    public Set<Object> getSingletons() 
    {
		/* use this to construct your services
		 
        ActionService foo = new ActionService(new FusekiGraphStore(dataEndpoint, queryEndpoint));
        return new HashSet<Object>(Arrays.asList(foo));
        
		*/
		return null;
    }
}
