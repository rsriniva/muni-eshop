/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.fi.muni.eshop.controller;

import cz.fi.muni.eshop.service.CustomerManager;
import cz.fi.muni.eshop.util.quilifier.JPA;
import cz.fi.muni.eshop.util.quilifier.MyLogger;
import java.io.Serializable;
//import org.jboss.seam.security.Identity;
import java.util.logging.Logger;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;

/**
 *
 * @author Petr Kremensky <207855@mail.muni.cz>
 */
@Named
@SessionScoped
public class CustomerController implements Serializable {
    @Inject
    @JPA
    private CustomerManager manager;
    
    @Inject
    @MyLogger
    private Logger log;
    
//    @Inject
//    Identity indentity;

}
