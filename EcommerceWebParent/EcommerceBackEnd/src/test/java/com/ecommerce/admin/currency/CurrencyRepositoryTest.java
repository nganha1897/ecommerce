package com.ecommerce.admin.currency;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import com.ecommerce.common.entity.Currency;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(true)
public class CurrencyRepositoryTest {
    @Autowired
    CurrencyRepository repo;
    
//    @Test
//    public void testCreateCurrency() {
//    	Currency dollar = new Currency("United States Dollar", "$", "USD");
//    	Currency pound = new Currency("British Pound", "$", "GBP");
//    	Currency yen = new Currency("Japanese Yen", "¥", "JPY");
//    	Currency euro = new Currency("Euro", "€", "EUR");
//    	Currency ruble = new Currency("Russian Ruble", "₽", "RUB");
//    	Currency won = new Currency("South Korean Won", "₩", "KRW");
//    	Currency yuan = new Currency("Chinese Yuan", "¥", "CNY");
//    	Currency real = new Currency("Brazilian Real", "R$", "BRL");
//    	Currency ausDollar = new Currency("Australian Dollar", "$", "AUD");
//    	Currency canDollar = new Currency("Canadian Dollar", "$", "CAD");
//    	Currency dong = new Currency("Vietnamese đồng", "đ", "VND");
//    	Currency rupee = new Currency("Indian Rupee", "₹", "INR");
//    	
//    	repo.saveAll(List.of(dollar, pound, yen, euro, ruble, won, yuan, real, ausDollar, canDollar, dong, rupee));
//    }
    
    @Test
    public void testListAll() {
    	List<Currency> currencies = repo.findAllByOrderByNameAsc();
    	currencies.forEach(System.out::println);
    	assertThat(currencies.size()).isGreaterThan(0);
    }
    
//    @Test void testDelete() {
//    	repo.deleteById(13);
//    	repo.deleteById(14);
//    	repo.deleteById(15);
//    	repo.deleteById(16);
//    	repo.deleteById(17);
//    	repo.deleteById(18);
//    	repo.deleteById(19);
//    	repo.deleteById(20);
//    	repo.deleteById(21);
//    	repo.deleteById(22);
//    	repo.deleteById(23);
//    	repo.deleteById(24);
//    }
}
