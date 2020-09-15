package com.julio.modconceitual.services;

import org.springframework.mail.SimpleMailMessage;

import com.julio.modconceitual.domain.Pedido;

public interface EmailService {
	
	void sendOrderConfirmationEmail(Pedido obj);
	
	void sendEmail(SimpleMailMessage msg);

}
