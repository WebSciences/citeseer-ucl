/**
 * 
 */
package edu.psu.citeseerx.myciteseer.acl;

/**
 * Exception used when an expected method is not found in within the domain 
 * object class.
 * @author Juan Pablo Fernandez Ramirez
 * @version $$Rev: 823 $$ $$Date: 2008-12-18 19:25:15 -0500 (Thu, 18 Dec 2008) $$
 */
public class MethodNotImplemented extends RuntimeException {

	private static final long serialVersionUID = 5467461315334235816L;

	public MethodNotImplemented(String message) {
		super(message);
	} //- MethodNotImplemented
} //- class MethodNotImplemented
