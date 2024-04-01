package com.rajeshkawali.config;

import com.rajeshkawali.entity.Customer;
import org.springframework.batch.item.ItemProcessor;


/**
 * @author Rajesh_Kawali
 *
 */
public class CustomerProcessor implements ItemProcessor<Customer, Customer> {

	@Override
	public Customer process(Customer item) throws Exception {

		return item;
	}

}