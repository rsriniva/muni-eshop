/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.fi.muni.eshop.security;

import cz.fi.muni.eshop.model.CustomerEntity;
import cz.fi.muni.eshop.model.Role;
import cz.fi.muni.eshop.service.CustomerManager;
import cz.fi.muni.eshop.util.NoCustomerFoundExeption;
import cz.fi.muni.eshop.util.quilifier.JPA;
import cz.fi.muni.eshop.util.quilifier.MuniEshopLogger;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import org.jboss.seam.security.BaseAuthenticator;
import org.jboss.seam.security.Credentials;
import org.picketlink.idm.impl.api.PasswordCredential;

/**
 *
 * @author Petr Kremensky <207855@mail.muni.cz>
 */
public class Authenticator extends BaseAuthenticator {

    @Inject
    private Credentials credentials;
    @Inject
    private FacesContext facesContext;
    @Inject
    @JPA
    private CustomerManager customerManager;
    @Inject
    @MuniEshopLogger
    private Logger log;

    // Don't look to DB until there is a chance that we will find something!
    @Override
    public void authenticate() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        String email = credentials.getUsername();
        CustomerEntity customer = new CustomerEntity(email, "name", "password", Role.BASIC);

        Set<ConstraintViolation<CustomerEntity>> constraintViolations =
                validator.validate(customer);
        if (!constraintViolations.isEmpty()) {
            log.log(Level.INFO, "Invalid email trying to authenticate: {0}", email);
            setStatus(AuthenticationStatus.FAILURE);
            facesContext.addMessage("loginForm:username", new FacesMessage(
                    "Invalid email, unable to authenticate")); // TODO maybe add content of constraintViolations
        } else {
            String password = ((PasswordCredential) credentials.getCredential()).getValue();
            try {
                customer = customerManager.verifyCustomer(email, password);
            } catch (NoCustomerFoundExeption ncfe) {
                log.log(Level.INFO, "Non-existing user trying to authenticate: {0}", email);
                setStatus(AuthenticationStatus.FAILURE);
                facesContext.addMessage("loginForm:username", new FacesMessage(
                        "Non existing user"));
                return;
            }
            if (customer == null) {
                setStatus(AuthenticationStatus.FAILURE);
                log.log(Level.WARNING, "Wrong password for user: {0}", customer.getEmail());
                FacesContext.getCurrentInstance().addMessage(
                        "loginForm:password",
                        new FacesMessage("Wrong Password"));
            } else {
                setStatus(AuthenticationStatus.SUCCESS);
                setUser(customer);
                log.log(Level.INFO, "Succesfully authenticate user: {0}", customer.toString());
            }
        }


    }
}