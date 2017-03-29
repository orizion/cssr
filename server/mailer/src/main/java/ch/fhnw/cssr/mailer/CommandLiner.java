package ch.fhnw.cssr.mailer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class CommandLiner implements CommandLineRunner {

	@Autowired
	private MailSender sender;
	
	public void run(String... args) throws Exception {
		System.out.println(args);
		sender.sendAll();
	}

}
