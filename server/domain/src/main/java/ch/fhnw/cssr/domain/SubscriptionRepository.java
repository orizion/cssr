package ch.fhnw.cssr.domain;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SubscriptionRepository extends CrudRepository<Subscription, Long> {

	public List<Subscription> findByPresentationId(int presentationId);
}