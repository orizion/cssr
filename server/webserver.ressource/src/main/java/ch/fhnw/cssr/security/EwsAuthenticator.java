package ch.fhnw.cssr.security;

import java.net.URI;
import java.net.URISyntaxException;

import org.springframework.beans.factory.annotation.Value;

import microsoft.exchange.webservices.data.core.ExchangeService;
import microsoft.exchange.webservices.data.core.PropertySet;
import microsoft.exchange.webservices.data.core.enumeration.misc.ExchangeVersion;
import microsoft.exchange.webservices.data.core.exception.http.HttpErrorException;
import microsoft.exchange.webservices.data.core.exception.service.remote.ServiceRequestException;
import microsoft.exchange.webservices.data.core.exception.service.remote.ServiceResponseException;
import microsoft.exchange.webservices.data.core.service.item.EmailMessage;
import microsoft.exchange.webservices.data.credential.ExchangeCredentials;
import microsoft.exchange.webservices.data.credential.WebCredentials;
import microsoft.exchange.webservices.data.property.complex.ItemId;

public class EwsAuthenticator {

    @Value("${cssr.mail.ewsurl}")
    private String ewsUrl;

    /**
     * Checks whether the given email and password to match. 
     * @param email The email to be checked
     * @param password The password to be checked
     * @return True for a matching password, otherwise false 
     */
    public boolean matchesPassword(String email, CharSequence password) {
        System.out.println("Hello World!");

        try (ExchangeService service = new ExchangeService(ExchangeVersion.Exchange2010_SP2)) {

            try {
                service.setUrl(new URI(ewsUrl));
            } catch (URISyntaxException e) { // A wrong url in the settings is really not expected
                throw new RuntimeException(e);
            }
            System.out.println(service.getUrl().toString());
            ExchangeCredentials credentials = new WebCredentials(email, password.toString());
            service.setCredentials(credentials);

            return login(service);
        }
    }

    static Integer getHttpErrorCode(Throwable error) {
        if (error == null) {
            return null;
        }
        if (error instanceof HttpErrorException) {
            return ((HttpErrorException) error).getHttpErrorCode();
        }
        if (error.getCause() == error) {
            return null;
        }
        return getHttpErrorCode(error.getCause());
    }

    private boolean login(ExchangeService service) {
        try {
            EmailMessage.bind(service, new ItemId("__dummyId__"), PropertySet.IdOnly);
            return false; // It that works, exchange should get a new job :)
        } catch (ServiceRequestException resp) {
            Integer code = getHttpErrorCode(resp);
            if (code == 401) {
                return false;
            }
            throw new RuntimeException(resp);
        } catch (ServiceResponseException resp) {
            if (resp.getMessage().contains("Id is malformed")) {
                return true;
            }
            throw new RuntimeException(resp);
        } catch (Exception err) {
            throw new RuntimeException(err);
        }
    }
}
