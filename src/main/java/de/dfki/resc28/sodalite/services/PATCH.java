/*
 * This file is part of Sodalite. It is subject to the license terms in
 * the LICENSE file found in the top-level directory of this distribution.
 * You may not use this file except in compliance with the License.
 */
package de.dfki.resc28.sodalite.services;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.ws.rs.HttpMethod;

/**
 * @author rmrschub
 *
 */
/**
 * Indicates that the annotated method responds to HTTP PATCH requests
 * http://tools.ietf.org/html/rfc5789
 * 
 * @see HttpMethod
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@HttpMethod("PATCH")
public @interface PATCH { }