package cz.fi.muni.eshop.controller;

import cz.fi.muni.eshop.model.Customer;
import cz.fi.muni.eshop.service.CustomerManager;
import cz.fi.muni.eshop.util.Controller;
import cz.fi.muni.eshop.util.DataGenerator;
import cz.fi.muni.eshop.util.EntityValidator;
import cz.fi.muni.eshop.util.Identity;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.enterprise.inject.Model;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.validation.ConstraintViolation;

/**
 *
 * @author Petr Kremensky <207855@mail.muni.cz>
 */
@Model
public class CustomerBean {

    private String email;
    private String name;
    private String password;
    @Inject
    private Identity identity;
    @EJB
    private Controller controller;
    @EJB
    private CustomerManager customerManager;
    private List<Customer> customers;
    @Inject
    private Logger log;
    @Inject
    private DataGenerator dataGenerator;
    @Inject
    private EntityValidator<Customer> validator;

    public List<Customer> getCustomers() {
        return customerManager.getCustomers();
    }

    private void clearBean() {
        email = "";
        name = "";
        password = "";
    }

    public void addCustomer() {
        if (validate()) {
            customerManager.addCustomer(email, name, password);
            clearBean();
        }
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void clearCustomers() {
        log.info("Clearing customers data with all orders and invoices");
        addMessage("Clearing customers data with all orders and invoices");
        customerManager.clearCustomersTable();
        identity.logOut();
    }

    private void addMessage(String summary) {
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_WARN,
                summary, null);
        FacesContext.getCurrentInstance().addMessage(null, message);
    }

    public void deleteCustomer(String email) {
        customerManager.deleteCustomer(email);
        if (identity.getEmail().equals(email)) {
            identity.logOut();
        }
    }

    public void generateRandomCustomer() {
        log.info("generating random customer");
        dataGenerator.generateRandomCustomer();
    }

    public void register() {
        if (validate()) {
            identity.logIn(customerManager.addCustomer(email, name, password));
            clearBean();
        }
    }

    // DO NOT USE required="true" property in input text widgets, unable to
    // generate random then without filling the form
    // just front end validation
    private boolean validate() {
        Set<ConstraintViolation<Customer>> violations = validator.validate(new Customer(email, name, password));
        if (violations.isEmpty()) {
            return true;
        } else {
            for (ConstraintViolation<Customer> constraintViolation : violations) {
                addMessage(constraintViolation.getMessageTemplate());
            }
        }
        return false;
    }

    public Long customersCount() {
        return customerManager.getCustomerTableCount();
    }
}
