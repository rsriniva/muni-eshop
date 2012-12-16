/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.fi.muni.eshop.util;

import cz.fi.muni.eshop.service.CustomerManager;
import cz.fi.muni.eshop.service.InvoiceManager;
import cz.fi.muni.eshop.service.OrderManager;
import cz.fi.muni.eshop.service.ProductManager;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;

/**
 * 
 * @author Petr Kremensky <207855@mail.muni.cz>
 */
@Startup
@Singleton
public class Controller {

	@Inject
	private ProductManager productManager;
	@Inject
	private CustomerManager customerManager;
	@Inject
	private OrderManager orderManager;
	@Inject
	private InvoiceManager invoiceManager;
	@Inject
	private Logger log;
	@Inject
	private DataGenerator dataGenerator;

	@PostConstruct
	public void showYourselve() {
		log.warning("ControllerBean initialized");
	}

	public void generateData() {
		dataGenerator.generateCustomers(1L);
		dataGenerator.generateProducts(1L, 1L, 1L);
		dataGenerator.generateOrders(1L, 1L);
	}

	/**
	 * Every minute clean oldest orders together with invoices
	 */
	@Schedule(minute="*", hour="*")
	public void controlData() {
		log.warning("I am alive");
	}
	
	/**
	 * To completely remove all data from db
	 */
	public boolean wipeOutDb() {
		log.warning("Deleteng all entries from db");
		customerManager.clearCustomersTable();
		productManager.clearProductsTable();
		boolean empty = true;
		if (orderManager.getOrderTableCount() > 0) {
			empty = false;
		}if (productManager.getProductTableCount() > 0) {
			empty = false;
		}
		if (customerManager.getCustomerTableCount() > 0) {
			empty = false;
		}
		if (invoiceManager.getInvoiceTableCount() > 0) {
			empty = false;
		}
		return empty;
	}
}